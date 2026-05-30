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

import com.expenzo.services.dto.payment.AddCreditCardRequest;
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

    @PostMapping
    public ResponseEntity<CreditCardDto> add(@RequestHeader("user-id") Integer userId, @RequestBody AddCreditCardRequest request) {
        CreditCardDto dto = creditCardService.add(userId, request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardDto> update(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id, @RequestBody AddCreditCardRequest request) {
        CreditCardDto dto = creditCardService.update(userId, id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id) {
        creditCardService.delete(userId, id);
        return ResponseEntity.ok().build();
    }
}
