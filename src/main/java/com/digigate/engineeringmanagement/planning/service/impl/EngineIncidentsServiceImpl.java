package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.EngineIncidentsEnum;
import com.digigate.engineeringmanagement.planning.entity.EngineIncidents;
import com.digigate.engineeringmanagement.planning.payload.request.EngineIncidentsDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AcStatisticsRepository;
import com.digigate.engineeringmanagement.planning.repository.EngineIncidentsRepository;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import com.digigate.engineeringmanagement.planning.service.EngineIncidentsService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Engine Incidents Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class EngineIncidentsServiceImpl extends AbstractService<EngineIncidents, EngineIncidentsDto>
        implements EngineIncidentsService {

    private final AircraftModelService aircraftModelService;

    private final EngineIncidentsRepository engineIncidentsRepository;

    private final AcStatisticsService acStatisticsService;

    private final OperationalReportServiceImpl operationalReportService;

    private final AcStatisticsRepository acStatisticsRepository;

    private final AircraftRepository aircraftRepository;

    public EngineIncidentsServiceImpl(AbstractRepository<EngineIncidents> repository,
                                      AircraftModelService aircraftModelService,
                                      EngineIncidentsRepository engineIncidentsRepository,
                                      AcStatisticsService acStatisticsService,
                                      OperationalReportServiceImpl operationalReportService,
                                      AcStatisticsRepository acStatisticsRepository, AircraftRepository aircraftRepository) {
        super(repository);
        this.aircraftModelService = aircraftModelService;
        this.engineIncidentsRepository = engineIncidentsRepository;
        this.acStatisticsService = acStatisticsService;
        this.operationalReportService = operationalReportService;
        this.acStatisticsRepository = acStatisticsRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @Override
    protected EngineIncidentsViewModel convertToResponseDto(EngineIncidents engineIncidents) {
        return EngineIncidentsViewModel.builder()
                .id(engineIncidents.getId())
                .aircraftModelId(engineIncidents.getAircraftModelId())
                .aircraftModelName(engineIncidents.getAircraftModel().getAircraftModelName())
                .engineIncidentsEnum(engineIncidents.getEngineIncidentsEnum())
                .date(engineIncidents.getDate())
                .isActive(engineIncidents.getIsActive())
                .build();
    }

    @Override
    protected EngineIncidents convertToEntity(EngineIncidentsDto engineIncidentsDto) {
        return prepareEntity(engineIncidentsDto, new EngineIncidents());
    }

    private EngineIncidents prepareEntity(EngineIncidentsDto engineIncidentsDto, EngineIncidents engineIncidents) {
        engineIncidents.setAircraftModel(aircraftModelService.findById(engineIncidentsDto.getAircraftModelId()));
        engineIncidents.setEngineIncidentsEnum(engineIncidentsDto.getEngineIncidentsEnum());
        engineIncidents.setDate(engineIncidentsDto.getDate());
        return engineIncidents;
    }

    @Override
    public Boolean validateClientData(EngineIncidentsDto engineIncidentsDto, Long id) {

        LocalDate currentDate = DateUtil.getCurrentUTCDate();

        if (engineIncidentsDto.getDate().isAfter(currentDate)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_INCIDENTS_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return true;
    }

    @Override
    protected EngineIncidents updateEntity(EngineIncidentsDto dto, EngineIncidents entity) {
        return prepareEntity(dto, entity);
    }

    @Override
    public PageData searchEngineIncidents(EngineIncidentsSearchDto engineIncidentsSearchDto, Pageable pageable) {

        Page<EngineIncidentsViewModel> engineIncidentsViewModels
                = engineIncidentsRepository.searchEngineIncidents(engineIncidentsSearchDto.getAircraftModelId(),
                engineIncidentsSearchDto.getFromDate(), engineIncidentsSearchDto.getToDate(),
                engineIncidentsSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(engineIncidentsViewModels.getContent())
                .totalPages(engineIncidentsViewModels.getTotalPages())
                .totalElements(engineIncidentsViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public EngineIncidentsReportViewModel engineIncidentsReport(EngineIncidentsSearchDto engineIncidentsSearchDto) {

        List<EngineInFlightShutDownsViewModel> engineInFlightShutDownsViewModelList = new ArrayList<>();
        List<EngineUnscheduledRemovalsViewModel> engineUnscheduledRemovalsViewModelList = new ArrayList<>();
        EngineIncidentsReportViewModel engineIncidentsReportViewModel = new EngineIncidentsReportViewModel();

        List<TotalHoursByDateViewModel> totalFlightHour = totalFlightHour(engineIncidentsSearchDto);

        List<EngineIncidents> engineIncidentsList = engineIncidentsRepository.engineIncidentsList(engineIncidentsSearchDto
                .getAircraftModelId(), engineIncidentsSearchDto.getFromDate(), engineIncidentsSearchDto.getToDate());

        List<Aircraft> aircraftList = aircraftRepository.findAllByAircraftModelIdAndIsActive(engineIncidentsSearchDto
                .getAircraftModelId(),true);

       for(Aircraft aircraft : aircraftList){
           if(ObjectUtils.isNotEmpty(aircraft.getEngineType())){
               engineIncidentsReportViewModel.setEngineType(aircraft.getEngineType());
               break;
           }
       }

        Map<Integer, EngineInFlightShutDownsViewModel> engineInFlightShutDownsViewModelHashMap = new HashMap<>();
        Map<Integer, EngineUnscheduledRemovalsViewModel> engineUnscheduledRemovalsViewModelHashMap = new HashMap<>();
        prepareEngineIncidentsStatisticsReport(engineIncidentsList, totalFlightHour,
                engineInFlightShutDownsViewModelHashMap, engineUnscheduledRemovalsViewModelHashMap);

        totalFlightHour.forEach(elem -> {
            EngineInFlightShutDownsViewModel engineInFlightShutDownsViewModel = engineInFlightShutDownsViewModelHashMap
                    .get(elem.getMonth());
            engineInFlightShutDownsViewModel.setMonth(elem.getMonth());
            engineInFlightShutDownsViewModel.setYear(elem.getYear());
            engineInFlightShutDownsViewModel.setNoOfIfsd(engineInFlightShutDownsViewModel.getNoOfIfsd());
            engineInFlightShutDownsViewModel.setRateByHours(elem.getTotalHour() > 0
                    ? DateUtil.twoDigitDoubleValueOf((engineInFlightShutDownsViewModel.getNoOfIfsd()
                    / elem.getTotalHour()) * 1000) : null);
            engineInFlightShutDownsViewModelList.add(engineInFlightShutDownsViewModel);

            EngineUnscheduledRemovalsViewModel engineUnscheduledRemovalsViewModel = engineUnscheduledRemovalsViewModelHashMap
                    .get(elem.getMonth());
            engineUnscheduledRemovalsViewModel.setMonth(elem.getMonth());
            engineUnscheduledRemovalsViewModel.setYear(elem.getYear());
            engineUnscheduledRemovalsViewModel.setNoOfRemv(engineUnscheduledRemovalsViewModel.getNoOfRemv());
            engineUnscheduledRemovalsViewModel.setRateByHours(elem.getTotalHour() > 0
                    ? DateUtil.twoDigitDoubleValueOf((engineUnscheduledRemovalsViewModel.getNoOfRemv()
                    / elem.getTotalHour()) * 1000) : null);
            engineUnscheduledRemovalsViewModelList.add(engineUnscheduledRemovalsViewModel);
        });
        engineIncidentsReportViewModel.setEngineInFlightShutDownsViewModelList(engineInFlightShutDownsViewModelList);
        engineIncidentsReportViewModel.setEngineUnscheduledRemovalsViewModelList(engineUnscheduledRemovalsViewModelList);
        return engineIncidentsReportViewModel;
    }

    private void prepareEngineIncidentsStatisticsReport(List<EngineIncidents> engineIncidentsList,
                                                        List<TotalHoursByDateViewModel> totalFlightHour,
                                                        Map<Integer, EngineInFlightShutDownsViewModel>
                                                                engineInFlightShutDownsViewModelHashMap, Map<Integer,
            EngineUnscheduledRemovalsViewModel> engineUnscheduledRemovalsViewModelHashMap) {

        totalFlightHour.forEach(elem -> {
            Integer month = elem.getMonth();
            EngineInFlightShutDownsViewModel engineInFlightShutDownsViewModel = new EngineInFlightShutDownsViewModel();
            EngineUnscheduledRemovalsViewModel engineUnscheduledRemovalsViewModel
                    = new EngineUnscheduledRemovalsViewModel();

            engineInFlightShutDownsViewModel.setNoOfIfsd(0);

            engineUnscheduledRemovalsViewModel.setNoOfRemv(0);

            engineInFlightShutDownsViewModelHashMap.put(month, engineInFlightShutDownsViewModel);
            engineUnscheduledRemovalsViewModelHashMap.put(month, engineUnscheduledRemovalsViewModel);
        });

        engineIncidentsList.forEach(elem -> {

            EngineInFlightShutDownsViewModel engineInFlightShutDownsViewModels = engineInFlightShutDownsViewModelHashMap
                    .get(elem.getDate().getMonthValue());
            EngineUnscheduledRemovalsViewModel engineUnscheduledRemovalsViewModels
                    = engineUnscheduledRemovalsViewModelHashMap.get(elem.getDate().getMonthValue());

            if (elem.getEngineIncidentsEnum().equals(EngineIncidentsEnum.ENGINE_IN_FLIGHT_SHUT_DOWNS)) {
                engineInFlightShutDownsViewModels.setNoOfIfsd(engineInFlightShutDownsViewModels.getNoOfIfsd() + 1);
            }
            if (elem.getEngineIncidentsEnum().equals(EngineIncidentsEnum.ENGINES_UNSCHEDULED_REMOVALS)) {
                engineUnscheduledRemovalsViewModels.setNoOfRemv(engineUnscheduledRemovalsViewModels.getNoOfRemv() + 1);
            }

        });

    }

    private List<TotalHoursByDateViewModel> totalFlightHour(EngineIncidentsSearchDto engineIncidentsSearchDto) {
        DateUtil.isValidFromDate(engineIncidentsSearchDto.getToDate());
        DateUtil.isValidateDateRangeWith12Months(engineIncidentsSearchDto.getFromDate(), engineIncidentsSearchDto.getToDate());

        Integer startYear = engineIncidentsSearchDto.getFromDate().getYear();
        Integer endYear = engineIncidentsSearchDto.getToDate().getYear();
        Integer startMonth = engineIncidentsSearchDto.getFromDate().getMonth().getValue();
        Integer endMonth = engineIncidentsSearchDto.getToDate().getMonth().getValue();

        if (acStatisticsService.checkIsAlreadyExist(engineIncidentsSearchDto.getAircraftModelId(), startMonth, startYear,
                endMonth, endYear).equals(false)) {
            operationalReportService.createAcStatData(engineIncidentsSearchDto.getAircraftModelId(), engineIncidentsSearchDto.getFromDate(),
                    engineIncidentsSearchDto.getToDate());
        }
        return findTotalFlightHours(engineIncidentsSearchDto.getAircraftModelId(),
                engineIncidentsSearchDto.getFromDate(), engineIncidentsSearchDto.getToDate());
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
