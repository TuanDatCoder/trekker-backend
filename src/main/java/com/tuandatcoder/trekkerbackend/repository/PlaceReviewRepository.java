package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

    @Query("SELECT r FROM PlaceReview r WHERE r.place.id = :placeId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<PlaceReview> findByPlaceIdActive(Long placeId);

    @Query("SELECT r FROM PlaceReview r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<PlaceReview> findActiveById(Long id);

    Optional<PlaceReview> findByPlaceIdAndAccountIdAndDeletedAtIsNull(Long placeId, Long accountId);

    long countByPlaceIdAndDeletedAtIsNull(Long placeId);
}