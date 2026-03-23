package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.BookingRequest;
import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.entity.*;
import com.mayank.airBnbApp.entity.enums.BookingStatus;
import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import com.mayank.airBnbApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.channels.FileLockInterruptionException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        for(Inventory inventory : inventoryList)
        {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }


        inventoryRepository.saveAll(inventoryList);

        //creating the booking



        // TODO : calculate dynamic pricing

        Booking booking= Booking.builder()
                 .hotel(hotel)
                .room(room)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .amount(BigDecimal.TEN)
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
            guest.setUser(getCurrentUser());
           guest= guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking  = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    public Boolean hasBookingExpired(Booking booking)
    {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }


    public User getCurrentUser()
    {
        User user= new User();
        user.setId(1L); // TODO : REMOVE THE DUMMY USER
        return user;
    }
}
