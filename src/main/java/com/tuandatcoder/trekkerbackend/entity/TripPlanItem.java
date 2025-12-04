package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.TripPlanItemCategoryEnum;
import com.tuandatcoder.trekkerbackend.enums.TripPlanItemStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_plan_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "trip_day_id")
    private TripDay tripDay;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private TripPlanItemCategoryEnum category; // FOOD, ACTIVITY, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripPlanItemStatusEnum status; // PLANNED, VISITED, SKIPPED, DELETED

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "actual_time")
    private LocalDateTime actualTime;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}