package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.targetType = :targetType AND t.targetId = :targetId AND t.deletedAt IS NULL")
    List<Tag> findByTarget(String targetType, Long targetId);

    @Query("SELECT t FROM Tag t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Tag> findActiveById(Long id);

    boolean existsByTargetTypeAndTargetIdAndAccountIdAndDeletedAtIsNull(
            String targetType, Long targetId, Long accountId);
}
