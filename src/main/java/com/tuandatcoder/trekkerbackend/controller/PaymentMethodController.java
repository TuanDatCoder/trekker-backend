package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.request.CreatePaymentMethodRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.request.UpdatePaymentMethodRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.response.PaymentMethodResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentMethodResponseDTO>> create(
            @Valid @RequestBody CreatePaymentMethodRequestDTO dto) {
        ApiResponse<PaymentMethodResponseDTO> response = paymentMethodService.createPaymentMethod(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethodResponseDTO>>> getAll() {
        return ResponseEntity.ok(paymentMethodService.getMyPaymentMethods());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethodResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentMethodRequestDTO dto) {
        ApiResponse<PaymentMethodResponseDTO> response = paymentMethodService.updatePaymentMethod(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.deletePaymentMethod(id));
    }
}
