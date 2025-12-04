package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    @Query("SELECT f FROM Feature f WHERE f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Feature> findAllActive();

    @Query("SELECT f FROM Feature f WHERE f.id = :id AND f.deletedAt IS NULL")
    Optional<Feature> findActiveById(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);
}