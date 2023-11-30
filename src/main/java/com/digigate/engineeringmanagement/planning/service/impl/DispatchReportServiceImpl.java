package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import com.digigate.engineeringmanagement.planning.entity.AcCancellations;
import com.digigate.engineeringmanagement.planning.payload.request.DispatchReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AcCancellationsRepository;
import com.digigate.engineeringmanagement.planning.repository.AcStatisticsRepository;
import com.digigate.engineeringmanagement.planning.repository.AircraftInterruptionsRepository;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import com.digigate.engineeringmanagement.planning.service.DispatchReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dispatch Report Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class DispatchReportServiceImpl implements DispatchReportService {

    private final AcStatisticsService acStatisticsService;

    private final AcStatisticsRepository acStatisticsRepository;

    private final AircraftInterruptionsRepository aircraftInterruptionsRepository;

    private final AcCancellationsRepository acCancellationsRepository;

    private final OperationalReportServiceImpl operationalReportService;

    public DispatchReportServiceImpl(AcStatisticsService acStatisticsService,
                                     AcStatisticsRepository acStatisticsRepository,
                                     AircraftInterruptionsRepository aircraftInterruptionsRepository,
                                     AcCancellationsRepository acCancellationsRepository,
                                     OperationalReportServiceImpl operationalReportService) {
        this.acStatisticsService = acStatisticsService;
        this.acStatisticsRepository = acStatisticsRepository;
        this.aircraftInterruptionsRepository = aircraftInterruptionsRepository;
        this.acCancellationsRepository = acCancellationsRepository;
        this.operationalReportService = operationalReportService;
    }

    @Override
    public List<DispatchReportViewModel> dispatchReport(DispatchReportSearchDto searchDto) {

        List<DispatchReportViewModel> dispatchReportViewModelList = new ArrayList<>();

        List<TotalCycleByDateViewModel> totalFlightCycles = totalFlightCycles(searchDto);

        List<LocalDate> dateWiseAircraftDelays = findDateWiseAircraftDelays(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        Map<Integer,Integer> delayMap = prepareDateWiseDelays(dateWiseAircraftDelays);

        List<AcCancellations> cancellationList = acCancellationsRepository
                .findDateWiseCancellation(searchDto.getAircraftModelId(),searchDto.getFromDate(), searchDto.getToDate());

        Map<Integer,Integer> initialCancellationMap = prepareInitialCancellation(cancellationList);

        Map<Integer,Integer> totalCancellationMap = prepareTotalCancellation(cancellationList);

        System.out.println(totalFlightCycles);

        totalFlightCycles.forEach(totalFlightCycle->{
            DispatchReportViewModel dispatchReportViewModel = new DispatchReportViewModel();
            dispatchReportViewModel.setMonth(totalFlightCycle.getMonth());
            dispatchReportViewModel.setYear(totalFlightCycle.getYear());
            dispatchReportViewModel.setDelay(Objects.nonNull(delayMap.get(totalFlightCycle.getMonth()))
                    ? delayMap.get(totalFlightCycle.getMonth()) : 0);
            dispatchReportViewModel.setInitialCancellation(Objects.nonNull(initialCancellationMap
                    .get(totalFlightCycle.getMonth())) ? initialCancellationMap.get(totalFlightCycle.getMonth()) : 0);
            dispatchReportViewModel.setTotalCancellation(Objects.nonNull(totalCancellationMap
                    .get(totalFlightCycle.getMonth())) ? totalCancellationMap.get(totalFlightCycle.getMonth()) : 0);
            dispatchReportViewModel.setScheduledDep(totalFlightCycle.getTotalCycle());
            if (totalFlightCycle.getTotalCycle() == 0) {
                dispatchReportViewModel.setDispatchReliability(0.0);
                dispatchReportViewModel.setScheduleCompletionRate(0.0);
            } else {
                dispatchReportViewModel.setDispatchReliability(DateUtil
                        .twoDigitDoubleValueOf(100 * (1 - ((dispatchReportViewModel.getDelay()
                        + dispatchReportViewModel.getInitialCancellation())
                                / totalFlightCycle.getTotalCycle().doubleValue()))));
                dispatchReportViewModel.setScheduleCompletionRate(DateUtil.twoDigitDoubleValueOf(
                        100 * (1 - (dispatchReportViewModel.getTotalCancellation()
                                / totalFlightCycle.getTotalCycle().doubleValue()))));
            }
            dispatchReportViewModelList.add(dispatchReportViewModel);
        });

        return dispatchReportViewModelList;
    }

    private Map<Integer, Integer> prepareTotalCancellation(List<AcCancellations> cancellationList) {

        Map<Integer, Integer> dataMap = new HashMap<>();
        cancellationList.forEach(acCancellations -> {
            Integer monthKey = acCancellations.getDate().getMonthValue();
            if(Objects.equals(acCancellations.getCancellationTypeEnum().getCancellationType(),
                    CancellationTypeEnum.TOTAL_CANCELLATION.getCancellationType())){
                if(dataMap.containsKey(monthKey)){
                    dataMap.put(monthKey, dataMap.get(monthKey)+1);
                } else{
                    dataMap.put(monthKey, 1);
                }
            }
        });
        return dataMap;
    }

    private Map<Integer, Integer> prepareInitialCancellation(List<AcCancellations> cancellationList) {

        Map<Integer, Integer> dataMap = new HashMap<>();

        cancellationList.forEach(elem->{
            Integer key = elem.getDate().getMonthValue();
            if(Objects.equals(elem.getCancellationTypeEnum().getCancellationType(),
                    CancellationTypeEnum.INITIAL_CANCELLATION.getCancellationType())){
                if (dataMap.containsKey(key)) {
                    dataMap.put(key, dataMap.get(key) + 1);
                } else {
                    dataMap.put(key, 1);
                }
            }
        });
        return dataMap;
    }

    private Map<Integer, Integer> prepareDateWiseDelays(List<LocalDate> dateWiseAircraftDelays) {

        Map<Integer, Integer> dataMap = new HashMap<>();

        dateWiseAircraftDelays.forEach(elem->{
            Integer key = elem.getMonthValue();
            if(dataMap.containsKey(key)){
                dataMap.put(key, dataMap.get(key)+1);
            }else{
                dataMap.put(key,1);
            }
        });

        return dataMap;
    }

    private List<TotalCycleByDateViewModel> totalFlightCycles(DispatchReportSearchDto searchDto) {
        DateUtil.isValidFromDate(searchDto.getToDate());
        DateUtil.isValidateDateRangeWith12Months(searchDto.getFromDate(), searchDto.getToDate());

        Integer startYear = searchDto.getFromDate().getYear();
        Integer endYear = searchDto.getToDate().getYear();
        Integer startMonth = searchDto.getFromDate().getMonth().getValue();
        Integer endMonth = searchDto.getToDate().getMonth().getValue();

        if (acStatisticsService.checkIsAlreadyExist(searchDto.getAircraftModelId(), startMonth, startYear,
                endMonth, endYear).equals(false)) {
            operationalReportService.createAcStatData(searchDto.getAircraftModelId(), searchDto.getFromDate(),
                    searchDto.getToDate());
        }
        return findTotalFlightCycles(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());
    }

    @Override
    public List<InterruptionReportViewModel> interruptionReport(DispatchReportSearchDto searchDto) {

        List<InterruptionReportViewModel> interruptionReportViewModelList = new ArrayList<>();

        List<TotalCycleByDateViewModel> totalFlightCycles = totalFlightCycles(searchDto);

        List<DispatchInterruptionData> dispatchInterruptionData = aircraftInterruptionsRepository
                .getInterruptionDataByDate(searchDto.getAircraftModelId(), searchDto.getFromDate(),
                        searchDto.getToDate());

        Map<String, Integer> initialDelay = prepareInitialDelay(dispatchInterruptionData);

        List<AcCancellations> initialCancellationList = acCancellationsRepository
                .findDateWiseInitialCancellation(searchDto.getAircraftModelId(),
                        searchDto.getFromDate(), searchDto.getToDate(), CancellationTypeEnum.INITIAL_CANCELLATION);

        Map<Integer, CancellationAndSdViewModel> cancellationAndSdViewModels =
                prepareFlightCycleAndInitialCancellation(initialCancellationList, totalFlightCycles);

        dispatchInterruptionData.forEach(elem -> {
            InterruptionReportViewModel interruptionReportViewModel = new InterruptionReportViewModel();
            if (cancellationAndSdViewModels.containsKey(elem.getDate().getMonthValue())
                    && initialDelay.containsKey(elem.getLocationName())) {
                CancellationAndSdViewModel cancellationAndSdViewModel = cancellationAndSdViewModels
                        .get(elem.getDate().getMonthValue());
                interruptionReportViewModel.setLocationName(elem.getLocationName());
                interruptionReportViewModel.setDate(elem.getDate());
                interruptionReportViewModel.setAircraftName(elem.getAircraftName());
                interruptionReportViewModel.setDefect(elem.getDefect());
                interruptionReportViewModel.setRectification(elem.getRectification());
                interruptionReportViewModel.setDuration(elem.getDuration());
                interruptionReportViewModel.setDir((cancellationAndSdViewModel.getScheduledDep() <= 0) ? null
                        : DateUtil.twoDigitDoubleValueOf(100 * (1 - ((initialDelay
                        .get(elem.getLocationName()).doubleValue() +
                        cancellationAndSdViewModel.getInitialCancellationCount())
                        / cancellationAndSdViewModel.getScheduledDep()))));
                interruptionReportViewModelList.add(interruptionReportViewModel);
            }

        });
        return interruptionReportViewModelList.stream()
                .sorted(Comparator.comparing(InterruptionReportViewModel::getDate))
                .collect(Collectors.toList());
    }

    private Map<Integer, CancellationAndSdViewModel> prepareFlightCycleAndInitialCancellation(
            List<AcCancellations> prepareInitialCancellation, List<TotalCycleByDateViewModel> totalFlightCycles) {

        Map<Integer, CancellationAndSdViewModel> dataMap = new HashMap<>();
        totalFlightCycles.forEach(elem -> {
            Integer month = elem.getMonth();
            if (!dataMap.containsKey(month)) {
                CancellationAndSdViewModel cancellationAndSdViewModel = new CancellationAndSdViewModel();
                cancellationAndSdViewModel.setMonth(month);
                cancellationAndSdViewModel.setScheduledDep(elem.getTotalCycle());
                cancellationAndSdViewModel.setInitialCancellationCount(0);
                dataMap.put(month, cancellationAndSdViewModel);
            }
        });

        prepareInitialCancellation.forEach(elem -> {
            CancellationAndSdViewModel cancellationAndSdViewModel = dataMap.get(elem.getDate().getMonthValue());
            cancellationAndSdViewModel.setInitialCancellationCount(cancellationAndSdViewModel
                    .getInitialCancellationCount() + 1);
        });
        return dataMap;
    }

    private Map<String, Integer> prepareInitialDelay(List<DispatchInterruptionData> dispatchInterruptionData) {
        Map<String, Integer> dataMap = new HashMap<>();
        dispatchInterruptionData.forEach(elem -> {
            String key = elem.getLocationName();
            if (dataMap.containsKey(key)) {
                dataMap.put(key, dataMap.get(key) + 1);
            } else {
                dataMap.put(key, 1);
            }
        });

        return dataMap;
    }

    private List<LocalDate> findDateWiseAircraftDelays(Long aircraftModelId, LocalDate fromDate,
                                                       LocalDate toDate) {

        return aircraftInterruptionsRepository.delayList(aircraftModelId, fromDate, toDate);
    }


    private List<TotalCycleByDateViewModel> findTotalFlightCycles(Long aircraftModelId, LocalDate fromDate,
                                                                  LocalDate toDate) {

        if (fromDate.getYear() == toDate.getYear()) {
            return acStatisticsRepository.findTotalCycleInSameYear(aircraftModelId, fromDate.getYear(),
                    fromDate.getMonthValue(),
                    toDate.getMonthValue());
        } else {
            return acStatisticsRepository.findTotalCycleInDifferentYear(aircraftModelId, fromDate.getYear(),
                    toDate.getYear(),
                    fromDate.getMonthValue(),
                    toDate.getMonthValue());
        }
    }

}
