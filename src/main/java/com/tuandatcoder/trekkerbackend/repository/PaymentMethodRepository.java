package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.account.id = :accountId AND pm.deletedAt IS NULL ORDER BY pm.createdAt DESC")
    List<PaymentMethod> findByAccountId(Long accountId);

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.id = :id AND pm.deletedAt IS NULL")
    Optional<PaymentMethod> findActiveById(Long id);

    Optional<PaymentMethod> findByAccountIdAndIsDefaultTrueAndDeletedAtIsNull(Long accountId);
}