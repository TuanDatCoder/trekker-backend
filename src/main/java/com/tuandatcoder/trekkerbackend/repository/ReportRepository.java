package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Report> findAllActive();

    @Query("SELECT r FROM Report r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Report> findActiveById(Long id);

    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' AND r.deletedAt IS NULL ORDER BY r.createdAt ASC")
    List<Report> findAllPending();

    boolean existsByReporterIdAndTargetTypeAndTargetIdAndDeletedAtIsNull(
            Long reporterId, String targetType, Long targetId);
}