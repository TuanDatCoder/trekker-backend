package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Lấy trip của user (chưa bị soft delete)
    @Query("SELECT t FROM Trip t WHERE t.account.id = :accountId AND t.deletedAt IS NULL")
    List<Trip> findByAccountId(Long accountId);

    // Lấy trip theo id + chưa bị delete + kiểm tra owner
    @Query("SELECT t FROM Trip t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Trip> findActiveById(Long id);

    // Public trips + Friends-only (sẽ mở rộng sau khi có friend system)
    @Query("SELECT t FROM Trip t WHERE t.privacy = 'PUBLIC' AND t.deletedAt IS NULL")
    List<Trip> findAllPublic();
}