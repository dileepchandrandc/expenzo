package com.expenzo.services.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expenzo.services.auth.dto.UserToken;
import com.expenzo.services.auth.repository.ExpenzoUserRepository;
import com.expenzo.services.auth.repository.model.ExpenzoUser;
import com.expenzo.services.auth.request.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthService {

    private final JwtService jwtService;
    private final ExpenzoUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserToken login(UserLoginRequest loginRequest) {
        ExpenzoUser user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return UserToken.builder()
            .accessToken(jwtService.generateAccessToken(user.getId()))
            .refreshToken(jwtService.generateRefreshToken(user.getId()))
        .build();
    }

    public UserToken refreshToken(String refreshToken) {
        return UserToken.builder().accessToken(jwtService.refreshToken(refreshToken)).build();
    }
}
