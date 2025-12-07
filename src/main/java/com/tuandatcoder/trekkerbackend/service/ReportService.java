package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.report.request.CreateReportRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.report.request.UpdateReportStatusRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.report.response.ReportResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Report;
import com.tuandatcoder.trekkerbackend.enums.ReportStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.ReportMapper;
import com.tuandatcoder.trekkerbackend.repository.ReportRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired private ReportRepository reportRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private ReportMapper reportMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage reports", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<ReportResponseDTO> createReport(CreateReportRequestDTO dto) {
        Account current = getCurrentAccount();

        // Kiểm tra đã báo cáo chưa
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetIdAndDeletedAtIsNull(
                current.getId(), dto.getTargetType().toUpperCase(), dto.getTargetId())) {
            throw new ApiException("You have already reported this content", ErrorCode.USER_EXISTED);
        }

        Report report = Report.builder()
                .reporter(current)
                .targetType(dto.getTargetType().toUpperCase())
                .targetId(dto.getTargetId())
                .reason(dto.getReason())
                .description(dto.getDescription())
                .status(ReportStatusEnum.PENDING)
                .build();

        report = reportRepository.save(report);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_REPORT,
                "Reported " + dto.getTargetType() + " ID " + dto.getTargetId() + " - Reason: " + dto.getReason(),
                current
        );

        return new ApiResponse<>(201, "Report submitted successfully", reportMapper.toDto(report));
    }

    // Admin: Lấy tất cả báo cáo
    public ApiResponse<List<ReportResponseDTO>> getAllReports() {
        checkAdmin();
        List<Report> reports = reportRepository.findAllActive();
        return new ApiResponse<>(200, "All reports retrieved", reportMapper.toDtoList(reports));
    }

    // Admin: Lấy báo cáo đang chờ xử lý
    public ApiResponse<List<ReportResponseDTO>> getPendingReports() {
        checkAdmin();
        List<Report> reports = reportRepository.findAllPending();
        return new ApiResponse<>(200, "Pending reports retrieved", reportMapper.toDtoList(reports));
    }

    // Admin: Cập nhật trạng thái báo cáo
    @Transactional
    public ApiResponse<ReportResponseDTO> updateReportStatus(Long reportId, UpdateReportStatusRequestDTO dto) {
        checkAdmin();

        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ApiException("Report not found", ErrorCode.NOT_FOUND));

        report.setStatus(dto.getStatus());
        report = reportRepository.save(report);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.UPDATE_REPORT_STATUS,
                "Updated report ID " + reportId + " to " + dto.getStatus(),
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Report status updated", reportMapper.toDto(report));
    }

    // Admin: Xóa báo cáo
    @Transactional
    public ApiResponse<String> deleteReport(Long reportId) {
        checkAdmin();

        Report report = reportRepository.findActiveById(reportId)
                .orElseThrow(() -> new ApiException("Report not found", ErrorCode.NOT_FOUND));

        report.setDeletedAt(LocalDateTime.now());
        report.setStatus(ReportStatusEnum.DELETED);
        reportRepository.save(report);

        return new ApiResponse<>(200, "Report deleted successfully", null);
    }
}