package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Friendship;
import com.tuandatcoder.trekkerbackend.enums.FriendshipStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Tìm mối quan hệ giữa 2 người (theo cả 2 chiều)
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.id = :user1Id AND f.receiver.id = :user2Id) OR " +
            "(f.requester.id = :user2Id AND f.receiver.id = :user1Id) " +
            "AND f.deletedAt IS NULL")
    Optional<Friendship> findActiveBetween(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // Lời mời đang chờ (người khác gửi cho mình)
    @Query("SELECT f FROM Friendship f WHERE f.receiver.id = :userId " +
            "AND f.status = 'PENDING' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Friendship> findPendingRequestsToMe(@Param("userId") Long userId);

    // Lời mời mình đã gửi
    @Query("SELECT f FROM Friendship f WHERE f.requester.id = :userId " +
            "AND f.status = 'PENDING' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Friendship> findPendingRequestsFromMe(@Param("userId") Long userId);

    // Danh sách bạn bè (ACCEPTED)
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.id = :userId OR f.receiver.id = :userId) " +
            "AND f.status = 'ACCEPTED' AND f.deletedAt IS NULL")
    List<Friendship> findAcceptedFriends(@Param("userId") Long userId);

    // Kiểm tra đã là bạn chưa
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.id = :user1Id AND f.receiver.id = :user2Id) OR " +
            "(f.requester.id = :user2Id AND f.receiver.id = :user1Id) " +
            "AND f.status = 'ACCEPTED' AND f.deletedAt IS NULL")
    Optional<Friendship> findAcceptedBetween(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}