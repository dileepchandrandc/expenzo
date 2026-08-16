package com.expenzo.services.paymentcard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.common.PaginatedResponse;
import com.expenzo.services.paymentcard.dto.CreditCardResponse;
import com.expenzo.services.paymentcard.request.CreateCreditCardRequest;
import com.expenzo.services.paymentcard.request.UpdateCreditCardRequest;
import com.expenzo.services.paymentcard.service.CreditCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/credit-card")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardResponse create(@Valid @RequestBody CreateCreditCardRequest request) {
        return creditCardService.create(request);
    }

    @PutMapping("/{id}")
    public CreditCardResponse update(@PathVariable String id, @Valid @RequestBody UpdateCreditCardRequest request) {
        return creditCardService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        creditCardService.delete(id);
    }

    @GetMapping("/{id}")
    public CreditCardResponse get(@PathVariable String id) {
        return creditCardService.get(id);
    }

    @GetMapping("/list")
    public PaginatedResponse<CreditCardResponse> list(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return creditCardService.list(page, size);
    }
}
