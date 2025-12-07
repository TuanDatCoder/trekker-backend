package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.deletedAt IS NULL ORDER BY sp.createdAt DESC")
    List<SubscriptionPlan> findAllActive();

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.id = :id AND sp.deletedAt IS NULL")
    Optional<SubscriptionPlan> findActiveById(Long id);

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true AND sp.deletedAt IS NULL ORDER BY sp.price ASC")
    List<SubscriptionPlan> findAllActiveAndVisible();
}