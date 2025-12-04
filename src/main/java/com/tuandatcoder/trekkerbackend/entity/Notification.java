package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.NotificationTypeEnum;
import com.tuandatcoder.trekkerbackend.enums.ReferenceTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Account recipient;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTypeEnum type; // REACTION, COMMENT, TAG, FRIEND_REQUEST, TRIP_INVITE, PLACE_REVIEW, SUBSCRIPTION_EXPIRE

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ReferenceTypeEnum referenceType;
    // PHOTO, TRIP, POST, COMMENT, FRIENDSHIP, PLACE, SUBSCRIPTION

    @Column
    private Long referenceId;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    private boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}