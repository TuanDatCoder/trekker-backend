package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.location.request.CreateLocationRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.location.request.UpdateLocationRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.location.response.LocationResponseDTO;
import com.tuandatcoder.trekkerbackend.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired private LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponseDTO>> create(
            @Valid @RequestBody CreateLocationRequestDTO dto) {
        ApiResponse<LocationResponseDTO> response = locationService.createLocation(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponseDTO>>> getAll() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateLocationRequestDTO dto) {
        ApiResponse<LocationResponseDTO> response = locationService.updateLocation(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.deleteLocation(id));
    }
}