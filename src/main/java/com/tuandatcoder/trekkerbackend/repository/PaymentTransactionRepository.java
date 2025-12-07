package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.account.id = :accountId AND pt.deletedAt IS NULL ORDER BY pt.createdAt DESC")
    List<PaymentTransaction> findByAccountId(Long accountId);

    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.id = :id AND pt.deletedAt IS NULL")
    Optional<PaymentTransaction> findActiveById(Long id);

    Optional<PaymentTransaction> findByTransactionIdAndDeletedAtIsNull(String transactionId);
}
