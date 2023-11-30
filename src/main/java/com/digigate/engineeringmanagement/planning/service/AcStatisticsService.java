package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.AcStatistics;
import com.digigate.engineeringmanagement.planning.payload.request.AcStatisticsDto;
import com.digigate.engineeringmanagement.planning.payload.response.MonthData;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface AcStatisticsService {

    void updateOrSaveAcStateWithOldAml(Long acModelId, LocalDate date);

    void createAcStatisticsList(List<AcStatisticsDto> acStatisticsDtos, boolean zeroInitial);

    Set<MonthData> findExistingMonths(Long aircraftModelId);

    Boolean checkIsAlreadyExist(Long acModelId, Integer startMonth, Integer startYear,
                                Integer endMonth, Integer endYear);

    List<AcStatistics> findAcStats(Long aircraftModelId, LocalDate fromDate, LocalDate toDate);
}
