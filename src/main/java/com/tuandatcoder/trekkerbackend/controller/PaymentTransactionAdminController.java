package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.response.PaymentTransactionResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payment-transactions")
@PreAuthorize("hasRole('ADMIN')")
public class PaymentTransactionAdminController {

    @Autowired
    private PaymentTransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentTransactionResponseDTO>>> getAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(id));
    }
}