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
import com.expenzo.services.paymentcard.dto.DebitCardResponse;
import com.expenzo.services.paymentcard.request.CreateDebitCardRequest;
import com.expenzo.services.paymentcard.request.UpdateDebitCardRequest;
import com.expenzo.services.paymentcard.service.DebitCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/debit-card")
public class DebitCardController {

    private final DebitCardService debitCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardResponse create(@Valid @RequestBody CreateDebitCardRequest request) {
        return debitCardService.create(request);
    }

    @PutMapping("/{id}")
    public DebitCardResponse update(@PathVariable String id, @Valid @RequestBody UpdateDebitCardRequest request) {
        return debitCardService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        debitCardService.delete(id);
    }

    @GetMapping("/{id}")
    public DebitCardResponse get(@PathVariable String id) {
        return debitCardService.get(id);
    }

    @GetMapping("/list")
    public PaginatedResponse<DebitCardResponse> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return debitCardService.list(page, size);
    }
}
