package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.NumberConstant;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckIndexDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckIndexSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.ManHourReportDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftCheckIndexRepository;
import com.digigate.engineeringmanagement.planning.service.impl.LdndServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AircraftCheckIndex Service
 *
 * @author Ashraful
 */
@Service
public class AircraftCheckIndexService extends AbstractSearchService<AircraftCheckIndex, AircraftCheckIndexDto,
        AircraftCheckIndexSearchDto> {

    private static final String PLUS_DELIMITER = " + ";
    private static final String SB_TASK_SOURCE_TYPE = "SB";
    private static final double PROPOSED_MAN_HOUR_MULTIPLY_FACTOR = 3.5;
    private static final String COMMA_DELIMITER = ", ";
    private static final String B_1 = "B1";
    private static final String B_2 = "B2";
    private final AircraftCheckService aircraftCheckService;
    private final LdndService ldndService;
    private static final String AC_CHECK_IS_ACTIVE = "isActive";
    private static final String AIRCRAFT_ID = "aircraftId";
    private static final String WORK_ORDER = "workOrder";
    private static final String WORK_ORDER_NO = "woNo";
    private final AircraftCheckIndexRepository aircraftCheckIndexRepository;

    /**
     * Autowired constructor
     * @param repository {@link AbstractRepository}
     * @param aircraftCheckService {@link AircraftCheckService}
     * @param ldndService {@link LdndServiceImpl}
     * @param aircraftCheckIndexRepository {@link AircraftCheckIndexRepository}
     */
    public AircraftCheckIndexService(AbstractRepository<AircraftCheckIndex> repository,
                                     AircraftCheckService aircraftCheckService, LdndServiceImpl ldndService,
                                     AircraftCheckIndexRepository aircraftCheckIndexRepository) {
        super(repository);
        this.aircraftCheckService = aircraftCheckService;
        this.ldndService = ldndService;
        this.aircraftCheckIndexRepository = aircraftCheckIndexRepository;
    }

    @Override
    protected Specification<AircraftCheckIndex> buildSpecification(AircraftCheckIndexSearchDto searchDto) {
        CustomSpecification<AircraftCheckIndex> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(),
                AC_CHECK_IS_ACTIVE)
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(),AIRCRAFT_ID))
                .and(customSpecification.likeSpecificationAtChild(searchDto.getWoNo(),
                        WORK_ORDER, WORK_ORDER_NO)));
    }

    @Override
    protected AircraftCheckIndexViewModel convertToResponseDto(AircraftCheckIndex aircraftCheckIndex) {

        Set<AircraftCheckForAircraftViewModel> aircraftCheckForAircraftViewModelSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(aircraftCheckIndex.getAircraftTypeCheckSet())) {

            aircraftCheckIndex.getAircraftTypeCheckSet().forEach(acTypeCheck -> {
                AircraftCheckForAircraftViewModel aircraftCheckForAircraftViewModel
                        = new AircraftCheckForAircraftViewModel();
                aircraftCheckForAircraftViewModel.setAcCheckId(acTypeCheck.getId());
                aircraftCheckForAircraftViewModel.setCheckTitle(Objects.nonNull(acTypeCheck.getCheck()) ? acTypeCheck
                        .getCheck().getTitle() : null);
                aircraftCheckForAircraftViewModelSet.add(aircraftCheckForAircraftViewModel);
            });
        }

        Set<LdndForTaskViewModel> ldndForTaskViewModelSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(aircraftCheckIndex.getLdndSet())) {

            aircraftCheckIndex.getLdndSet().forEach(ldnd -> {
                LdndForTaskViewModel ldndForTaskViewModel
                        = new LdndForTaskViewModel();
                ldndForTaskViewModel.setLdndId(ldnd.getId());
                ldndForTaskViewModel.setTaskNo(Objects.nonNull(ldnd.getTask()) ? ldnd.getTask().getTaskNo() : null);
                ldndForTaskViewModel.setPartNo(Objects.nonNull(ldnd.getPart()) ? ldnd.getPart().getPartNo() : null);
                ldndForTaskViewModel.setSerialNo(Objects.nonNull(ldnd.getSerial()) ?
                        ldnd.getSerial().getSerialNumber() : null);
                ldndForTaskViewModelSet.add(ldndForTaskViewModel);
            });
        }

        return AircraftCheckIndexViewModel.builder()
                .id(aircraftCheckIndex.getId())
                .aircraftId(aircraftCheckIndex.getAircraftId())
                .aircraftName(Objects.nonNull(aircraftCheckIndex.getAircraft()) ? aircraftCheckIndex.getAircraft()
                        .getAircraftName() : null)
                .woId(aircraftCheckIndex.getWoId())
                .woNo(Objects.nonNull(aircraftCheckIndex.getWorkOrder()) ? aircraftCheckIndex.getWorkOrder()
                        .getWoNo() : null)
                .doneDate(aircraftCheckIndex.getDoneDate())
                .doneHour(aircraftCheckIndex.getDoneHour())
                .doneCycle(aircraftCheckIndex.getDoneCycle())
                .aircraftTypeCheckSet(aircraftCheckForAircraftViewModelSet)
                .ldndForTaskViewModelSet(ldndForTaskViewModelSet)
                .isActive(aircraftCheckIndex.getIsActive())
                .build();
    }

    @Override
    protected AircraftCheckIndex convertToEntity(AircraftCheckIndexDto aircraftCheckIndexDto) {
        return saveOrUpdate(aircraftCheckIndexDto, new AircraftCheckIndex());
    }

    /**
     * convert dto to entity for save/update purpose
     *
     * @param aircraftCheckIndexDto {@link AircraftCheckIndexDto}
     * @param aircraftCheckIndex    {@link AircraftCheckIndex}
     * @return aircraftCheckIndex   {@link AircraftCheckIndex}
     */
    private AircraftCheckIndex saveOrUpdate(AircraftCheckIndexDto aircraftCheckIndexDto,
                                            AircraftCheckIndex aircraftCheckIndex) {

        Aircraft aircraft = new Aircraft();
        aircraft.setId(aircraftCheckIndexDto.getAircraftId());
        aircraftCheckIndex.setAircraft(aircraft);

        if (aircraftCheckIndexDto.getWoId() == null) {
            aircraftCheckIndex.setWorkOrder(null);
        } else {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setId(aircraftCheckIndexDto.getWoId());
            aircraftCheckIndex.setWorkOrder(workOrder);
        }

        aircraftCheckIndex.setDoneDate(aircraftCheckIndexDto.getDoneDate());
        aircraftCheckIndex.setDoneHour(aircraftCheckIndexDto.getDoneHour());
        aircraftCheckIndex.setDoneCycle(aircraftCheckIndexDto.getDoneCycle());

        if (CollectionUtils.isNotEmpty(aircraftCheckIndex.getAircraftTypeCheckSet())) {
            aircraftCheckIndex.getAircraftTypeCheckSet().clear();
        }

        if (CollectionUtils.isNotEmpty(aircraftCheckIndexDto.getAircraftTypeCheckIds())) {
            List<AircraftCheck> aircraftCheckList = aircraftCheckService.getAllByDomainIdIn(aircraftCheckIndexDto
                    .getAircraftTypeCheckIds(), true);
            if (aircraftCheckIndexDto.getAircraftTypeCheckIds().size() != aircraftCheckList.size()) {
                throw EngineeringManagementServerException.notFound(ErrorId.AC_TYPE_CHECK_NOT_FOUND);
            }
            aircraftCheckIndex.setAircraftTypeCheckSet(new HashSet<>(aircraftCheckList));
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.AC_TYPE_CHECK_IDS_MUST_NOT_BE_NULL, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        if (CollectionUtils.isNotEmpty(aircraftCheckIndex.getLdndSet())) {
            aircraftCheckIndex.getLdndSet().clear();
        }

        if (CollectionUtils.isNotEmpty(aircraftCheckIndexDto.getLdndIds())) {
            List<Ldnd> ldndList = ldndService.getAllLdndByDomainIdIn(aircraftCheckIndexDto
                    .getLdndIds(), true);
            if (aircraftCheckIndexDto.getLdndIds().size() != ldndList.size()) {
                throw EngineeringManagementServerException.notFound(ErrorId.LDND_NOT_FOUND);
            }
            aircraftCheckIndex.setLdndSet(new HashSet<>(ldndList));
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.LDNDIDS_MUST_NOT_BE_NULL, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return aircraftCheckIndex;
    }

    @Override
    protected AircraftCheckIndex updateEntity(AircraftCheckIndexDto dto, AircraftCheckIndex entity) {
        return saveOrUpdate(dto, entity);
    }

    /**
     * This method is responsible for getAcCheckIndexById ForSingleView
     *
     * @param acCheckIndexId {@link Long}
     * @return aircraftCheckIndexForSingleViewModel  {@link AircraftCheckIndexForSingleViewModel}
     */
    public AircraftCheckIndexForSingleViewModel getAcCheckIndexByIdForSingleView(Long acCheckIndexId) {
        List<String> acCheckIndexNameList = new ArrayList<>();
        List<AircraftCheckIndexLdndViewModel> aircraftCheckIndexLdndViewModels = new ArrayList<>();
        Optional<AircraftCheckIndex> aircraftCheckIndex = aircraftCheckIndexRepository.findById(acCheckIndexId);
        if (aircraftCheckIndex.isPresent()) {
            Set<Ldnd> ldndSet = aircraftCheckIndex.get().getLdndSet();
            ldndSet.forEach(
                    ldnd -> {
                        AircraftCheckIndexLdndViewModel aircraftCheckIndexLdndViewModel =
                                new AircraftCheckIndexLdndViewModel();
                        aircraftCheckIndexLdndViewModel.setTaskCardReference(Objects.nonNull(ldnd.getTaskProcedure())
                                ? ldnd.getTaskProcedure().getJobProcedure() : null);
                        aircraftCheckIndexLdndViewModel.setAmpReference(Objects.nonNull(ldnd.getTask())
                                ? ldnd.getTask().getTaskNo() : null);
                        aircraftCheckIndexLdndViewModel.setTaskDescription(Objects.nonNull(ldnd.getTask())
                                ? ldnd.getTask().getDescription() : null);
                        aircraftCheckIndexLdndViewModel.setPartNo(Objects.nonNull(ldnd.getPart())
                                ? ldnd.getPart().getPartNo() : null);
                        aircraftCheckIndexLdndViewModel.setSerialNo(ldnd.getSerial().getSerialNumber());
                        aircraftCheckIndexLdndViewModel.setSerialId(ldnd.getSerialId());
                        aircraftCheckIndexLdndViewModel.setInspType(Objects.nonNull(ldnd.getTask().getTaskType())
                                ? ldnd.getTask().getTaskType().getName() : null);
                        aircraftCheckIndexLdndViewModel.setCompletionDate(ldnd.getDoneDate());
                        aircraftCheckIndexLdndViewModel.setTrades(ldnd.getTask().getTrade());
                        aircraftCheckIndexLdndViewModels.add(aircraftCheckIndexLdndViewModel);
                    }
            );
            Set<AircraftCheck> aircraftCheckSet = aircraftCheckIndex.get().getAircraftTypeCheckSet();
            aircraftCheckSet.forEach(
                    aircraftCheck -> {
                        String acCheckIndexName = Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck.getCheck()
                                .getTitle() : null;
                        if(Objects.nonNull(acCheckIndexName)){
                            acCheckIndexNameList.add(acCheckIndexName);  
                        }
                    }
            );

        }
        return AircraftCheckIndexForSingleViewModel.builder()
                .id(acCheckIndexId)
                .acRegn(Objects.nonNull(aircraftCheckIndex.get().getAircraft()) ? aircraftCheckIndex.get()
                        .getAircraft().getAircraftName() : null)
                .acMsn(Objects.nonNull(aircraftCheckIndex.get().getAircraft()) ? aircraftCheckIndex.get()
                        .getAircraft().getAirframeSerial() : null)
                .acCycles(Objects.nonNull(aircraftCheckIndex.get().getAircraft()) ? aircraftCheckIndex.get()
                        .getAircraft().getAirframeTotalCycle() : null)
                .acHours(Objects.nonNull(aircraftCheckIndex.get().getAircraft()) ? aircraftCheckIndex.get()
                        .getAircraft().getAirFrameTotalTime() : null)
                .woDate(Objects.nonNull(aircraftCheckIndex.get().getWorkOrder()) ? aircraftCheckIndex.get()
                        .getWorkOrder().getDate() : null)
                .woNo(Objects.nonNull(aircraftCheckIndex.get().getWorkOrder()) ? aircraftCheckIndex.get()
                        .getWorkOrder().getWoNo() : null)
                .asOfDate(Objects.nonNull(aircraftCheckIndex.get().getWorkOrder()) ? aircraftCheckIndex.get()
                        .getWorkOrder().getAsOfDate() : null)
                .acCheckIndexNames(acCheckIndexNameList)
                .aircraftCheckIndexLdnds(aircraftCheckIndexLdndViewModels)
                .build();
    }

    /**
     * This method is responsible for getting Aircraft check index id as well as check title list
     *
     * @param aircraftId {@link Long}
     * @return a viewmodel with Ac check Id and list ot titiles {@link AircraftCheckIndexIdAndCheckViewModel}
     */
    public List<AircraftCheckIndexIdAndCheckViewModel> getAcCheckIndexByAircraftId(Long aircraftId) {
        List<AircraftCheckIndexIdAndCheckViewModel> aircraftCheckIndexIdAndCheckViewModels = new ArrayList<>();
        aircraftCheckIndexRepository.getAircraftCheckIndexByAircraftIdAndIsActiveTrue(aircraftId).forEach(aircraftCheckIndex -> {
            AircraftCheckIndexIdAndCheckViewModel viewModel = new AircraftCheckIndexIdAndCheckViewModel();
            List<String> checksTitles = new ArrayList<>();
            for (AircraftCheck aircraftCheck : aircraftCheckIndex.getAircraftTypeCheckSet()) {
                checksTitles.add(aircraftCheck.getCheck().getTitle());
            }
            viewModel.setId(aircraftCheckIndex.getId());
            viewModel.setTitles(checksTitles);
            aircraftCheckIndexIdAndCheckViewModels.add(viewModel);

        });
        return aircraftCheckIndexIdAndCheckViewModels;
    }

    /**
     * responsible generating man hour report
     *
     * @param acCheckIndexId   acCheckIndexId
     * isPageable              boolean value which decided report will be printed or not
     * @param pageable         {@link Pageable}
     * @return                 man hour report response
     */
        public ManHourReportViewModel getManHourReport(Long acCheckIndexId, Boolean isPageable, Pageable pageable) {
        Optional<AircraftCheckIndex> aircraftCheckIndexOptional =
                aircraftCheckIndexRepository.findByIdAndIsActiveTrue(acCheckIndexId);

        if (aircraftCheckIndexOptional.isPresent()) {
            AircraftCheckIndex aircraftCheckIndex = aircraftCheckIndexOptional.get();

            ManHourReportViewModel manHourReportViewModel = new ManHourReportViewModel();

            manHourReportViewModel.setAircraftChecksName(prepareAircraftChecks(aircraftCheckIndex));
            manHourReportViewModel.setAircraftName(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAircraftName() : null);
            manHourReportViewModel.setAirframeSerial(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAirframeSerial() : null);
            manHourReportViewModel.setWoNo(Objects.nonNull(aircraftCheckIndex.getWorkOrder()) ?
                    aircraftCheckIndex.getWorkOrder().getWoNo() : null);
            manHourReportViewModel.setDate(DateUtil.getCurrentUTCDate());

            List<ManHourTaskViewModel> manHourTaskViewModels = new ArrayList<>();
            if (!isPageable) {
                processedAircraftIndexTasks(new ArrayList<>(aircraftCheckIndex.getLdndSet()),
                        manHourTaskViewModels, manHourReportViewModel);
                manHourReportViewModel.setManHourTaskViewModels(manHourTaskViewModels);
            } else {
                Page<Ldnd> ldndPage =
                        aircraftCheckIndexRepository.getAllLdndByAircraftCheckIndexId(acCheckIndexId, pageable);

                processedAircraftIndexTasks(ldndPage.getContent(), manHourTaskViewModels, manHourReportViewModel);
                PageData pageData = new PageData(manHourTaskViewModels,
                        ldndPage.getTotalPages(), pageable.getPageNumber() + 1, ldndPage.getTotalElements());
                manHourReportViewModel.setPageData(pageData);
            }

            return manHourReportViewModel;
        } else {
            return new ManHourReportViewModel();
        }
    }

    /**
     * responsible generating man work scope report
     *
     * @param acCheckIndexId  acCheckIndexId
     * @param isPageable      boolean value which decided report will be printed or not
     * @param pageable        {@link Pageable}
     * @return                work scope report response
     */
    public WorkScopeReportViewModel getWorkScopeReport(Long acCheckIndexId, Boolean isPageable, Pageable pageable) {
        Optional<AircraftCheckIndex> aircraftCheckIndexOptional =
                aircraftCheckIndexRepository.findByIdAndIsActiveTrue(acCheckIndexId);

        if (aircraftCheckIndexOptional.isPresent()) {
            AircraftCheckIndex aircraftCheckIndex = aircraftCheckIndexOptional.get();

            WorkScopeReportViewModel workScopeReportViewModel = new WorkScopeReportViewModel();

            workScopeReportViewModel.setAircraftChecksName(prepareAircraftChecks(aircraftCheckIndex));
            workScopeReportViewModel.setAircraftName(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAircraftName() : null);
            workScopeReportViewModel.setAircraftModelName(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    Objects.nonNull(aircraftCheckIndex.getAircraft().getAircraftModel()) ?
                            aircraftCheckIndex.getAircraft().getAircraftModel().getAircraftModelName() : null : null);
            workScopeReportViewModel.setAirframeSerial(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAirframeSerial() : null);
            workScopeReportViewModel.setAirFrameTotalTime(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAirFrameTotalTime() : null);
            workScopeReportViewModel.setAirframeTotalCycle(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getAirframeTotalCycle() : null);
            workScopeReportViewModel.setManufactureDate(Objects.nonNull(aircraftCheckIndex.getAircraft()) ?
                    aircraftCheckIndex.getAircraft().getManufactureDate() : null);
            workScopeReportViewModel.setWoNo(Objects.nonNull(aircraftCheckIndex.getWorkOrder()) ?
                    aircraftCheckIndex.getWorkOrder().getWoNo() : null);
            workScopeReportViewModel.setAsOfDate(DateUtil.getCurrentUTCDate());

            List<WorkScopeTaskViewModel> workScopeTaskViewModels = new ArrayList<>();
            if (!isPageable) {
                processedAircraftIndexTasksForWorkScope(new ArrayList<>(aircraftCheckIndex.getLdndSet()),
                        workScopeTaskViewModels);
                workScopeReportViewModel.setWorkScopeTaskViewModels(workScopeTaskViewModels);
            } else {
                Page<Ldnd> ldndPage =
                        aircraftCheckIndexRepository.getAllLdndByAircraftCheckIndexId(acCheckIndexId, pageable);
                processedAircraftIndexTasksForWorkScope(ldndPage.getContent(), workScopeTaskViewModels);
                PageData pageData = new PageData(workScopeTaskViewModels,
                        ldndPage.getTotalPages(), pageable.getPageNumber() + 1, ldndPage.getTotalElements());
                workScopeReportViewModel.setPageData(pageData);
            }

            return workScopeReportViewModel;
        } else {
            return new WorkScopeReportViewModel();
        }
    }

    private void processedAircraftIndexTasksForWorkScope(List<Ldnd> ldndList,
                                             List<WorkScopeTaskViewModel> workScopeTaskViewModels) {
        if (CollectionUtils.isNotEmpty(ldndList)) {
            ldndList.forEach(ldnd -> {
                Task task = ldnd.getTask();
                workScopeTaskViewModels.add(
                        WorkScopeTaskViewModel.builder()
                                .ldndId(ldnd.getId())
                                .taskNo(task.getTaskNo())
                                .taskType(Objects.nonNull(task.getTaskType()) ? task.getTaskType().getName() : null)
                                .isApuControl(task.getIsApuControl())
                                .intervalDay(task.getIntervalDay())
                                .intervalHour(task.getIntervalHour())
                                .intervalCycle(task.getIntervalCycle())
                                .thresholdDay(task.getThresholdDay())
                                .thresholdHour(task.getThresholdHour())
                                .thresholdCycle(task.getThresholdCycle())
                                .taskDescriptionViewModel(prepareTaskDescriptionViewModel(ldnd))
                                .build()
                );
            });
        }
    }

    private void processedAircraftIndexTasks(List<Ldnd> ldndList,
                                             List<ManHourTaskViewModel> manHourTaskViewModels,
                                             ManHourReportViewModel manHourReportViewModel) {
        double totalManHour = 0.0;
        double totalProposedManHour = 0.0;
        double totalB1ProposedManHour = 0.0;
        double totalB2ProposedManHour = 0.0;
        Integer totalB1Task = 0;
        Integer totalB2Task = 0;
        for (Ldnd ldnd : ldndList) {
            Task task = ldnd.getTask();
            String trades = String.join(COMMA_DELIMITER, task.getTrade());
            Double manHour = ldnd.getTask().getManHours();
            String sources =  task.getTaskSource();

            double proposedManHours = StringUtils.isNotBlank(sources)
                    && task.getTaskSource().equals(SB_TASK_SOURCE_TYPE) ?
                    NumberUtil.getDefaultIfNull(task.getManHours(), 0.0) :
                    Objects.nonNull(task.getManHours()) ? task.getManHours() * PROPOSED_MAN_HOUR_MULTIPLY_FACTOR : 0.0;

            totalProposedManHour += proposedManHours;
            totalManHour += Objects.nonNull(manHour) ? manHour : 0.0;

            if (trades.contains(B_1)) {
                totalB1ProposedManHour += proposedManHours;
                totalB1Task++;
            }

            if (trades.contains(B_2)) {
                totalB2ProposedManHour += proposedManHours;
                totalB2Task++;
            }

            manHourTaskViewModels.add(
                    ManHourTaskViewModel.builder()
                            .ldndId(ldnd.getId())
                            .jobProcedure(Objects.nonNull(ldnd.getTaskProcedure()) ?
                                    ldnd.getTaskProcedure().getJobProcedure() : null)
                            .taskNo(task.getTaskNo())
                            .taskDescriptionViewModel(prepareTaskDescriptionViewModel(ldnd))
                            .taskType(Objects.nonNull(task.getTaskType()) ? task.getTaskType().getName() : null)
                            .trade(trades)
                            .manHours(NumberUtil.formatDecimalValue(manHour, NumberConstant.TWO_DECIMAL_FORMAT))
                            .proposedManHours(NumberUtil.formatDecimalValue(proposedManHours,
                                    NumberConstant.TWO_DECIMAL_FORMAT))
                            .noOfMan(ldnd.getNoOfMan())
                            .elapsedTime(ldnd.getElapsedTime())
                            .actualManHour(ldnd.getActualManHour())
                            .build()
            );
        }

        manHourReportViewModel.setTotalManHour(totalManHour);
        manHourReportViewModel.setTotalProposedManHour(totalProposedManHour);
        manHourReportViewModel.setTotalB1ProposedManHour(totalB1ProposedManHour);
        manHourReportViewModel.setTotalB2ProposedManHour(totalB2ProposedManHour);
        manHourReportViewModel.setTotalB1Task(totalB1Task);
        manHourReportViewModel.setTotalB2Task(totalB2Task);
    }

    private TaskDescriptionViewModel prepareTaskDescriptionViewModel(Ldnd ldnd) {
        return TaskDescriptionViewModel.builder()
                .taskDescription(ldnd.getTask().getDescription())
                .partNo(Objects.nonNull(ldnd.getPart()) ? ldnd.getPart().getPartNo() : null)
                .serialNo(ldnd.getSerial().getSerialNumber())
                .serialId(ldnd.getSerialId())
                .build();
    }

    private String prepareAircraftChecks(AircraftCheckIndex aircraftCheckIndex) {
        List<String> acCheckIndexNameList = new ArrayList<>();
        Set<AircraftCheck> aircraftCheckSet = aircraftCheckIndex.getAircraftTypeCheckSet();
        aircraftCheckSet.forEach(
                aircraftCheck -> {
                    String acCheckIndexName = Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck.getCheck()
                            .getTitle() : null;
                    if(Objects.nonNull(acCheckIndexName)){
                        acCheckIndexNameList.add(acCheckIndexName);
                    }
                }
        );
        List<String> sortedAcCheckIndexNameList = acCheckIndexNameList.stream().sorted().collect(Collectors.toList());
        return String.join(PLUS_DELIMITER, sortedAcCheckIndexNameList);
    }

    /**
     * responsible for updating ldnd
     *
     * @param manHourReportDto   {@link ManHourReportDto}
     * @return                   response status message
     */
    public String updateLdndFromManHourReport(ManHourReportDto manHourReportDto) {
        ldndService.updateLdndFromManHourReport(manHourReportDto);
        return "Ldnd updated successfully.";
    }

    /**
     * responsible for finding ac check index list by aircraft
     *
     * @param aircraftId   aircraft id
     * @return             list of aircraft check index list as view model
     */
    public List<AircraftCheckIndexForListView> getAllAircraftCheckIndex(Long aircraftId) {
        List<AircraftCheckIndex> aircraftCheckIndexList =
                aircraftCheckIndexRepository.findAllByIsActiveTrueAndAircraftId(aircraftId);

        List<AircraftCheckIndexForListView> viewModels = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(aircraftCheckIndexList)) {
            aircraftCheckIndexList.forEach(aircraftCheckIndex -> viewModels.add(convertToResponse(aircraftCheckIndex)));
        }

        return viewModels;
    }

    private AircraftCheckIndexForListView convertToResponse(AircraftCheckIndex aircraftCheckIndex) {
        return AircraftCheckIndexForListView.builder()
                .acCheckIndexId(aircraftCheckIndex.getId())
                .aircraftChecksName(prepareAircraftChecks(aircraftCheckIndex))
                .build();
    }
}
