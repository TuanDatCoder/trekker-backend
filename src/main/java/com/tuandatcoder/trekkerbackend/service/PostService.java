package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.post.request.CreatePostRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.post.request.UpdatePostRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.post.response.PostResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PostMapper;
import com.tuandatcoder.trekkerbackend.repository.PhotoRepository;
import com.tuandatcoder.trekkerbackend.repository.PostRepository;
import com.tuandatcoder.trekkerbackend.repository.TripRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired private PostRepository postRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PostMapper postMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    @Transactional
    public ApiResponse<PostResponseDTO> createPost(CreatePostRequestDTO dto) {
        Account current = getCurrentAccount();

        Post.PostBuilder builder = Post.builder()
                .account(current)
                .title(dto.getTitle())
                .content(dto.getContent())
                .privacy(dto.getPrivacy())
                .publishedAt(LocalDateTime.now());

        if (dto.getTripId() != null) {
            Trip trip = tripRepository.findActiveById(dto.getTripId())
                    .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));
            if (!trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only post on your own trip", ErrorCode.TRIP_FORBIDDEN);
            }
            builder.trip(trip);
        }

        if (dto.getCoverPhotoId() != null) {
            Photo photo = photoRepository.findActiveById(dto.getCoverPhotoId())
                    .orElseThrow(() -> new ApiException("Photo not found", ErrorCode.PHOTO_NOT_FOUND));
            if (!photo.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only use your own photo as cover", ErrorCode.PHOTO_FORBIDDEN);
            }
            builder.coverPhoto(photo);
        }

        Post post = postRepository.save(builder.build());

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_POST,
                "Created new post: " + (post.getTitle() != null ? post.getTitle() : "No title"),
                current
        );

        return new ApiResponse<>(201, "Post created successfully", postMapper.toDto(post));
    }

    public ApiResponse<List<PostResponseDTO>> getMyPosts() {
        Account current = getCurrentAccount();
        List<Post> posts = postRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Your posts retrieved successfully", postMapper.toDtoList(posts));
    }

    public ApiResponse<List<PostResponseDTO>> getPublicPosts() {
        List<Post> posts = postRepository.findAllPublic();
        return new ApiResponse<>(200, "Public posts retrieved successfully", postMapper.toDtoList(posts));
    }

    public ApiResponse<PostResponseDTO> getPostById(Long id) {
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Post not found", ErrorCode.NOT_FOUND));

        Account current = accountUtils.getCurrentAccount();

        // Kiểm tra quyền xem
        if (post.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.PostPrivacyEnum.PRIVATE) {
            if (current == null || !post.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You don't have permission to view this post", ErrorCode.FORBIDDEN);
            }
        }

        // Tăng view count (trừ khi là chủ post)
        if (current == null || !post.getAccount().getId().equals(current.getId())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }

        return new ApiResponse<>(200, "Post retrieved successfully", postMapper.toDto(post));
    }

    @Transactional
    public ApiResponse<PostResponseDTO> updatePost(Long id, UpdatePostRequestDTO dto) {
        Account current = getCurrentAccount();
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Post not found", ErrorCode.NOT_FOUND));

        if (!post.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only edit your own post", ErrorCode.FORBIDDEN);
        }

        postMapper.updateEntityFromDto(dto, post);

        if (dto.getCoverPhotoId() != null) {
            Photo photo = photoRepository.findActiveById(dto.getCoverPhotoId())
                    .orElseThrow(() -> new ApiException("Photo not found", ErrorCode.PHOTO_NOT_FOUND));
            if (!photo.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only use your own photo", ErrorCode.PHOTO_FORBIDDEN);
            }
            post.setCoverPhoto(photo);
        }

        post = postRepository.save(post);

        return new ApiResponse<>(200, "Post updated successfully", postMapper.toDto(post));
    }

    @Transactional
    public ApiResponse<String> deletePost(Long id) {
        Account current = getCurrentAccount();
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Post not found", ErrorCode.NOT_FOUND));

        if (!post.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own post", ErrorCode.FORBIDDEN);
        }

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_POST,
                "Deleted post ID: " + id,
                current
        );

        return new ApiResponse<>(200, "Post deleted successfully", null);
    }
}