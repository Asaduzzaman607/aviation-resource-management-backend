package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.entity.AcStatistics;
import com.digigate.engineeringmanagement.planning.payload.request.AcStatisticsDto;
import com.digigate.engineeringmanagement.planning.payload.request.OpStatSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.FleetUtilizationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.MonthData;
import com.digigate.engineeringmanagement.planning.payload.response.OpStatReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ServiceUtilizationReportViewModel;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import com.digigate.engineeringmanagement.planning.service.OperationalReportService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class OperationalReportServiceImpl implements OperationalReportService {


    private final AircraftMaintenanceLogRepository amlRepository;
    private final AircraftRepository aircraftRepository;
    private final AcStatisticsService acStatisticsService;
    private final AircraftModelService aircraftModelService;

    private final Integer SIXTY_MINUTES = 60;
    private final Integer TWELVE_MONTHS = 12;
    private final Integer MONTH_JANUARY = 1;

    public OperationalReportServiceImpl(AircraftMaintenanceLogRepository amlRepository,
                                        AircraftRepository aircraftRepository, AcStatisticsService acStatisticsService,
                                        AircraftModelService aircraftModelService) {
        this.amlRepository = amlRepository;
        this.aircraftRepository = aircraftRepository;
        this.acStatisticsService = acStatisticsService;
        this.aircraftModelService = aircraftModelService;
    }


    public void createAcStatData(Long aircraftModelId, LocalDate fromDate, LocalDate toDate) {

        AircraftModel aircraftModel = aircraftModelService.findById(aircraftModelId);

        Set<MonthData> monthsFromAmls = amlRepository.getMonths(fromDate, toDate,
                aircraftModelId, AmlType.REGULAR);

        Set<MonthData> monthRangeChecks = createMonthRange(fromDate, toDate);

        monthRangeChecks.forEach(mr -> {
            if (!monthsFromAmls.contains(mr)) {
                monthsFromAmls.add(MonthData.builder().month(mr.getMonth()).year(mr.getYear()).addAsZero(true).build());
            }
        });

        Set<MonthData> exitingMonthsInStats = acStatisticsService.findExistingMonths(aircraftModelId);

        List<AcStatisticsDto> zeroStatDtos = new ArrayList<>();
        List<AcStatisticsDto> statisticsDtos = new ArrayList<>();

        monthsFromAmls.forEach(m -> {
            if (!exitingMonthsInStats.contains(m)) {
                AcStatisticsDto dto = AcStatisticsDto.builder()
                        .aircraftModelId(aircraftModelId)
                        .aircraftModel(aircraftModel)
                        .month(m.getMonth())
                        .year(m.getYear())
                        .build();
                if (m.getAddAsZero().equals(true)) {
                    zeroStatDtos.add(dto);
                } else {
                    statisticsDtos.add(dto);
                }
            }
        });

        if (CollectionUtils.isNotEmpty(zeroStatDtos)) {
            acStatisticsService.createAcStatisticsList(zeroStatDtos, true);
        }

        if (CollectionUtils.isNotEmpty(statisticsDtos)) {
            acStatisticsService.createAcStatisticsList(statisticsDtos, false);
        }
    }

    private Set<MonthData> createMonthRange(LocalDate fromDate, LocalDate toDate) {

        Set<MonthData> monthDataList = new HashSet<>();
        int startMonth = fromDate.getMonth().getValue();
        int endMonth = toDate.getMonth().getValue();
        Integer startYear = fromDate.getYear();
        Integer endYear = toDate.getYear();

        if (startYear.equals(endYear)) {
            while (startMonth <= endMonth) {
                monthDataList.add(MonthData
                        .builder()
                        .month(startMonth)
                        .year(startYear)
                        .build());
                startMonth++;
            }
        } else {
            while (startMonth <= TWELVE_MONTHS) {
                monthDataList.add(MonthData
                        .builder()
                        .month(startMonth)
                        .year(startYear)
                        .build());
                startMonth++;
            }

            startMonth = MONTH_JANUARY;

            while (startMonth <= endMonth) {
                monthDataList.add(MonthData
                        .builder()
                        .month(startMonth)
                        .year(endYear)
                        .build());
                startMonth++;
            }
        }
        return monthDataList;
    }


    private void validateAndCreateAcStatDataIfNotExist(OpStatSearchDto searchDto){

        DateUtil.isValidFromDate(searchDto.getToDate());
        DateUtil.isValidateDateRangeWith12Months(searchDto.getFromDate(), searchDto.getToDate());

        Integer startYear = searchDto.getFromDate().getYear();
        Integer endYear = searchDto.getToDate().getYear();
        Integer startMonth = searchDto.getFromDate().getMonth().getValue();
        Integer endMonth = searchDto.getToDate().getMonth().getValue();

        if (acStatisticsService.checkIsAlreadyExist(searchDto.getAircraftModelId(), startMonth, startYear,
                endMonth, endYear).equals(false)) {
            createAcStatData(searchDto.getAircraftModelId(), searchDto.getFromDate(), searchDto.getToDate());
        }
    }

    @Override
    public List<OpStatReportViewModel> opStatReport(OpStatSearchDto searchDto) {

        validateAndCreateAcStatDataIfNotExist(searchDto);

        List<AcStatistics> acStatisticsList = acStatisticsService.findAcStats(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        Long acInFleet = aircraftRepository.countByAircraftModelIdAndIsActiveTrue(searchDto.getAircraftModelId());

        List<OpStatReportViewModel> reportViewModels = new ArrayList<>();
        acStatisticsList.forEach(acStatistics -> {
            OpStatReportViewModel report = new OpStatReportViewModel();
            report.setNumOfAcFleet(acInFleet);
            report.setMonth(acStatistics.getMonth());
            report.setYear(acStatistics.getYear());
            double days = (double) Month.of(acStatistics.getMonth()).length(false);
            Double acInService = acStatistics.getTotalServiceDay().doubleValue() / days;
            report.setNumOfAcInService(DateUtil.twoDigitDoubleValueOf(acInService));
            report.setAvailability(DateUtil.twoDigitDoubleValueOf(100.00 * (acInService / acInFleet)));
            report.setTotalFlightHour(acStatistics.getTotalFlightHour());
            report.setTotalFlightCycle(acStatistics.getTotalFlightCycle());
            reportViewModels.add(report);
        });
        return reportViewModels;
    }

    public List<FleetUtilizationReportViewModel> getFleetUtilReport(OpStatSearchDto searchDto) {

        validateAndCreateAcStatDataIfNotExist(searchDto);

        List<AcStatistics> acStatisticsList = acStatisticsService.findAcStats(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        Long acInFleet = aircraftRepository.countByAircraftModelIdAndIsActiveTrue(searchDto.getAircraftModelId());

        List<FleetUtilizationReportViewModel> reportViewModels = new ArrayList<>();
        acStatisticsList.forEach(acStatistics -> {
            FleetUtilizationReportViewModel report = new FleetUtilizationReportViewModel();
            report.setNumOfAcFleet(acInFleet);
            report.setMonth(acStatistics.getMonth());
            report.setYear(acStatistics.getYear());

            report.setMonthlyHours(acStatistics.getTotalFlightHour());
            report.setMonthlyCycles(acStatistics.getTotalFlightCycle());

            report.setMonthlyAvgFltUtilHours(DateUtil.twoDigitDoubleValueOf(
                    acStatistics.getTotalFlightHour() / acInFleet.doubleValue()));

            report.setMonthlyAvgFltUtilCycles(DateUtil.twoDigitDoubleValueOf(
                     (acStatistics.getTotalFlightCycle() / acInFleet.doubleValue())));


            Double days = (double) Month.of(acStatistics.getMonth()).length(false);


            report.setDailyAvgFltUtilHours(
                    DateUtil.twoDigitDoubleValueOf(report.getMonthlyAvgFltUtilHours() / days));

            report.setDailyAvgFltUtilCycles(DateUtil.twoDigitDoubleValueOf(report.getMonthlyAvgFltUtilCycles() / days));

            reportViewModels.add(report);
        });
        return reportViewModels;
    }


    public List<ServiceUtilizationReportViewModel> getServiceUtilReport(OpStatSearchDto searchDto) {

        validateAndCreateAcStatDataIfNotExist(searchDto);

        List<AcStatistics> acStatisticsList = acStatisticsService.findAcStats(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        Long acInFleet = aircraftRepository.countByAircraftModelIdAndIsActiveTrue(searchDto.getAircraftModelId());

        List<ServiceUtilizationReportViewModel> reportViewModels = new ArrayList<>();
        acStatisticsList.forEach(acStatistics -> {
            ServiceUtilizationReportViewModel report = new ServiceUtilizationReportViewModel();
            report.setNumOfAcFleet(acInFleet);
            report.setMonth(acStatistics.getMonth());
            report.setYear(acStatistics.getYear());

            report.setMonthlyHours(acStatistics.getTotalFlightHour());
            report.setMonthlyCycles(acStatistics.getTotalFlightCycle());
            Double days = (double) Month.of(acStatistics.getMonth()).length(false);


            Double dailyAvgFltUtilHours = acStatistics.getTotalFlightHour() / (acInFleet * days);
            report.setDailyAvgFltUtilHours(DateUtil.twoDigitDoubleValueOf(dailyAvgFltUtilHours));

            Double dailyAvgFltUtilCycles = acStatistics.getTotalFlightCycle() / (acInFleet * days);
            report.setDailyAvgFltUtilCycles(DateUtil.twoDigitDoubleValueOf(dailyAvgFltUtilCycles));

            if (!Objects.equals(acStatistics.getTotalFlightHour(), 0.0) &&
                    !Objects.equals(acStatistics.getTotalFlightHour(), 0.0)) {
                Double avgFlightDurationInMin =
                        (acStatistics.getTotalFlightHour() * SIXTY_MINUTES) / acStatistics.getTotalFlightCycle();
                report.setAvgFlightDurationInMin((DateUtil.twoDigitDoubleValueOf(avgFlightDurationInMin)));
            }

            reportViewModels.add(report);
        });
        return reportViewModels;
    }
}
