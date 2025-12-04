package com.tuandatcoder.trekkerbackend.helper;

import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.ActivityLog;
import com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum;
import com.tuandatcoder.trekkerbackend.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ActivityLogger {

    @Autowired private ActivityLogRepository activityLogRepository;
    @Autowired private HttpServletRequest request;

    @Async
    public void log(ActivityActionTypeEnum actionType, String details, Account account) {
        String ip = getClientIp();

        ActivityLog log = ActivityLog.builder()
                .account(account)
                .actionType(actionType)
                .details(details)
                .ipAddress(ip)
                .build();

        activityLogRepository.save(log);
    }

    // Lấy IP thật của user (xử lý proxy nếu có)
    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}