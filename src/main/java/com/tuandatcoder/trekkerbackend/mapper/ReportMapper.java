package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.report.response.ReportResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Report;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ReportMapper {

    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reporterUsername", source = "reporter.username")
    @Mapping(target = "reporterName", source = "reporter.name")
    ReportResponseDTO toDto(Report entity);

    List<ReportResponseDTO> toDtoList(List<Report> entities);
}