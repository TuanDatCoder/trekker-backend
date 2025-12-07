package com.tuandatcoder.trekkerbackend.controller;


import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.CreateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.UpdateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response.SubscriptionPlanResponseDTO;
import com.tuandatcoder.trekkerbackend.service.AccountService;
import com.tuandatcoder.trekkerbackend.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private AccountService accountService;
    @Autowired private SubscriptionPlanService planService;
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAllAccounts() {
        ApiResponse<List<AccountResponseDTO>> response = accountService.getAllAccounts();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> create(
            @Valid @RequestBody CreateSubscriptionPlanRequestDTO dto) {
        ApiResponse<SubscriptionPlanResponseDTO> response = planService.createPlan(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>> getAll() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSubscriptionPlanRequestDTO dto) {
        ApiResponse<SubscriptionPlanResponseDTO> response = planService.updatePlan(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(planService.deletePlan(id));
    }
}