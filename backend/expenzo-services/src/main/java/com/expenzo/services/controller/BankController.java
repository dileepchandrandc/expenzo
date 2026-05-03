package com.expenzo.services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.service.BankService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping("/list")
    public List<BankDto> listAll() {
        return bankService.listAll();
    }
}
