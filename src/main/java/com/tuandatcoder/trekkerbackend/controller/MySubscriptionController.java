package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.request.CreateUserSubscriptionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.response.UserSubscriptionResponseDTO;
import com.tuandatcoder.trekkerbackend.service.UserSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/subscription")
public class MySubscriptionController {

    @Autowired
    private UserSubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserSubscriptionResponseDTO>> subscribe(
            @Valid @RequestBody CreateUserSubscriptionRequestDTO dto) {
        ApiResponse<UserSubscriptionResponseDTO> response = subscriptionService.subscribe(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<UserSubscriptionResponseDTO>> getActive() {
        return ResponseEntity.ok(subscriptionService.getMyActiveSubscription());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<UserSubscriptionResponseDTO>>> getHistory() {
        return ResponseEntity.ok(subscriptionService.getMySubscriptionHistory());
    }
}
