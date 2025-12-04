package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.trip.request.CreateTripRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.trip.request.UpdateTripRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.trip.response.TripResponseDTO;
import com.tuandatcoder.trekkerbackend.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<ApiResponse<TripResponseDTO>> createTrip(@Valid @RequestBody CreateTripRequestDTO dto) {
        ApiResponse<TripResponseDTO> response = tripService.createTrip(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TripResponseDTO>>> getMyTrips() {
        ApiResponse<List<TripResponseDTO>> response = tripService.getMyTrips();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<TripResponseDTO>>> getPublicTrips() {
        ApiResponse<List<TripResponseDTO>> response = tripService.getPublicTrips();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponseDTO>> getTripById(@PathVariable Long id) {
        ApiResponse<TripResponseDTO> response = tripService.getTripById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponseDTO>> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTripRequestDTO dto) {
        ApiResponse<TripResponseDTO> response = tripService.updateTrip(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTrip(@PathVariable Long id) {
        ApiResponse<String> response = tripService.deleteTrip(id);
        return ResponseEntity.ok(response);
    }
}