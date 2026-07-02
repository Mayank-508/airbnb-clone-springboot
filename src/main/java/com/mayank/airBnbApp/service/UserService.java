package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    User getUserById(Long id);
}
