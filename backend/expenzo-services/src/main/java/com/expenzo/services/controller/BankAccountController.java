package com.expenzo.services.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.payment.AddBankAccountRequest;
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

    @PostMapping
    public ResponseEntity<BankAccountDto> add(@RequestHeader("user-id") Integer userId, @RequestBody AddBankAccountRequest request) {
        BankAccountDto dto = bankAccountService.add(userId, request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountDto> update(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id, @RequestBody AddBankAccountRequest request) {
        BankAccountDto dto = bankAccountService.update(userId, id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id) {
        bankAccountService.delete(userId, id);
        return ResponseEntity.ok().build();
    }
}
