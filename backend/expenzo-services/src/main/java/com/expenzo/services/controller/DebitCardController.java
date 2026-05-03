package com.expenzo.services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.DebitCardDto;
import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.service.DebitCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/debit-card")
@RequiredArgsConstructor
public class DebitCardController {

    private final DebitCardService debitCardService;

    @GetMapping("/list")
    public PaginatedResponse<DebitCardDto> list(@RequestHeader("user-id") Integer userId, @RequestParam("page") int page) {
        return debitCardService.list(userId, page);
    }
}
