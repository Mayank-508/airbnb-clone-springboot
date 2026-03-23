package com.mayank.airBnbApp.controller;


import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.BookingRequest;
import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.entity.Booking;
import com.mayank.airBnbApp.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {
    private final BookingService bookingService;


    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest bookingRequest)
    {
        log.info("getting the roomsCount as = " + bookingRequest.getRoomsCount());
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }



    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList)
    {
        return ResponseEntity.ok(bookingService.addGuest(bookingId, guestDtoList));
    }

}
