package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.entity.AcStatistics;
import com.digigate.engineeringmanagement.planning.payload.request.AcStatisticsDto;
import com.digigate.engineeringmanagement.planning.payload.response.AcFlightStatisticData;
import com.digigate.engineeringmanagement.planning.payload.response.MonthData;
import com.digigate.engineeringmanagement.planning.repository.AcStatisticsRepository;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AcStatisticsServiceImpl implements AcStatisticsService {
    private final AircraftMaintenanceLogRepository amlRepository;
    private final AircraftModelService aircraftModelService;
    private final AcStatisticsRepository acStatisticsRepository;


    public AcStatisticsServiceImpl(AircraftMaintenanceLogRepository amlRepository, AircraftModelService aircraftModelService,
                                   AcStatisticsRepository acStatisticsRepository) {
        this.amlRepository = amlRepository;
        this.aircraftModelService = aircraftModelService;
        this.acStatisticsRepository = acStatisticsRepository;
    }

    @Override
    public void updateOrSaveAcStateWithOldAml(Long acModelId, LocalDate date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        Optional<AcStatistics> acStatistics = findByAcModelIdAndMonthAndYear(acModelId, month, year);
        if (acStatistics.isPresent()) {
            AcStatisticsDto dto = AcStatisticsDto.builder()
                    .id(acStatistics.get().getId())
                    .aircraftModelId(acModelId)
                    .aircraftModel(acStatistics.get().getAircraftModel())
                    .month(month)
                    .year(year)
                    .build();
            saveItem(prepareAcStatData(dto));
        }

    }

    @Override
    public void createAcStatisticsList(List<AcStatisticsDto> acStatisticsDtos, boolean zeroInitial) {
        List<AcStatistics> acStatisticsList = new ArrayList<>();
        if (Objects.equals(zeroInitial, true)) {
            acStatisticsDtos.forEach(ac -> {
                acStatisticsList.add(prepareAcStatDataWithZeroHourCycle(ac));
            });
        } else {
            acStatisticsDtos.forEach(ac -> {
                acStatisticsList.add(prepareAcStatData(ac));
            });

        }
        saveItemList(acStatisticsList);
    }

    private AcStatistics prepareAcStatDataWithZeroHourCycle(AcStatisticsDto dto) {
        AcStatistics acStatistics = new AcStatistics();
        acStatistics.setMonth(dto.getMonth());
        acStatistics.setYear(dto.getYear());
        acStatistics.setTotalFlightHour(0.0);
        acStatistics.setTotalFlightCycle(0);
        acStatistics.setTotalServiceDay(0L);
        acStatistics.setAircraftModel(dto.getAircraftModel());
        return acStatistics;
    }

    @Override
    public Set<MonthData> findExistingMonths(Long aircraftModelId) {
        return acStatisticsRepository.findMonths(aircraftModelId);
    }

    @Override
    public Boolean checkIsAlreadyExist(Long acModelId, Integer startMonth, Integer startYear,
                                       Integer endMonth, Integer endYear) {
        Set<MonthData> monthDataSet = acStatisticsRepository.getExistingMonths(acModelId, startMonth, startYear,
                endMonth, endYear);
        return Objects.equals(monthDataSet.size(), 2);
    }

    public Optional<AcStatistics> findByAcModelIdAndMonthAndYear(Long aircraftModelId, Integer month, Integer year) {
        return acStatisticsRepository.findByAcModelIdAndMonthAndYear(aircraftModelId, month, year);
    }

    @Override
    public List<AcStatistics> findAcStats(Long aircraftModelId, LocalDate fromDate, LocalDate toDate) {
        if (fromDate.getYear() == toDate.getYear()) {
            return acStatisticsRepository.findAcStatInSameYear(aircraftModelId, fromDate.getYear(), fromDate.getMonthValue(),
                    toDate.getMonthValue());
        } else {
            return acStatisticsRepository.findAcStatInDifferentYear(aircraftModelId, fromDate.getYear(), toDate.getYear(),
                    fromDate.getMonthValue(),
                    toDate.getMonthValue());
        }
    }

    private AcStatistics prepareAcStatData(AcStatisticsDto dto) {
        LocalDate monthStart = LocalDate.of(dto.getYear(), dto.getMonth(), 1);

        LocalDate monthEnd = LocalDate.of(dto.getYear(), dto.getMonth(), Month.of(dto.getMonth()).length(false));

        List<AcFlightStatisticData> statisticData = amlRepository.getAcFlightStatData(
                monthStart, monthEnd, dto.getAircraftModelId(), AmlType.REGULAR);

        AtomicReference<Double> hour = new AtomicReference<>(0.0);
        AtomicReference<Integer> cycle = new AtomicReference<>(0);
        statisticData.forEach(d -> {
            if (Objects.nonNull(d.getHour())) {
                hour.set(DateUtil.addTimes(d.getHour(), hour.get()));
            }
            if (Objects.nonNull(d.getCycle())) {
                cycle.set(d.getCycle() + cycle.get());
            }
        });

        Long numOfAcInService = statisticData.stream().distinct().count();

        AcStatistics acStatistics = new AcStatistics();
        if (Objects.nonNull(dto.getId())) {
            acStatistics.setId(dto.getId());
        }
        acStatistics.setMonth(dto.getMonth());
        acStatistics.setYear(dto.getYear());
        acStatistics.setTotalFlightHour(DateUtil.convertMinuteIntoHundredValue(hour.get()));
        acStatistics.setTotalFlightCycle(cycle.get());
        acStatistics.setTotalServiceDay(numOfAcInService);
        acStatistics.setAircraftModel(dto.getAircraftModel());
        return acStatistics;
    }

    public AcStatistics saveItem(AcStatistics entity) {
        try {
            return acStatisticsRepository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
                    ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    private void saveItemList(List<AcStatistics> entityList) {
        try {
            acStatisticsRepository.saveAll(entityList);
        } catch (Exception e) {
            String name = entityList.getClass().getSimpleName();
            throw EngineeringManagementServerException.dataSaveException(
                    Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                            name));
        }
    }
}
