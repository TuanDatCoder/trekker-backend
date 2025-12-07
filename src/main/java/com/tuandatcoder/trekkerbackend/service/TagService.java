package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tag.request.CreateTagRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tag.response.TagResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.TagMapper;
import com.tuandatcoder.trekkerbackend.repository.AccountRepository;
import com.tuandatcoder.trekkerbackend.repository.PhotoRepository;
import com.tuandatcoder.trekkerbackend.repository.PostRepository;
import com.tuandatcoder.trekkerbackend.repository.TagRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagService {

    @Autowired private TagRepository tagRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private TagMapper tagMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // Kiểm tra quyền gắn thẻ (chỉ owner của Photo/Post mới được tag)
    private void checkOwnership(String targetType, Long targetId) {
        Account current = getCurrentAccount();
        boolean isOwner = false;

        if ("PHOTO".equalsIgnoreCase(targetType)) {
            Photo photo = photoRepository.findActiveById(targetId)
                    .orElseThrow(() -> new ApiException("Photo not found", ErrorCode.PHOTO_NOT_FOUND));
            isOwner = photo.getAccount().getId().equals(current.getId());
        } else if ("POST".equalsIgnoreCase(targetType)) {
            Post post = postRepository.findActiveById(targetId)
                    .orElseThrow(() -> new ApiException("Post not found", ErrorCode.NOT_FOUND));
            isOwner = post.getAccount().getId().equals(current.getId());
        }

        if (!isOwner) {
            throw new ApiException("You can only tag people in your own content", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<TagResponseDTO> createTag(CreateTagRequestDTO dto) {
        Account current = getCurrentAccount();

        // Kiểm tra target có tồn tại và có quyền
        checkOwnership(dto.getTargetType(), dto.getTargetId());

        // Kiểm tra người được tag có tồn tại
        Account taggedUser = accountRepository.findById(dto.getTaggedUserId())
                .orElseThrow(() -> new ApiException("Tagged user not found", ErrorCode.ACCOUNT_NOT_FOUND));

        // Không cho tag chính mình (tùy chọn)
        if (taggedUser.getId().equals(current.getId())) {
            throw new ApiException("You cannot tag yourself", ErrorCode.FORBIDDEN);
        }

        // Kiểm tra đã tag chưa
        if (tagRepository.existsByTargetTypeAndTargetIdAndAccountIdAndDeletedAtIsNull(
                dto.getTargetType().toUpperCase(), dto.getTargetId(), dto.getTaggedUserId())) {
            throw new ApiException("This user is already tagged", ErrorCode.USER_EXISTED);
        }

        Tag tag = Tag.builder()
                .targetType(dto.getTargetType().toUpperCase())
                .targetId(dto.getTargetId())
                .account(taggedUser)
                .taggedBy(current)
                .xPosition(dto.getXPosition())
                .yPosition(dto.getYPosition())
                .build();

        tag = tagRepository.save(tag);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.TAG_USER,
                "Tagged @" + taggedUser.getUsername() + " in " + dto.getTargetType() + " ID " + dto.getTargetId(),
                current
        );

        return new ApiResponse<>(201, "User tagged successfully", tagMapper.toDto(tag));
    }

    public ApiResponse<List<TagResponseDTO>> getTags(String targetType, Long targetId) {
        List<Tag> tags = tagRepository.findByTarget(targetType.toUpperCase(), targetId);
        return new ApiResponse<>(200, "Tags retrieved successfully", tagMapper.toDtoList(tags));
    }

    @Transactional
    public ApiResponse<String> deleteTag(Long tagId) {
        Account current = getCurrentAccount();

        Tag tag = tagRepository.findActiveById(tagId)
                .orElseThrow(() -> new ApiException("Tag not found", ErrorCode.NOT_FOUND));

        // Chỉ owner hoặc người được tag mới được xóa
        boolean canDelete = tag.getTaggedBy().getId().equals(current.getId()) ||
                tag.getAccount().getId().equals(current.getId());

        if (!canDelete && current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("You can only remove your own tag", ErrorCode.FORBIDDEN);
        }

        tag.setDeletedAt(LocalDateTime.now());
        tagRepository.save(tag);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REMOVE_TAG,
                "Removed tag from " + tag.getTargetType() + " ID " + tag.getTargetId(),
                current
        );

        return new ApiResponse<>(200, "Tag removed successfully", null);
    }
}