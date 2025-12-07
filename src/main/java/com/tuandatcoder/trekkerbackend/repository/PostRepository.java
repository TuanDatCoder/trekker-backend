package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.account.id = :accountId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Post> findByAccountId(Long accountId);

    @Query("SELECT p FROM Post p WHERE p.trip.id = :tripId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Post> findByTripId(Long tripId);

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Post> findActiveById(Long id);

    @Query("SELECT p FROM Post p WHERE p.privacy = 'PUBLIC' AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Post> findAllPublic();
}