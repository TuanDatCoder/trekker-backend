package com.tuandatcoder.trekkerbackend.repository;
import com.tuandatcoder.trekkerbackend.entity.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {

    @Query("SELECT pf FROM PlanFeature pf WHERE pf.deletedAt IS NULL")
    List<PlanFeature> findAllActive();

    @Query("SELECT pf FROM PlanFeature pf WHERE pf.id = :id AND pf.deletedAt IS NULL")
    Optional<PlanFeature> findActiveById(Long id);

    @Query("SELECT pf FROM PlanFeature pf WHERE pf.plan.id = :planId AND pf.deletedAt IS NULL")
    List<PlanFeature> findByPlanIdActive(Long planId);

    boolean existsByPlanIdAndFeatureIdAndDeletedAtIsNull(Long planId, Long featureId);
}