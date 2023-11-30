package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.*;
import com.digigate.engineeringmanagement.planning.dto.AcBuildPartReturnDto;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftApusRepository;
import com.digigate.engineeringmanagement.planning.repository.AircraftBuildRepository;
import com.digigate.engineeringmanagement.planning.repository.LdndRepository;
import com.digigate.engineeringmanagement.planning.repository.SerialRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftBuildIService;
import com.digigate.engineeringmanagement.planning.service.ModelTreeIService;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.SerialService;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.PartLifeStatusResponseDto;
import com.digigate.engineeringmanagement.storeinspector.planning.PartLifeStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.planning.PartInactiveInfoPostService;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.planning.constant.AircraftBuildConstant.*;

/**
 * Model Tree Service
 *
 * @author Masud Rana
 */
@Service
public class AircraftBuildService extends AbstractSearchService<AircraftBuild, AircraftBuildPayload, AircraftBuildSearchPayload>
        implements AircraftBuildIService {
    private static final String COLON = ":";
    private final IService<Aircraft, AircraftDto> aircraftIService;
    private final ModelService modelService;
    private final PartService partService;
    private final IService<AircraftLocation, AircraftLocationDto> aircraftLocationService;
    private final IService<Position, PositionDto> positionIService;
    private final AircraftBuildRepository repository;
    private final Environment environment;
    private final ModelTreeIService modelTreeIService;
    private final LdndRepository ldndRepository;
    private final AircraftRepository aircraftRepository;
    private final SerialRepository serialRepository;
    private final SerialService serialService;
    private static final String ON_COND = "ON COND";
    private final AircraftService aircraftService;

    private final AircraftApusRepository aircraftApusRepository;
    private final PartLifeStatusService partLifeStatusService;
    private final PartInactiveInfoPostService partInactiveInfoPostService;

    private final AircraftMaintenanceLogServiceImpl aircraftMaintenanceLogService;

    private final SharedAircraftInformation sharedAircraftInformation;

    /**
     * Autowired constructor
     *
     * @param repository                    {@link AbstractRepository<AircraftBuild>}
     * @param aircraftIService              {@link IService}
     * @param modelService                  {@link ModelService}
     * @param partService                   {@link PartService}
     * @param aircraftLocationService       {@link IService}
     * @param positionIService              {@link IService}
     * @param aircraftBuildRepository       {@link AircraftBuildRepository}
     * @param environment                   {@link Environment}
     * @param modelTreeIService             {@link ModelTreeIService}
     * @param ldndRepository                {@link LdndRepository}
     * @param aircraftRepository            {@link AircraftRepository}
     * @param serialRepository              {@link SerialRepository}
     * @param serialService                 {@link SerialService}
     * @param repository                  {@link AbstractRepository<AircraftBuild>}
     * @param aircraftIService            {@link IService}
     * @param modelService                {@link ModelService}
     * @param partService                 {@link PartService}
     * @param aircraftLocationService     {@link IService}
     * @param positionIService            {@link IService}
     * @param aircraftBuildRepository     {@link AircraftBuildRepository}
     * @param environment                 {@link Environment}
     * @param modelTreeIService           {@link ModelTreeIService}
     * @param ldndRepository              {@link LdndRepository}
     * @param aircraftRepository          {@link AircraftRepository}
     * @param serialRepository            {@link SerialRepository}
     * @param serialService               {@link SerialService}
     * @param aircraftService
     * @param aircraftApusRepository
     * @param aircraftMaintenanceLogService
     * @param sharedAircraftInformation
     * @param partLifeStatusService
     * @param partInactiveInfoPostService
     */
    public AircraftBuildService(AbstractRepository<AircraftBuild> repository,
                                IService<Aircraft, AircraftDto> aircraftIService,
                                ModelService modelService,
                                PartService partService,
                                IService<AircraftLocation, AircraftLocationDto> aircraftLocationService,
                                IService<Position, PositionDto> positionIService,
                                AircraftBuildRepository aircraftBuildRepository,
                                Environment environment,
                                ModelTreeIService modelTreeIService,
                                LdndRepository ldndRepository,
                                AircraftRepository aircraftRepository,
                                AircraftMaintenanceLogServiceImpl aircraftMaintenanceLogService,
                                SerialRepository serialRepository,
                                SerialService serialService,
                                AircraftService aircraftService,
                                AircraftApusRepository aircraftApusRepository,
                                PartLifeStatusService partLifeStatusService,
                                SharedAircraftInformation sharedAircraftInformation,
                                PartInactiveInfoPostService partInactiveInfoPostService) {
        super(repository);
        this.aircraftIService = aircraftIService;
        this.modelService = modelService;
        this.partService = partService;
        this.aircraftLocationService = aircraftLocationService;
        this.positionIService = positionIService;
        this.repository = aircraftBuildRepository;
        this.environment = environment;
        this.modelTreeIService = modelTreeIService;
        this.ldndRepository = ldndRepository;
        this.aircraftRepository = aircraftRepository;
        this.serialRepository = serialRepository;
        this.serialService = serialService;
        this.aircraftService = aircraftService;
        this.aircraftApusRepository = aircraftApusRepository;
        this.partLifeStatusService = partLifeStatusService;
        this.partInactiveInfoPostService = partInactiveInfoPostService;
        this.aircraftMaintenanceLogService = aircraftMaintenanceLogService;
        this.sharedAircraftInformation = sharedAircraftInformation;
    }

    public Aircraft getAircraftInfoByAircraftBuildId(Long aircraftBuildId, LocalDate givenDate) {
        Aircraft aircraft = aircraftRepository
                .getAircraftByAircraftBuildId(aircraftBuildId);
        if (Objects.nonNull(givenDate)
                && givenDate.isBefore(aircraft.getUpdatedAt())) {
            FlightDataInfoViewModel flightDataInfoViewModel = aircraftMaintenanceLogService
                    .getFlightDataInfoByDate(givenDate, aircraft.getId());
            if (Objects.nonNull(flightDataInfoViewModel)) {
                aircraft.setAirFrameTotalTime(flightDataInfoViewModel.getGrandTotalAirTime());
                aircraft.setAirframeTotalCycle(flightDataInfoViewModel.getGrandTotalLanding());
                aircraft.setUpdatedAt(flightDataInfoViewModel.getUpdateDate());
            } else {
                throw new EngineeringManagementServerException(
                        ErrorId.NO_DATA_AVAILABLE_FOR_THIS_BACKDATE, HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
        return aircraft;
    }

    public Aircraft getAircraftInfoByAircraftId(Long aircraftId, LocalDate givenDate) {
        Aircraft aircraft = aircraftRepository.getById(aircraftId);
        if (Objects.nonNull(givenDate)
                && givenDate.isBefore(aircraft.getUpdatedAt())) {
            FlightDataInfoViewModel flightDataInfoViewModel = aircraftMaintenanceLogService
                    .getFlightDataInfoByDate(givenDate, aircraft.getId());
            if (Objects.nonNull(flightDataInfoViewModel)) {
                aircraft.setAirFrameTotalTime(flightDataInfoViewModel.getGrandTotalAirTime());
                aircraft.setAirframeTotalCycle(flightDataInfoViewModel.getGrandTotalLanding());
                aircraft.setUpdatedAt(flightDataInfoViewModel.getUpdateDate());
            } else {
                throw new EngineeringManagementServerException(
                        ErrorId.NO_DATA_AVAILABLE_FOR_THIS_BACKDATE, HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
        return aircraft;
    }

    /**
     * convert response  from entity
     *
     * @param aircraftBuild {@link AircraftBuild}
     * @return {@link AircraftBuildViewModel}
     */
    @Override
    protected AircraftBuildViewModel convertToResponseDto(AircraftBuild aircraftBuild) {
        AircraftBuildViewModel aircraftBuildViewModel = AircraftBuildViewModel.builder()
                .id(aircraftBuild.getId())
                .isActive(BooleanUtils.toBoolean(aircraftBuild.getIsActive()))
                .higherSerialId(aircraftBuild.getHigherSerialId())
                .higherSerialNo(aircraftBuild.getHigherSerial().getSerialNumber())
                .serialId(aircraftBuild.getSerialId())
                .serialNo(aircraftBuild.getSerial().getSerialNumber())
                .tsnHour(aircraftBuild.getTsnHour())
                .tsnCycle(aircraftBuild.getTsnCycle())
                .isTsnAvailable(aircraftBuild.getIsTsnAvailable())
                .tsoHour(aircraftBuild.getTsoHour())
                .tsoCycle(aircraftBuild.getTsoCycle())
                .isOverhauled(BooleanUtils.toBoolean(aircraftBuild.getIsOverhauled()))
                .tslsvHour(aircraftBuild.getTslsvHour())
                .tslsvCycle(aircraftBuild.getTslsvCycle())
                .isShopVisited(BooleanUtils.toBoolean(aircraftBuild.getIsShopVisited()))
                .attachDate(aircraftBuild.getAttachDate())
                .comManufactureDate(aircraftBuild.getComManufactureDate())
                .comCertificateDate(aircraftBuild.getComCertificateDate())
                .createdAt(aircraftBuild.getCreatedAt())
                .aircraftId(aircraftBuild.getAircraftId())
                .aircraftName(aircraftBuild.getAircraft().getAircraftName())
                .modelId(aircraftBuild.getModelId())
                .modelName(aircraftBuild.getModel().getModelName())
                .higherModelId(aircraftBuild.getHigherModelId())
                .higherModelName(aircraftBuild.getHigherModel().getModelName())
                .locationId(aircraftBuild.getLocationId())
                .locationName(aircraftBuild.getAircraftLocation().getName())
                .partId(aircraftBuild.getPartId())
                .partNo(aircraftBuild.getPart().getPartNo())
                .higherPartId(aircraftBuild.getHigherPartId())
                .higherPartNo(aircraftBuild.getHigherPart().getPartNo())
                .positionId(aircraftBuild.getPositionId())
                .aircraftInHour(aircraftBuild.getAircraftInHour())
                .aircraftInCycle(aircraftBuild.getAircraftInCycle())
                .outDate(aircraftBuild.getOutDate())
                .inRefMessage(aircraftBuild.getInRefMessage())
                .outRefMessage(aircraftBuild.getOutRefMessage())
                .removalReason(aircraftBuild.getRemovalReason())
                .aircraftOutHour(aircraftBuild.getAircraftOutHour())
                .aircraftOutCycle(aircraftBuild.getAircraftOutCycle())
                .authNo(aircraftBuild.getAuthNo())
                .sign(aircraftBuild.getSign())
                .build();
        if (Objects.nonNull(aircraftBuild.getPositionId())) {
            Position position = aircraftBuild.getPosition();
            if (Objects.nonNull(position)) {
                aircraftBuildViewModel.setPositionName(position.getName());
            }
        }
        return aircraftBuildViewModel;

    }

    private Double getCountFactor(Part part) {
        if (Objects.isNull(part)) {
            return 1.0;
        }
        return Objects.nonNull(part.getCountFactor()) ? part.getCountFactor() : 1.0;
    }
//
//    private void addCycleAndHour(AircraftBuild aircraftBuild, AircraftBuildViewModel aircraftBuildViewModel,
//                                 Double countFactor) {
//        if (BooleanUtils.toBoolean(aircraftBuild.getIsOverhauled())) {
//            Integer tsoCycle = Math.max((aircraftBuild.getTsoCycle() + aircraftBuild.getFitLifeCycle()) * countFactor.intValue(), 0);
//            aircraftBuildViewModel.setTsoCycle(tsoCycle);
//
//            aircraftBuildViewModel.setTsoHour(
//                    Double.max(NumberUtil.getDefaultIfNull(aircraftBuild.getTsoHour(), 0.0)
//                            + aircraftBuild.getFitLifeHour(), 0.0));
//        }
//
//        if (BooleanUtils.toBoolean(aircraftBuild.getIsShopVisited())
//                || BooleanUtils.toBoolean(aircraftBuild.getIsOverhauled())) {
//            int tslvsCycle = Math.max((aircraftBuild.getTslsvCycle() + aircraftBuild.getFitLifeCycle()) * countFactor.intValue(), 0);
//            aircraftBuildViewModel.setTslsvCycle(tslvsCycle);
//
//            aircraftBuildViewModel.setTslsvHour(
//                    Double.max(NumberUtil.getDefaultIfNull(aircraftBuild.getTslsvHour(), 0.0)
//                            + aircraftBuild.getFitLifeHour(), 0.0));
//        }
//
//        if (BooleanUtils.toBoolean(aircraftBuild.getIsTsnAvailable())) {
//            int tsnCycle = Math.max((aircraftBuild.getTsnCycle() + aircraftBuild.getFitLifeCycle()) * countFactor.intValue(), 0);
//            aircraftBuildViewModel.setTsnCycle(tsnCycle);
//
//            aircraftBuildViewModel.setTsnHour(Double.max(NumberUtil.getDefaultIfNull(aircraftBuild.getTsnHour(), 0.0)
//                    + aircraftBuild.getFitLifeHour(), 0.0));
//
//        }
//    }

    /**
     * create entity
     *
     * @param aircraftBuildPayload {@link AircraftBuildPayload}
     * @return {@link AircraftBuild}
     */
    @Override
    @Transactional
    public AircraftBuild create(AircraftBuildPayload aircraftBuildPayload) {
        validateClientData(aircraftBuildPayload, null);
        AircraftBuild aircraftBuild = convertToEntity(aircraftBuildPayload);
        return saveItem(aircraftBuild);
    }

    /**
     * convert entity  from dto
     *
     * @param aircraftBuildPayload {@link AircraftBuildPayload}
     * @return {@link AircraftBuild}
     */
    @Override
    protected AircraftBuild convertToEntity(AircraftBuildPayload aircraftBuildPayload) {
        AircraftBuild aircraftBuild = new AircraftBuild();
        prepareEntity(aircraftBuildPayload, aircraftBuild);
        aircraftBuild.setIsActive(Boolean.TRUE);
        return aircraftBuild;
    }

    /**
     * update entity
     *
     * @param aircraftBuildPayload {@link AircraftBuildPayload}
     * @param id                   {@link Long}
     * @return {@link AircraftBuild}
     */
    @Override
    @Transactional
    public AircraftBuild update(AircraftBuildPayload aircraftBuildPayload, Long id) {
        validateClientData(aircraftBuildPayload, id);
        AircraftBuild aircraftBuild = updateEntity(aircraftBuildPayload, findByIdUnfiltered(id));
        return saveItem(aircraftBuild);
    }

    /**
     * convert entity  from dto
     *
     * @param aircraftBuildPayload {@link AircraftBuildPayload}
     * @param aircraftBuild        {@link AircraftBuild}
     * @return {@link AircraftBuild}
     */
    @Override
    protected AircraftBuild updateEntity(AircraftBuildPayload aircraftBuildPayload, AircraftBuild aircraftBuild) {
        return prepareEntity(aircraftBuildPayload, aircraftBuild);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        AircraftBuild aircraftBuild = findByIdUnfiltered(id);
        if (aircraftBuild.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == Boolean.FALSE) {
            ldndRepository.updateLdndByPartAndSerialIsActiveFalse(aircraftBuild.getPartId(),
                    aircraftBuild.getSerialId());
        }
        if (isActive == Boolean.TRUE) {
            checkForDuplicateEntity(aircraftBuild.getPartId(), aircraftBuild.getSerialId(), id);
        }
        aircraftBuild.setIsActive(isActive);
        saveItem(aircraftBuild);
    }

    /**
     * validate client data
     *
     * @param aircraftBuildPayload {@link AircraftBuildPayload}
     * @param id                   {@link Long}
     * @return {@link Boolean}
     */
    @Override
    public Boolean validateClientData(AircraftBuildPayload aircraftBuildPayload, Long id) {
        if (!BooleanUtils.toBoolean(aircraftBuildPayload.getIsOverhauled())) {
            if (Objects.nonNull(aircraftBuildPayload.getTsoHour())
                    || Objects.nonNull(aircraftBuildPayload.getTsoCycle())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.SHOULD_NOT_HAVE_TSO);
            }
        }
        if (!BooleanUtils.toBoolean(aircraftBuildPayload.getIsShopVisited()) &&
                !BooleanUtils.toBoolean(aircraftBuildPayload.getIsOverhauled())) {
            if (Objects.nonNull(aircraftBuildPayload.getTslsvCycle())
                    || Objects.nonNull(aircraftBuildPayload.getTslsvHour())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.SHOULD_NOT_HAVE_TSLSV);
            }
        }
        if (aircraftBuildPayload.getPartId().equals(aircraftBuildPayload.getHigherPartId())) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PART_AND_HIGHER_PART_HIERARCHY_SHOULD_BE_DIFFERENT);
        }
        if (aircraftBuildPayload.getModelId().equals(aircraftBuildPayload.getHigherModelId())) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.MODEL_CAN_NOT_BE_HIGHER_MODEL_ITSELF);
        }
        if (Objects.isNull(aircraftBuildPayload.getPositionId())) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.POSITION_NAME_CAN_NOT_BE_NULL);
        }

        checkPartAndPosition(aircraftBuildPayload.getAircraftId(), aircraftBuildPayload.getPartId(),
                aircraftBuildPayload.getPositionId(), id);

        checkForDuplicateEntity(aircraftBuildPayload.getPartId(), aircraftBuildPayload.getSerialId(), id);

        if (modelTreeIService.findIdForUniqueEntry(aircraftBuildPayload.getModelId(),
                aircraftBuildPayload.getHigherModelId(),
                aircraftBuildPayload.getLocationId(), aircraftBuildPayload.getPositionId()).isEmpty()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.BUILD_IS_NOT_VALID_ACCORDING_TO_MODEL_TREE);
        }

        Model model = modelService.findById(aircraftBuildPayload.getModelId());
        Aircraft aircraft = aircraftIService.findById(aircraftBuildPayload.getAircraftId());
//        if(Objects.isNull(id)){
//            if (model.getLifeCodes().contains(LifeCodes.APU_HOUR.val) || model.getLifeCodes().contains(LifeCodes.APU_CYCLE.val)) {
//                if (model.getLifeCodes().contains(LifeCodes.APU_HOUR.val)) {
//                    if (aircraftBuildPayload.getAircraftInHour() < aircraft.getTotalApuHours()) {
//                        throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_IN_HOUR_IS_NOT_VALID);
//                    }
//                }
//                if (model.getLifeCodes().contains(LifeCodes.APU_CYCLE.val)) {
//                    if (aircraftBuildPayload.getAircraftInCycle() < aircraft.getTotalApuCycle()) {
//                        throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_IN_CYCLE_IS_NOT_VALID);
//                    }
//                }
//            } else {
//                if (model.getLifeCodes().contains(LifeCodes.FLY_HOUR.val)) {
//                    if (aircraftBuildPayload.getAircraftInHour() < aircraft.getAirFrameTotalTime()) {
//                        throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_IN_HOUR_IS_NOT_VALID);
//                    }
//                }
//                if (model.getLifeCodes().contains(LifeCodes.FLY_CYCLE.val)) {
//                    if (aircraftBuildPayload.getAircraftInCycle() < aircraft.getAirframeTotalCycle()) {
//                        throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_IN_CYCLE_IS_NOT_VALID);
//                    }
//                }
//            }
//        }



        return true;
    }

    private void checkPartAndPosition(Long aircraftId, Long partId, Long positionId, Long id) {
        Optional<Long> existingPartAndPositionId = repository.findByPartIdAndPositionId(aircraftId,
                partId, positionId);
        if (existingPartAndPositionId.isPresent()) {
            if (Objects.isNull(id)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.ENTITY_EXISTS_WITH_SAME_PART_AND_POSITION);
            } else {
                if (!id.equals(existingPartAndPositionId.get())) {
                    throw EngineeringManagementServerException
                            .badRequest(ErrorId.ENTITY_EXISTS_WITH_SAME_PART_AND_POSITION);
                }
            }
        }
    }

    private void checkForDuplicateEntity(Long partId, Long serialId, Long id) {
        Optional<Long> existingAircraftBuildId = repository.findByPartIdAndSerialId(partId, serialId);
        if (existingAircraftBuildId.isPresent()) {
            if (Objects.isNull(id)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.ENTITY_EXISTS_WITH_SAME_CONFIGURATION);
            } else {
                if (!id.equals(existingAircraftBuildId.get())) {
                    throw EngineeringManagementServerException
                            .badRequest(ErrorId.ENTITY_EXISTS_WITH_SAME_CONFIGURATION);
                }
            }
        }
    }

    private AircraftBuild prepareEntity(AircraftBuildPayload aircraftBuildPayload, AircraftBuild aircraftBuild) {
        if (Objects.nonNull(aircraftBuild.getId()) && !BooleanUtils.toBoolean(aircraftBuild.getIsActive())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CAN_NOT_UPDATE_INACTIVE_ENTITY);
        }

        //TODO: need to check validation later
        /*if (Objects.isNull(aircraftBuild.getId()) && CollectionUtils.isNotEmpty(
                repository.checkDuplicate(aircraftBuildPayload.getAircraftId(),
                        aircraftBuildPayload.getModelId(), aircraftBuildPayload.getHigherModelId(),
                aircraftBuildPayload.getLocationId(), aircraftBuildPayload.getPositionId()))) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_AIRCRAFT_BUILD, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }*/

        aircraftBuild.setAircraft(aircraftIService.findById(aircraftBuildPayload.getAircraftId()));
        aircraftBuild.setHigherModel(findModelById(aircraftBuildPayload.getHigherModelId()));
        aircraftBuild.setModel(findModelById(aircraftBuildPayload.getModelId()));

        if (aircraftBuildPayload.getIsTsnAvailable() == Boolean.TRUE) {
            aircraftBuild.setTsnHour(aircraftBuildPayload.getTsnHour());
            aircraftBuild.setTsnCycle(aircraftBuildPayload.getTsnCycle());
        }
        aircraftBuild.setIsTsnAvailable(aircraftBuildPayload.getIsTsnAvailable());

        aircraftBuild.setTsoHour(aircraftBuildPayload.getTsoHour());
        aircraftBuild.setTsoCycle(aircraftBuildPayload.getTsoCycle());

        aircraftBuild.setIsOverhauled(BooleanUtils.toBoolean(aircraftBuildPayload.getIsOverhauled()));
        aircraftBuild.setTslsvHour(aircraftBuildPayload.getTslsvHour());
        aircraftBuild.setTslsvCycle(aircraftBuildPayload.getTslsvCycle());
        aircraftBuild.setAircraftInHour(aircraftBuildPayload.getAircraftInHour());
        aircraftBuild.setAircraftInCycle(aircraftBuildPayload.getAircraftInCycle());
        aircraftBuild.setInRefMessage(aircraftBuildPayload.getInRefMessage());
        aircraftBuild.setIsShopVisited(BooleanUtils.toBoolean(aircraftBuildPayload.getIsShopVisited()));
        if (aircraftBuild.getIsOverhauled()) {
            aircraftBuild.setIsShopVisited(Boolean.TRUE);
        }

        aircraftBuild.setAttachDate(aircraftBuildPayload.getAttachDate());
        aircraftBuild.setComManufactureDate(aircraftBuildPayload.getComManufactureDate());
        aircraftBuild.setComCertificateDate(aircraftBuildPayload.getComCertificateDate());
        aircraftBuild.setAircraftLocation(aircraftLocationService.findById(aircraftBuildPayload.getLocationId()));
        if (Objects.nonNull(aircraftBuildPayload.getPositionId())) {
            aircraftBuild.setPosition(positionIService.findById(aircraftBuildPayload.getPositionId()));
        }

        aircraftBuild.setIsActive(Boolean.TRUE);
        addPart(aircraftBuild, aircraftBuildPayload.getPartId(), aircraftBuildPayload.getHigherPartId());

        Serial serial = serialService.findById(aircraftBuildPayload.getSerialId());
        aircraftBuild.setSerial(serial);

        Serial higherSerial = serialService.findById(aircraftBuildPayload.getHigherSerialId());
        aircraftBuild.setHigherSerial(higherSerial);
        aircraftBuild.setAuthNo(aircraftBuildPayload.getAuthNo());
        aircraftBuild.setSign(aircraftBuildPayload.getSign());
        return aircraftBuild;
    }


    private AircraftBuild addPart(AircraftBuild aircraftBuild, Long partId, Long higherPartId) {
        List<Part> aircraftPartList = partService.getAllByDomainIdIn(
                Set.of(partId, higherPartId), true);
        Map<Long, Part> partMap = aircraftPartList.stream().collect(Collectors.toMap(Part::getId, Function.identity()));
        if (Objects.isNull(partMap.get(partId))) {
            throw EngineeringManagementServerException.notFound(ErrorId.PART_NOT_FOUND);
        }
        if (Objects.isNull(partMap.get(higherPartId))) {
            throw EngineeringManagementServerException.notFound(ErrorId.HIGHER_PART_NOT_FOUND);
        }
        aircraftBuild.setPart(partMap.get(partId));
        aircraftBuild.setHigherPart(partMap.get(higherPartId));
        return aircraftBuild;
    }

    private Model findModelById(Long modelId) {
        return Objects.nonNull(modelId) ? modelService.findById(modelId) : null;
    }

    /**
     * search entity by criteria
     *
     * @param searchDto {@link AircraftBuildSearchPayload}
     * @param pageable  {@link Pageable}
     * @return {@link User}
     */
    @Override
    public PageData search(AircraftBuildSearchPayload searchDto, Pageable pageable) {
        Page<AircraftBuildViewModel> aircraftBuildPage
                = repository.findBySearchCriteria(searchDto.getAircraftId(), searchDto.getModelName(),
                searchDto.getPartNo(), searchDto.getHigherModelName(),
                searchDto.getHigherPartNo(), searchDto.getIsActive(), pageable);
        List<AircraftBuildViewModel> aircraftBuildViewModels = aircraftBuildPage.getContent();
        addPositionToResponse(aircraftBuildViewModels);
        return PageData.builder()
                .model(aircraftBuildViewModels)
                .totalPages(aircraftBuildPage.getTotalPages())
                .totalElements(aircraftBuildPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private void addPositionToResponse(List<AircraftBuildViewModel> aircraftBuildViewModelList) {
        if (CollectionUtils.isEmpty(aircraftBuildViewModelList)) {
            return;
        }

        Set<Long> positionIds = aircraftBuildViewModelList
                .stream().filter(aircraftBuildViewModel -> Objects.nonNull(aircraftBuildViewModel
                        .getPositionId()))
                .map(aircraftBuildViewModel -> aircraftBuildViewModel.getPositionId())
                .collect(Collectors.toSet());

        List<Position> positionList = positionIService.getAllByDomainIdIn(positionIds, Boolean.TRUE);
        if (CollectionUtils.isEmpty(positionList)) {
            return;
        }
        Map<Long, Position> positionMap = positionList.stream()
                .collect(Collectors.toMap(position -> position.getId(), Function.identity()));
        aircraftBuildViewModelList.forEach(aircraftBuildViewModel -> {
            Long positionId = aircraftBuildViewModel.getPositionId();
            if (Objects.nonNull(positionId) && Objects.nonNull(positionMap.get(positionId))) {
                aircraftBuildViewModel.setPositionName(positionMap.get(positionId).getName());
            }
        });
    }

    /**
     * build specification for given entity
     *
     * @param searchPayload {@link AircraftBuildSearchPayload}
     * @return {@link Specification<AircraftBuild>}
     */
    @Override
    protected Specification<AircraftBuild> buildSpecification(AircraftBuildSearchPayload searchPayload) {
        CustomSpecification<AircraftBuild> customSpecification = new CustomSpecification<>();
        return Specification
                .where(customSpecification.equalSpecificationAtRoot(searchPayload.getAircraftId(), "aircraftId"));
    }

    /**
     * Get partial aircraft build list
     *
     * @param aircraftId {@link Long}
     * @return {@link List<AircraftBuild>}
     */
    @Override
    public List<AircraftBuild> findByAircraftId(Long aircraftId) {
        return repository.findByAircraftId(aircraftId);
    }

    /**
     * Save all entity
     *
     * @param aircraftBuildList {@link List<AircraftBuild>}
     * @return {@link List<AircraftBuild>}
     */
    @Override
    public List<AircraftBuild> saveAll(List<AircraftBuild> aircraftBuildList) {
        return saveItemList(aircraftBuildList);
    }

    @Override
    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftId) {
        if (Objects.isNull(aircraftId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_ID_IS_REQUIRED);
        }
        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_AIRCRAFT_BUILD), AC_BUILD);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = validateAndPrepareEntity(excelData, aircraftId);

        if (CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    /**
     * This method is responsible for searching aircraft build with part and serial
     *
     * @param aircraftBuildPartSerialSearchDto {@link AircraftBuildPartSerialSearchDto}
     * @return {@link AircraftBuildPartSerialSearchViewModel}
     */
    @Override
    public AircraftBuildPartSerialSearchViewModel searchByPartIdAndSerial(
            AircraftBuildPartSerialSearchDto aircraftBuildPartSerialSearchDto) {

        Optional<AircraftBuildPartSerialSearchViewModel> searchViewModel =
                repository.findByPartIdAndSerialIdIsActiveFalse(aircraftBuildPartSerialSearchDto.getPartId(),
                        aircraftBuildPartSerialSearchDto.getSerialId());
        AircraftBuildPartSerialSearchViewModel model;
        if (searchViewModel.isPresent()) {
            model = searchViewModel.get();
            Double usedHour = 0D;
            Integer usedCycle = 0;

            if (Objects.nonNull(model.getAircraftOutHour())) {
                usedHour = DateUtil.calculateHour(model.getAircraftOutHour(), model.getAircraftInHour(),
                        HourCalculationType.SUBTRACT);
            }

            if (Objects.nonNull(model.getAircraftOutCycle())) {
                usedCycle = model.getAircraftOutCycle() - model.getAircraftInCycle();
            }

            if (model.getIsTsnAvailable()) {
                Double tsnHour = DateUtil.calculateHour(model.getTsnHour(), usedHour,
                        HourCalculationType.ADD);
                Integer tsnCycle = model.getTsnCycle() + usedCycle;
                model.setTsnHour(tsnHour);
                model.setTsnCycle(tsnCycle);
            }

            if (model.getIsOverhauled() || model.getIsShopVisited()) {
                if (Objects.nonNull(model.getTslsvHour())) {
                    model.setTslsvHour(DateUtil.calculateHour(model.getTslsvHour(), usedHour,
                            HourCalculationType.ADD));
                }

                if (Objects.nonNull(model.getTslsvCycle())) {
                    Integer tslvCycle = model.getTslsvCycle() + usedCycle;
                    model.setTslsvCycle(tslvCycle);
                }

                if (model.getIsOverhauled()) {
                    if (Objects.nonNull(model.getTsoHour())) {
                        model.setTsoHour(DateUtil.calculateHour(model.getTsoHour(), usedHour,
                                HourCalculationType.ADD));
                    }

                    if (Objects.nonNull(model.getTsoCycle())) {
                        model.setTsoCycle(model.getTsoCycle() + usedCycle);
                    }
                }
            }
        } else {
            throw EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_FOUND);
        }
        return model;
    }

    @Override
    public AircraftBuildPartSerialSearchViewModel searchByPartIdAndSerialByStoreInspection(
            AircraftBuildPartSerialSearchDto aircraftBuildPartSerialSearchDto) {
        PartLifeStatusResponseDto partLifeStatusResponseDto = partLifeStatusService.getPartLifeStatus(
                aircraftBuildPartSerialSearchDto.getPartId(),
                aircraftBuildPartSerialSearchDto.getSerialId()
        );
        if (partLifeStatusResponseDto.getIsPresent().equals(Boolean.TRUE)) {
            AircraftBuildPartSerialSearchViewModel viewModel = new AircraftBuildPartSerialSearchViewModel();
            if (Objects.nonNull(partLifeStatusResponseDto.getTsn()) &&
                    Objects.nonNull(partLifeStatusResponseDto.getCsn())) {
                viewModel.setIsTsnAvailable(Boolean.TRUE);
            } else {
                viewModel.setIsTsnAvailable(Boolean.FALSE);
            }
            viewModel.setTsnHour(partLifeStatusResponseDto.getTsn());
            viewModel.setTsnCycle(partLifeStatusResponseDto.getCsn());

            viewModel.setIsOverhauled(partLifeStatusResponseDto.getIsOverHaul());
            viewModel.setTsoHour(partLifeStatusResponseDto.getTso());
            viewModel.setTsoCycle(partLifeStatusResponseDto.getCso());

            viewModel.setIsShopVisited(partLifeStatusResponseDto.getIsShopCheck());
            viewModel.setTslsvHour(partLifeStatusResponseDto.getTsr());
            viewModel.setTslsvCycle(partLifeStatusResponseDto.getCsr());

            return viewModel;
        } else {
            throw EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_FOUND);
        }
    }

    /**
     * This method is responsible for get propeller report by serial No. & part No
     * from Aircraft Build where model type is propeller
     *
     * @return {@link PropellerResponseData}
     */
    @Override
    public PropellerResponseData getPropellerReport(PropellerReportDto propellerReportDto) {

        LocalDate currentDate = DateUtil.getCurrentUTCDate();
        if (Objects.nonNull(propellerReportDto.getDate())
                && propellerReportDto.getDate().isAfter(currentDate)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DATE_FORMAT);
        }

        PropellerResponseData propellerResponseData = new PropellerResponseData();
        PropellerReportHeaderData propellerReportHeaderData = new PropellerReportHeaderData();

        AircraftBuild aircraftBuild = repository.findById(propellerReportDto.getAircraftBuildId())
                .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));

        Aircraft aircraft = null;

        if (Objects.nonNull(propellerReportDto.getDate())) {
            aircraft = getAircraftInfoByAircraftBuildId
                    (propellerReportDto.getAircraftBuildId(), propellerReportDto.getDate());
        } else {
            aircraft = aircraftBuild.getAircraft();
        }

        List<PropellerReportQueryViewModel> propellerReportViewModels = repository.getPropellerReport(
                aircraft.getId(),
                aircraftBuild.getPartId(),
                aircraftBuild.getSerialId());

        List<PropellerReportViewModel> propellerReportViewModelList = new ArrayList<>();

        Aircraft finalAircraft = aircraft;

        propellerReportViewModels.forEach(propellerReport -> {
            PropellerReportViewModel propellerReportViewModel = new PropellerReportViewModel();
            propellerReportViewModel.setNomenClature(propellerReport.getPartDesc());
            propellerReportViewModel.setPartNo(propellerReport.getPartNo());
            propellerReportViewModel.setSerialNo(propellerReport.getSerialNo());
            propellerReportViewModel.setInstallationDate(propellerReport.getDoneDate());
            propellerReportViewModel.setInstallationTsn(propellerReport.getAircraftInHour());
            propellerReportViewModel.setInstallationTso(propellerReport.getAircraftInHour());

            if (Objects.nonNull(propellerReport.getDueDate())) {
                propellerReportViewModel.setRemainingDay(Objects.nonNull(propellerReportDto.getDate()) ?
                        ChronoUnit.DAYS.between(propellerReportDto.getDate(), propellerReport.getDueDate())
                        : ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(),
                        propellerReport.getDueDate()));
            }

            if (propellerReport.getIsTsnAvailable() == Boolean.TRUE) {
                propellerReportViewModel.setCurrentTsn(calculateAirtime(propellerReport.getTsnHour(),
                        finalAircraft.getAirFrameTotalTime(), propellerReportViewModel.getInstallationTsn()));
            }

            if (propellerReport.getIsOverhauled() == Boolean.TRUE) {
                propellerReportViewModel.setCurrentTso(calculateAirtime(propellerReport.getTsoHour(),
                        finalAircraft.getAirFrameTotalTime(), propellerReportViewModel.getInstallationTso()));
            }

            if (Objects.nonNull(propellerReport.getDueHour())) {
                propellerReportViewModel.setRemainingHour(DateUtil.calculateHour(propellerReport.getDueHour(),
                        finalAircraft.getAirFrameTotalTime(),
                        HourCalculationType.SUBTRACT));
            }

            propellerReportViewModel.setDueDate(propellerReport.getDueDate());
            propellerReportViewModel.setLimitFh(propellerReport.getDueHour());
            propellerReportViewModel.setEstimatedDate(propellerReport.getEstimatedDueDate());

            propellerReportViewModelList.add(propellerReportViewModel);
        });

        propellerReportHeaderData.setTat(aircraft.getAirFrameTotalTime());
        propellerReportHeaderData.setTac(aircraft.getAirframeTotalCycle());

        if (aircraftBuild.getIsTsnAvailable() == Boolean.TRUE) {
            propellerReportHeaderData.setPropTsn(calculateAirtime(aircraftBuild.getTsnHour(),
                    aircraft.getAirFrameTotalTime(), aircraftBuild.getAircraftInHour()));
            propellerReportHeaderData.setPropCsn(calculateAirCycle(aircraftBuild.getTsnCycle(),
                    aircraft.getAirframeTotalCycle(), aircraftBuild.getAircraftInCycle(),
                    aircraftBuild.getPart().getCountFactor()));
        }
        if (aircraftBuild.getIsOverhauled() == Boolean.TRUE) {
            propellerReportHeaderData.setPropTso(calculateAirtime(aircraftBuild.getTsoHour(),
                    aircraft.getAirFrameTotalTime(), aircraftBuild.getAircraftInHour()));
            propellerReportHeaderData.setPropCso(calculateAirCycle(aircraftBuild.getTsoCycle(),
                    aircraft.getAirframeTotalCycle(), aircraftBuild.getAircraftInCycle(),
                    aircraftBuild.getPart().getCountFactor()));
        }
        propellerReportHeaderData.setModelName(aircraftBuild.getModel().getModelName());
        propellerReportHeaderData.setPropPartNo(aircraftBuild.getPart().getPartNo());
        propellerReportHeaderData.setPropSerialNo(aircraftBuild.getSerial().getSerialNumber());
        propellerReportHeaderData.setPositionName(aircraftBuild.getPosition().getName());
        propellerReportHeaderData.setUpdatedDate(aircraft.getUpdatedAt());

        propellerResponseData.setPropellerReportHeaderData(propellerReportHeaderData);

        propellerResponseData.setPropellerReportViewModelList(propellerReportViewModelList);

        return propellerResponseData;
    }

    @Override
    public List<PropellerACBuildIdAndPositionViewModel> getPropellerPositionNameByAircraftId(Long aircraftId) {
        return repository.getPropellerPositionNameByAircraftId(aircraftId, ModelType.getByName(PROPELLER));
    }

    @Override
    @Transactional
    public void makeAcBuildInActive(AircraftBuildInactiveDto aircraftBuildInactiveDto) {

        Optional<AircraftBuild> aircraftBuildOptional = repository.findById(aircraftBuildInactiveDto.getId());
        if (aircraftBuildOptional.isPresent()) {
            AircraftBuild aircraftBuild = aircraftBuildOptional.get();
            if (aircraftBuild.getAircraftInHour() > aircraftBuildInactiveDto.getAircraftOutHour() ||
                    aircraftBuild.getAircraftInCycle() > aircraftBuildInactiveDto.getAircraftOutCycle()) {
                throw EngineeringManagementServerException.
                        badRequest(ErrorId.OUT_HOUR_CYCLE_MUST_BE_GREATER_THAN_IN_HOUR_CYCLE);
            }
            AircraftBuild updatedEntity = convertToInactiveEntity(aircraftBuild, aircraftBuildInactiveDto);
            List<Ldnd> exLdnds = ldndRepository.findAllByAircraftIdAndPartIdAndSerialIdAndIsActiveTrue(
                    updatedEntity.getAircraftId(), updatedEntity.getPartId(), updatedEntity.getSerialId());

            if (aircraftBuildInactiveDto.getCheckLowerPart().equals(true)) {
                updateWithLowerPart(updatedEntity, exLdnds, aircraftBuildInactiveDto);
            } else {
                this.saveItem(updatedEntity);
                if (CollectionUtils.isNotEmpty(exLdnds)) {
                    try {
                        this.ldndRepository.makeInActive(exLdnds.stream().map(AbstractDomainBasedEntity::getId)
                                .collect(Collectors.toList()));
                    } catch (Exception ex) {
                        String name = exLdnds.get(0).getClass().getSimpleName();
                        throw EngineeringManagementServerException.dataSaveException(
                                Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, name));
                    }
                }

                try {
                    partInactiveInfoPostService.setPartInactiveInfo(List.of(populatePartReturnDto(updatedEntity)));
                } catch (Exception ignored) {}
            }

        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.AIRCRAFT_BUILD_NOT_FOUND);
        }
    }

    private void updateWithLowerPart(AircraftBuild updatedEntity, List<Ldnd> exLdnds,
                                     AircraftBuildInactiveDto aircraftBuildInactiveDto) {

        List<AircraftBuild> childAcBuilds = repository.findChildAcBuild(updatedEntity.getAircraftId(),
                updatedEntity.getPartId(), updatedEntity.getSerialId());

        List<AircraftBuild> updatedAcBuildList = new ArrayList<>();

        updatedAcBuildList.add(convertToInactiveEntity(updatedEntity, aircraftBuildInactiveDto));

        childAcBuilds.forEach(ac -> updatedAcBuildList.add(convertToInactiveEntity(ac, aircraftBuildInactiveDto)));

        List<Long> ldndIds = ldndRepository.findAllChildAcBuildLdnd(updatedEntity.getAircraftId(),
                updatedEntity.getPartId(), updatedEntity.getSerialId());

        ldndIds.addAll(exLdnds.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toList()));
        this.saveAll(updatedAcBuildList);
        if (CollectionUtils.isNotEmpty(ldndIds)) {
            try {
                this.ldndRepository.makeInActive(ldndIds);
            } catch (Exception ex) {
                String name = exLdnds.get(0).getClass().getSimpleName();
                throw EngineeringManagementServerException.dataSaveException(
                        Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, name));
            }
        }

        try {
            List<AcBuildPartReturnDto> acBuildPartReturnDtoList = new ArrayList<>();
            updatedAcBuildList.forEach(acBuild -> {
                acBuildPartReturnDtoList.add(populatePartReturnDto(acBuild));
            });
            partInactiveInfoPostService.setPartInactiveInfo(acBuildPartReturnDtoList);
        } catch (Exception ignored) {}
    }

    private AircraftBuild convertToInactiveEntity(AircraftBuild aircraftBuild, AircraftBuildInactiveDto dto) {
        aircraftBuild.setIsActive(false);
        aircraftBuild.setRemovalReason(dto.getRemovalReason());
        aircraftBuild.setOutDate(dto.getOutDate());
        aircraftBuild.setOutRefMessage(dto.getOutRefMessage());
        aircraftBuild.setAircraftOutHour(dto.getAircraftOutHour());
        aircraftBuild.setAircraftOutCycle(dto.getAircraftOutCycle());
        return aircraftBuild;
    }

    @Override
    public Page<OCCMViewModel> findOCCMByAircraftId(OCCMSearchDto occmSearchDto, Pageable pageable) {
        Page<OCCMQueryViewModel> occmQueryViewModels = repository.findAircraftBuildByAircraftId(occmSearchDto.getAircraftId(),
                occmSearchDto.getDescription(), occmSearchDto.getPartNumber(), occmSearchDto.getSerialNumber(),
                occmSearchDto.getInstallationDate(), occmSearchDto.getInstallationFH(), occmSearchDto.getInstallationFC(),
                ModelType.getOccmModelTypes(), pageable);

        Aircraft aircraft = aircraftRepository.getById(occmSearchDto.getAircraftId());

        Page<OCCMViewModel> occmViewModelPage = occmQueryViewModels.map(occmQueryViewModel -> {
            OCCMViewModel viewModel = convertToOCCViewModel(occmQueryViewModel, aircraft);
            return viewModel;
        });
        return occmViewModelPage;
    }

    OCCMViewModel convertToOCCViewModel(OCCMQueryViewModel viewModel, Aircraft aircraft) {
        OCCMViewModel occmViewModel = new OCCMViewModel();

        occmViewModel.setAta(viewModel.getAta());
        occmViewModel.setDescription(viewModel.getDescription());
        occmViewModel.setPartNumber(viewModel.getPartNumber());
        occmViewModel.setSerialNumber(viewModel.getSerialNumber());

        if (Objects.nonNull(viewModel.getPosition())) {
            occmViewModel.setLocation(viewModel.getPosition().getName());
        }

        occmViewModel.setTaskType(ON_COND);
        occmViewModel.setInstallationDate(viewModel.getInstallationDate());
        occmViewModel.setInstallationFH(viewModel.getInstallationFH());
        occmViewModel.setInstallationFC(viewModel.getInstallationFC());

        if (viewModel.getIsTsnAvailable() == Boolean.TRUE) {
            occmViewModel.setCurrentTSN(calculateAirtime(viewModel.getCurrentTSN(),
                    aircraft.getAirFrameTotalTime(), viewModel.getInstallationFH()));

            occmViewModel.setCurrentCSN(calculateAirCycle(viewModel.getCurrentCSN(),
                    aircraft.getAirframeTotalCycle(),
                    viewModel.getInstallationFC(),
                    viewModel.getCountFactor()));

        }

        if (viewModel.getIsOverhauled() == Boolean.TRUE) {
            occmViewModel.setCurrentTSO(calculateAirtime(viewModel.getCurrentTSO(),
                    aircraft.getAirFrameTotalTime(), viewModel.getInstallationFH()));

            occmViewModel.setCurrentCSO(calculateAirCycle(viewModel.getCurrentCSO(),
                    aircraft.getAirframeTotalCycle(),
                    viewModel.getInstallationFC(),
                    viewModel.getCountFactor()));
        }

        if (viewModel.getIsShopVisited() == Boolean.TRUE) {
            occmViewModel.setCurrentTSR(calculateAirtime(viewModel.getCurrentTSR(),
                    aircraft.getAirFrameTotalTime(), viewModel.getInstallationFH()));

            occmViewModel.setCurrentCSR(calculateAirCycle(viewModel.getCurrentCSR(),
                    aircraft.getAirframeTotalCycle(),
                    viewModel.getInstallationFC(),
                    viewModel.getCountFactor()));
        }

        return occmViewModel;
    }

    Integer calculateAirCycle(Integer baseCycle, Integer totalCycle, Integer inCycle,
                              Double countFactor) {
        if (Objects.isNull(baseCycle) || Objects.isNull(countFactor) || Objects.isNull(inCycle)) {
            return null;
        }
        // TODO:
        //return (int)( countFactor * requiredCycle );
        return baseCycle + totalCycle - inCycle;
    }

    Double calculateAirtime(Double baseAirtime, Double totalAirtime, Double inAirtime) {

        if (Objects.isNull(baseAirtime) || Objects.isNull(inAirtime)) {
            return null;
        }
        return DateUtil.convertMinutesToHour(DateUtil.convertToMinutes(baseAirtime)
                + DateUtil.convertToMinutes(totalAirtime) - DateUtil.convertToMinutes(inAirtime));
    }

    /**
     * responsible for finding aircraft engines by aircraft id
     *
     * @param aircraftId aircraft id
     * @return aircraft engines as view model
     */
    @Override
    public List<EngineViewModel> findAircraftEnginesByAircraftId(Long aircraftId) {
        return repository.findAircraftEnginesByAircraftId(aircraftId, ModelType.ENGINE);
    }

    @Override
    public Set<AcSerialResponse> findAcSerialResponseByPartIdAndModelId(Long partId, Long modelId) {
        return repository.findAcSerialResponseByPartIdAndModelId(partId, modelId);
    }

    @Override
    public Set<AcPartResponse> getAcPartResponseByModelId(Long modelId) {
        return repository.findAcPartResponseByModelId(modelId);
    }

    @Override
    public AcComponentViewModel getComponentHistoryList(Long partId, Long serialId) {
        AcComponentViewModel acComponentViewModel = new AcComponentViewModel();
        AircraftBuild aircraftBuild = repository.findTopByPartIdAndSerialId(partId, serialId);
        if (ObjectUtils.isNotEmpty(aircraftBuild)) {
            acComponentViewModel.setSerialNo(aircraftBuild.getSerial().getSerialNumber());
            acComponentViewModel.setAtaChapter(aircraftBuild.getAircraftLocation().getName());
            acComponentViewModel.setAlternatePartNo(
                    aircraftBuild.getPart().getAlternatePartSet().stream().map(Part::getPartNo).collect(Collectors.toSet()));
            acComponentViewModel.setPartName(aircraftBuild.getModel().getModelName());
            acComponentViewModel.setPartNo(aircraftBuild.getPart().getPartNo());
            acComponentViewModel.setHigherPartNo(aircraftBuild.getHigherPart().getPartNo());
            acComponentViewModel.setComManufactureDate(aircraftBuild.getComManufactureDate());
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.AIRCRAFT_BUILD_NOT_FOUND_BY_PART_SERIAL);
        }

        List<TaskComponentResponse> taskComponentResponses = ldndRepository.findTaskResponseByPartIdAndSerialNo(
                aircraftBuild.getPartId(), aircraftBuild.getSerialId(), IntervalType.INTERVAL);

        Set<String> dueFor = new HashSet<>();
        Set<String> taskCardRef = new HashSet<>();
        Set<String> ampRef = new HashSet<>();

        if (CollectionUtils.isNotEmpty(taskComponentResponses)) {
            taskComponentResponses.forEach(task -> {
                if (Objects.nonNull(task.getTaskType())) {
                    dueFor.add(task.getTaskType());
                }
                if (Objects.nonNull(task.getJobProcedure())) {
                    taskCardRef.add(task.getJobProcedure());
                }

                if (Objects.nonNull(task.getTaskNo())) {
                    ampRef.add(task.getTaskNo());
                }
            });
        }

        taskComponentResponses.removeIf(t -> t.getIsActive().equals(false));
        acComponentViewModel.setTboData(taskComponentResponses);

        acComponentViewModel.setTaskCardRef(taskCardRef);
        acComponentViewModel.setDueFor(dueFor);
        acComponentViewModel.setAmpRef(ampRef);
        Set<String> higherSerials = new HashSet<>();
        List<AcComponentHistory> acComponentHistories = repository.getComponentHistoryList(aircraftBuild.getPartId(),
                aircraftBuild.getSerialId());
        acComponentHistories.forEach(ac -> {
            addUsedTimeCycle(ac);
            addFittedTimeCycle(ac);
            higherSerials.add(ac.getHigherSerialNo());
        });
        acComponentViewModel.setHigherSerialNo(higherSerials);
        acComponentViewModel.setAcComponentHistories(acComponentHistories);


        Part part = aircraftBuild.getPart();
        acComponentViewModel.setLifeLimit(part.getLifeLimit());
        acComponentViewModel.setLifeLimitUnit(Objects.nonNull(part.getLifeLimitUnit()) ? part.getLifeLimitUnit().name() : null);
        if (Objects.nonNull(acComponentViewModel.getLifeLimit())) {
            if (part.getLifeLimitUnit().equals(LifeLimitUnit.AH) || part.getLifeLimitUnit().equals(LifeLimitUnit.FH)) {
                Double hour = DateUtil.calculateHour(aircraftBuild.getAircraftInHour(), acComponentViewModel.getLifeLimit().doubleValue(),
                        HourCalculationType.ADD);
                hour = DateUtil.calculateHour(hour, aircraftBuild.getTsnHour(), HourCalculationType.SUBTRACT);
                acComponentViewModel.setDiscardDueHour(hour);
            } else if (part.getLifeLimitUnit().equals(LifeLimitUnit.AC) || part.getLifeLimitUnit().equals(LifeLimitUnit.FC)) {
                Long cycle = aircraftBuild.getAircraftInCycle() + acComponentViewModel.getLifeLimit() - aircraftBuild.getTsnCycle();
                acComponentViewModel.setDiscardDueCycle(cycle);
            } else if (part.getLifeLimitUnit().equals(LifeLimitUnit.DY)) {
                LocalDate dom = aircraftBuild.getComManufactureDate();
                dom = dom.plusDays(acComponentViewModel.getLifeLimit());
                acComponentViewModel.setDiscardDueDate(dom);
            }
        }

        return acComponentViewModel;
    }

    private void calculateMinHourCycle(AtomicReference<Double> tboHour, AtomicReference<Integer> tboCycle,
                                       AtomicReference<Integer> tboDay, TaskComponentResponse componentResponse) {
        if (Objects.nonNull(componentResponse.getTboIntervalHour())) {
            tboHour.set(Math.min(tboHour.get(), componentResponse.getTboIntervalHour()));
        }
        if (Objects.nonNull(componentResponse.getTboIntervalCycle())) {
            tboCycle.set(Math.min(tboCycle.get(), componentResponse.getTboIntervalCycle()));
        }

        if (Objects.nonNull(componentResponse.getTboIntervalDay())) {
            tboDay.set(Math.min(tboDay.get(), componentResponse.getTboIntervalDay()));
        }
    }

    private void addUsedTimeCycle(AcComponentHistory ac) {
        if (Objects.nonNull(ac.getAircraftOutHour()) && Objects.nonNull(ac.getAircraftInHour())) {
            ac.setUsedHour(DateUtil.calculateHour(ac.getAircraftOutHour(), ac.getAircraftInHour(),
                    HourCalculationType.SUBTRACT));
        }

        if (Objects.nonNull(ac.getAircraftOutCycle()) && Objects.nonNull(ac.getAircraftInCycle())) {
            ac.setUsedCycle(ac.getAircraftOutCycle() - ac.getAircraftInCycle());
        }
    }

    private void addFittedTimeCycle(AcComponentHistory ac) {

        if (Objects.nonNull(ac.getUsedHour())) {

            if (Objects.nonNull(ac.getTimeNewHour())) {
                ac.setFittedTsn(DateUtil.calculateHour(ac.getTimeNewHour(), ac.getUsedHour(),
                        HourCalculationType.ADD));
            }

            if (Objects.nonNull(ac.getTimeOverHaulHour())) {
                ac.setFittedTso(DateUtil.calculateHour(ac.getTimeOverHaulHour(), ac.getUsedHour(),
                        HourCalculationType.ADD));
            }
        }

        if (Objects.nonNull(ac.getUsedCycle())) {

            if (Objects.nonNull(ac.getTimeNewCycle())) {
                ac.setFittedCsn(ac.getTimeNewCycle() + ac.getUsedCycle());
            }

            if (Objects.nonNull(ac.getTimeOverHaulCycle())) {
                ac.setFittedCso(ac.getTimeOverHaulCycle() + ac.getUsedCycle());
            }
        }
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData, Long aircraftId) {

        List<String> errorMessages = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }
        Set<Long> modelIds = modelService.findModelIdsByAircraftId(aircraftId);
        Aircraft aircraft = aircraftIService.findById(aircraftId);
        List<ModelTree> modelTreeList = modelTreeIService.findAllModelTreeByAircraftId(modelIds);

        Set<String> existingModelTreeKeys = new HashSet<>();
        Map<String, Model> modelMap = new HashMap<>();
        Map<String, Position> positionMap = new HashMap<>();
        Map<String, AircraftLocation> aircraftLocationMap = new HashMap<>();

        updateEntityMap(modelTreeList, existingModelTreeKeys, modelMap, positionMap, aircraftLocationMap);

        Set<Part> partList = partService.findAllParteByModelIdIn(modelIds);
        Map<String, Part> partMap = partList.stream().collect(Collectors
                .toMap(part -> StringUtil.buildKey(part.getPartNo(), part.getModelId()), Function.identity()));


        Set<Serial> serials = serialRepository.findAllByPartIdInAndIsActiveTrue(partList.stream()
                .map(AbstractDomainBasedEntity::getId).collect(Collectors.toList()));

        Map<String, Serial> serialMap = serials.stream().collect(Collectors
                .toMap(serial -> StringUtil.buildKey(serial.getSerialNumber(), serial.getPartId()), Function.identity()));

        List<Map<String, ?>> dataList = excelData.getDataList();
        List<AircraftBuild> aircraftBuildList = new ArrayList<>();
        Set<String> existingAircraftBuildKeys = getExistingAircraftBuildKey();
        Set<String> uniqueModelTreeKeys = new HashSet<>();

        List<AircraftBuild> aircraftBuilds = repository.findByAircraftId(aircraftId);
        //TODO Need to check for model tree
//        Map<String, AircraftBuild> aircraftBuildMap = aircraftBuilds.stream()
//                .collect(Collectors.toMap(aircraftBuild -> buildKey(aircraftBuild.getModelId(),
//                        aircraftBuild.getHigherModelId(), aircraftBuild.getPositionId(), aircraftBuild.getLocationId()),
//                        Function.identity()));
        Set<String> partAndPositionKeySet = new HashSet<>();

        for (AircraftBuild aircraftBuild : aircraftBuilds) {
            partAndPositionKeySet.add((Objects.nonNull(aircraftBuild.getPart()) ? aircraftBuild.getPart().getPartNo() :
                    " ") + COLON + (Objects.nonNull(aircraftBuild.getPosition())
                    ? aircraftBuild.getPosition().getName() : " "));
        }

        for (Map<String, ?> dataMap : dataList) {

            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            if (!isValidByModelTreeConfiguration(dataMap, existingModelTreeKeys, errorMessages, rowNumber)) {
                continue;
            }

            boolean isValid = isValidClientData(
                    dataMap, modelMap, errorMessages, aircraftLocationMap, positionMap, partMap);

            if (isValid) {
                AircraftBuild aircraftBuild = new AircraftBuild();
                Model model = modelMap.get(dataMap.get(MODEL));
                aircraftBuild.setModel(model);
                Model higherModel = modelMap.get(dataMap.get(HIGHER_MODEL));
                aircraftBuild.setHigherModel(higherModel);
                aircraftBuild.setPosition(positionMap.get(dataMap.get(POSITION)));
                aircraftBuild.setAircraftLocation(aircraftLocationMap.get(dataMap.get(LOCATION)));
                String partNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(PART_NUMBER)));
                aircraftBuild.setPart(partMap.get(StringUtil.buildKey(partNo, model.getId())));
                String higherPartNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(HIGHER_PART_NUMBER)));
                aircraftBuild.setHigherPart(partMap.get(StringUtil.buildKey(higherPartNo, higherModel.getId())));
                aircraftBuild.setAircraft(aircraft);

                //TODO: validate later
                /*prepareModelTreeKeyAndValidate(model, higherModel, aircraftBuild, aircraftBuildMap, rowNumber,
                        errorMessages, String.valueOf(dataMap.get(MODEL)), String.valueOf(dataMap.get(HIGHER_MODEL)),
                                String.valueOf(dataMap.get(POSITION)), String.valueOf(dataMap.get(LOCATION)),
                        uniqueModelTreeKeys);*/

                String serialNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(SERIAL_NUMBER)));
                String serialKey = StringUtil.buildKey(serialNo, aircraftBuild.getPart().getId());

                if (serialMap.containsKey(serialKey)) {
                    aircraftBuild.setSerial(serialMap.get(serialKey));
                } else {
                    errorMessages.add(String.format("Serial no doesn't exists : Serial: {%s}, at row: {%s}",
                            serialNo, rowNumber));
                }

                if (existingAircraftBuildKeys.contains(serialKey)) {
                    isValid = false;
                    errorMessages.add(String.format("Similar data exists for part : {%s} serial: {%s}, at row: {%s}",
                            aircraftBuild.getPart().getPartNo(), aircraftBuild.getSerial().getSerialNumber(),
                            rowNumber));
                }

                String higherSerialNo =
                        StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(HIGHER_SERIAL_NUMBER)));
                String higherSerialKey = StringUtil.buildKey(higherSerialNo, aircraftBuild.getHigherPart().getId());

                if (serialMap.containsKey(higherSerialKey)) {
                    aircraftBuild.setHigherSerial(serialMap.get(higherSerialKey));
                } else {
                    errorMessages.add(String.format("Higher serial no doesn't exists : higher serial: {%s}, at row: {%s}"
                            , higherSerialNo, rowNumber));
                }

                if (isValid) {
                    updateAircraftBuildFromClientData(aircraftBuild, dataMap);
                    aircraftBuildList.add(aircraftBuild);
                }
            }
        }

        if (CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(aircraftBuildList)) {
            saveAircraftBuilds(aircraftBuildList, errorMessages);
        }
        return errorMessages;
    }

    private void prepareModelTreeKeyAndValidate(Model model, Model higherModel, AircraftBuild aircraftBuild,
                                                Map<String, AircraftBuild> aircraftBuildMap, int rowNumber,
                                                List<String> errorMessages, String modelName, String higherModelName,
                                                String positionName, String locationName,
                                                Set<String> uniqueModelTreeKeys) {
        Long modelId = Objects.nonNull(model) ? model.getId() : null;
        Long higherModelId = Objects.nonNull(higherModel) ? higherModel.getId() : null;
        Long locationId = Objects.nonNull(aircraftBuild.getAircraftLocation()) ?
                aircraftBuild.getAircraftLocation().getId() : null;
        Long positionId = Objects.nonNull(aircraftBuild.getPosition()) ? aircraftBuild.getPosition().getId() : null;

        String modelTreeKey = buildKey(modelId, higherModelId, positionId, locationId);

        if (!uniqueModelTreeKeys.add(modelTreeKey)) {
            addErrorMessage(rowNumber, errorMessages, modelName, higherModelName, positionName, locationName);
            return;
        }

        if (aircraftBuildMap.containsKey(modelTreeKey)) {
            addErrorMessage(rowNumber, errorMessages, modelName, higherModelName, positionName, locationName);
        }
        uniqueModelTreeKeys.add(modelTreeKey);
    }

    private void addErrorMessage(int rowNumber, List<String> errorMessages, String modelName, String higherModelName,
                                 String positionName, String locationName) {
        errorMessages.add(
                String.format("Duplicate aircraft build exists for same aircraft, model, higher model, " +
                        "location and position. Model: {%s}, HigherModel: {%s}, Location: {%s}, Position: {%s}, " +
                        "at row {%s}", modelName, higherModelName, locationName, positionName, rowNumber));
    }

    private String buildKey(Long modelId, Long higherModelId, Long positionId, Long locationId) {

        return (Objects.nonNull(modelId) ? modelId + ApplicationConstant.SEPARATOR : "") +
                (Objects.nonNull(higherModelId) ? higherModelId + ApplicationConstant.SEPARATOR : "") +
                (Objects.nonNull(locationId) ? locationId + ApplicationConstant.SEPARATOR : "") +
                (Objects.nonNull(positionId) ? positionId + ApplicationConstant.SEPARATOR : "");
    }

    private void saveAircraftBuilds(List<AircraftBuild> aircraftBuildList, List<String> errorMessages) {
        try {
            repository.saveAll(aircraftBuildList);
        } catch (Exception ex) {
            LOGGER.error("Exception happened while saving aircraft build. Exception: {}.", ex.getMessage());
            errorMessages.add(String.format("Exception happened while saving aircraft build. Exception: {%s}",
                    ex.getMessage()));
        }
    }

    private Set<String> getExistingAircraftBuildKey() {
        List<AcBuildPartSerialResponse> acBuildPartSerialResponseList = repository.findAllExistingPartSerialList();
        if (CollectionUtils.isEmpty(acBuildPartSerialResponseList)) {
            return Collections.emptySet();
        }
        Set<String> keys = new HashSet<>();
        acBuildPartSerialResponseList.forEach(
                aircraftBuild -> keys.add(StringUtil.buildKey(aircraftBuild.getSerialNo(), aircraftBuild.getPartId())));
        return keys;
    }

    private void updateEntityMap(List<ModelTree> modelTreeList, Set<String> existingModelTreeKeys,
                                 Map<String, Model> modelMap, Map<String, Position> positionMap,
                                 Map<String, AircraftLocation> aircraftLocationMap) {
        if (CollectionUtils.isEmpty(modelTreeList)) {
            return;
        }

        modelTreeList.forEach(modelTree -> {
            existingModelTreeKeys.add(StringUtil.buildKey(modelTree.getModel().getModelName(),
                    modelTree.getHigherModel().getModelName(),
                    Objects.isNull(modelTree.getPosition()) ? " " : modelTree.getPosition().getName(),
                    modelTree.getAircraftLocation().getName()));

            modelMap.put(modelTree.getModel().getModelName(), modelTree.getModel());
            modelMap.put(modelTree.getHigherModel().getModelName(), modelTree.getHigherModel());
            if (Objects.nonNull(modelTree.getPosition())) {
                positionMap.put(modelTree.getPosition().getName(), modelTree.getPosition());
            }
            aircraftLocationMap.put(modelTree.getAircraftLocation().getName(), modelTree.getAircraftLocation());
        });
    }

    private boolean isValidByModelTreeConfiguration(Map<String, ?> dataMap,
                                                    Set<String> existingModelTreeKeys,
                                                    List<String> errorMessages, int rowNumber) {
        String modelName = String.valueOf(dataMap.get(MODEL));
        String higherModelName = String.valueOf(dataMap.get(HIGHER_MODEL));
        String locationName = String.valueOf(dataMap.get(LOCATION));
        String positionName = StringUtil.valueOf(dataMap.get(POSITION));
        String key = StringUtil.buildKey(modelName, higherModelName, StringUtils.isBlank(positionName) ? " " :
                        positionName,
                locationName);
        if (!existingModelTreeKeys.contains(key)) {
            errorMessages.add(String.format("Aircraft build not possible. Model tree is not available with " +
                            "model: {%s}, higherModel: {%s}, location: {%s}, position: {%s} at row : {%s}",
                    modelName, higherModelName, locationName, positionName, rowNumber));
            return false;
        }
        return true;
    }

    private boolean isValidClientData(Map<String, ?> dataMap, Map<String, Model> modelMap,
                                      List<String> errorMessages, Map<String, AircraftLocation> aircraftLocationMap,
                                      Map<String, Position> positionMap, Map<String, Part> partMap) {
        int rowNumber = Integer.valueOf(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
        boolean isValid;
        String modelName = StringUtil.valueOf(dataMap.get(MODEL));
        String higherModelName = StringUtil.valueOf(dataMap.get(HIGHER_MODEL));

        isValid = ExcelFileUtil.addErrorIfKeyNotExists(modelName, MODEL, modelMap, rowNumber, errorMessages);

        isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(
                StringUtil.valueOf(dataMap.get(HIGHER_MODEL)), HIGHER_MODEL, modelMap, rowNumber, errorMessages);

        isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(
                StringUtil.valueOf(dataMap.get(LOCATION)), LOCATION, aircraftLocationMap, rowNumber, errorMessages);

        String position = StringUtil.valueOf(dataMap.get(POSITION));
        if (position != null && !position.isBlank()) {
            isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(
                    StringUtil.valueOf(dataMap.get(POSITION)), POSITION, positionMap, rowNumber, errorMessages);
        }

        String partNumber = StringUtil.valueOf(dataMap.get(PART_NUMBER));
        String partKey = StringUtil.buildKey(partNumber,
                Objects.nonNull(modelMap.get(modelName)) ? modelMap.get(modelName).getId() : null);
        isValid = isValid & addErrorIfPartNotExists(partKey, PART_NUMBER, partMap, rowNumber, errorMessages);

        String higherPartNumber = StringUtil.valueOf(dataMap.get(HIGHER_PART_NUMBER));
        String higherPartKey = StringUtil.buildKey(higherPartNumber,
                Objects.nonNull(modelMap.get(higherModelName)) ? modelMap.get(higherModelName).getId() : null);
        isValid = isValid & addErrorIfPartNotExists(higherPartKey,
                HIGHER_PART_NUMBER, partMap, rowNumber, errorMessages);

        return isValid;
    }

    public static boolean addErrorIfPartNotExists(String keyName,
                                                  String column,
                                                  Map dataMap, Integer rowNumber, List<String> errorMessages) {
        if (!dataMap.containsKey(keyName)) {
            String[] keys = keyName.split(ApplicationConstant.SEPARATOR);
            errorMessages.add(String.format("{%s}: {%s} is not present or not active at row : {%s}" +
                    " Or this is not a part of this model", column, keys.length > 1 ? keys[1] : "part", rowNumber));
            return false;
        }
        return true;
    }

    private void updateAircraftBuildFromClientData(AircraftBuild aircraftBuild,
                                                   Map<String, ?> data) {
        try {
            aircraftBuild.setTsnHour((Double) data.get(TSN_HOUR));
            aircraftBuild.setTsnCycle((Integer) data.get(TSN_CYCLE));
            if (Objects.nonNull(aircraftBuild.getTsnHour()) || Objects.nonNull(aircraftBuild.getTsnCycle())) {
                aircraftBuild.setIsTsnAvailable(Boolean.TRUE);
            } else {
                aircraftBuild.setIsTsnAvailable(Boolean.FALSE);
            }
            aircraftBuild.setAttachDate((LocalDate) data.get(ATTACH_DATE));
            aircraftBuild.setTsoHour((Double) data.get(TSO_HOUR));
            aircraftBuild.setTsoCycle((Integer) data.get(TSO_CYCLE));
            aircraftBuild.setTslsvHour((Double) data.get(TSLSV_HOUR));
            aircraftBuild.setTslsvCycle((Integer) data.get(TSLSV_CYCLE));
            aircraftBuild.setComManufactureDate((LocalDate) data.get(COMPONENT_MANUFACTURE_DATE));
            aircraftBuild.setComCertificateDate((LocalDate) data.get(COMPONENT_CERTIFICATE_DATE));
            aircraftBuild.setIsOverhauled(Boolean.FALSE);
            aircraftBuild.setIsShopVisited(Boolean.FALSE);
            aircraftBuild.setAircraftInHour((Double) data.get(AIRCRAFT_IN_HOUR));
            aircraftBuild.setAircraftInCycle((Integer) data.get(AIRCRAFT_IN_CYCLE));
            aircraftBuild.setInRefMessage((String) data.get(IN_REF_MESSAGE));
            if (Objects.nonNull(aircraftBuild.getTsoHour())
                    || Objects.nonNull(aircraftBuild.getTsoCycle())
                    || Objects.nonNull(aircraftBuild.getTslsvCycle())
                    || Objects.nonNull(aircraftBuild.getTslsvHour())) {
                aircraftBuild.setIsShopVisited(Boolean.TRUE);
                aircraftBuild.setIsOverhauled(Boolean.TRUE);
            }
        } catch (ClassCastException classCastException) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.CLASS_CAST_EXCEPTION);
        }
    }

    @Override
    public List<AircraftBuild> findAllTmmAndRgbByHigherSerialAndPart(Long higherSerialId, Long higherPartId) {
        return repository.findAllTmmAndRgbByHigherSerial(higherSerialId, higherPartId, ModelType.ENGINE_TMM,
                ModelType.ENGINE_RGB);
    }

    @Override
    public List<AircraftBuild> findAllInactivateTmmAndRgbByHigherSerialAndPart(Long higherSerialId, Long higherPartId) {
        return repository.findAllInactivateTmmAndRgbByHigherSerialAndPart(higherSerialId, higherPartId, ModelType.ENGINE_TMM,
                ModelType.ENGINE_RGB);
    }

    @Override
    public List<AircraftBuild> findAllEngineLlpParts(Long serialId, Long partId) {
        return repository.findAllEngineLlpParts(serialId, partId, ModelType.ENGINE_LLP);
    }

    @Override
    public List<AircraftBuild> findAllInactivateEngineLlpParts(Long serialId, Long partId) {
        return repository.findAllInactivateEngineLlpParts(serialId, partId, ModelType.ENGINE_LLP);
    }

    @Override
    public AircraftEngineDetailsViewModel findAircraftEngineDetailsForAdReport(Long serialId, Long partId,
                                                                               Long aircraftId, LocalDate date) {

        LocalDate currentDate = DateUtil.getCurrentUTCDate();
        if (Objects.nonNull(date)
                && date.isAfter(currentDate)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DATE_FORMAT);
        }
        Optional<AircraftEngineDetailsViewModel> optionalData = repository.findAircraftEngineDetailsByPartAndSerialId(
                serialId, partId, aircraftId);

        if (optionalData.isPresent()) {
            AircraftEngineDetailsViewModel data = optionalData.get();

            if (Objects.nonNull(date)) {
                Aircraft aircraft = getAircraftInfoByAircraftId(aircraftId, date);
                data.setTat(aircraft.getAirFrameTotalTime());
                data.setTac(aircraft.getAirframeTotalCycle());
                data.setDate(aircraft.getUpdatedAt());
            }

            if (Objects.nonNull(data.getTat()) && Objects.nonNull(data.getInHour()) && Objects.nonNull(data.getTsn())) {
                data.setTsn(DateUtil.addTimes(DateUtil.subtractTimes(data.getTat(), data.getInHour()), data.getTsn()));
            }

            if (Objects.nonNull(data.getTac()) && Objects.nonNull(data.getInCycle()) && Objects.nonNull(data.getCsn())) {
                data.setCsn(data.getCsn() + (data.getTac() - data.getInCycle()));
            }

            sharedAircraftInformation.setTac(data.getTac());
            sharedAircraftInformation.setTat(data.getTat());
            return data;
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
    }

    @Override
    public ApuStatusReportViewModel getApuStatusReport(Long aircraftId) {

        Aircraft aircraft = aircraftService.findById(aircraftId);

        AircraftApus aircraftApus = aircraftApusRepository.findByAircraftId(aircraftId);

        ApuShopVisitInfo apuShopVisitInfo = new ApuShopVisitInfo();

        if (Objects.nonNull(aircraftApus)) {
            apuShopVisitInfo.setModel(aircraftApus.getModel());
            apuShopVisitInfo.setStatus(aircraftApus.getStatus());
            apuShopVisitInfo.setTsn(aircraftApus.getTsn());
            apuShopVisitInfo.setDate(aircraftApus.getDate());
            apuShopVisitInfo.setCsr(aircraftApus.getCsr());
            apuShopVisitInfo.setCsn(aircraftApus.getCsn());
            apuShopVisitInfo.setTsr(aircraftApus.getTsr());
        }

        List<ApuStatusModel> apuStatusViewModelList = repository.getApuStatusReport(aircraftId);

        List<ApuStatusAircraftInfo> getApuStatusAircraftInfo = repository.getApuStatusAircraftInfo(aircraftId,
                ModelType.APU);

        ApuInfo apuInfo = new ApuInfo();

        apuInfo.setApuTsn(aircraft.getTotalApuHours());
        apuInfo.setApuCsn(aircraft.getTotalApuCycle());
        if (Objects.nonNull(apuShopVisitInfo.getTsr()) && Objects.nonNull(apuShopVisitInfo.getCsr())
                && Objects.nonNull(apuShopVisitInfo.getTsn()) && Objects.nonNull(apuInfo.getApuCsn())) {
            apuInfo.setApuTSR(DateUtil.addTimes(DateUtil.subtractTimes(aircraft.getTotalApuHours(),
                    apuShopVisitInfo.getTsn()), apuShopVisitInfo.getTsr()));
            apuInfo.setApuCSR((aircraft.getTotalApuCycle() - apuShopVisitInfo.getCsn()) + apuShopVisitInfo.getCsr());
        }

        if (ObjectUtils.isNotEmpty(getApuStatusAircraftInfo)) {
            prepareApuInfo(getApuStatusAircraftInfo.get(0), apuInfo);
        }

        List<ApuStatusReportModel> apuStatusReportModelList = new ArrayList<>();

        apuStatusViewModelList.forEach(d -> {

            ApuStatusReportModel apuStatusReportModel = new ApuStatusReportModel();
            apuStatusReportModel.setNoMenClature(d.getNoMenClature());
            apuStatusReportModel.setPartNo(d.getPartNo());
            apuStatusReportModel.setSerialNo(d.getSerialNo());
            apuStatusReportModel.setInstallationDate(d.getInstallationDate());
            apuStatusReportModel.setInstallationCsn(d.getInstallationCsn());
            apuStatusReportModel.setInstallationTsn(d.getInstallationTsn());
            apuStatusReportModel.setLifeLimit(d.getLifeLimit());
            apuStatusReportModel.setEstimatedDueDate(d.getEstimatedDueDate());
            apuStatusReportModel.setLifeLimitUnit(d.getLifeLimitUnit());
            apuStatusReportModel.setDueDate(d.getDueDate());
            apuStatusReportModel.setDueCycle(d.getDueCycle());
            apuStatusReportModel.setDueHour(d.getDueHour());
            apuStatusReportModel.setRemainingCycle(d.getRemainingCycle());
            apuStatusReportModel.setRemainingHour(d.getRemainingHour());

            if (Objects.nonNull(d.getDueDate())) {
                apuStatusReportModel.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(),
                        d.getDueDate()));
            }

            if (Objects.nonNull(d.getInstallationTsn())) {
                apuStatusReportModel.setCurrentTsn(DateUtil.addTimes(
                        DateUtil.subtractTimes(aircraft.getTotalApuHours(), d.getAircraftInHour())
                        , d.getInstallationTsn()));
            }

            if (Objects.nonNull(d.getInstallationCsn())) {
                apuStatusReportModel.setCurrentCsn(aircraft.getTotalApuCycle() - d.getAircraftInCycle()
                        + d.getInstallationCsn());
            }

            apuStatusReportModelList.add(apuStatusReportModel);
        });
        return ApuStatusReportViewModel.builder()
                .apuStatusReportModel(apuStatusReportModelList)
                .apuShopVisitInfo(apuShopVisitInfo)
                .apuInfo(apuInfo)
                .acType(aircraft.getAircraftModel().getAircraftModelName())
                .acRegn(aircraft.getAircraftName())
                .acMsn(aircraft.getAirframeSerial())
                .date(aircraft.getUpdatedAt())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .averageHours(aircraft.getDailyAverageApuHours())
                .averageCycle(aircraft.getDailyAverageApuCycle())
                .build();
    }

    @Override
    public List<EngineViewModel> findInactivateAircraftEnginesByAircraftId(Long aircraftId) {
        return repository.findInactivateAircraftEnginesByAircraftId(aircraftId, ModelType.ENGINE);
    }

    private void prepareApuInfo(ApuStatusAircraftInfo apuStatusAircraftInfo, ApuInfo apuInfo) {
        apuInfo.setApuPartNo(apuStatusAircraftInfo.getPartNo());
        apuInfo.setApuSerialNo(apuStatusAircraftInfo.getSerialNo());
    }

    @Override
    public ApuStatusReportViewModel getApuRemovedStatusReport(Long aircraftId) {

        Aircraft aircraft = aircraftService.findById(aircraftId);

        AircraftApus aircraftApus = aircraftApusRepository.findByAircraftId(aircraftId);
        ApuShopVisitInfo apuShopVisitInfo = new ApuShopVisitInfo();


        List<ApuRemovedStatusModel> apuStatusViewModelList = repository.getApuRemovedStatusReport(aircraftId);

        List<ApuRemovedStatusAircraftInfo> getApuStatusAircraftInfo = repository.getApuRemovedStatusAircraftInfo(aircraftId,
                ModelType.APU);

        ApuInfo apuInfo = new ApuInfo();


        if (ObjectUtils.isNotEmpty(getApuStatusAircraftInfo)) {
            aircraft = getAircraftInfoByAircraftId(aircraftId, getApuStatusAircraftInfo.get(0).getOutDate());
            prepareApuRemovedInfo(getApuStatusAircraftInfo.get(0), apuInfo);
        }

        apuInfo.setApuTsn(aircraft.getTotalApuHours());
        apuInfo.setApuCsn(aircraft.getTotalApuCycle());
        if (Objects.nonNull(apuShopVisitInfo.getTsr()) && Objects.nonNull(apuShopVisitInfo.getCsr())
                && Objects.nonNull(apuShopVisitInfo.getTsn()) && Objects.nonNull(apuInfo.getApuCsn())) {
            apuInfo.setApuTSR(DateUtil.addTimes(DateUtil.subtractTimes(aircraft.getTotalApuHours(),
                    apuShopVisitInfo.getTsn()), apuShopVisitInfo.getTsr()));
            apuInfo.setApuCSR((aircraft.getTotalApuCycle() - apuShopVisitInfo.getCsn()) + apuShopVisitInfo.getCsr());
        }

        if (Objects.nonNull(aircraftApus) && (aircraftApus.getDate().equals(aircraft.getUpdatedAt()) ||
                aircraftApus.getDate().isBefore(aircraft.getUpdatedAt()))) {
            apuShopVisitInfo.setModel(aircraftApus.getModel());
            apuShopVisitInfo.setStatus(aircraftApus.getStatus());
            apuShopVisitInfo.setTsn(aircraftApus.getTsn());
            apuShopVisitInfo.setDate(aircraftApus.getDate());
            apuShopVisitInfo.setCsr(aircraftApus.getCsr());
            apuShopVisitInfo.setCsn(aircraftApus.getCsn());
            apuShopVisitInfo.setTsr(aircraftApus.getTsr());
        }

        List<ApuStatusReportModel> apuStatusReportModelList = new ArrayList<>();

        apuStatusViewModelList.forEach(d -> {

            Double usedHour = PlanningUtil.calculateUsedHours(d.getAircraftOutHour(), d.getAircraftInHour());
            Integer usedCycle = PlanningUtil.calculateUsedCycle(d.getAircraftOutCycle(), d.getAircraftInCycle());

            ApuStatusReportModel apuStatusReportModel = new ApuStatusReportModel();
            apuStatusReportModel.setNoMenClature(d.getNoMenClature());
            apuStatusReportModel.setPartNo(d.getPartNo());
            apuStatusReportModel.setSerialNo(d.getSerialNo());
            apuStatusReportModel.setInstallationDate(d.getInstallationDate());
            apuStatusReportModel.setInstallationCsn(d.getInstallationCsn());
            apuStatusReportModel.setInstallationTsn(d.getInstallationTsn());
            apuStatusReportModel.setLifeLimit(d.getLifeLimit());
            apuStatusReportModel.setEstimatedDueDate(d.getEstimatedDueDate());
            apuStatusReportModel.setLifeLimitUnit(d.getLifeLimitUnit());
            apuStatusReportModel.setDueDate(d.getDueDate());
            apuStatusReportModel.setDueCycle(d.getDueCycle());
            apuStatusReportModel.setDueHour(d.getDueHour());
            apuStatusReportModel.setRemainingCycle(d.getRemainingCycle());
            apuStatusReportModel.setRemainingHour(d.getRemainingHour());

            if (Objects.nonNull(d.getDueDate())) {
                apuStatusReportModel.setRemainingDay(ChronoUnit.DAYS.between(d.getOutDate(),
                        d.getDueDate()));
            }

            if (Objects.nonNull(d.getInstallationTsn())) {
                apuStatusReportModel.setCurrentTsn(DateUtil.addTimes(
                       usedHour, d.getInstallationTsn()));
            }

            if (Objects.nonNull(d.getInstallationCsn())) {
                apuStatusReportModel.setCurrentCsn(usedCycle + d.getInstallationCsn());
            }

            apuStatusReportModelList.add(apuStatusReportModel);
        });
        return ApuStatusReportViewModel.builder()
                .apuStatusReportModel(apuStatusReportModelList)
                .apuShopVisitInfo(apuShopVisitInfo)
                .apuInfo(apuInfo)
                .acType(aircraft.getAircraftModel().getAircraftModelName())
                .acRegn(aircraft.getAircraftName())
                .acMsn(aircraft.getAirframeSerial())
                .date(aircraft.getUpdatedAt())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .averageHours(aircraft.getDailyAverageApuHours())
                .averageCycle(aircraft.getDailyAverageApuCycle())
                .build();
    }

    private void prepareApuRemovedInfo(ApuRemovedStatusAircraftInfo apuRemovedStatusAircraftInfo,
                                       ApuInfo apuInfo) {
        apuInfo.setApuPartNo(apuRemovedStatusAircraftInfo.getPartNo());
        apuInfo.setApuSerialNo(apuRemovedStatusAircraftInfo.getSerialNo());
    }

    @Override
    public Optional<AcBuildPartReturnDto> getAcBuildPartReturn(Long partId, Long serialId) {
        Optional<AircraftBuild> optionalAircraftBuild = repository.findInactiveAcBuildByPartSerial(partId, serialId);
        if (optionalAircraftBuild.isPresent()) {
            AircraftBuild aircraftBuild = optionalAircraftBuild.get();
            AcBuildPartReturnDto acBuildPartReturnDto = populatePartReturnDto(aircraftBuild);
            return Optional.of(acBuildPartReturnDto);
        }
        return Optional.empty();
    }

    private AcBuildPartReturnDto populatePartReturnDto(AircraftBuild aircraftBuild) {
        return AcBuildPartReturnDto.builder()
                .aircraftId(aircraftBuild.getAircraftId())
                .partId(aircraftBuild.getPartId())
                .serialId(aircraftBuild.getSerialId())
                .positionId(aircraftBuild.getPositionId())
                .tsn(aircraftBuild.getTsnHour())
                .csn(aircraftBuild.getTsnCycle())
                .tso(aircraftBuild.getTsoHour())
                .cso(aircraftBuild.getTsoCycle())
                .tsr(aircraftBuild.getTslsvHour())
                .csr(aircraftBuild.getTslsvCycle())
                .removalReason(aircraftBuild.getRemovalReason())
                .removalDate(aircraftBuild.getOutDate())
                .isInactive(!aircraftBuild.getIsActive())
                .sign(aircraftBuild.getSign())
                .authNo(aircraftBuild.getAuthNo())
                .createdDate(aircraftBuild.getAttachDate())
                .build();
    }


    @Override
    public List<AircraftBuildExcelViewModel> getAllBuildAircraft() {
        return repository.findAllIsActiveAircraftBuild();
    }
}
