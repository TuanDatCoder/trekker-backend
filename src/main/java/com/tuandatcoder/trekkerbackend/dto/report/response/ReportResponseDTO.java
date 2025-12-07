package com.tuandatcoder.trekkerbackend.dto.report.response;

import com.tuandatcoder.trekkerbackend.enums.ReportStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private Long id;

    private Long reporterId;
    private String reporterUsername;
    private String reporterName;

    private String targetType;
    private Long targetId;

    private String reason;
    private String description;

    private ReportStatusEnum status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
