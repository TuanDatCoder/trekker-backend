package com.tuandatcoder.trekkerbackend.dto.report.request;

import com.tuandatcoder.trekkerbackend.enums.ReportStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportStatusRequestDTO {

    @NotNull(message = "Status is required")
    private ReportStatusEnum status;

    private String adminNote; // Ghi chú xử lý
}