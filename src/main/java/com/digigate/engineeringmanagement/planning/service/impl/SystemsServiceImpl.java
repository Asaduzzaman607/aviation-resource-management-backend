package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.AlertLevelSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.AlertLevelReport;
import com.digigate.engineeringmanagement.common.payload.response.AlertLevelViewModel;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.entity.AcAlertLevel;
import com.digigate.engineeringmanagement.planning.entity.Defect;
import com.digigate.engineeringmanagement.planning.entity.Systems;
import com.digigate.engineeringmanagement.planning.payload.request.AlertLevelViewSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemReliabilitySearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemsDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AcAlertLevelRepository;
import com.digigate.engineeringmanagement.planning.repository.AcStatisticsRepository;
import com.digigate.engineeringmanagement.planning.repository.DefectRepository;
import com.digigate.engineeringmanagement.planning.repository.SystemsRepository;
import com.digigate.engineeringmanagement.planning.service.AcAlertLevelService;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;
import com.digigate.engineeringmanagement.planning.service.SystemsService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Systems Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class SystemsServiceImpl extends AbstractService<Systems, SystemsDto> implements SystemsService {

    private final AircraftLocationService aircraftLocationService;

    private final SystemsRepository systemsRepository;

    private final AcStatisticsService acStatisticsService;

    private final OperationalReportServiceImpl operationalReportService;

    private final AcStatisticsRepository acStatisticsRepository;

    private final DefectRepository defectRepository;

    private final AcAlertLevelRepository acAlertLevelRepository;

    private final AcAlertLevelService acAlertLevelService;

    private final AircraftModelService aircraftModelService;

    private static final String hyphen = "-";

    public SystemsServiceImpl(AbstractRepository<Systems> repository, AircraftLocationService aircraftLocationService,
                              SystemsRepository systemsRepository, AcStatisticsService acStatisticsService,
                              OperationalReportServiceImpl operationalReportService,
                              AcStatisticsRepository acStatisticsRepository, DefectRepository defectRepository,
                              AcAlertLevelRepository acAlertLevelRepository, AcAlertLevelService acAlertLevelService,
                              AircraftModelService aircraftModelService) {
        super(repository);
        this.aircraftLocationService = aircraftLocationService;
        this.systemsRepository = systemsRepository;
        this.acStatisticsService = acStatisticsService;
        this.operationalReportService = operationalReportService;
        this.acStatisticsRepository = acStatisticsRepository;
        this.defectRepository = defectRepository;
        this.acAlertLevelRepository = acAlertLevelRepository;
        this.acAlertLevelService = acAlertLevelService;
        this.aircraftModelService = aircraftModelService;
    }

    @Override
    public Systems create(SystemsDto systemsDto) {
        if (Objects.nonNull(systemsRepository.getByLocationId(systemsDto.getLocationId()))) {
            throw new EngineeringManagementServerException(
                    ErrorId.LOCATION_ID_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return super.create(systemsDto);
    }

    @Override
    protected SystemsViewModel convertToResponseDto(Systems systems) {
        return SystemsViewModel.builder()
                .locationId(systems.getLocationId())
                .locationName(systems.getAircraftLocation().getName())
                .id(systems.getId())
                .name(systems.getName())
                .isActive(systems.getIsActive())
                .createdAt(systems.getCreatedAt())
                .build();
    }

    @Override
    protected Systems convertToEntity(SystemsDto systemsDto) {
        Systems systems = new Systems();
        systems.setAircraftLocation(aircraftLocationService.findById(systemsDto.getLocationId()));
        systems.setName(systemsDto.getName());
        return systems;
    }

    @Override
    protected Systems updateEntity(SystemsDto dto, Systems entity) {
        if (!dto.getLocationId().equals(entity.getLocationId())) {
            if (Objects.nonNull(systemsRepository.getByLocationId(dto.getLocationId()))) {
                throw new EngineeringManagementServerException(
                        ErrorId.LOCATION_ID_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
        }
        entity.setAircraftLocation(aircraftLocationService.findById(dto.getLocationId()));
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public PageData searchSystems(SystemsSearchDto systemsSearchDto, Pageable pageable) {
        Page<SystemsViewModel> systemsViewModels
                = systemsRepository.searchSystems(systemsSearchDto.getLocationId(),
                systemsSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(systemsViewModels.getContent())
                .totalPages(systemsViewModels.getTotalPages())
                .totalElements(systemsViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public AlertLevelReport alertLevelReport(AlertLevelSearchDto alertLevelSearchDto) {

        DateUtil.isValidFromDate(alertLevelSearchDto.getToDate());
        DateUtil.isValidateDateRangeWith12Months(alertLevelSearchDto.getFromDate(), alertLevelSearchDto.getToDate());

        AlertLevelReport alertLevelModel = new AlertLevelReport();
        List<AlertLevelViewModel> alertLevelViewModelList = new ArrayList<>();

        alertLevelSearchDto.setFromDate(alertLevelSearchDto.getFromDate().minusMonths(alertLevelSearchDto.getMonthRange()));

        List<Defect> defects = defectRepository.findDefectByDate(alertLevelSearchDto.getAircraftModelId(),
                alertLevelSearchDto.getLocationId(), alertLevelSearchDto.getFromDate(),alertLevelSearchDto.getToDate());

        List<TotalHoursByDateViewModel> totalFlightHour = totalFlightHour(alertLevelSearchDto.getFromDate()
                , alertLevelSearchDto.getToDate(), alertLevelSearchDto.getAircraftModelId());

        Map<String, Integer> totalDefectTypeMap = prepareTotalDefectType(defects);

        double meanValue = 0.0;
        double meanBarSquare = 0.0;
        double totalPirep = 0.0;
        int monthRange = alertLevelSearchDto.getMonthRange();
        int size = 0;
        int index = 0;
        double sum = 0.0;
        int start = 0;

        for (TotalHoursByDateViewModel elem : totalFlightHour) {
            AlertLevelViewModel alertLevelViewModel = new AlertLevelViewModel();
            String monthYearKey = elem.getMonth() + hyphen + elem.getYear();
            alertLevelViewModel.setMonth(elem.getMonth());
            alertLevelViewModel.setYear(elem.getYear());
            alertLevelViewModel.setPirepOrMarep(Objects.nonNull(totalDefectTypeMap.get(monthYearKey))
                    ? totalDefectTypeMap.get(monthYearKey) : 0);
            alertLevelViewModel.setAirTime(elem.getTotalHour());
            alertLevelViewModel.setPirepRate(elem.getTotalHour() > 0 ? (DateUtil.twoDigitDoubleValueOf(
                    (alertLevelViewModel.getPirepOrMarep()
                            * 1000) / elem.getTotalHour())) : 0);

            if (index >= monthRange) {
                sum += alertLevelViewModel.getPirepRate();
                sum -= alertLevelViewModelList.get(start).getPirepRate();
                start++;
                alertLevelViewModel.setPirepRateMonthRange(DateUtil.twoDigitDoubleValueOf(sum / monthRange));
                meanValue = meanValue + alertLevelViewModel.getPirepRateMonthRange();
                totalPirep = totalPirep + alertLevelViewModel.getPirepRateMonthRange();
                size++;
            } else {
                sum += alertLevelViewModel.getPirepRate();
                alertLevelViewModel.setPirepRateMonthRange(DateUtil.twoDigitDoubleValueOf(sum / monthRange));
            }

            index++;
            alertLevelViewModelList.add(alertLevelViewModel);
        }

        meanValue = DateUtil.twoDigitDoubleValueOf(meanValue / size);
        int indexValue = 0;

        for (AlertLevelViewModel elem : alertLevelViewModelList) {
            elem.setMean(meanValue);
            elem.setMeanBar(DateUtil.twoDigitDoubleValueOf(elem.getPirepRateMonthRange() - meanValue));
            elem.setMeanSquare(DateUtil.twoDigitDoubleValueOf(Math.pow(elem.getMeanBar(), 2)));
            if (indexValue >= monthRange) {
                meanBarSquare = DateUtil.twoDigitDoubleValueOf(meanBarSquare + elem.getMeanSquare());
            }
            indexValue++;
        }

        alertLevelModel.setMeanXBar(meanValue);
        alertLevelModel.setTotalPirepRateWithThereeMonths(DateUtil.twoDigitDoubleValueOf(totalPirep));
        alertLevelModel.setTotalMeanBarSquare(meanBarSquare);
        alertLevelModel.setSd(DateUtil.twoDigitDoubleValueOf(meanBarSquare / size));
        alertLevelModel.setAlertLevel(DateUtil.twoDigitDoubleValueOf(meanBarSquare
                + (monthRange * alertLevelModel.getSd())));

        alertLevelViewModelList.subList(0, monthRange).clear();

        alertLevelModel.setAlertLevelViewModelList(alertLevelViewModelList);
        createOrUpdateAcAlertLevel(alertLevelViewModelList, alertLevelModel, alertLevelSearchDto);

        return alertLevelModel;
    }

    @Override
    public List<AlertLevelListViewModel> alertLevelListView(AlertLevelViewSearchDto alertLevelViewSearchDto) {

        DateUtil.isValidFromDate(alertLevelViewSearchDto.getToDate());
        DateUtil.isValidateDateRangeWith12Months(alertLevelViewSearchDto.getFromDate(), alertLevelViewSearchDto.getToDate());

        if (alertLevelViewSearchDto.getFromDate().getYear() == alertLevelViewSearchDto.getToDate().getYear()) {
            return acAlertLevelRepository.findAlertLevelInSameYear(alertLevelViewSearchDto.getAircraftModelId(),
                    alertLevelViewSearchDto.getLocationId(),
                    alertLevelViewSearchDto.getFromDate().getYear(),
                    alertLevelViewSearchDto.getFromDate().getMonthValue(),
                    alertLevelViewSearchDto.getToDate().getMonthValue());
        } else {
            return acAlertLevelRepository.findAlertLevelInDifferentYear(alertLevelViewSearchDto.getAircraftModelId(),
                    alertLevelViewSearchDto.getLocationId(),
                    alertLevelViewSearchDto.getFromDate().getYear(),
                    alertLevelViewSearchDto.getToDate().getYear(),
                    alertLevelViewSearchDto.getFromDate().getMonthValue(),
                    alertLevelViewSearchDto.getToDate().getMonthValue());
        }

    }

    @Override
    public List<LocationViewModel> createSystemReliability(SystemReliabilitySearchDto systemReliabilitySearchDto) {

        DateUtil.isValidateDateRangeWith1Months(systemReliabilitySearchDto.getFromDate(), systemReliabilitySearchDto.getToDate());
        List<LocationViewModel> locationViewModelList = new ArrayList<>();
        List<AlertLevelByLocation> alertLevelByLocationId = acAlertLevelRepository.findAlertLevelByLocationId(
                systemReliabilitySearchDto.getAircraftModelId(), systemReliabilitySearchDto.getFromDate().getYear(),
                systemReliabilitySearchDto.getFromDate().getMonthValue());

        List<AircraftDefectListViewModel> aircraftDefectListViewModelList = acAlertLevelRepository
                .aircraftDefectListViewModelList(systemReliabilitySearchDto.getAircraftModelId(),
                        systemReliabilitySearchDto.getFromDate(),
                        systemReliabilitySearchDto.getToDate());

        List<TotalHoursByDateViewModel> totalFlightHour = totalFlightHour(systemReliabilitySearchDto.getFromDate()
                , systemReliabilitySearchDto.getToDate(), systemReliabilitySearchDto.getAircraftModelId());

        Map<Integer, Double> totalFlightHourMap = new HashMap<>();

        for (TotalHoursByDateViewModel totalHoursByDateViewModel : totalFlightHour) {
            totalFlightHourMap.put(totalHoursByDateViewModel.getMonth(), totalHoursByDateViewModel.getTotalHour());
        }

        Map<Long, Double> alertLevelMap = new HashMap<>();

        for (AlertLevelByLocation alertLevel : alertLevelByLocationId) {
            alertLevelMap.put(alertLevel.getLocationId(), alertLevel.getAlertLevel());
        }

        aircraftDefectListViewModelList.forEach(elem -> {
            if (!alertLevelMap.containsKey(elem.getLocationId())) {
                alertLevelMap.put(elem.getLocationId(), 0.0);
                AlertLevelByLocation alertLevelByLocation = new AlertLevelByLocation();
                alertLevelByLocation.setLocationId(elem.getLocationId());
                alertLevelByLocation.setAlertLevel(0.0);
                alertLevelByLocation.setLocationName(elem.getLocationName());
                alertLevelByLocation.setSystemName(elem.getSystemName());
                alertLevelByLocationId.add(alertLevelByLocation);
            }
        });

        Map<Long, List<AircraftDefectListViewModel>> aircraftsByLocationId = aircraftDefectListViewModelList.stream()
                .collect(Collectors.groupingBy(AircraftDefectListViewModel::getLocationId));

        alertLevelByLocationId.forEach(elem -> {
            if (aircraftsByLocationId.containsKey(elem.getLocationId())) {
                LocationViewModel locationViewModel = new LocationViewModel();
                locationViewModel.setLocationId(elem.getLocationId());
                locationViewModel.setLocationName(elem.getLocationName());
                locationViewModel.setAlertLevel(elem.getAlertLevel());
                locationViewModel.setSystemName(elem.getSystemName());
                locationViewModel.setAircraftDefectListViewModelList(aircraftsByLocationId.get(elem.getLocationId()));
                Map<Long, Long> defectCountByLocationId = aircraftsByLocationId.values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.groupingBy(AircraftDefectListViewModel::getLocationId,
                                Collectors.summingLong(AircraftDefectListViewModel::getDefectCount)));
                locationViewModel.setTotal(defectCountByLocationId.get(elem.getLocationId()));

                double result = (locationViewModel.getTotal() * 1000) / totalFlightHourMap.get(systemReliabilitySearchDto
                        .getFromDate().getMonthValue());
                if (Double.isInfinite(result)) {
                    locationViewModel.setRate(0.0);
                } else {
                    locationViewModel.setRate(DateUtil.twoDigitDoubleValueOf(result));
                }

                locationViewModelList.add(locationViewModel);
            }

        });

        return locationViewModelList;
    }

    private void createOrUpdateAcAlertLevel(List<AlertLevelViewModel> alertLevelViewModelList
            , AlertLevelReport alertLevelModel, AlertLevelSearchDto alertLevelSearchDto) {

        List<AcAlertLevel> acAlertLevelList = new ArrayList<>();

        alertLevelViewModelList.forEach(elem -> {

            AcAlertLevel acAlertLevel = acAlertLevelRepository.findByMonthAndYear(elem.getMonth(), elem.getYear(),
                    alertLevelSearchDto.getAircraftModelId(), alertLevelSearchDto.getLocationId());

            if (ObjectUtils.isEmpty(acAlertLevel)) {

                AcAlertLevel acAlertLevelData = new AcAlertLevel();
                acAlertLevelData.setAircraftModel(aircraftModelService.findById(alertLevelSearchDto.getAircraftModelId()));
                acAlertLevelData.setAircraftLocation(aircraftLocationService.findById(alertLevelSearchDto.getLocationId()));
                acAlertLevelData.setAlertLevel(alertLevelModel.getAlertLevel());
                acAlertLevelData.setYear(elem.getYear());
                acAlertLevelData.setMonth(elem.getMonth());

                acAlertLevelList.add(acAlertLevelData);
            } else {
                acAlertLevel.setAlertLevel(alertLevelModel.getAlertLevel());
                acAlertLevelList.add(acAlertLevel);
            }
        });
        acAlertLevelService.saveItem(acAlertLevelList);
    }

    private Map<String, Integer> prepareTotalDefectType(List<Defect> defects) {
        Map<String, Integer> dataMap = new HashMap<>();
        defects.forEach(defect -> {
            String monthKey = defect.getDate().getMonthValue() + hyphen + defect.getDate().getYear();
            if (defect.getDefectType().isPresent()) {
                if (dataMap.containsKey(monthKey)) {
                    dataMap.put(monthKey, dataMap.get(monthKey) + 1);
                } else {
                    dataMap.put(monthKey, 1);
                }
            }
        });
        return dataMap;
    }

    private List<TotalHoursByDateViewModel> totalFlightHour(LocalDate fromDate, LocalDate toDate, Long aircraftModelId) {

        Integer startYear = fromDate.getYear();
        Integer endYear = toDate.getYear();
        Integer startMonth = fromDate.getMonth().getValue();
        Integer endMonth = toDate.getMonth().getValue();

        if (acStatisticsService.checkIsAlreadyExist(aircraftModelId, startMonth, startYear,
                endMonth, endYear).equals(false)) {
            operationalReportService.createAcStatData(aircraftModelId, fromDate, toDate);
        }
        return findTotalFlightHours(aircraftModelId, fromDate, toDate);
    }

    private List<TotalHoursByDateViewModel> findTotalFlightHours(Long aircraftModelId, LocalDate fromDate,
                                                                 LocalDate toDate) {
        if (fromDate.getYear() == toDate.getYear()) {
            return acStatisticsRepository.findTotalHoursInSameYear(aircraftModelId, fromDate.getYear(),
                    fromDate.getMonthValue(),
                    toDate.getMonthValue());
        } else {
            return acStatisticsRepository.findTotalHoursInDifferentYear(aircraftModelId, fromDate.getYear(),
                    toDate.getYear(),
                    fromDate.getMonthValue(),
                    toDate.getMonthValue());
        }
    }

}
