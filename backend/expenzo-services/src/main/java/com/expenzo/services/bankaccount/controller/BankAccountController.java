package com.expenzo.services.bankaccount.controller;

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

import com.expenzo.services.bankaccount.dto.BankAccountResponse;
import com.expenzo.services.bankaccount.request.CreateBankAccountRequest;
import com.expenzo.services.bankaccount.request.UpdateBankAccountRequest;
import com.expenzo.services.bankaccount.service.BankAccountService;
import com.expenzo.services.common.PaginatedResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bank-account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponse create(@Valid @RequestBody CreateBankAccountRequest request) {
        return bankAccountService.create(request);
    }

    @PutMapping("/{id}")
    public BankAccountResponse update(@PathVariable String id, @Valid @RequestBody UpdateBankAccountRequest request) {
        return bankAccountService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        bankAccountService.delete(id);
    }

    @GetMapping("/{id}")
    public BankAccountResponse get(@PathVariable String id) {
        return bankAccountService.get(id);
    }

    @GetMapping("/list")
    public PaginatedResponse<BankAccountResponse> list(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return bankAccountService.list(page, size);
    }
}
