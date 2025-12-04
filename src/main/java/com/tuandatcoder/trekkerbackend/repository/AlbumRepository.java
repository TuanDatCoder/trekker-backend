package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Album của user hiện tại (cả cá nhân + trip)
    @Query("SELECT a FROM Album a WHERE a.account.id = :accountId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<Album> findByAccountId(@Param("accountId") Long accountId);

    // Album của 1 trip cụ thể
    @Query("SELECT a FROM Album a WHERE a.trip.id = :tripId AND a.deletedAt IS NULL")
    List<Album> findByTripId(@Param("tripId") Long tripId);

    // Tìm album active (chưa delete)
    @Query("SELECT a FROM Album a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Album> findActiveById(@Param("id") Long id);

    // Public albums (dùng cho feed sau này)
    @Query("SELECT a FROM Album a WHERE a.privacy = 'PUBLIC' AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<Album> findAllPublic();
}