package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.GuestDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;


public interface GuestService {

    List<GuestDto> getAllGuests();

    GuestDto createGuest(GuestDto guestDto);

    void updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);
}
