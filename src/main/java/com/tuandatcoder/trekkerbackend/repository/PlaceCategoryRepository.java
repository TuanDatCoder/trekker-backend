package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {

    @Query("SELECT pc FROM PlaceCategory pc WHERE pc.deletedAt IS NULL ORDER BY pc.orderIndex ASC NULLS LAST, pc.createdAt DESC")
    List<PlaceCategory> findAllActive();

    @Query("SELECT pc FROM PlaceCategory pc WHERE pc.id = :id AND pc.deletedAt IS NULL")
    Optional<PlaceCategory> findActiveById(Long id);

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    Optional<PlaceCategory> findBySlugAndDeletedAtIsNull(String slug);
}