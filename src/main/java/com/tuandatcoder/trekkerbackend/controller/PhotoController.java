package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.photo.request.UploadPhotoRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.photo.response.PhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<PhotoResponseDTO>> upload(@ModelAttribute UploadPhotoRequestDTO dto) {
        return ResponseEntity.status(201).body(photoService.uploadPhoto(dto));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PhotoResponseDTO>>> getMyPhotos() {
        return ResponseEntity.ok(photoService.getMyPhotos());
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse<List<PhotoResponseDTO>>> getTripPhotos(@PathVariable Long tripId) {
        return ResponseEntity.ok(photoService.getTripPhotos(tripId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePhoto(@PathVariable Long id) {
        return ResponseEntity.ok(photoService.deletePhoto(id));
    }
}