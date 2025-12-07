package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("SELECT us FROM UserSubscription us WHERE us.account.id = :accountId AND us.deletedAt IS NULL ORDER BY us.createdAt DESC")
    List<UserSubscription> findByAccountId(Long accountId);

    @Query("SELECT us FROM UserSubscription us WHERE us.id = :id AND us.deletedAt IS NULL")
    Optional<UserSubscription> findActiveById(Long id);

    @Query("SELECT us FROM UserSubscription us WHERE us.account.id = :accountId AND us.status = 'ACTIVE' AND us.deletedAt IS NULL")
    Optional<UserSubscription> findActiveSubscriptionByAccount(Long accountId);
}