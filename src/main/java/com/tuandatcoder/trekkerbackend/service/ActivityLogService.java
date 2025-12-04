package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.activity.request.ActivityLogFilterDTO;
import com.tuandatcoder.trekkerbackend.dto.activity.response.ActivityLogResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.ActivityLog;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.mapper.ActivityLogMapper;
import com.tuandatcoder.trekkerbackend.repository.ActivityLogRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    @Autowired private ActivityLogRepository activityLogRepository;
    @Autowired private ActivityLogMapper activityLogMapper;
    @Autowired private AccountUtils accountUtils;

    // Admin: Lấy tất cả log (có filter + phân trang)
    public ApiResponse<List<ActivityLogResponseDTO>> getAllLogs(ActivityLogFilterDTO filter) {
        Account current = accountUtils.getCurrentAccount();
        if (current == null || current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can view activity logs", ErrorCode.FORBIDDEN);
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<ActivityLog> page = activityLogRepository.filterLogs(
                filter.getAccountId(),
                filter.getActionType(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        List<ActivityLogResponseDTO> dtos = page.getContent().stream()
                .map(activityLogMapper::toDto)
                .collect(Collectors.toList());

        // Bạn có thể thêm totalElements, totalPages vào ApiResponse nếu cần
        return new ApiResponse<>(200, "Activity logs retrieved successfully", dtos);
    }
}
