package com.expenzo.coreservices.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.coreservices.dto.BankDto;
import com.expenzo.coreservices.service.BankService;

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
