package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.report.request.UpdateReportStatusRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.report.response.ReportResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.CreateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.UpdateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response.SubscriptionPlanResponseDTO;
import com.tuandatcoder.trekkerbackend.service.AccountService;
import com.tuandatcoder.trekkerbackend.service.ReportService;
import com.tuandatcoder.trekkerbackend.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private AccountService accountService;
    @Autowired private SubscriptionPlanService planService;
    @Autowired private ReportService reportService;

    // ====================== ACCOUNT MANAGEMENT ======================
    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAllAccounts() {
        ApiResponse<List<AccountResponseDTO>> response = accountService.getAllAccounts();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ====================== SUBSCRIPTION PLAN MANAGEMENT ======================
    @PostMapping("/subscription-plans")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanRequestDTO dto) {
        ApiResponse<SubscriptionPlanResponseDTO> response = planService.createPlan(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/subscription-plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PutMapping("/subscription-plans/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubscriptionPlanRequestDTO dto) {
        ApiResponse<SubscriptionPlanResponseDTO> response = planService.updatePlan(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/subscription-plans/{id}")
    public ResponseEntity<ApiResponse<String>> deletePlan(@PathVariable Long id) {
        ApiResponse<String> response = planService.deletePlan(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ====================== REPORT MANAGEMENT ======================
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<ReportResponseDTO>>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<ApiResponse<List<ReportResponseDTO>>> getPendingReports() {
        return ResponseEntity.ok(reportService.getPendingReports());
    }

    @PutMapping("/reports/{id}/status")
    public ResponseEntity<ApiResponse<ReportResponseDTO>> updateReportStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportStatusRequestDTO dto) {
        ApiResponse<ReportResponseDTO> response = reportService.updateReportStatus(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReport(@PathVariable Long id) {
        ApiResponse<String> response = reportService.deleteReport(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}