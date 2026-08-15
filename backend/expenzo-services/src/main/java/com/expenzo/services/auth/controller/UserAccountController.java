package com.expenzo.services.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PutMapping
    public UserAccount update(@Valid @RequestBody UpdateUserRequest request) {
        return userAccountService.update(request);
    }

    @PutMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userAccountService.changePassword(request);
    }

    @GetMapping
    public UserAccount get() {
        return userAccountService.get();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        userAccountService.delete();
    }
}
