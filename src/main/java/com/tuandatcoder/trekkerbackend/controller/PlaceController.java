package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.place.request.CreatePlaceRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.place.request.UpdatePlaceRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.place.response.PlaceResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaceResponseDTO>> create(@Valid @RequestBody CreatePlaceRequestDTO dto) {
        ApiResponse<PlaceResponseDTO> response = placeService.createPlace(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceResponseDTO>>> getAll() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<PlaceResponseDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(placeService.getPlaceBySlug(slug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdatePlaceRequestDTO dto) {
        ApiResponse<PlaceResponseDTO> response = placeService.updatePlace(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.deletePlace(id));
    }
}