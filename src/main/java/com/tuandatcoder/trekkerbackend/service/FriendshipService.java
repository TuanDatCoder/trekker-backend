package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.friendship.response.FriendDTO;
import com.tuandatcoder.trekkerbackend.dto.friendship.response.FriendshipResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Friendship;
import com.tuandatcoder.trekkerbackend.enums.FriendshipStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.FriendshipMapper;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import com.tuandatcoder.trekkerbackend.repository.FriendshipRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    @Autowired private FriendshipRepository friendshipRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private FriendshipMapper friendshipMapper  ;


    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // GỬI LỜI MỜI KẾT BẠN
    @Transactional
    public ApiResponse<FriendshipResponseDTO> sendFriendRequest(Long receiverId) {
        Account requester = getCurrentAccount();

        if (requester.getId().equals(receiverId)) {
            throw new ApiException("You cannot send friend request to yourself", ErrorCode.FORBIDDEN);
        }

        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new ApiException("User not found", ErrorCode.ACCOUNT_NOT_FOUND));

        friendshipRepository.findActiveBetween(requester.getId(), receiverId)
                .ifPresent(existing -> {
                    if (existing.getStatus() == FriendshipStatusEnum.PENDING) {
                        throw new ApiException("Friend request already sent", ErrorCode.USER_EXISTED);
                    }
                    if (existing.getStatus() == FriendshipStatusEnum.ACCEPTED) {
                        throw new ApiException("You are already friends", ErrorCode.USER_EXISTED);
                    }
                });

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatusEnum.PENDING)
                .build();

        friendship = friendshipRepository.save(friendship);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.SEND_FRIEND_REQUEST,
                "Sent friend request to @" + receiver.getUsername(),
                requester
        );

        return new ApiResponse<>(201, "Friend request sent", friendshipMapper.toDto(friendship));
    }

    /* ====================== CHẤP NHẬN LỜI MỜI ====================== */
    @Transactional
    public ApiResponse<FriendshipResponseDTO> acceptFriendRequest(Long friendshipId) {
        Account current = getCurrentAccount();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ApiException("Friend request not found", ErrorCode.NOT_FOUND));

        if (!friendship.getReceiver().getId().equals(current.getId())) {
            throw new ApiException("You can only accept requests sent to you", ErrorCode.FORBIDDEN);
        }
        if (friendship.getStatus() != FriendshipStatusEnum.PENDING) {
            throw new ApiException("This request is no longer pending", ErrorCode.FORBIDDEN);
        }

        friendship.setStatus(FriendshipStatusEnum.ACCEPTED);
        friendshipRepository.save(friendship);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.ACCEPT_FRIEND_REQUEST,
                "Accepted friend request from @" + friendship.getRequester().getUsername(),
                current
        );

        return new ApiResponse<>(200, "Friend request accepted", friendshipMapper.toDto(friendship));
    }

    /* ====================== TỪ CHỐI HOẶC HỦY LỜI MỜI ====================== */
    @Transactional
    public ApiResponse<String> declineOrCancelRequest(Long friendshipId) {
        Account current = getCurrentAccount();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ApiException("Friend request not found", ErrorCode.NOT_FOUND));

        boolean isRequester = friendship.getRequester().getId().equals(current.getId());
        boolean isReceiver = friendship.getReceiver().getId().equals(current.getId());

        if (!isRequester && !isReceiver) {
            throw new ApiException("You cannot manage this request", ErrorCode.FORBIDDEN);
        }
        if (friendship.getStatus() != FriendshipStatusEnum.PENDING) {
            throw new ApiException("This request is no longer pending", ErrorCode.FORBIDDEN);
        }

        friendship.setStatus(FriendshipStatusEnum.DECLINED);
        friendship.setDeletedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);

        String action = isRequester ? "canceled" : "declined";
        String targetUsername = isRequester
                ? friendship.getReceiver().getUsername()
                : friendship.getRequester().getUsername();

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DECLINE_FRIEND_REQUEST,
                String.format("%s friend request with @%s", action, targetUsername),
                current
        );

        return new ApiResponse<>(200, "Friend request " + action + " successfully", null);
    }

    /* ====================== HỦY KẾT BẠN ====================== */
    @Transactional
    public ApiResponse<String> unfriend(Long friendId) {
        Account current = getCurrentAccount();

        Friendship friendship = friendshipRepository.findAcceptedBetween(current.getId(), friendId)
                .orElseThrow(() -> new ApiException("You are not friends with this user", ErrorCode.NOT_FOUND));

        friendship.setStatus(FriendshipStatusEnum.DELETED);
        friendship.setDeletedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);

        String friendUsername = current.getId().equals(friendship.getRequester().getId())
                ? friendship.getReceiver().getUsername()
                : friendship.getRequester().getUsername();

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.UNFRIEND,
                "Unfriended @" + friendUsername,
                current
        );

        return new ApiResponse<>(200, "Unfriended successfully", null);
    }

    /* ====================== DANH SÁCH BẠN BÈ ====================== */
    public ApiResponse<List<FriendDTO>> getFriends() {
        Account current = getCurrentAccount();

        List<Friendship> friendships = friendshipRepository.findAcceptedFriends(current.getId());

        List<FriendDTO> friends = friendships.stream()
                .map(f -> {
                    Account friend = f.getRequester().getId().equals(current.getId())
                            ? f.getReceiver()
                            : f.getRequester();

                    LocalDateTime friendSince = f.getUpdatedAt() != null ? f.getUpdatedAt() : f.getCreatedAt();

                    return new FriendDTO(
                            friend.getId(),
                            friend.getUsername(),
                            friend.getName(),
                            friend.getPicture(),
                            friendSince
                    );
                })
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Friends retrieved successfully", friends);
    }

    /* ====================== LỜI MỜI ĐANG CHỜ ====================== */
    public ApiResponse<List<FriendshipResponseDTO>> getPendingRequests() {
        Account current = getCurrentAccount();

        List<Friendship> requests = friendshipRepository.findPendingRequestsToMe(current.getId());

        return new ApiResponse<>(
                200,
                "Pending friend requests retrieved",
                friendshipMapper.toDtoList(requests)
        );
    }
}