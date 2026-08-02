package com.expenzo.services.bankaccount.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.service.BankService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    @GetMapping("/list")
    public List<BankResponse> list() {
        return bankService.list();
    }
}
