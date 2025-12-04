package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum;
import com.tuandatcoder.trekkerbackend.enums.TripStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String destination;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "cover_photo_id")
    private Photo coverPhoto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatusEnum status; // PLANNING, ONGOING, FINISHED, CANCELLED, DELETED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripPrivacyEnum privacy; // PUBLIC, FRIENDS_ONLY, PRIVATE

    @Column
    private boolean isCollaborative = false;

    @Column
    private Integer totalDays;

    @Column
    private Integer totalPhotos = 0;

    @Column
    private Integer totalParticipants = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}