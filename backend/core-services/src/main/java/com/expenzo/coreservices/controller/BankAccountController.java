package com.expenzo.coreservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.coreservices.dto.BankAccountDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.service.BankAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("/list")
    public PaginatedResponse<BankAccountDto> list(@RequestHeader("user-id") Integer userId, @RequestParam("page") int page) {
        return bankAccountService.list(userId, page);
    }
}
