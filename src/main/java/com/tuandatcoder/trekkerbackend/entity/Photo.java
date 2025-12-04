package com.tuandatcoder.trekkerbackend.entity;

import com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "trip_day_id")
    private TripDay tripDay;

    @ManyToOne
    @JoinColumn(name = "trip_plan_item_id")
    private TripPlanItem tripPlanItem;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place; // Thay thế PlacePhoto

    @Column(length = 255)
    private String url;

    @Column(length = 255)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column
    private PhotoMediaTypeEnum mediaType; // IMAGE, VIDEO

    @Column
    private Double fileSizeMb;

    @Column
    private Integer width;

    @Column
    private Integer height;

    @Column
    private boolean isRealtime = false;

    @Column
    private boolean isPublicOnPlace = false;

    @Column(columnDefinition = "JSONB")
    private String exifData;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete
}