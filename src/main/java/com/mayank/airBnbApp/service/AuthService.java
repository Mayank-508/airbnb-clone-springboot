package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.LoginDto;
import com.mayank.airBnbApp.dto.LoginResponseDto;
import com.mayank.airBnbApp.dto.SignUpRequestDto;
import com.mayank.airBnbApp.dto.UserDto;
import com.mayank.airBnbApp.entity.User;
import com.mayank.airBnbApp.entity.enums.Role;
import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import com.mayank.airBnbApp.repository.UserRepository;
import com.mayank.airBnbApp.security.JWTService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;


    public UserDto signup(SignUpRequestDto signUpRequestDto)
    {
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if(user!=null)
        {
            throw new RuntimeException("User Already present with this email id");
        }
         user= modelMapper.map(signUpRequestDto, User.class);
         user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
         user.setRoles(Set.of(Role.GUEST));



         user=userRepository.save(user);

        return modelMapper.map(user, UserDto.class);


    }

    public String[] login(LoginDto loginDto) {

        Authentication authentication= authenticationManager
                .authenticate(new
                        UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

        User user= (User) authentication.getPrincipal();
        String[] tokens= new String[2];
        tokens[0]=jwtService.generateAccessToken(user);
        tokens[1]= jwtService.generateRefreshToken(user);

        return tokens;
    }

    public String refreshToken(String refreshToken)
    {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
