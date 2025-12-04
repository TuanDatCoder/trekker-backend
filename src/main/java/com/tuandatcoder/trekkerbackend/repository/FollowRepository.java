package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Kiểm tra đã follow chưa
    @Query("SELECT f FROM Follow f WHERE f.follower.id = :followerId " +
            "AND f.following.id = :followingId AND f.deletedAt IS NULL")
    Optional<Follow> findActiveByFollowerAndFollowing(
            @Param("followerId") Long followerId,
            @Param("followingId") Long followingId);

    // Danh sách người mà user đang follow (following)
    @Query("SELECT f FROM Follow f WHERE f.follower.id = :userId AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Follow> findFollowingByUserId(@Param("userId") Long userId);

    // Danh sách người đang follow user (followers)
    @Query("SELECT f FROM Follow f WHERE f.following.id = :userId AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Follow> findFollowersByUserId(@Param("userId") Long userId);

    // Đếm số người đang follow / được follow
    long countByFollowerIdAndDeletedAtIsNull(Long followerId);
    long countByFollowingIdAndDeletedAtIsNull(Long followingId);
}