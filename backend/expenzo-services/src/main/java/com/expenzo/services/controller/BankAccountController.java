package com.expenzo.services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.service.BankAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("/list")
    public List<BankAccountDto> list(@RequestHeader("user-id") Integer userId) {
        return bankAccountService.list(userId);
    }
}
