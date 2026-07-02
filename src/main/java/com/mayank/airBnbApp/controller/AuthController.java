package com.mayank.airBnbApp.controller;


import com.mayank.airBnbApp.dto.LoginDto;
import com.mayank.airBnbApp.dto.LoginResponseDto;
import com.mayank.airBnbApp.dto.SignUpRequestDto;
import com.mayank.airBnbApp.dto.UserDto;
import com.mayank.airBnbApp.service.AuthService;
import com.mayank.airBnbApp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto)
    {
        return new ResponseEntity<>(authService.signup(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        String[] tokens = authService.login(loginDto);

        Cookie cookie= new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);

        return  ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")




    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {


// Constant-first equals(): safer than cookie.getName().equals("refreshToken") because getName() may be null.
        // in order to avoid the Null Pointer Exception
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }



}
