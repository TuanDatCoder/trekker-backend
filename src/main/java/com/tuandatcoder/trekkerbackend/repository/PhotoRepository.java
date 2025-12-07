package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("SELECT p FROM Photo p WHERE p.account.id = :accountId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Photo> findByAccountId(Long accountId);

    @Query("SELECT p FROM Photo p WHERE p.trip.id = :tripId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Photo> findByTripId(Long tripId);

    @Query("SELECT p FROM Photo p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Photo> findActiveById(Long id);


    
}