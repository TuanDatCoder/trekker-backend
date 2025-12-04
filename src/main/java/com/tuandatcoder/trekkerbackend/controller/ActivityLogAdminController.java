package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.activity.request.ActivityLogFilterDTO;
import com.tuandatcoder.trekkerbackend.dto.activity.response.ActivityLogResponseDTO;
import com.tuandatcoder.trekkerbackend.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity-logs")
@PreAuthorize("hasRole('ADMIN')")
public class ActivityLogAdminController {

    @Autowired private ActivityLogService activityLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponseDTO>>> getLogs(
            @RequestBody(required = false) ActivityLogFilterDTO filter) {

        if (filter == null) {
            filter = new ActivityLogFilterDTO();
        }

        ApiResponse<List<ActivityLogResponseDTO>> response = activityLogService.getAllLogs(filter);
        return ResponseEntity.ok(response);
    }
}
