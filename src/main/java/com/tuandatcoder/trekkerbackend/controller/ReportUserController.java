package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.report.request.CreateReportRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.report.response.ReportResponseDTO;
import com.tuandatcoder.trekkerbackend.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportUserController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponseDTO>> create(
            @Valid @RequestBody CreateReportRequestDTO dto) {
        ApiResponse<ReportResponseDTO> response = reportService.createReport(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
