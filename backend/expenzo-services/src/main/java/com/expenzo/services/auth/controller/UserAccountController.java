package com.expenzo.services.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.auth.dto.UserAccount;
import com.expenzo.services.auth.request.ChangePasswordRequest;
import com.expenzo.services.auth.request.CreateUserRequest;
import com.expenzo.services.auth.request.UpdateUserRequest;
import com.expenzo.services.auth.service.UserAccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount create(@Valid @RequestBody CreateUserRequest request) {
        return userAccountService.create(request);
    }

    @PutMapping("/{userId}")
    public UserAccount update(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest request) {
        return userAccountService.update(userId, request);
    }

    @PutMapping("/{userId}/change-password")
    public void changePassword(@PathVariable String userId, @Valid @RequestBody ChangePasswordRequest request) {
        userAccountService.changePassword(userId, request);
    }

    @GetMapping("/{userId}")
    public UserAccount get(@PathVariable String userId) {
        return userAccountService.get(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String userId) {
        userAccountService.delete(userId);
    }
}
