package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.payload.dto.request.MultipleDailyHrsReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.PageNoDto;
import com.digigate.engineeringmanagement.planning.payload.request.UtilizationReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AircraftMaintenanceLogService {
    List<AmlDropdownViewModel> getAllActiveAml();

    void validateAmlPageNo(PageNoDto pageNoDto);
    UtilizationReportResponse getUtilizationReport(UtilizationReportSearchDto searchDto);
    AmlLastPageAndAircraftInfo findAircraftInfoAndLastAmlPageNo(Long aircraftId);


    DailyFlyingHoursReportViewModel getDailyHrsReport(LocalDate date, Long aircraftId, Integer page, Integer size);

    Page<OilUpLiftReportViewModel> getOilUpLiftReport(LocalDate fromDate, LocalDate toDate, Long aircraftId, Pageable pageable);


    List<AircraftMaintenanceLog> findAllNextAmlsWithCurrentAml(Integer pageNo, Long amlAircraftId);

    AmlLastPageAndAircraftInfo findAirframeInfoByPageNo(Integer pageNo, Long aircraftId);

    List<AircraftMaintenanceLog> findAllNextAmls(Integer pageNo, Long aircraftId);

    Boolean verifyAtl(Long amlId);

    List<MultipleDailyFlyingHoursReportViewModel> getMultipleDailyHrsReport(MultipleDailyHrsReportSearchDto
                                                                                    multipleDailyHrsReportSearchDto);

    List<AmlPageViewModel> getAmlPageAndAlphabets(Long aircraftId);

    List<DefectRectViewModel> getInterruptionInfo(Long amlId);
}
