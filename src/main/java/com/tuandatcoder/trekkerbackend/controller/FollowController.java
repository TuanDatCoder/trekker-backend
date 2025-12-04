package com.tuandatcoder.trekkerbackend.controller;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.follow.response.FollowResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.follow.response.FollowerDTO;
import com.tuandatcoder.trekkerbackend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired private FollowService followService;

    @PostMapping("/{followingId}")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> follow(@PathVariable Long followingId) {
        return ResponseEntity.ok(followService.follow(followingId));
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> unfollow(@PathVariable Long followingId) {
        return ResponseEntity.ok(followService.unfollow(followingId));
    }

    @GetMapping("/status/{followingId}")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@PathVariable Long followingId) {
        return ResponseEntity.ok(followService.isFollowing(followingId));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<FollowerDTO>>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<FollowerDTO>>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}