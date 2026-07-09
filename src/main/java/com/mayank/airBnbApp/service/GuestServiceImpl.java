package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.entity.Guest;
import com.mayank.airBnbApp.entity.User;
import com.mayank.airBnbApp.repository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;
import java.util.stream.Collectors;

import static com.mayank.airBnbApp.util.AppUtils.getCurrentUser;

@RequiredArgsConstructor
@Slf4j
@Service
public class GuestServiceImpl  implements  GuestService{

    private  final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> getAllGuests() {
        User user= getCurrentUser();
        log.info("Fetching all guests of user with id: {}", user.getId());

        List<Guest> guests= guestRepository.findByUser(user);

        return guests.stream().map((element) -> modelMapper.map(element, GuestDto.class)).collect(Collectors.toList());
    }

    @Override
    public GuestDto createGuest(GuestDto guestDto) {
        log.info("Adding a new guest : {}", guestDto);
        Guest guest=modelMapper.map(guestDto, Guest.class);
        guest.setUser(getCurrentUser());

        return modelMapper.map(guestRepository.save(guest), GuestDto.class) ;
    }

    @Override
    public void updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        modelMapper.map(guestDto, guest);
        guest.setUser(user);
        guest.setId(guestId);

        guestRepository.save(guest);
        log.info("Guest with ID: {} updated successfully", guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guestRepository.deleteById(guestId);
        log.info("Guest with ID: {} deleted successfully", guestId);
    }


}
