package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.checkin.request.CreateCheckInRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.checkin.request.UpdateCheckInRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.checkin.response.CheckInResponseDTO;
import com.tuandatcoder.trekkerbackend.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired private CheckInService checkInService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckInResponseDTO>> create(
            @Valid @RequestBody CreateCheckInRequestDTO dto) {
        ApiResponse<CheckInResponseDTO> response = checkInService.createCheckIn(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CheckInResponseDTO>>> getMyCheckIns() {
        return ResponseEntity.ok(checkInService.getMyCheckIns());
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<ApiResponse<List<CheckInResponseDTO>>> getByPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(checkInService.getCheckInsByPlace(placeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CheckInResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCheckInRequestDTO dto) {
        ApiResponse<CheckInResponseDTO> response = checkInService.updateCheckIn(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(checkInService.deleteCheckIn(id));
    }
}