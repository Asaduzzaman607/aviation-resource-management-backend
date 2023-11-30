package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.AdReportTitleDataViewModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskReportService {


    PageData getLdNdReportData(LdndReportSearchDto searchDto,Pageable pageable);

    PageData getLdndHardTimeReport(LdndReportSearchDto searchDto,Pageable pageable);

    List<Long> updateLdndData();

    PageData getAdReportData(AdReportSearchDto adReportSearchDto, Pageable pageable);

    PageData getSbReport(AdReportSearchDto adReportSearchDto, Pageable pageable);

    PageData getStcReport(AdReportSearchDto adReportSearchDto, Pageable pageable);

    PageData getAdEngineReportData(EngineAdReportSearchDto dto, Pageable pageable);

    AdReportTitleDataViewModel getAdReportTitleData(Long aircraftId);

    PageData getTaskStatusReport(TaskStatusReportSearchDto taskStatusReportSearchDto, Pageable pageable);

    PageData searchTaskListByTaskSourceType(TaskListSearchDto searchDto, Pageable pageable);
}
