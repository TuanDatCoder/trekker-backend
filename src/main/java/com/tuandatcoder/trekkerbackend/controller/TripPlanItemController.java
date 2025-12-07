package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.request.CreateTripPlanItemRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.request.UpdateTripPlanItemRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.response.TripPlanItemResponseDTO;
import com.tuandatcoder.trekkerbackend.service.TripPlanItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/plan-items")
public class TripPlanItemController {

    @Autowired
    private TripPlanItemService planItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<TripPlanItemResponseDTO>> create(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateTripPlanItemRequestDTO dto) {
        dto.setTripId(tripId);
        ApiResponse<TripPlanItemResponseDTO> response = planItemService.createPlanItem(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripPlanItemResponseDTO>>> getByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(planItemService.getPlanItemsByTrip(tripId));
    }

    @GetMapping("/day/{tripDayId}")
    public ResponseEntity<ApiResponse<List<TripPlanItemResponseDTO>>> getByDay(@PathVariable Long tripDayId) {
        return ResponseEntity.ok(planItemService.getPlanItemsByDay(tripDayId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripPlanItemResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTripPlanItemRequestDTO dto) {
        ApiResponse<TripPlanItemResponseDTO> response = planItemService.updatePlanItem(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(planItemService.deletePlanItem(id));
    }
}
