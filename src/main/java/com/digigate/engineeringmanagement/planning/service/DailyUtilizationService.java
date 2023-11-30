package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.DailyUtilization;
import com.digigate.engineeringmanagement.planning.entity.MonthlyUtilization;
import com.digigate.engineeringmanagement.planning.payload.request.DailyUtilizationReqDto;
import com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReportList;
import com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport;
import com.digigate.engineeringmanagement.planning.payload.response.YearlyUtilizationReport;

import java.time.LocalDate;
import java.util.List;

public interface DailyUtilizationService {

    void createNew(DailyUtilizationReqDto reqDto);

    void updateUtilization(DailyUtilizationReqDto reqDto, DailyUtilization dailyUtilization);

    void createDailyUtilization(DailyUtilizationReqDto reqDto);

    DailyUtilization saveItem(DailyUtilization dailyUtilization);

    DailyUtilizationReportList getDailyUtilizationReport(LocalDate fromDate, LocalDate toDate,
                                                         Long aircraftId);

    void deleteItem(DailyUtilization exUtilization);
    List<MonthlyUtilizationReport> getMonthlyUtilizationReport(LocalDate fromDate, LocalDate toDate, Long aircraftId);

    List<YearlyUtilizationReport> getYearlyUtilizationReport(LocalDate fromDate, LocalDate toDate, Long aircraftId);

    void generateAndSaveMonthlyUtilization();
}
