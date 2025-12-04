package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Chỉ lấy log chưa bị soft delete
    @Query("SELECT a FROM ActivityLog a WHERE a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<ActivityLog> findAllActive();

    @Query("SELECT a FROM ActivityLog a WHERE a.deletedAt IS NULL AND a.account.id = :accountId ORDER BY a.createdAt DESC")
    List<ActivityLog> findByAccountId(@Param("accountId") Long accountId);

    // Dùng cho phân trang + filter
    @Query("""
            SELECT a FROM ActivityLog a 
            WHERE a.deletedAt IS NULL 
              AND (:accountId IS NULL OR a.account.id = :accountId)
              AND (:actionType IS NULL OR a.actionType = :actionType)
              AND (:fromDate IS NULL OR a.createdAt >= :fromDate)
              AND (:toDate IS NULL OR a.createdAt <= :toDate)
            ORDER BY a.createdAt DESC
            """)
    Page<ActivityLog> filterLogs(
            @Param("accountId") Long accountId,
            @Param("actionType") com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum actionType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}