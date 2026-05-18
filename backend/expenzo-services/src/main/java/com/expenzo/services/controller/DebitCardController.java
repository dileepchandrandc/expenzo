package com.expenzo.services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.DebitCardDto;
import com.expenzo.services.service.DebitCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/debit-card")
@RequiredArgsConstructor
public class DebitCardController {

    private final DebitCardService debitCardService;

    @GetMapping("/list")
    public List<DebitCardDto> list(@RequestHeader("user-id") Integer userId) {
        return debitCardService.list(userId);
    }
}
