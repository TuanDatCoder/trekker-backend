package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.UserPlaceCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlaceCheckInRepository extends JpaRepository<UserPlaceCheckIn, Long> {

    @Query("SELECT c FROM UserPlaceCheckIn c WHERE c.account.id = :accountId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<UserPlaceCheckIn> findByAccountId(Long accountId);

    @Query("SELECT c FROM UserPlaceCheckIn c WHERE c.place.id = :placeId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<UserPlaceCheckIn> findByPlaceId(Long placeId);

    @Query("SELECT c FROM UserPlaceCheckIn c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<UserPlaceCheckIn> findActiveById(Long id);

    boolean existsByAccountIdAndPlaceIdAndDeletedAtIsNull(Long accountId, Long placeId);
}