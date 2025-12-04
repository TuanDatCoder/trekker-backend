package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.ReactionTargetTypeEnum;
import com.tuandatcoder.trekkerbackend.enums.ReactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ReactionTargetTypeEnum targetType;
    // PHOTO, TRIP, POST, COMMENT

    @Column
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionTypeEnum reactionType; // HEART, LIKE, HAHA, WOW, SAD, ANGRY

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}