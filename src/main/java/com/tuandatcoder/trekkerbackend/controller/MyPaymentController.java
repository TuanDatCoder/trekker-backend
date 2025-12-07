package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.request.CreatePaymentTransactionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.response.PaymentTransactionResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PaymentTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/payments")
public class MyPaymentController {

    @Autowired
    private PaymentTransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentTransactionResponseDTO>> create(
            @Valid @RequestBody CreatePaymentTransactionRequestDTO dto) {
        ApiResponse<PaymentTransactionResponseDTO> response = transactionService.createTransaction(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentTransactionResponseDTO>>> getMyHistory() {
        return ResponseEntity.ok(transactionService.getMyTransactions());
    }
}