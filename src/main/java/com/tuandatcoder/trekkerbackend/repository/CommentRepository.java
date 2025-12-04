package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Lấy comment gốc (không có parent) của 1 target
    @Query("SELECT c FROM Comment c WHERE c.targetType = :targetType AND c.targetId = :targetId " +
            "AND c.parentComment IS NULL AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findRootComments(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    // Lấy reply của 1 comment
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);

    // Tìm comment active
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Comment> findActiveById(@Param("id") Long id);
}
