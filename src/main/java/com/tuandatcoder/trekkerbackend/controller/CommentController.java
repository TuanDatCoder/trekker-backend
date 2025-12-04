package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.comment.request.CreateCommentRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.comment.request.UpdateCommentRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.comment.response.CommentResponseDTO;
import com.tuandatcoder.trekkerbackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired private CommentService commentService;

    // Tạo comment/reply cho Photo, Trip, Post...
    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @Valid @RequestBody CreateCommentRequestDTO dto) {

        return ResponseEntity.status(201).body(
                commentService.createComment(targetType, targetId, dto)
        );
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getComments(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(commentService.getComments(targetType, targetId));
    }

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getReplies(
            @PathVariable Long parentCommentId) {
        return ResponseEntity.ok(commentService.getReplies(parentCommentId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, dto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}