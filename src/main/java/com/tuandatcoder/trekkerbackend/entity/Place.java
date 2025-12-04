package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.PlaceStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "place")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(unique = true, length = 255)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PlaceCategory category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "JSONB")
    private String openingHours;

    @Column(length = 10)
    private String priceRange;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column
    private Integer totalReviews = 0;

    @Column
    private Integer totalCheckins = 0;

    @Column
    private boolean isVerified = false;

    @Column
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlaceStatusEnum status = PlaceStatusEnum.ACTIVE; // With DELETED
}