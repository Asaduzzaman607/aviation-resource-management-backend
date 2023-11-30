package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.entity.Ldnd;
import com.digigate.engineeringmanagement.planning.payload.request.ManHourReportDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneDto;
import com.digigate.engineeringmanagement.planning.payload.response.LdndForTaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LdndViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LdndViewModelForForecast;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface LdndService {

    Ldnd findExistingLdnd(Long taskId, Long partId, Long serialId);

    Ldnd save(Ldnd ldnd);

    Ldnd convertToLdndEntity(TaskDoneDto TaskDoneDto, Ldnd ldnd);

    Ldnd calculateLdnd(Ldnd ldnd);

    Ldnd calculateNextDue(Ldnd ldnd);

    Ldnd calculateRemainForFlight(Ldnd ldnd, Aircraft aircraft);

    Ldnd calculateRemainForApu(Ldnd ldnd, Aircraft aircraft);

    Ldnd calculateEstimatedDateForFlight(Ldnd ldnd, Aircraft aircraft, LocalDate amlDate);

    Ldnd calculateEstimatedDateForApu(Ldnd ldnd, Aircraft aircraft, LocalDate amlDate);

    Ldnd calculateRemainForFlight(Ldnd ldnd, AmlFlightData amlFlightData, LocalDate amlDate);

    Ldnd calculateRemainForApu(Ldnd ldnd, AmlFlightData amlFlightData, LocalDate amlDate);

    List<Ldnd> findLdndListByAircraftId(Long aircraftId);

    List<LdndForTaskViewModel> findAllLdndTaskByTaskIdIn(Set<Long> taskIds, Long aircraftId);

    List<LdndForTaskViewModel> findAllLdndTaskByAircraftId(Long aircraftId);

    List<Ldnd> getAllLdndByDomainIdIn(Set<Long> ldndIds, boolean isActive);

    LdndViewModel getCalculatedLdnd(TaskDoneDto taskDoneDto);

    void updateWithAmlFlightData(Aircraft aircraft, AmlFlightData amlFlightData, LocalDate date);

    void updateLdndFromManHourReport(ManHourReportDto manHourReportDto);

    List<LdndViewModelForForecast> findAllLdndByLdndIds(Set<Long> ldndIds);

    void processAndUpdateLdndRemainingValueCalculation();
}
