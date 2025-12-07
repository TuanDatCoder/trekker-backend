package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.Reaction;
import com.tuandatcoder.trekkerbackend.enums.ReactionTargetTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByAccountIdAndTargetTypeAndTargetIdAndDeletedAtIsNull(
            Long accountId, ReactionTargetTypeEnum targetType, Long targetId);

    @Query("SELECT r FROM Reaction r WHERE r.targetType = :targetType AND r.targetId = :targetId AND r.deletedAt IS NULL")
    List<Reaction> findByTarget(ReactionTargetTypeEnum targetType, Long targetId);

    long countByTargetTypeAndTargetIdAndDeletedAtIsNull(ReactionTargetTypeEnum targetType, Long targetId);
}