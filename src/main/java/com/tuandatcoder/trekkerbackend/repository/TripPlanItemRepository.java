package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.TripPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripPlanItemRepository extends JpaRepository<TripPlanItem, Long> {

    @Query("SELECT tpi FROM TripPlanItem tpi WHERE tpi.trip.id = :tripId AND tpi.deletedAt IS NULL ORDER BY tpi.orderIndex ASC, tpi.createdAt ASC")
    List<TripPlanItem> findByTripIdActive(Long tripId);

    @Query("SELECT tpi FROM TripPlanItem tpi WHERE tpi.tripDay.id = :tripDayId AND tpi.deletedAt IS NULL ORDER BY tpi.orderIndex ASC")
    List<TripPlanItem> findByTripDayIdActive(Long tripDayId);

    @Query("SELECT tpi FROM TripPlanItem tpi WHERE tpi.id = :id AND tpi.deletedAt IS NULL")
    Optional<TripPlanItem> findActiveById(Long id);
}