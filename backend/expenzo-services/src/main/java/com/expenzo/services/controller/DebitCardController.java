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

import com.expenzo.services.dto.payment.AddDebitCardRequest;
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

    @PostMapping
    public ResponseEntity<DebitCardDto> add(@RequestHeader("user-id") Integer userId, @RequestBody AddDebitCardRequest request) {
        DebitCardDto dto = debitCardService.add(userId, request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebitCardDto> update(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id, @RequestBody AddDebitCardRequest request) {
        DebitCardDto dto = debitCardService.update(userId, id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer id) {
        debitCardService.delete(userId, id);
        return ResponseEntity.ok().build();
    }
}
