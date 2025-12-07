package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tripparticipant.request.InviteParticipantRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripparticipant.request.UpdateParticipantRoleRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripparticipant.response.TripParticipantResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.enums.TripParticipantRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.TripParticipantStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.TripParticipantMapper;
import com.tuandatcoder.trekkerbackend.repository.*;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripParticipantService {

    @Autowired private TripParticipantRepository participantRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private TripParticipantMapper participantMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkTripOwnership(Trip trip) {
        Account current = getCurrentAccount();
        if (!trip.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You must be the trip owner", ErrorCode.TRIP_FORBIDDEN);
        }
    }

    // MỜI THAM GIA
    @Transactional
    public ApiResponse<TripParticipantResponseDTO> inviteParticipant(Long tripId, InviteParticipantRequestDTO dto) {
        Account current = getCurrentAccount();

        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        checkTripOwnership(trip);

        if (!trip.isCollaborative()) {
            throw new ApiException("This trip is not collaborative", ErrorCode.TRIP_NOT_COLLABORATIVE);
        }

        Account invitedUser = accountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException("User not found", ErrorCode.ACCOUNT_NOT_FOUND));

        if (invitedUser.getId().equals(current.getId())) {
            throw new ApiException("You cannot invite yourself", ErrorCode.FORBIDDEN);
        }

        // Kiểm tra đã mời/chấp nhận chưa
        participantRepository.findByAccountAndTrip(invitedUser.getId(), tripId)
                .ifPresent(existing -> {
                    if (existing.getStatus() == TripParticipantStatusEnum.PENDING ||
                            existing.getStatus() == TripParticipantStatusEnum.ACCEPTED) {
                        throw new ApiException("User already invited or joined", ErrorCode.USER_EXISTED);
                    }
                });

        TripParticipant participant = TripParticipant.builder()
                .trip(trip)
                .account(invitedUser)
                .role(dto.getRole())
                .status(TripParticipantStatusEnum.PENDING)
                .invitedBy(current)
                .invitedAt(LocalDateTime.now())
                .build();

        participant = participantRepository.save(participant);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.INVITE_TO_TRIP,
                "Invited @" + invitedUser.getUsername() + " to trip: " + trip.getTitle(),
                current
        );

        return new ApiResponse<>(201, "Invitation sent successfully", participantMapper.toDto(participant));
    }

    // CHẤP NHẬN LỜI MỜI
    @Transactional
    public ApiResponse<TripParticipantResponseDTO> acceptInvitation(Long participantId) {
        Account current = getCurrentAccount();

        TripParticipant participant = participantRepository.findActiveById(participantId)
                .orElseThrow(() -> new ApiException("Invitation not found", ErrorCode.NOT_FOUND));

        if (!participant.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only accept your own invitation", ErrorCode.FORBIDDEN);
        }
        if (participant.getStatus() != TripParticipantStatusEnum.PENDING) {
            throw new ApiException("This invitation is no longer pending", ErrorCode.FORBIDDEN);
        }

        participant.setStatus(TripParticipantStatusEnum.ACCEPTED);
        participant.setJoinedAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Cập nhật totalParticipants
        Trip trip = participant.getTrip();
        trip.setTotalParticipants(trip.getTotalParticipants() + 1);
        tripRepository.save(trip);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.ACCEPT_TRIP_INVITE,
                "Accepted invitation to trip: " + trip.getTitle(),
                current
        );

        return new ApiResponse<>(200, "Invitation accepted", participantMapper.toDto(participant));
    }

    // TỪ CHỐI / RỜI CHUYẾN ĐI
    @Transactional
    public ApiResponse<String> declineOrLeave(Long participantId) {
        Account current = getCurrentAccount();

        TripParticipant participant = participantRepository.findActiveById(participantId)
                .orElseThrow(() -> new ApiException("Participant not found", ErrorCode.NOT_FOUND));

        boolean isOwner = participant.getTrip().getAccount().getId().equals(current.getId());
        boolean isParticipant = participant.getAccount().getId().equals(current.getId());

        if (!isOwner && !isParticipant) {
            throw new ApiException("You cannot manage this participant", ErrorCode.FORBIDDEN);
        }

        participant.setStatus(TripParticipantStatusEnum.DELETED);
        participant.setDeletedAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Giảm totalParticipants nếu đã ACCEPTED
        if (participant.getStatus() == TripParticipantStatusEnum.ACCEPTED) {
            Trip trip = participant.getTrip();
            trip.setTotalParticipants(Math.max(1, trip.getTotalParticipants() - 1));
            tripRepository.save(trip);
        }

        String action = isParticipant ? "left" : "removed from";
        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.LEAVE_TRIP,
                action + " trip: " + participant.getTrip().getTitle(),
                current
        );

        return new ApiResponse<>(200, "Successfully " + action + " the trip", null);
    }

    // LẤY DANH SÁCH THAM GIA
    public ApiResponse<List<TripParticipantResponseDTO>> getParticipants(Long tripId) {
        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        Account current = accountUtils.getCurrentAccount();
        if (current == null || !trip.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You must be the trip owner", ErrorCode.TRIP_FORBIDDEN);
        }

        List<TripParticipant> participants = participantRepository.findByTripIdActive(tripId);
        return new ApiResponse<>(200, "Participants retrieved", participantMapper.toDtoList(participants));
    }

    // LẤY LỜI MỜI ĐANG CHỜ CỦA MÌNH
    public ApiResponse<List<TripParticipantResponseDTO>> getMyPendingInvites() {
        Account current = getCurrentAccount();
        List<TripParticipant> invites = participantRepository.findPendingInvitesByAccount(current.getId());
        return new ApiResponse<>(200, "Pending trip invitations", participantMapper.toDtoList(invites));
    }
}