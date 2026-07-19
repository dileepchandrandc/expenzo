package com.expenzo.services.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expenzo.services.auth.dto.UserAccount;
import com.expenzo.services.auth.repository.ExpenzoUserRepository;
import com.expenzo.services.auth.repository.model.ExpenzoUser;
import com.expenzo.services.auth.request.ChangePasswordRequest;
import com.expenzo.services.auth.request.CreateUserRequest;
import com.expenzo.services.auth.request.UpdateUserRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final ExpenzoUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccount create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException(
                "User with email " + request.getEmail() + " already exists"
            );
        }

        ExpenzoUser user = ExpenzoUser.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .countryCode(request.getCountryCode())
            .mobileNumber(request.getMobileNumber())
            .build();

        user = userRepository.save(user);
        return toUserAccount(user);
    }

    public UserAccount update(String userId, UpdateUserRequest request) {
        ExpenzoUser user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getCountryCode() != null) {
            user.setCountryCode(request.getCountryCode());
        }
        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }

        user = userRepository.save(user);
        return toUserAccount(user);
    }

    public UserAccount get(String userId) {
        ExpenzoUser user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return toUserAccount(user);
    }

    public void delete(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        ExpenzoUser user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserAccount toUserAccount(ExpenzoUser user) {
        UserAccount dto = new UserAccount();
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCountryCode(user.getCountryCode());
        dto.setMobile(user.getMobileNumber());
        return dto;
    }
}
