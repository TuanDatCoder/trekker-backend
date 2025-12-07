package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripDayRepository extends JpaRepository<TripDay, Long> {

    @Query("SELECT td FROM TripDay td WHERE td.trip.id = :tripId AND td.deletedAt IS NULL ORDER BY td.dayIndex ASC")
    List<TripDay> findByTripIdActive(Long tripId);

    @Query("SELECT td FROM TripDay td WHERE td.id = :id AND td.deletedAt IS NULL")
    Optional<TripDay> findActiveById(Long id);

    boolean existsByTripIdAndDayIndexAndDeletedAtIsNull(Long tripId, Integer dayIndex);
}