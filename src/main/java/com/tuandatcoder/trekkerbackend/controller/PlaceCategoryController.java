package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.placecategory.request.CreatePlaceCategoryRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placecategory.request.UpdatePlaceCategoryRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placecategory.response.PlaceCategoryResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PlaceCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place-categories")
public class PlaceCategoryController {

    @Autowired private PlaceCategoryService categoryService;

    // === ADMIN ONLY ===
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PlaceCategoryResponseDTO>> create(
            @Valid @RequestBody CreatePlaceCategoryRequestDTO dto) {
        ApiResponse<PlaceCategoryResponseDTO> response = categoryService.createCategory(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceCategoryResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlaceCategoryRequestDTO dto) {
        ApiResponse<PlaceCategoryResponseDTO> response = categoryService.updateCategory(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        ApiResponse<String> response = categoryService.deleteCategory(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // === PUBLIC ===
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceCategoryResponseDTO>>> getAll() {
        ApiResponse<List<PlaceCategoryResponseDTO>> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceCategoryResponseDTO>> getById(@PathVariable Long id) {
        ApiResponse<PlaceCategoryResponseDTO> response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<PlaceCategoryResponseDTO>> getBySlug(@PathVariable String slug) {
        ApiResponse<PlaceCategoryResponseDTO> response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(response);
    }
}