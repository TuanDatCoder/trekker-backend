package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.placereview.request.CreatePlaceReviewRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placereview.request.UpdatePlaceReviewRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placereview.response.PlaceReviewResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Place;
import com.tuandatcoder.trekkerbackend.entity.PlaceReview;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PlaceReviewMapper;
import com.tuandatcoder.trekkerbackend.repository.PlaceRepository;
import com.tuandatcoder.trekkerbackend.repository.PlaceReviewRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaceReviewService {

    @Autowired private PlaceReviewRepository reviewRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PlaceReviewMapper reviewMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    @Transactional
    public ApiResponse<PlaceReviewResponseDTO> createReview(Long placeId, CreatePlaceReviewRequestDTO dto) {
        Account current = getCurrentAccount();

        Place place = placeRepository.findActiveById(placeId)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        // Kiểm tra đã review chưa
        if (reviewRepository.findByPlaceIdAndAccountIdAndDeletedAtIsNull(placeId, current.getId()).isPresent()) {
            throw new ApiException("You have already reviewed this place", ErrorCode.USER_EXISTED);
        }

        PlaceReview review = PlaceReview.builder()
                .place(place)
                .account(current)
                .rating(dto.getRating())
                .content(dto.getContent())
                .build();

        review = reviewRepository.save(review);

        // Cập nhật averageRating + totalReviews cho Place
        updatePlaceRating(placeId);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REVIEW_PLACE,
                "Reviewed place: " + place.getName() + " (Rating: " + dto.getRating() + ")",
                current
        );

        return new ApiResponse<>(201, "Review created successfully", reviewMapper.toDto(review));
    }

    public ApiResponse<List<PlaceReviewResponseDTO>> getReviewsByPlaceId(Long placeId) {
        placeRepository.findActiveById(placeId)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        List<PlaceReview> reviews = reviewRepository.findByPlaceIdActive(placeId);
        return new ApiResponse<>(200, "Reviews retrieved successfully", reviewMapper.toDtoList(reviews));
    }

    @Transactional
    public ApiResponse<PlaceReviewResponseDTO> updateReview(Long reviewId, UpdatePlaceReviewRequestDTO dto) {
        Account current = getCurrentAccount();

        PlaceReview review = reviewRepository.findActiveById(reviewId)
                .orElseThrow(() -> new ApiException("Review not found", ErrorCode.NOT_FOUND));

        if (!review.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only edit your own review", ErrorCode.FORBIDDEN);
        }

        reviewMapper.updateEntityFromDto(dto, review);
        review = reviewRepository.save(review);

        // Cập nhật lại rating của place
        updatePlaceRating(review.getPlace().getId());

        return new ApiResponse<>(200, "Review updated successfully", reviewMapper.toDto(review));
    }

    @Transactional
    public ApiResponse<String> deleteReview(Long reviewId) {
        Account current = getCurrentAccount();

        PlaceReview review = reviewRepository.findActiveById(reviewId)
                .orElseThrow(() -> new ApiException("Review not found", ErrorCode.NOT_FOUND));

        if (!review.getAccount().getId().equals(current.getId()) && current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("You can only delete your own review", ErrorCode.FORBIDDEN);
        }

        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);

        // Cập nhật lại rating của place
        updatePlaceRating(review.getPlace().getId());

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_REVIEW,
                "Deleted review for place ID " + review.getPlace().getId(),
                current
        );

        return new ApiResponse<>(200, "Review deleted successfully", null);
    }

    // Helper: Cập nhật averageRating + totalReviews cho Place
    private void updatePlaceRating(Long placeId) {
        List<PlaceReview> reviews = reviewRepository.findByPlaceIdActive(placeId);
        Place place = placeRepository.findActiveById(placeId).get();

        if (reviews.isEmpty()) {
            place.setAverageRating(null);
            place.setTotalReviews(0);
        } else {
            double avg = reviews.stream()
                    .mapToInt(PlaceReview::getRating)
                    .average()
                    .orElse(0.0);

            place.setAverageRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            place.setTotalReviews(reviews.size());
        }

        placeRepository.save(place);
    }
}