package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.post.request.CreatePostRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.post.request.UpdatePostRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.post.response.PostResponseDTO;
import com.tuandatcoder.trekkerbackend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired private PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDTO>> create(@Valid @RequestBody CreatePostRequestDTO dto) {
        ApiResponse<PostResponseDTO> response = postService.createPost(dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getMyPosts() {
        return ResponseEntity.ok(postService.getMyPosts());
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getPublicPosts() {
        return ResponseEntity.ok(postService.getPublicPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdatePostRequestDTO dto) {
        ApiResponse<PostResponseDTO> response = postService.updatePost(id, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }
}