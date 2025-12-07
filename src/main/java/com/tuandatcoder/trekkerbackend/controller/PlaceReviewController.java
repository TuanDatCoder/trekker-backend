package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.placereview.request.CreatePlaceReviewRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placereview.request.UpdatePlaceReviewRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placereview.response.PlaceReviewResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PlaceReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places/{placeId}/reviews")
public class PlaceReviewController {

    @Autowired private PlaceReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaceReviewResponseDTO>> create(
            @PathVariable Long placeId,
            @Valid @RequestBody CreatePlaceReviewRequestDTO dto) {
        ApiResponse<PlaceReviewResponseDTO> response = reviewService.createReview(placeId, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceReviewResponseDTO>>> getAll(@PathVariable Long placeId) {
        ApiResponse<List<PlaceReviewResponseDTO>> response = reviewService.getReviewsByPlaceId(placeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<PlaceReviewResponseDTO>> update(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdatePlaceReviewRequestDTO dto) {
        ApiResponse<PlaceReviewResponseDTO> response = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long reviewId) {
        ApiResponse<String> response = reviewService.deleteReview(reviewId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}