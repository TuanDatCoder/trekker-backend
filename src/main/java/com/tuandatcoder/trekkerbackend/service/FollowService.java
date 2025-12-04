package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.follow.response.FollowResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.follow.response.FollowerDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Follow;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import com.tuandatcoder.trekkerbackend.repository.FollowRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired private FollowRepository followRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // FOLLOW
    @Transactional
    public ApiResponse<FollowResponseDTO> follow(Long followingId) {
        Account current = getCurrentAccount();

        if (current.getId().equals(followingId)) {
            throw new ApiException("You cannot follow yourself", ErrorCode.FORBIDDEN);
        }

        Account following = accountRepository.findById(followingId)
                .orElseThrow(() -> new ApiException("User not found", ErrorCode.ACCOUNT_NOT_FOUND));

        // Kiểm tra đã follow chưa
        boolean alreadyFollowing = followRepository
                .findActiveByFollowerAndFollowing(current.getId(), followingId)
                .isPresent();

        if (alreadyFollowing) {
            throw new ApiException("You are already following this user", ErrorCode.USER_EXISTED);
        }

        Follow follow = Follow.builder()
                .follower(current)
                .following(following)
                .build();

        followRepository.save(follow);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.FOLLOW_USER,
                "Followed user @" + following.getUsername(),
                current
        );

        return buildFollowResponse(following, true);
    }

    // UNFOLLOW
    @Transactional
    public ApiResponse<FollowResponseDTO> unfollow(Long followingId) {
        Account current = getCurrentAccount();

        Follow follow = followRepository
                .findActiveByFollowerAndFollowing(current.getId(), followingId)
                .orElseThrow(() -> new ApiException("You are not following this user", ErrorCode.NOT_FOUND));

        follow.setDeletedAt(LocalDateTime.now());
        followRepository.save(follow);

        Account following = follow.getFollowing();

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.UNFOLLOW_USER,
                "Unfollowed user @" + following.getUsername(),
                current
        );

        return buildFollowResponse(following, false);
    }

    // CHECK ĐANG FOLLOW HAY CHƯA
    public ApiResponse<Boolean> isFollowing(Long followingId) {
        Account current = getCurrentAccount();
        boolean isFollowing = followRepository
                .findActiveByFollowerAndFollowing(current.getId(), followingId)
                .isPresent();

        return new ApiResponse<>(200, "Check follow status", isFollowing);
    }

    // LẤY DANH SÁCH NGƯỜI ĐANG FOLLOW USER (FOLLOWERS)
    public ApiResponse<List<FollowerDTO>> getFollowers(Long userId) {
        List<Follow> follows = followRepository.findFollowersByUserId(userId);

        List<FollowerDTO> dtos = follows.stream()
                .map(f -> new FollowerDTO(
                        f.getFollower().getId(),
                        f.getFollower().getUsername(),
                        f.getFollower().getName(),
                        f.getFollower().getPicture(),
                        f.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Followers retrieved", dtos);
    }

    // LẤY DANH SÁCH NGƯỜI USER ĐANG FOLLOW (FOLLOWING)
    public ApiResponse<List<FollowerDTO>> getFollowing(Long userId) {
        List<Follow> follows = followRepository.findFollowingByUserId(userId);

        List<FollowerDTO> dtos = follows.stream()
                .map(f -> new FollowerDTO(
                        f.getFollowing().getId(),
                        f.getFollowing().getUsername(),
                        f.getFollowing().getName(),
                        f.getFollowing().getPicture(),
                        f.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Following retrieved", dtos);
    }

    // Helper: tạo response giống nhau cho follow/unfollow
    private ApiResponse<FollowResponseDTO> buildFollowResponse(Account following, boolean isFollowing) {
        int followersCount = (int) followRepository.countByFollowingIdAndDeletedAtIsNull(following.getId());
        int followingCount = (int) followRepository.countByFollowerIdAndDeletedAtIsNull(following.getId());

        FollowResponseDTO dto = new FollowResponseDTO(
                following.getId(),
                following.getUsername(),
                following.getName(),
                following.getPicture(),
                isFollowing,
                followersCount,
                followingCount
        );

        String message = isFollowing ? "Followed successfully" : "Unfollowed successfully";
        return new ApiResponse<>(200, message, dto);
    }
}