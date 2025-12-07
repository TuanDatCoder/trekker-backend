package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tripday.request.CreateTripDayRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripday.request.UpdateTripDayRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripday.response.TripDayResponseDTO;
import com.tuandatcoder.trekkerbackend.service.TripDayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/days")
public class TripDayController {

    @Autowired private TripDayService tripDayService;

    @PostMapping
    public ResponseEntity<ApiResponse<TripDayResponseDTO>> create(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateTripDayRequestDTO dto) {
        dto.setTripId(tripId);
        ApiResponse<TripDayResponseDTO> response = tripDayService.createTripDay(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripDayResponseDTO>>> getAll(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripDayService.getTripDays(tripId));
    }

    @GetMapping("/day/{dayId}")
    public ResponseEntity<ApiResponse<TripDayResponseDTO>> getById(@PathVariable Long dayId) {
        return ResponseEntity.ok(tripDayService.getTripDayById(dayId));
    }

    @PutMapping("/day/{dayId}")
    public ResponseEntity<ApiResponse<TripDayResponseDTO>> update(
            @PathVariable Long dayId,
            @Valid @RequestBody UpdateTripDayRequestDTO dto) {
        ApiResponse<TripDayResponseDTO> response = tripDayService.updateTripDay(dayId, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/day/{dayId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long dayId) {
        return ResponseEntity.ok(tripDayService.deleteTripDay(dayId));
    }
}