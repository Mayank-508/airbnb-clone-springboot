package com.mayank.airBnbApp.controller;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.dto.ProfileUpdateRequestDto;
import com.mayank.airBnbApp.dto.UserDto;
import com.mayank.airBnbApp.entity.Guest;
import com.mayank.airBnbApp.service.BookingService;
import com.mayank.airBnbApp.service.GuestService;
import com.mayank.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto)
    {
          userService.updateProfile(profileUpdateRequestDto );
          return ResponseEntity.noContent().build();
    }

    @GetMapping("myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings()
    {
        List<BookingDto> bookings=bookingService.getMyBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("myProfile")
    public ResponseEntity<UserDto> getMyProfile()
    {
        UserDto bookings=userService.getMyProfile();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/getAllGuests")
    public ResponseEntity<List<GuestDto>> getAllGuests()
    {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("createGuest")
    public ResponseEntity<GuestDto> createGuest(@RequestBody GuestDto guestDto)
    {
        GuestDto guest= guestService.createGuest(guestDto);
        return new ResponseEntity<>(guest,HttpStatus.CREATED);
    }

    @PutMapping("/guests/{guestId}")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto)
    {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }



}
