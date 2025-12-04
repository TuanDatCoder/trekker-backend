package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.TripParticipantRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.TripParticipantStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripParticipantRoleEnum role; // OWNER, CO_OWNER, CONTRIBUTOR, VIEWER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripParticipantStatusEnum status; // PENDING, ACCEPTED, DECLINED, DELETED

    @ManyToOne
    @JoinColumn(name = "invited_by")
    private Account invitedBy;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}