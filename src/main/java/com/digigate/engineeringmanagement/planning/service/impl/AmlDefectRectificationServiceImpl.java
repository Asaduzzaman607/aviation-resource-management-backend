package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.planning.constant.MelType;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationModelView;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import com.digigate.engineeringmanagement.planning.repository.AmlDefectRectificationRepository;
import com.digigate.engineeringmanagement.planning.service.*;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AMLDefectRectification service implementation
 *
 * @author Asifuf Rahman
 */
@Service
public class AmlDefectRectificationServiceImpl implements AmlDefectRectificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final IService<Signature, SignatureDto> signatureService;
    private final AirportService airportService;
    private final IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto> aircraftMaintenanceLogService;
    private final AmlDefectRectificationRepository amlDefectRectificationRepository;
    private final MelIService melIService;
    private final NonRoutineCardIService nonRoutineCardIService;
    private final AircraftService aircraftService;
    private final PartService partService;

    private final AircraftLocationService aircraftLocationService;


    private AmlDefectRectificationReportViewModel amlDefectRectificationReportViewModel;

    /**
     * Autowired constructor*
     *
     * @param amlDefectRectificationRepository {@link AmlDefectRectificationRepository}
     * @param signatureService                 {@link SignatureService}
     * @param airportService                   {@link AirportService}
     * @param aircraftMaintenanceLogService    {@link AircraftMaintenanceLogServiceImpl}
     * @param melIService                      {@link MelIService}
     * @param nonRoutineCardIService           {@link NonRoutineCardIService}
     * @param aircraftService
     * @param partService
     * @param aircraftLocationService          {@link AircraftLocationService}
     */

    public AmlDefectRectificationServiceImpl(AmlDefectRectificationRepository amlDefectRectificationRepository,
                                             IService<Signature, SignatureDto> signatureService,
                                             AirportService airportService,
                                             IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto>
                                                     aircraftMaintenanceLogService, @Lazy MelIService melIService,
                                             @Lazy NonRoutineCardIService nonRoutineCardIService, AircraftService aircraftService, PartService partService, AircraftLocationService aircraftLocationService) {
        this.signatureService = signatureService;
        this.airportService = airportService;
        this.aircraftMaintenanceLogService = aircraftMaintenanceLogService;
        this.amlDefectRectificationRepository = amlDefectRectificationRepository;
        this.melIService = melIService;
        this.nonRoutineCardIService = nonRoutineCardIService;
        this.aircraftService = aircraftService;
        this.partService = partService;
        this.aircraftLocationService = aircraftLocationService;
    }

    /**
     * * mapper entity to response dto
     *
     * @param defectRectification {@link AMLDefectRectification}
     * @return {@link AmlDefectRectificationModelView}
     */
    private AmlDefectRectificationModelView convertToResponseDto(AMLDefectRectification defectRectification) {

        AmlDefectRectificationModelView defectRectificationResponseDto = new AmlDefectRectificationModelView();

        defectRectificationResponseDto.setId(defectRectification.getId());

        if (Objects.nonNull(defectRectification.getAircraftMaintenanceLog())) {
            defectRectificationResponseDto.setAmlId(defectRectification.getAircraftMaintenanceLog().getId());
            defectRectificationResponseDto.setAmlPageNo(defectRectification.getAircraftMaintenanceLog().getPageNo());
        }

        if (Objects.nonNull(defectRectification.getNonRoutineCard())) {
            defectRectificationResponseDto.setNrcId(defectRectification.getNonRoutineCard().getId());
        }


        if (Objects.nonNull(defectRectification.getDefectAirport())) {
            defectRectificationResponseDto.setDefectStaId(defectRectification.getDefectAirport().getId());
            defectRectificationResponseDto.setDefectStaName(defectRectification.getDefectAirport().getIataCode());
        }

        if (Objects.nonNull(defectRectification.getRectAirport())) {
            defectRectificationResponseDto.setRectStaId(defectRectification.getRectAirport().getId());
            defectRectificationResponseDto.setRectStaName(defectRectification.getRectAirport().getIataCode());
        }

        if (Objects.nonNull(defectRectification.getDefectSign())) {
            defectRectificationResponseDto.setDefectSignId(defectRectification.getDefectSign().getId());
            defectRectificationResponseDto.setDefectSignedEmployeeName(defectRectification.getDefectSign()
                    .getEmployee().getName());
            defectRectificationResponseDto.setDefectSignAuthNo(defectRectification.getDefectSign().getAuthNo());
        }

        if (Objects.nonNull(defectRectification.getRectSign())) {
            defectRectificationResponseDto.setRectSignId(defectRectification.getRectSign().getId());
            defectRectificationResponseDto.setRectSignedEmployeeName(defectRectification.getRectSign().
                    getEmployee().getName());
            defectRectificationResponseDto.setRectSignAuthNo(defectRectification.getRectSign().getAuthNo());
        }

        defectRectificationResponseDto.setSeqNo(defectRectification.getSeqNo());
        defectRectificationResponseDto.setDefectDmiNo(defectRectification.getDefectDmiNo());
        defectRectificationResponseDto.setDefectDescription(defectRectification.getDefectDescription());
        defectRectificationResponseDto.setDefectSignTime(defectRectification.getDefectSignTime());
        defectRectificationResponseDto.setRectDmiNo(defectRectification.getRectDmiNo());
        defectRectificationResponseDto.setRectMelRef(defectRectification.getRectMelRef());
        defectRectificationResponseDto.setMelCategory(defectRectification.getMelCategory());
        defectRectificationResponseDto.setRectAta(defectRectification.getRectAta());
        defectRectificationResponseDto.setRectPos(defectRectification.getRectPos());
        defectRectificationResponseDto.setRectPnOff(defectRectification.getRectPnOff());
        defectRectificationResponseDto.setRectPnOn(defectRectification.getRectPnOn());
        defectRectificationResponseDto.setRectSnOff(defectRectification.getRectSnOff());
        defectRectificationResponseDto.setRectSnOn(defectRectification.getRectSnOn());
        defectRectificationResponseDto.setRectGrn(defectRectification.getRectGrn());
        defectRectificationResponseDto.setRectDescription(defectRectification.getRectDescription());
        defectRectificationResponseDto.setRectSignTime(defectRectification.getRectSignTime());
        defectRectificationResponseDto.setMelType(defectRectification.getMelType());
        defectRectificationResponseDto.setDueDate(defectRectification.getDueDate());
        defectRectificationResponseDto.setIsActive(defectRectification.getIsActive());
        defectRectificationResponseDto.setReasonForRemoval(defectRectification.getReasonForRemoval());
        defectRectificationResponseDto.setRemark(defectRectification.getRemark());
        defectRectificationResponseDto.setWoNo(defectRectification.getWoNo());
        return defectRectificationResponseDto;
    }

    private List<AMLDefectRectification> mapToEntityList(List<AMLDefectRectificationDto> dtoList) {
        List<AMLDefectRectification> entityList = new ArrayList<>();
        dtoList.forEach(dto -> {
            if (Objects.nonNull(dto.getId())) {
                AMLDefectRectification exEntity = findById(dto.getId());
                entityList.add(prepareEntity(exEntity, dto));
            } else {
                entityList.add(prepareEntity(new AMLDefectRectification(), dto));
            }
        });
        return entityList;
    }

    private List<AmlDefectRectificationModelView> mapToResponseDtoList(List<AMLDefectRectification> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        List<AmlDefectRectificationModelView> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(convertToResponseDto(entity)));
        return dtoList;
    }

    public AMLDefectRectification findById(Long id) {
        if (Objects.isNull(id)) {
            throw EngineeringManagementServerException.notFound(ErrorId.ID_IS_REQUIRED);
        }
        Optional<AMLDefectRectification> amlDefectRectification = amlDefectRectificationRepository.findById(id);
        if (amlDefectRectification.isPresent()) {
            return amlDefectRectification.get();
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
    }

    /**
     * find defect and rectification info using id
     *
     * @param id defect and rectification id
     * @return {@link AMLDefectRectification}
     */
    @Override
    public AMLDefectRectification findByIdUnfiltered(Long id) {
        if (Objects.nonNull(id)) {
            Optional<AMLDefectRectification> amlDefectRectificationOptional
                    = amlDefectRectificationRepository.findById(id);
            if (amlDefectRectificationOptional.isPresent()) {
                return amlDefectRectificationOptional.get();
            }
        }

        return null;
    }

    /**
     * This method is responsible for generate report of AmlDefectRectification
     *
     * @param pageable {@link Pageable}
     * @return return AmlDefectRectificationReportViewModel as page data
     */
    @Override
    public Page<AmlDefectRectificationReportViewModel> generateAmlDefectRectificationReport(
            AmlDefectRectificationReportDto reportDto, Pageable pageable) {
        setNullIfEmptyString(reportDto);
        if (!reportDto.getIsDecimal()) {
            return amlDefectRectificationRepository.generateReport(reportDto.getAircraftId(), reportDto.getStartDate(),
                    reportDto.getEndDate(), reportDto.getAirportId(),
                    reportDto.getRectDescription(), reportDto.getDefDescription(), reportDto.getPosition(),
                    reportDto.getRectPnOff(), reportDto.getRectSnOff(), reportDto.getRectPnOn(), reportDto.getRectSnOn(),
                    reportDto.getRectAta(), reportDto.getReasonForRemoval(), reportDto.getRemark(), pageable);
        } else {
            Page<AmlDefectRectificationReportViewModel> report = amlDefectRectificationRepository
                    .generateReport(reportDto.getAircraftId(), reportDto.getStartDate(),
                            reportDto.getEndDate(), reportDto.getAirportId(),
                            reportDto.getRectDescription(), reportDto.getDefDescription(), reportDto.getPosition(),
                            reportDto.getRectPnOff(), reportDto.getRectSnOff(), reportDto.getRectPnOn(), reportDto.getRectSnOn(),
                            reportDto.getRectAta(), reportDto.getReasonForRemoval(), reportDto.getRemark(), pageable);
            List<AmlDefectRectificationReportViewModel> modifiedReport = new ArrayList<>();
            for (AmlDefectRectificationReportViewModel viewModel : report.getContent()) {
                viewModel.setAmlAirFrameTotalTime(DateUtil.convertHourMinutesToDecimalHourMinutes(viewModel
                        .getAmlAirFrameTotalTime()));
                modifiedReport.add(viewModel);
            }
            return new PageImpl<>(modifiedReport, pageable, report.getTotalElements());
        }
    }

    private void setNullIfEmptyString(AmlDefectRectificationReportDto reportDto) {
        PlanningUtil.setNullIfEmptyString(reportDto.getRectDescription());
        PlanningUtil.setNullIfEmptyString(reportDto.getDefDescription());
        PlanningUtil.setNullIfEmptyString(reportDto.getPosition());
        PlanningUtil.setNullIfEmptyString(reportDto.getRectPnOff());
        PlanningUtil.setNullIfEmptyString(reportDto.getRectSnOff());
        PlanningUtil.setNullIfEmptyString(reportDto.getRectPnOn());
        PlanningUtil.setNullIfEmptyString(reportDto.getRectSnOn());
        PlanningUtil.setNullIfEmptyString(reportDto.getRectAta());
        PlanningUtil.setNullIfEmptyString(reportDto.getReasonForRemoval());
        PlanningUtil.setNullIfEmptyString(reportDto.getRemark());
    }

    /**
     * Conversion of dto to entity
     *
     * @param defectRectification    {@link AMLDefectRectification}
     * @param defectRectificationDto {@link AMLDefectRectificationDto}
     * @return amlDefectRectification {@link AMLDefectRectification}
     */
    private AMLDefectRectification prepareEntity(AMLDefectRectification defectRectification,
                                                 AMLDefectRectificationDto defectRectificationDto) {

        if (Objects.nonNull(defectRectificationDto.getAmlId())) {
            defectRectification.setAircraftMaintenanceLog(aircraftMaintenanceLogService.findById(
                    defectRectificationDto.getAmlId()));
        }

        if (Objects.nonNull(defectRectificationDto.getNrcId())) {
            defectRectification.setNonRoutineCard(nonRoutineCardIService.findById(defectRectificationDto.getNrcId()));
        }

        if (Objects.nonNull(defectRectificationDto.getDefectStaId())) {
            defectRectification.setDefectAirport(airportService.findActiveAirportById(
                    defectRectificationDto.getDefectStaId()));
        }

        if (Objects.nonNull(defectRectificationDto.getRectStaId())) {
            defectRectification.setRectAirport(airportService.findActiveAirportById(
                    defectRectificationDto.getRectStaId()));
        }

        if (Objects.nonNull(defectRectificationDto.getDefectSignId())) {
            defectRectification.setDefectSign(signatureService.findById(defectRectificationDto.getDefectSignId()));
        }

        if (Objects.nonNull(defectRectificationDto.getRectSignId())) {
            defectRectification.setRectSign(signatureService.findById(defectRectificationDto.getRectSignId()));
        }


        defectRectification.setSeqNo(defectRectificationDto.getSeqNo());
        defectRectification.setDefectDmiNo(defectRectificationDto.getDefectDmiNo());
        defectRectification.setDefectDescription(defectRectificationDto.getDefectDescription());
        defectRectification.setDefectSignTime(defectRectificationDto.getDefectSignTime());
        defectRectification.setRectDmiNo(defectRectificationDto.getRectDmiNo());
        defectRectification.setRectMelRef(defectRectificationDto.getRectMelRef());
        defectRectification.setMelCategory(defectRectificationDto.getMelCategory());
        defectRectification.setRectAta(defectRectificationDto.getRectAta());
        defectRectification.setRectPos(defectRectificationDto.getRectPos());
        defectRectification.setRectPnOff(defectRectificationDto.getRectPnOff());
        defectRectification.setRectPnOn(defectRectificationDto.getRectPnOn());
        defectRectification.setRectSnOff(defectRectificationDto.getRectSnOff());
        defectRectification.setRectSnOn(defectRectificationDto.getRectSnOn());
        defectRectification.setRectGrn(defectRectificationDto.getRectGrn());
        defectRectification.setRectDescription(defectRectificationDto.getRectDescription());
        defectRectification.setRectSignTime(defectRectificationDto.getRectSignTime());
        defectRectification.setReasonForRemoval(defectRectificationDto.getReasonForRemoval());
        defectRectification.setRemark(defectRectificationDto.getRemark());
        defectRectification.setDueDate(defectRectificationDto.getDueDate());
        defectRectification.setMelCategory(defectRectificationDto.getMelCategory());
        defectRectification.setWoNo(defectRectificationDto.getWoNo());
        return defectRectification;
    }

    /**
     * create of AMLDefectRectification
     *
     * @param defectRectificationDtos {@link ClientRequestListData<AMLDefectRectificationDto>}
     * @return savedEntities {@link List<AmlDefectRectificationModelView>}
     */
    private List<AmlDefectRectificationModelView> saveOrUpdateItems(List<AMLDefectRectificationDto>
                                                                            defectRectificationDtos) {
        List<AMLDefectRectification> amlDefectRectifications = mapToEntityList(defectRectificationDtos);

        try {
            saveOrClearMel(amlDefectRectifications, defectRectificationDtos);
            List<AMLDefectRectification> savedEntities =
                    amlDefectRectificationRepository.saveAll(amlDefectRectifications);
            return mapToResponseDtoList(savedEntities);
        } catch (EngineeringManagementServerException e) {
            String entityName = amlDefectRectifications.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException
                    .dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, entityName));
        }
    }

    /**
     * used for adding defect and rectification
     *
     * @param defectRectificationDtos {@link List}
     * @return list of AmlDefectRectification as view model
     */
    @Transactional
    @Override
    public List<AmlDefectRectificationModelView> create(
            List<AMLDefectRectificationDto> defectRectificationDtos) {
        if (CollectionUtils.isEmpty(defectRectificationDtos)) {
            throw EngineeringManagementServerException.notFound(ErrorId.REQUEST_DATA_IS_EMPTY);
        }
        return saveOrUpdateItems(defectRectificationDtos);
    }

    /**
     * update of AMLDefectRectification
     *
     * @param defectRectificationDtos {@link ClientRequestListData<AMLDefectRectificationDto>}
     * @return savedEntities {@link List<AmlDefectRectificationModelView>}
     */
    @Override
    @Transactional
    public List<AmlDefectRectificationModelView> update(List<AMLDefectRectificationDto>
                                                                defectRectificationDtos) {
        if (CollectionUtils.isEmpty(defectRectificationDtos)) {
            throw EngineeringManagementServerException.notFound(ErrorId.REQUEST_DATA_IS_EMPTY);
        }
        return saveOrUpdateItems(defectRectificationDtos);
    }

    /**
     * get defect-rectification list by aml id
     *
     * @param amlId {@link Long}
     * @return response {@link List<AmlDefectRectificationModelView>}
     */
    @Override
    public List<AmlDefectRectificationModelView> getDefectRectificationsByAmlId(Long amlId) {
        return mapToResponseDtoList(amlDefectRectificationRepository.findAllByAircraftMaintenanceLogId(amlId));
    }

    private void saveOrClearMel(List<AMLDefectRectification> savedEntities, List<AMLDefectRectificationDto>
            defectRectificationDtos) {
        Map<String, AMLDefectRectificationDto> defectRectificationMap = defectRectificationDtos.stream()
                .collect(Collectors.toMap(AMLDefectRectificationDto::getSeqNo, Function.identity()));

        Set<Long> melIds = defectRectificationDtos.stream()
                .map(AMLDefectRectificationDto::getMelId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Mel> melList = melIService.getAllByDomainIdIn(melIds, true);

        Map<Long, Mel> melMap = melList.stream().collect(Collectors.toMap(Mel::getId, Function.identity()));


        List<Mel> mels = new ArrayList<>();
        savedEntities.forEach(amlDefectRectification -> {
            if (defectRectificationMap.containsKey(amlDefectRectification.getSeqNo())) {
                AMLDefectRectificationDto amlDefectRectificationDto =
                        defectRectificationMap.get(amlDefectRectification.getSeqNo());
                if (Objects.nonNull(amlDefectRectificationDto)
                        && !amlDefectRectificationDto.getMelType().equals(MelType.NONE)) {
                    Mel mel;
                    if (amlDefectRectificationDto.getMelType().equals(MelType.ADD)
                            && (Objects.isNull(amlDefectRectification.getMelType())
                            || !amlDefectRectification.getMelType().equals(MelType.ADD))) {
                        amlDefectRectification.setMelType(amlDefectRectificationDto.getMelType());
                        mel = new Mel();
                        mel.setIntDefRect(amlDefectRectification);
                        mel.setAircraft(amlDefectRectification.getAircraftMaintenanceLog().getAircraft());
                        mels.add(mel);
                    } else if (amlDefectRectificationDto.getMelType().equals(MelType.CLEAR)
                            && (Objects.isNull(amlDefectRectification.getMelType())
                            || !amlDefectRectification.getMelType().equals(MelType.CLEAR))
                            && melMap.containsKey(amlDefectRectificationDto.getMelId())) {
                        amlDefectRectification.setMelType(amlDefectRectificationDto.getMelType());
                        mel = melMap.get(amlDefectRectificationDto.getMelId());
                        mel.setCorrectDefRect(amlDefectRectification);
                        mels.add(mel);
                    }
                } else if (amlDefectRectificationDto.getMelType().equals(MelType.NONE)) {
                    amlDefectRectification.setMelType(amlDefectRectificationDto.getMelType());
                }
            }
        });

        if (CollectionUtils.isNotEmpty(mels)) {
            melIService.saveItemList(mels);
        }
    }

    @Override
    public AmlDefectRectificationModelView findDefectRectificationByNrcId(Long nrcId) {
        return convertToResponseDto(amlDefectRectificationRepository.findByNonRoutineCardId(nrcId));
    }

    @Override
    public void deleteDefectAndRectifications(List<Long> defectRectificationIds) {
        List<AMLDefectRectification> amlDefectRectificationList =
                amlDefectRectificationRepository.findAllByIdIn(defectRectificationIds);
        if (CollectionUtils.isNotEmpty(amlDefectRectificationList)) {
            for (AMLDefectRectification amlDefectRectification : amlDefectRectificationList) {
                if (amlDefectRectification.getMelType().equals(MelType.ADD)
                        || amlDefectRectification.getMelType().equals(MelType.CLEAR)) {
                    throw new EngineeringManagementServerException(ErrorId.CAN_NOT_DELETE_DEFECT_AND_RECTIFICATION,
                            HttpStatus.BAD_REQUEST, ApplicationConstant.TRACE_ID);
                }
            }

            try {
                amlDefectRectificationRepository.deleteAll(amlDefectRectificationList);
            } catch (Exception ex) {
                throw new EngineeringManagementServerException(
                        ErrorId.EXCEPTION_HAPPENED_WHILE_DELETING_DEFECT_AND_RECTIFICATION,
                        HttpStatus.INTERNAL_SERVER_ERROR, ApplicationConstant.TRACE_ID);
            }
        }
    }

    @Override
    public List<DefRectSearchViewModel> searchDefectRectificationList(Long aircraftId, LocalDate fromDate,
                                                                      LocalDate toDate) {
        List<DefRectSearchViewModel> defRectList = amlDefectRectificationRepository.searchDefectRect(
                aircraftId, fromDate, toDate);

        Set<AircraftLocation> aircraftLocationSet = aircraftLocationService.findAllActiveAircraftLocation();

        List<Part> partList = partService.findAllByAircraftModelId(aircraftService.findById(aircraftId).getAircraftModelId());

        Map<String, Long> aircraftLocationMap = aircraftLocationSet.stream().collect(Collectors.toMap(
                AircraftLocation::getName, AircraftLocation::getId));

        Map<String, Long> partMap = partList.stream().collect(Collectors.toMap(
                Part::getPartNo, Part::getId));

        defRectList.forEach(d -> {
            d.setAircraftId(aircraftId);
            if (Objects.nonNull(d.getAlphabet())) {
                d.setReference(String.valueOf(d.getPageNo()) + d.getAlphabet());
            } else {
                d.setReference(String.valueOf(d.getPageNo()));
            }

            if(Objects.nonNull(d.getAta())){
                d.setLocationId(aircraftLocationMap.get(d.getAta()));
            }

            if(Objects.nonNull(d.getPartNo())){
                d.setPartId(partMap.get(d.getPartNo()));
            }

        });
        return defRectList;
    }
}
