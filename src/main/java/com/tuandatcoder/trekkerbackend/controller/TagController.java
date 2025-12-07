package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tag.request.CreateTagRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tag.response.TagResponseDTO;
import com.tuandatcoder.trekkerbackend.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired private TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponseDTO>> create(
            @Valid @RequestBody CreateTagRequestDTO dto) {
        ApiResponse<TagResponseDTO> response = tagService.createTag(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<List<TagResponseDTO>>> getTags(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(tagService.getTags(targetType, targetId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}