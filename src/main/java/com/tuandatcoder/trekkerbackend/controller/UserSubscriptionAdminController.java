package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.request.UpdateUserSubscriptionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.response.UserSubscriptionResponseDTO;
import com.tuandatcoder.trekkerbackend.service.UserSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user-subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionAdminController {

    private final UserSubscriptionService subscriptionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSubscriptionResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserSubscriptionRequestDTO dto) {

        ApiResponse<UserSubscriptionResponseDTO> response = subscriptionService.updateSubscription(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        ApiResponse<String> response = subscriptionService.deleteSubscription(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
