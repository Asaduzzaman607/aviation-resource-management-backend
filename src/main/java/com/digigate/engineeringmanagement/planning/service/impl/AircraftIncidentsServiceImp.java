package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.ClassificationTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.IncidentTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AircraftIncidents;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftIncidentsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.IncidentsStatisticsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AcStatisticsRepository;
import com.digigate.engineeringmanagement.planning.repository.AircraftIncidentsRepository;
import com.digigate.engineeringmanagement.planning.service.AcStatisticsService;
import com.digigate.engineeringmanagement.planning.service.AircraftIncidentsService;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Aircraft Incidents Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class AircraftIncidentsServiceImp extends AbstractService<AircraftIncidents, AircraftIncidentsDto>
        implements AircraftIncidentsService {

    private final AircraftIncidentsRepository aircraftIncidentsRepository;

    private final AircraftService aircraftService;

    private final AcStatisticsService acStatisticsService;

    private final OperationalReportServiceImpl operationalReportService;

    private final AcStatisticsRepository acStatisticsRepository;

    public AircraftIncidentsServiceImp(AbstractRepository<AircraftIncidents> repository,
                                       AircraftIncidentsRepository aircraftIncidentsRepository,
                                       AircraftService aircraftService, AcStatisticsService acStatisticsService,
                                       OperationalReportServiceImpl operationalReportService,
                                       AcStatisticsRepository acStatisticsRepository) {
        super(repository);
        this.aircraftIncidentsRepository = aircraftIncidentsRepository;
        this.aircraftService = aircraftService;
        this.acStatisticsService = acStatisticsService;
        this.operationalReportService = operationalReportService;
        this.acStatisticsRepository = acStatisticsRepository;
    }


    @Override
    protected AircraftIncidentsViewModel convertToResponseDto(AircraftIncidents aircraftIncidents) {
        return AircraftIncidentsViewModel.builder()
                .id(aircraftIncidents.getId())
                .aircraftId(aircraftIncidents.getAircraftId())
                .aircraftName(aircraftIncidents.getAircraft().getAircraftName())
                .incidentDesc(aircraftIncidents.getIncidentDesc())
                .incidentTypeEnum(aircraftIncidents.getIncidentTypeEnum())
                .date(aircraftIncidents.getDate())
                .actionDesc(aircraftIncidents.getActionDesc())
                .referenceAtl(aircraftIncidents.getReferenceAtl())
                .classificationTypeEnum(aircraftIncidents.getClassificationTypeEnum())
                .seqNo(aircraftIncidents.getSeqNo())
                .remarks(aircraftIncidents.getRemarks())
                .createdAt(aircraftIncidents.getCreatedAt())
                .isActive(aircraftIncidents.getIsActive())
                .build();
    }

    @Override
    protected AircraftIncidents convertToEntity(AircraftIncidentsDto aircraftIncidentsDto) {
        return mapToEntity(aircraftIncidentsDto, new AircraftIncidents());
    }

    private AircraftIncidents mapToEntity(AircraftIncidentsDto aircraftIncidentsDto,
                                          AircraftIncidents aircraftIncidents) {

        aircraftIncidents.setAircraft(aircraftService.findById(aircraftIncidentsDto.getAircraftId()));
        aircraftIncidents.setDate(aircraftIncidentsDto.getDate());
        aircraftIncidents.setIncidentDesc(aircraftIncidentsDto.getIncidentDesc());
        aircraftIncidents.setActionDesc(aircraftIncidentsDto.getActionDesc());
        aircraftIncidents.setIncidentTypeEnum(aircraftIncidentsDto.getIncidentTypeEnum());
        aircraftIncidents.setClassificationTypeEnum(aircraftIncidentsDto.getClassificationTypeEnum());
        aircraftIncidents.setReferenceAtl(aircraftIncidentsDto.getReferenceAtl());
        aircraftIncidents.setSeqNo(aircraftIncidentsDto.getSeqNo());
        aircraftIncidents.setRemarks(aircraftIncidentsDto.getRemarks());

        return aircraftIncidents;
    }

    @Override
    protected AircraftIncidents updateEntity(AircraftIncidentsDto dto, AircraftIncidents entity) {
        return mapToEntity(dto, entity);
    }

    @Override
    public Boolean validateClientData(AircraftIncidentsDto aircraftIncidentsDto, Long id) {

        LocalDate currentDate = DateUtil.getCurrentUTCDate();

        if (aircraftIncidentsDto.getDate().isAfter(currentDate)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_INCIDENTS_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (aircraftIncidentsDto.getIncidentTypeEnum().equals(IncidentTypeEnum.TECHNICAL_INCIDENTS) &&
                !ClassificationTypeEnum.technicalEnumSet.contains(aircraftIncidentsDto.getClassificationTypeEnum())) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_INCIDENTS_CLASSIFICATION, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (aircraftIncidentsDto.getIncidentTypeEnum().equals(IncidentTypeEnum.NON_TECHNICAL_INCIDENTS) &&
                !ClassificationTypeEnum.nonTechnicalEnumSet.contains(aircraftIncidentsDto.getClassificationTypeEnum())) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_INCIDENTS_CLASSIFICATION, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return true;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        super.updateActiveStatus(id, isActive);
    }

    @Override
    public PageData searchAircraftIncidents(AircraftIncidentsSearchDto aircraftIncidentsSearchDto, Pageable pageable) {
        Page<AircraftIncidentsViewModel> aircraftIncidentsViewModels
                = aircraftIncidentsRepository.searchAircraftIncidents(aircraftIncidentsSearchDto.getAircraftId(),
                aircraftIncidentsSearchDto.getStartDate(), aircraftIncidentsSearchDto.getEndDate(),
                aircraftIncidentsSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(aircraftIncidentsViewModels.getContent())
                .totalPages(aircraftIncidentsViewModels.getTotalPages())
                .totalElements(aircraftIncidentsViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public IncidentsStatisticsViewModel incidentStatisticsReport(IncidentsStatisticsSearchDto searchDto) {

        List<TechnicalViewModel> technicalViewModelList = new ArrayList<>();
        List<NonTechnicalViewModel> nonTechnicalViewModelList = new ArrayList<>();
        IncidentsStatisticsViewModel incidentsStatisticsViewModel = new IncidentsStatisticsViewModel();
        List<TotalHoursByDateViewModel> totalFlightHour = totalFlightHour(searchDto);

        List<AircraftIncidents> incidentList = aircraftIncidentsRepository.getIncidentList(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        Map<Integer, TechnicalViewModel> technicalViewModelMap = new HashMap<>();
        Map<Integer, NonTechnicalViewModel> nonTechnicalViewModelMap = new HashMap<>();
        prepareIncidentsStatisticsReport(incidentList, totalFlightHour, technicalViewModelMap, nonTechnicalViewModelMap);

        totalFlightHour.forEach(elem -> {

            TechnicalViewModel technicalViewModel = technicalViewModelMap.get(elem.getMonth());
            technicalViewModel.setMonth(elem.getMonth());
            technicalViewModel.setYear(elem.getYear());
            technicalViewModel.setOtherReportableDefect(technicalViewModel.getOtherReportableDefect());
            technicalViewModel.setFuelDumping(technicalViewModel.getFuelDumping());
            technicalViewModel.setFireWarningLight(technicalViewModel.getFireWarningLight());
            technicalViewModel.setReturnBeforeTakeOff(technicalViewModel.getReturnBeforeTakeOff());
            technicalViewModel.setReturnAfterTakeOff(technicalViewModel.getReturnAfterTakeOff());
            technicalViewModel.setEngineShutDownInFlight(technicalViewModel.getEngineShutDownInFlight());
            technicalViewModel.setTakeOffAbandoned(technicalViewModel.getTakeOffAbandoned());
            technicalViewModel.setTechnicalTotal(technicalViewModel.getTechnicalTotal());
            technicalViewModel.setTechnicalRate(elem.getTotalHour() > 0 ? DateUtil.twoDigitDoubleValueOf((technicalViewModel.getTechnicalTotal()
                    / elem.getTotalHour()) * 1000) : null);
            technicalViewModelList.add(technicalViewModel);

            NonTechnicalViewModel nonTechnicalViewModel = nonTechnicalViewModelMap.get(elem.getMonth());
            nonTechnicalViewModel.setMonth(elem.getMonth());
            nonTechnicalViewModel.setYear(elem.getYear());
            nonTechnicalViewModel.setOther(nonTechnicalViewModel.getOther());
            nonTechnicalViewModel.setTurbulence(nonTechnicalViewModel.getTurbulence());
            nonTechnicalViewModel.setBirdStrike(nonTechnicalViewModel.getBirdStrike());
            nonTechnicalViewModel.setLightningStrike(nonTechnicalViewModel.getLightningStrike());
            nonTechnicalViewModel.setAcDamagedByGroundEqpt(nonTechnicalViewModel.getAcDamagedByGroundEqpt());
            nonTechnicalViewModel.setForeignObjectDamage(nonTechnicalViewModel.getForeignObjectDamage());
            nonTechnicalViewModel.setNonTechnicalTotal(nonTechnicalViewModel.getNonTechnicalTotal());
            nonTechnicalViewModel.setNonTechnicalRate(elem.getTotalHour() > 0
                    ? DateUtil.twoDigitDoubleValueOf((nonTechnicalViewModel.getNonTechnicalTotal()
                    / elem.getTotalHour()) * 1000) : null);
            nonTechnicalViewModelList.add(nonTechnicalViewModel);

        });
        incidentsStatisticsViewModel.setTechnicalViewModel(technicalViewModelList);
        incidentsStatisticsViewModel.setNonTechnicalViewModel(nonTechnicalViewModelList);
        return incidentsStatisticsViewModel;
    }

    @Override
    public TechIncViewModel techIncReport(IncidentsStatisticsSearchDto searchDto) {

        List<AircraftIncidents> incidentList = aircraftIncidentsRepository.getIncidentList(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());

        TechIncViewModel techIncViewModel = new TechIncViewModel();
        List<TechnicalIncidentsViewModel> technicalIncidentsViewModelList = new ArrayList<>();
        List<NonTechnicalIncidentsViewModel> nonTechnicalIncidentsViewModelList = new ArrayList<>();

        incidentList.forEach(elem -> {

            if (elem.getIncidentTypeEnum().equals(IncidentTypeEnum.TECHNICAL_INCIDENTS)) {
                TechnicalIncidentsViewModel technicalIncidentsViewModel = new TechnicalIncidentsViewModel();
                technicalIncidentsViewModel.setAircraftName(elem.getAircraft().getAircraftName());
                technicalIncidentsViewModel.setDate(elem.getDate());
                technicalIncidentsViewModel.setIncidentDes(elem.getIncidentDesc());
                technicalIncidentsViewModel.setReferenceAtl(elem.getReferenceAtl());
                technicalIncidentsViewModel.setActionDes(elem.getActionDesc());
                technicalIncidentsViewModel.setSeqNo(elem.getSeqNo());
                technicalIncidentsViewModel.setRemarks(elem.getRemarks());
                technicalIncidentsViewModelList.add(technicalIncidentsViewModel);
            } else if (elem.getIncidentTypeEnum().equals(IncidentTypeEnum.NON_TECHNICAL_INCIDENTS)) {
                NonTechnicalIncidentsViewModel nonTechnicalIncidentsViewModel = new NonTechnicalIncidentsViewModel();
                nonTechnicalIncidentsViewModel.setAircraftName(elem.getAircraft().getAircraftName());
                nonTechnicalIncidentsViewModel.setDate(elem.getDate());
                nonTechnicalIncidentsViewModel.setIncidentDes(elem.getIncidentDesc());
                nonTechnicalIncidentsViewModel.setReferenceAtl(elem.getReferenceAtl());
                nonTechnicalIncidentsViewModel.setActionDes(elem.getActionDesc());
                nonTechnicalIncidentsViewModel.setSeqNo(elem.getSeqNo());
                nonTechnicalIncidentsViewModel.setRemarks(elem.getRemarks());
                nonTechnicalIncidentsViewModelList.add(nonTechnicalIncidentsViewModel);
            }
        });

        techIncViewModel.setTechnicalIncidentsViewModelList(technicalIncidentsViewModelList);
        techIncViewModel.setNonTechnicalIncidentsViewModelList(nonTechnicalIncidentsViewModelList);

        return techIncViewModel;
    }

    private void prepareIncidentsStatisticsReport(List<AircraftIncidents> incidentList,
                                                  List<TotalHoursByDateViewModel> totalFlightHour,
                                                  Map<Integer, TechnicalViewModel> technicalViewModelMap,
                                                  Map<Integer, NonTechnicalViewModel> nonTechnicalViewModelMap) {

        totalFlightHour.forEach(elem -> {
            Integer month = elem.getMonth();
            NonTechnicalViewModel nonTechnicalViewModel = new NonTechnicalViewModel();
            TechnicalViewModel technicalViewModel = new TechnicalViewModel();

            nonTechnicalViewModel.setOther(0);
            nonTechnicalViewModel.setTurbulence(0);
            nonTechnicalViewModel.setBirdStrike(0);
            nonTechnicalViewModel.setLightningStrike(0);
            nonTechnicalViewModel.setAcDamagedByGroundEqpt(0);
            nonTechnicalViewModel.setForeignObjectDamage(0);
            nonTechnicalViewModel.setNonTechnicalTotal(0);
            nonTechnicalViewModel.setMonth(elem.getMonth());
            nonTechnicalViewModel.setYear(elem.getYear());

            technicalViewModel.setFuelDumping(0);
            technicalViewModel.setOtherReportableDefect(0);
            technicalViewModel.setEngineShutDownInFlight(0);
            technicalViewModel.setFireWarningLight(0);
            technicalViewModel.setReturnBeforeTakeOff(0);
            technicalViewModel.setReturnAfterTakeOff(0);
            technicalViewModel.setTakeOffAbandoned(0);
            technicalViewModel.setTechnicalTotal(0);
            technicalViewModel.setMonth(elem.getMonth());
            technicalViewModel.setYear(elem.getYear());

            technicalViewModelMap.put(month, technicalViewModel);
            nonTechnicalViewModelMap.put(month, nonTechnicalViewModel);
        });

        incidentList.forEach(el -> {

            NonTechnicalViewModel nonTechnicalViewModels = nonTechnicalViewModelMap.get(el.getDate().getMonthValue());
            TechnicalViewModel technicalViewModels = technicalViewModelMap.get(el.getDate().getMonthValue());

            if (el.getIncidentTypeEnum().equals(IncidentTypeEnum.TECHNICAL_INCIDENTS)) {

                switch (el.getClassificationTypeEnum()) {

                    case TAKE_OFF_ABANDONED:
                        technicalViewModels.setTakeOffAbandoned(technicalViewModels.getTakeOffAbandoned() + 1);
                    case OTHER_REPORTABLE_DEFECT:
                        technicalViewModels.setOtherReportableDefect(technicalViewModels.getOtherReportableDefect() + 1);
                    case FUEL_DUMPING:
                        technicalViewModels.setFuelDumping(technicalViewModels.getFuelDumping() + 1);
                    case ENGINE_SHUT_DOWN_IN_FLIGHT:
                        technicalViewModels.setEngineShutDownInFlight(technicalViewModels.getEngineShutDownInFlight() + 1);
                    case FIRE_WARNING_LIGHT:
                        technicalViewModels.setFireWarningLight(technicalViewModels.getFireWarningLight() + 1);
                    case RETURNS_AFTER_TAKE_OFF:
                        technicalViewModels.setReturnAfterTakeOff(technicalViewModels.getReturnAfterTakeOff() + 1);
                    case RETURNS_BEFORE_TAKE_OFF:
                        technicalViewModels.setReturnBeforeTakeOff(technicalViewModels.getReturnBeforeTakeOff() + 1);

                }
                technicalViewModels.setTechnicalTotal(technicalViewModels.getTakeOffAbandoned()
                        + technicalViewModels.getReturnAfterTakeOff() + technicalViewModels.getEngineShutDownInFlight()
                        + technicalViewModels.getReturnBeforeTakeOff() + technicalViewModels.getFuelDumping()
                        + technicalViewModels.getOtherReportableDefect() + technicalViewModels.getFireWarningLight());

            } else if (el.getIncidentTypeEnum().equals(IncidentTypeEnum.NON_TECHNICAL_INCIDENTS)) {

                switch (el.getClassificationTypeEnum()) {
                    case OTHER:
                        nonTechnicalViewModels.setOther(nonTechnicalViewModels.getOther() + 1);
                    case BIRD_STRIKE_JACKAL_HIT:
                        nonTechnicalViewModels.setBirdStrike(nonTechnicalViewModels.getBirdStrike() + 1);
                    case AC_DAMAGED_BY_GROUND_EQPT:
                        nonTechnicalViewModels.setAcDamagedByGroundEqpt(nonTechnicalViewModels
                                .getAcDamagedByGroundEqpt() + 1);
                    case FOREIGN_OBJECT_DAMAGE:
                        nonTechnicalViewModels.setForeignObjectDamage(nonTechnicalViewModels
                                .getForeignObjectDamage() + 1);
                    case LIGHTNING_STRIKE:
                        nonTechnicalViewModels.setLightningStrike(nonTechnicalViewModels.getLightningStrike() + 1);
                    case TURBULENCE:
                        nonTechnicalViewModels.setTurbulence(nonTechnicalViewModels.getTurbulence() + 1);

                }

                nonTechnicalViewModels.setNonTechnicalTotal(nonTechnicalViewModels.getOther()
                        + nonTechnicalViewModels.getTurbulence()
                        + nonTechnicalViewModels.getBirdStrike() + nonTechnicalViewModels.getAcDamagedByGroundEqpt()
                        + nonTechnicalViewModels.getForeignObjectDamage()
                        + nonTechnicalViewModels.getLightningStrike());
            }

        });
    }

    private List<TotalHoursByDateViewModel> totalFlightHour(IncidentsStatisticsSearchDto searchDto) {
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
        return findTotalFlightHours(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate());
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
