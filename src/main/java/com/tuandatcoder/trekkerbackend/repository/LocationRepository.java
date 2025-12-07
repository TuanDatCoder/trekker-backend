package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    List<Location> findAllActive();

    @Query("SELECT l FROM Location l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Location> findActiveById(Long id);

    // Tìm location gần giống (dùng cho tránh duplicate khi upload ảnh)
    List<Location> findByLatitudeBetweenAndLongitudeBetweenAndDeletedAtIsNull(
            BigDecimal latMin, BigDecimal latMax, BigDecimal lngMin, BigDecimal lngMax);
}