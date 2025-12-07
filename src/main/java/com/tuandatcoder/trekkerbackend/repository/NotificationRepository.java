package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientId(Long recipientId);

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.isRead = false AND n.deletedAt IS NULL")
    List<Notification> findUnreadByRecipientId(Long recipientId);

    long countByRecipientIdAndIsReadFalseAndDeletedAtIsNull(Long recipientId);
}