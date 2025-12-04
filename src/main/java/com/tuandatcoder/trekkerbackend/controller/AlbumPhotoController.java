package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.request.AddPhotosToAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.request.ReorderAlbumPhotosRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.response.AlbumPhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.service.AlbumPhotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums/{albumId}/photos")
public class AlbumPhotoController {

    @Autowired private AlbumPhotoService albumPhotoService;

    // Lấy danh sách ảnh
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlbumPhotoResponseDTO>>> getPhotos(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumPhotoService.getPhotosInAlbum(albumId));
    }

    // Thêm ảnh
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addPhotos(
            @PathVariable Long albumId,
            @Valid @RequestBody AddPhotosToAlbumRequestDTO dto) {
        return ResponseEntity.ok(albumPhotoService.addPhotosToAlbum(albumId, dto));
    }

    // Xóa ảnh khỏi album
    @DeleteMapping("/{albumPhotoId}")
    public ResponseEntity<ApiResponse<String>> removePhoto(
            @PathVariable Long albumId,
            @PathVariable Long albumPhotoId) {
        return ResponseEntity.ok(albumPhotoService.removePhotoFromAlbum(albumId, albumPhotoId));
    }

    // Sắp xếp lại
    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse<String>> reorderPhotos(
            @PathVariable Long albumId,
            @Valid @RequestBody ReorderAlbumPhotosRequestDTO dto) {
        return ResponseEntity.ok(albumPhotoService.reorderPhotos(albumId, dto));
    }
}