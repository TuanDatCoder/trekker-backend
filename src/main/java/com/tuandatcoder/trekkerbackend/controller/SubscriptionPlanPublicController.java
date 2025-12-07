package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response.SubscriptionPlanResponseDTO;
import com.tuandatcoder.trekkerbackend.service.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanPublicController {

    @Autowired
    private SubscriptionPlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>> getActivePlans() {
        return ResponseEntity.ok(planService.getActivePlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }
}