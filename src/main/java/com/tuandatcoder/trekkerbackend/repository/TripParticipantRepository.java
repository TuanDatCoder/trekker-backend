package com.tuandatcoder.trekkerbackend.repository;

import com.tuandatcoder.trekkerbackend.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {

    @Query("SELECT tp FROM TripParticipant tp WHERE tp.trip.id = :tripId AND tp.deletedAt IS NULL")
    List<TripParticipant> findByTripIdActive(Long tripId);

    @Query("SELECT tp FROM TripParticipant tp WHERE tp.id = :id AND tp.deletedAt IS NULL")
    Optional<TripParticipant> findActiveById(Long id);

    @Query("SELECT tp FROM TripParticipant tp WHERE tp.account.id = :accountId AND tp.trip.id = :tripId AND tp.deletedAt IS NULL")
    Optional<TripParticipant> findByAccountAndTrip(Long accountId, Long tripId);

    @Query("SELECT tp FROM TripParticipant tp WHERE tp.account.id = :accountId AND tp.status = 'PENDING' AND tp.deletedAt IS NULL")
    List<TripParticipant> findPendingInvitesByAccount(Long accountId);
}