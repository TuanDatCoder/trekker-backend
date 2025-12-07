package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.placecategory.request.CreatePlaceCategoryRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placecategory.request.UpdatePlaceCategoryRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placecategory.response.PlaceCategoryResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.PlaceCategory;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PlaceCategoryMapper;
import com.tuandatcoder.trekkerbackend.repository.PlaceCategoryRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaceCategoryService {

    @Autowired private PlaceCategoryRepository categoryRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PlaceCategoryMapper categoryMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) {
            throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        }
        return acc;
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage place categories", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<PlaceCategoryResponseDTO> createCategory(CreatePlaceCategoryRequestDTO dto) {
        checkAdmin();

        String slug = generateSlug(dto.getName());
        if (categoryRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug += "-" + System.currentTimeMillis();
        }

        PlaceCategory category = PlaceCategory.builder()
                .name(dto.getName())
                .slug(slug)
                .icon(dto.getIcon())
                .color(dto.getColor())
                .description(dto.getDescription())
                .orderIndex(dto.getOrderIndex())
                .isActive(true)
                .build();

        category = categoryRepository.save(category);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_PLACE_CATEGORY,
                "Created place category: " + category.getName(),
                getCurrentAccount()
        );

        return new ApiResponse<>(201, "Place category created successfully", categoryMapper.toDto(category));
    }

    public ApiResponse<List<PlaceCategoryResponseDTO>> getAllCategories() {
        List<PlaceCategory> categories = categoryRepository.findAllActive();
        return new ApiResponse<>(200, "Place categories retrieved successfully", categoryMapper.toDtoList(categories));
    }

    public ApiResponse<PlaceCategoryResponseDTO> getCategoryById(Long id) {
        PlaceCategory category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place category not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Place category retrieved successfully", categoryMapper.toDto(category));
    }

    public ApiResponse<PlaceCategoryResponseDTO> getCategoryBySlug(String slug) {
        PlaceCategory category = categoryRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ApiException("Place category not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Place category retrieved successfully", categoryMapper.toDto(category));
    }

    @Transactional
    public ApiResponse<PlaceCategoryResponseDTO> updateCategory(Long id, UpdatePlaceCategoryRequestDTO dto) {
        checkAdmin();

        PlaceCategory category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place category not found", ErrorCode.NOT_FOUND));

        categoryMapper.updateEntityFromDto(dto, category);

        if (dto.getName() != null) {
            category.setSlug(generateSlug(dto.getName()));
        }

        if (dto.getIsActive() != null) {
            category.setIsActive(dto.getIsActive());
        }

        category = categoryRepository.save(category);

        return new ApiResponse<>(200, "Place category updated successfully", categoryMapper.toDto(category));
    }

    @Transactional
    public ApiResponse<String> deleteCategory(Long id) {
        checkAdmin();

        PlaceCategory category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place category not found", ErrorCode.NOT_FOUND));

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_PLACE_CATEGORY,
                "Deleted place category: " + category.getName(),
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Place category deleted successfully", null);
    }
}