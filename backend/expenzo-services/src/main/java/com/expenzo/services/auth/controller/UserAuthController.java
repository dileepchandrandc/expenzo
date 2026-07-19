package com.expenzo.services.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.auth.dto.UserToken;
import com.expenzo.services.auth.request.TokenRefreshRequest;
import com.expenzo.services.auth.request.UserLoginRequest;
import com.expenzo.services.auth.service.UserAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class UserAuthController {

    private final UserAuthService userService;

    @PostMapping("/login")
    public UserToken login(@RequestBody UserLoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PutMapping("/refresh")
    public UserToken refresh(@RequestBody TokenRefreshRequest request) {
        return userService.refreshToken(request.getRefreshToken());
    }
}
