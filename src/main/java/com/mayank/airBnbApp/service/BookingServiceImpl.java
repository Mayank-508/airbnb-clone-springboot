package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.BookingRequest;
import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.dto.HotelReportDto;
import com.mayank.airBnbApp.entity.*;
import com.mayank.airBnbApp.entity.enums.BookingStatus;
import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import com.mayank.airBnbApp.exceptions.UnAuthorisedException;
import com.mayank.airBnbApp.repository.*;
import com.mayank.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.mayank.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final PricingService pricingService;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {

        log.info("Inside the initialize booking service method");
        log.info("Initializing the booking for the hotel {}, room {}, date {} - {} for {} rooms",
                bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount()
        );

        Hotel hotel =hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(
                ()->  new ResourceNotFoundException("Hotel does not exist with id "+bookingRequest.getHotelId())
        );

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(
                ()->  new ResourceNotFoundException("Room does not exist with id "+bookingRequest.getRoomId())
        );

         List<Inventory> inventoryList= inventoryRepository.findAndLockAvailableInventory(
                 room.getId(),
                 bookingRequest.getCheckInDate(),
                 bookingRequest.getCheckOutDate(),
                 bookingRequest.getRoomsCount()
         );

         long daysCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate()) +1;
         if(inventoryList.size() < daysCount)
         {
             throw  new IllegalStateException("Room not available anymore");
         }

         // Reserve the rooms / update the booked count



        //creating the booking



        // TODO : calculate dynamic pricing -> #done
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking= Booking.builder()
                 .hotel(hotel)
                .room(room)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .amount(totalPrice)
                .bookingStatus(BookingStatus.RESERVED)
                .build()
        ;


        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);

    }


    @Override
    @Transactional
    public BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList) {


        Booking booking= bookingRepository.findById(bookingId)
                .orElseThrow(()-> new ResourceNotFoundException("No booking exist with id "+ bookingId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(hasBookingExpired(booking))
        {
            throw new IllegalStateException("Booking has already Expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED)
        {
            throw new IllegalStateException("Booking is not under the reserved state, cannot add guests");
        }

        for(GuestDto guestDto : guestDtoList)
        {
            Guest guest= modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
           guest= guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking  = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {

        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking does not exist with id "+bookingId));

        User user=getCurrentUser();
        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorisedException("This booking does not belong to the user with id "+user.getId());
        }

        if(hasBookingExpired(booking))
        {
            throw new IllegalStateException("Booking has already expired");
        }

     String sessionUrl= checkoutService.getCheckoutSession(booking, frontendUrl+"/payments/success", frontendUrl+"/payments/failure");


        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }


    @Override
    @Transactional
    public void capturePayment(Event event) {
        log.info("************Event type = {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {

            // --- ADD THESE TWO LINES TO SEE THE MISMATCH ---
            log.info("Incoming Webhook API Version: {}", event.getApiVersion());
            log.info("Stripe Java SDK Pinned Version: {}", com.stripe.Stripe.API_VERSION);
            // -----------------------------------------------
            Session session = null;
            try {
                if (event.getDataObjectDeserializer().getObject().isPresent()) {
                    session = (Session) event.getDataObjectDeserializer().getObject().get();
                } else {
                    session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                }
            } catch (com.stripe.exception.EventDataObjectDeserializationException e) {
                log.error("Error deserializing Stripe session: {}", e.getMessage());
                return;
            }
            if (session == null) {
                log.error("Failed to deserialize Stripe Session from Webhook");
                return;
            }

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            List<Inventory> inventoryList= inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            long daysCount= getBookingDays(booking);
            if(inventoryList.size() !=daysCount){
                throw new IllegalStateException(
                        String.format(
                                "Inventory inconsistency detected. Expected %d inventory records but found %d.",
                                daysCount,
                                inventoryList.size()
                        )
                );
            }
         int updatedRows= inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            if(updatedRows != getBookingDays(booking)){
                throw new IllegalStateException(
                        "Inventory update incomplete."
                );
            }

            // both , reserve the inventory and confirm booking is done in different queries
            // because for updating the inventory , Query need to be made @Modifying

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);



      List<Inventory> inventoryList= inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        long daysCount= getBookingDays(booking);
      if(inventoryList.size() !=daysCount){
          throw new IllegalStateException(
                  String.format(
                          "Inventory inconsistency detected. Expected %d inventory records but found %d.",
                          daysCount,
                          inventoryList.size()
                  )
          );
      }

      int updatedRows= inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        if(updatedRows != getBookingDays(booking)){
            throw new IllegalStateException(
                    "Inventory update incomplete."
            );
        }



        // handle the refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {

        Hotel hotel= hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel does not exist with id "+hotelId));

        User user= getCurrentUser();
        log.info("Getting all Bookings with hotel with id {}",hotelId);

        if(!user.equals(hotel.getOwner()))
        {
            throw new AccessDeniedException("You are not the owner of the hotel with id "+hotelId);
        }

       List<Booking> bookings= bookingRepository.findByHotel(hotel);

      return bookings.stream().map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);

    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream().map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toList());

    }


        public Boolean hasBookingExpired(Booking booking)
    {
        return booking.getCreatedAt().plusMinutes(15).isBefore(LocalDateTime.now());
    }



    private long getBookingDays(Booking booking){
        return ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        ) + 1;
    }
}
