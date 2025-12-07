package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Place> findAllActive();

    @Query("SELECT p FROM Place p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Place> findActiveById(Long id);

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    Optional<Place> findBySlugAndDeletedAtIsNull(String slug);
}