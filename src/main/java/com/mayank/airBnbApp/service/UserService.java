package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.GuestDto;
import com.mayank.airBnbApp.dto.ProfileUpdateRequestDto;
import com.mayank.airBnbApp.dto.UserDto;
import com.mayank.airBnbApp.entity.Guest;
import com.mayank.airBnbApp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);


    UserDto getMyProfile();


}
