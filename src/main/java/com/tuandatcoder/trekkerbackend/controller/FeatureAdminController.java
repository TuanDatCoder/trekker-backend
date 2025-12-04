package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.feature.request.FeatureRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.feature.response.FeatureResponseDTO;
import com.tuandatcoder.trekkerbackend.service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/features")
@PreAuthorize("hasRole('ADMIN')")
public class FeatureAdminController {

    @Autowired private FeatureService featureService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeatureResponseDTO>> create(@Valid @RequestBody FeatureRequestDTO dto) {
        return ResponseEntity.status(201).body(featureService.createFeature(dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeatureResponseDTO>>> getAll() {
        return ResponseEntity.ok(featureService.getAllFeatures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeatureResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(featureService.getFeatureById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeatureResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody FeatureRequestDTO dto) {
        return ResponseEntity.ok(featureService.updateFeature(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(featureService.deleteFeature(id));
    }
}