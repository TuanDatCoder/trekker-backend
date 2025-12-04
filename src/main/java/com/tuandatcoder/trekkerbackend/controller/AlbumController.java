package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.album.request.CreateAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.album.request.UpdateAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.album.response.AlbumResponseDTO;
import com.tuandatcoder.trekkerbackend.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired private AlbumService albumService;

    @PostMapping
    public ResponseEntity<ApiResponse<AlbumResponseDTO>> createAlbum(@Valid @RequestBody CreateAlbumRequestDTO dto) {
        ApiResponse<AlbumResponseDTO> response = albumService.createAlbum(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AlbumResponseDTO>>> getMyAlbums() {
        return ResponseEntity.ok(albumService.getMyAlbums());
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse<List<AlbumResponseDTO>>> getAlbumsByTripId(@PathVariable Long tripId) {
        return ResponseEntity.ok(albumService.getAlbumsByTripId(tripId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlbumResponseDTO>> getAlbumById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AlbumResponseDTO>> updateAlbum(
            @PathVariable Long id, @Valid @RequestBody UpdateAlbumRequestDTO dto) {
        return ResponseEntity.ok(albumService.updateAlbum(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAlbum(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.deleteAlbum(id));
    }
}