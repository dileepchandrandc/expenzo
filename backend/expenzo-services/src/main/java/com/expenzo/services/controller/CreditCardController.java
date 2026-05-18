package com.expenzo.services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.CreditCardDto;
import com.expenzo.services.service.CreditCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/credit-card")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping("/list")
    public List<CreditCardDto> list(@RequestHeader("user-id") Integer userId) {
        return creditCardService.list(userId);
    }
}
