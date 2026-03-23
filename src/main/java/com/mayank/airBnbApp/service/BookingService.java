package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.BookingRequest;
import com.mayank.airBnbApp.dto.GuestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList);
}
