package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DepartmentService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorService;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.AirportService;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.PartAvailabilityCountType;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.*;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.IdVoucherDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreDemandResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreDemandRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_DEMAND;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class StoreDemandService extends AbstractSearchService<StoreDemand, StoreDemandsDto, CommonWorkFlowSearchDto> {
    private final StoreDemandRepository storeDemandRepository;
    private final StoreDemandDetailsService storeDemandDetailsService;
    private final StoreIssueService storeIssueService;
    private final AircraftService aircraftService;
    private final DepartmentService departmentService;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final ProcurementRequisitionService procurementRequisitionService;
    private final AirportService airportService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final Helper helper;
    private final WorkFlowUtil workFlowUtil;
    private final GenericAttachmentService genericAttachmentService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final VendorService vendorService;
    private final DemandStatusService demandStatusService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final PartRemarkService partRemarkService;
    private final PartService partService;

    public StoreDemandService(StoreDemandRepository storeDemandRepository,
                              StoreDemandDetailsService storeDemandDetailsService,
                              @Lazy StoreIssueService storeIssueService, AircraftService aircraftService,
                              DepartmentService departmentService,
                              ApprovalStatusService approvalStatusService,
                              WorkFlowActionService workFlowActionService,
                              ApprovalEmployeeService approvalEmployeeService,
                              @Lazy ProcurementRequisitionService procurementRequisitionService,
                              AirportService airportService,
                              Helper helper,
                              WorkFlowUtil workFlowUtil,
                              StoreVoucherTrackingService storeVoucherTrackingService,
                              GenericAttachmentService genericAttachmentService,
                              StorePartAvailabilityService storePartAvailabilityService,
                              VendorService vendorService,
                              PartRemarkService partRemarkService,
                              DemandStatusServiceImpl demandStatusServiceImpl,
                              DemandStatusService demandStatusService,
                              PartService partService) {
        super(storeDemandRepository);
        this.storeDemandRepository = storeDemandRepository;
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.storeIssueService = storeIssueService;
        this.aircraftService = aircraftService;
        this.departmentService = departmentService;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.procurementRequisitionService = procurementRequisitionService;
        this.airportService = airportService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.helper = helper;
        this.workFlowUtil = workFlowUtil;
        this.genericAttachmentService = genericAttachmentService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.vendorService = vendorService;
        this.partRemarkService = partRemarkService;
        this.demandStatusService = demandStatusService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.partService = partService;
    }

    /**
     * This method is responsible for create new part for SPA which is already created in planning part.
     *
     * @param storeDemandsDto {@link StoreDemandsDto}
     */
    private void prepareUnknownPart(StoreDemandsDto storeDemandsDto) {
        List<List<Long>> alternatePartList = storeDemandsDto.getStoreDemandDetailsDtoList()
                .stream()
                .filter(alternatePart -> CollectionUtils.isNotEmpty(alternatePart.getAlterPartDtoList()))
                .map(alternatePart -> alternatePart.getAlterPartDtoList().stream()
                        .map(AlterPartDto::getPartId)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        alternatePartList.add(storeDemandsDto.getStoreDemandDetailsDtoList().stream()
                .map(StoreDemandDetailsDto::getPartId)
                .collect(Collectors.toList()));

        List<Long> allPartList = alternatePartList.stream().flatMap(List::stream).collect(Collectors.toList());

        Set<Long> allPartListSet = new HashSet<>(allPartList);

        allPartListSet.forEach(partId -> {
            Optional<Part> part = partService.findByPartId(partId);
            storePartAvailabilityService.findOrCreateAvailability(part.get());
        });
    }

    /**
     * Create method with validation
     *
     * @param storeDemandsDto {@link StoreDemandsDto}
     * @return Successfully saved message
     */
    @Transactional
    @Override
    public StoreDemand create(StoreDemandsDto storeDemandsDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        StoreDemand storeDemand = convertToEntity(storeDemandsDto);
        storeDemand.setVoucherNo(storeVoucherTrackingService.generateUniqueNo(VoucherType.DEMAND));
        storeDemand.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        StoreDemand entity = super.saveItem(storeDemand);

        if (storeDemandsDto.getDepartmentType() == DepartmentType.EXTERNAL && !CollectionUtils.isEmpty(storeDemandsDto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(storeDemandsDto.getAttachment(), FeatureName.STORE_DEMAND, entity.getId());
        }

        prepareUnknownPart(storeDemandsDto);

        storeDemandsDto.getStoreDemandDetailsDtoList().forEach(storeDemandItem -> {
            demandStatusService.create(storeDemandItem.getPartId(),
                    entity.getId(),
                    entity.getId(),
                    entity.getId(),
                    storeDemandItem.getQuantityDemanded(),
                    entity.getWorkFlowAction().getId(),
                    VoucherType.DEMAND,
                    storeDemand.getIsActive(),
                    RfqType.PROCUREMENT.name());
        });
        approvalStatusService.create(ApprovalStatusDto.of(entity.getId(), STORE_DEMAND, workFlowAction));
        storeDemandDetailsService.saveAll(storeDemandsDto.getStoreDemandDetailsDtoList(), entity);

        return storeDemand;
    }

    @Transactional
    public Pair<StoreDemand, List<StoreDemandItem>> create(StoreDemandsDto storeDemandsDto, OrderType orderType) {
        StoreDemand storeDemand = convertToEntity(storeDemandsDto);
        storeDemand.setVoucherNo(ApplicationConstant.INVISIBLE + ZonedDateTime.now().toInstant().toEpochMilli());
        storeDemand.setIsAlive(FALSE);
        storeDemand.setOrderType(orderType);

        WorkFlowAction workFlowAction = workFlowActionService.findFinalAction();
        storeDemand.setWorkFlowAction(workFlowAction);

        storeDemand = super.saveItem(storeDemand);

        approvalStatusService.create(ApprovalStatusDto.of(storeDemand.getId(), STORE_DEMAND, workFlowAction));
        //TODO: Attachment might be added?
        List<StoreDemandItem> storeDemandItems = storeDemandDetailsService.saveAll(storeDemandsDto.getStoreDemandDetailsDtoList(), storeDemand);

        return Pair.of(storeDemand, storeDemandItems);
    }

    @Transactional
    @Override
    public StoreDemand update(StoreDemandsDto storeDemandsDto, Long id) {
        StoreDemand storeDemand = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeDemand.getWorkFlowActionId());

        WorkFlowAction currentAction = storeDemand.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));
        prepareUnknownPart(storeDemandsDto);
        StoreDemand entity = updateEntity(storeDemandsDto, storeDemand);
        if (storeDemandsDto.getDepartmentType() == DepartmentType.EXTERNAL) {
            genericAttachmentService.updateByRecordId(FeatureName.STORE_DEMAND, entity.getId(), storeDemandsDto.getAttachment());
        }

        boolean isDeleted = true;
        for (StoreDemandDetailsDto storeDemandItem : storeDemandsDto.getStoreDemandDetailsDtoList()) {
            if (isDeleted) {
                demandStatusServiceImpl.deleteAllDemandStatus(entity.getId(), entity.getId(), VoucherType.DEMAND);
                isDeleted = false;
            }
            demandStatusService.entityUpdate(
                    storeDemandItem.getPartId(),
                    entity.getId(),
                    entity.getId(),
                    entity.getId(),
                    storeDemandItem.getQuantityDemanded(),
                    entity.getWorkFlowAction().getId(),
                    VoucherType.DEMAND,
                    storeDemand.getIsActive(),
                    RfqType.PROCUREMENT.name());
        }

        storeDemandDetailsService.updateAll(storeDemandsDto.getStoreDemandDetailsDtoList(), storeDemand);
        return super.saveItem(entity);
    }

    @Transactional
    public Pair<StoreDemand, List<StoreDemandItem>> update(StoreDemandsDto storeDemandsDto, OrderType orderType, Long poId) {
        StoreDemand storeDemand = storeDemandRepository.findStoreDemandByPartOrderId(poId);
        storeDemand = updateEntity(storeDemandsDto, storeDemand);
        storeDemand.setOrderType(orderType);

        storeDemand = super.saveItem(storeDemand);

        List<StoreDemandItem> storeDemandItems = storeDemandDetailsService.updateAll(storeDemandsDto.getStoreDemandDetailsDtoList(), storeDemand);

        return Pair.of(storeDemand, storeDemandItems);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreDemand storeDemand = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(storeDemand.getWorkFlowActionId());
        storeDemand.setIsActive(isActive);

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, storeDemand.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(storeDemand.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(storeDemand.getId(), workFlowAction, RemarkType.STORE_DEMAND_APPROVAL_REMARK);
            storeDemand.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(storeDemand.getWorkFlowAction(), STORE_DEMAND,
                    storeDemand.getId()));
            storeDemand.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(storeDemand);
            setDemandStatusRejectedUpdatedValue(storeDemand);
        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(storeDemand.getWorkFlowActionId()));
            if (procurementRequisitionService.isStoreDemandExistInRequisition(id)) {
                throw new EngineeringManagementServerException(
                        ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY,
                        HttpStatus.PRECONDITION_FAILED,
                        MDC.get(TRACE_ID)
                );
            }
            setDemandStatusIsActiveUpdatedValue(storeDemand);
        }
        saveItem(storeDemand);
    }

    private void setDemandStatusIsActiveUpdatedValue(StoreDemand storeDemand) {
        List<StoreDemandItem> storeDemandItems = storeDemandDetailsService.findByStoreDemandId(storeDemand.getId());
        storeDemandItems.forEach(storeDemandItem -> {
            demandStatusService.updateActiveStatus(storeDemand.getId(),
                    storeDemand.getId(),
                    storeDemandItem.getPart().getId(),
                    VoucherType.DEMAND,
                    storeDemand.getIsActive(),
                    storeDemand.getWorkFlowAction().getId());
        });
    }

    private void setDemandStatusRejectedUpdatedValue(StoreDemand storeDemand) {
        List<StoreDemandItem> storeDemandItems = storeDemandDetailsService.findByStoreDemandId(storeDemand.getId());
        storeDemandItems.forEach(storeDemandItem -> {
            demandStatusService.updateRejectedStatus(storeDemand.getId(),
                    storeDemand.getId(),
                    storeDemandItem.getPart().getId(),
                    VoucherType.DEMAND,
                    storeDemand.getIsRejected());
        });
    }

    @Override
    public PageData search(CommonWorkFlowSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StoreDemand> pagedData;
        List<WorkFlowActionProjection> approvedActionsForUser = new ArrayList<>();
        Map<Long, List<StoreIssueProjection>> issueProjectionMap = new HashMap<>();
        Map<Long, List<RequisitionProjection>> requisitionProjectionMap = new HashMap<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionsForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pagedData = Page.empty();
                    break;
                }
                pagedData = customBuildSpecification(dto, pageable, pendingSearchWorkFlowIds, false);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService
                        .findFinalAction().getId();
                pagedData = customBuildSpecification(dto, pageable, Collections.singleton(approvedId), null);
                List<StoreDemand> storeDemandList = pagedData.getContent();
                Set<Long> storeDemandIds = storeDemandList.stream().map(StoreDemand::getId).collect(Collectors.toSet());
                issueProjectionMap = getIssuedProjectionMap(storeDemandIds);
                requisitionProjectionMap = getRequisitionProjectionMap(storeDemandIds);
                break;
            case REJECTED:
                pagedData = customBuildSpecification(dto, pageable, new HashSet<>(), true);
                break;
            default:
                pagedData = customBuildSpecification(dto, pageable, new HashSet<>(), null);
                break;
        }

        return PageData.builder()
                .model(getResponseData(pagedData.getContent(), approvedActionsForUser, issueProjectionMap, requisitionProjectionMap, false))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private Page<StoreDemand> customBuildSpecification(CommonWorkFlowSearchDto searchDto,
                                                       Pageable pageable,
                                                       Set<Long> approvedIds,
                                                       Boolean isRejected) {
        if (FALSE == searchDto.getNotRequisition()) {
            searchDto.setNotRequisition(null);
        }
        if (searchDto.getNotIssued() == FALSE) {
            searchDto.setNotIssued(null);
        }
        CustomSpecification<StoreDemand> customSpecification = new CustomSpecification<>();
        Specification<StoreDemand> specification = Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                        .and(customSpecification.equalSpecificationAtRoot(isRejected, IS_REJECTED_FIELD))
                        .and(customSpecification.inSpecificationAtRoot(approvedIds, WORKFLOW_ACTION_ID))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), VOUCHER_NO))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getOrderType(), ORDER_TYPE))
                        .and(customSpecification.notEqualSpecificationAtRoot(searchDto.getNotIssued(), IS_ISSUED))
                        .and(customSpecification.notEqualSpecificationAtRoot(searchDto.getNotRequisition(), IS_REQUISITION))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsAlive(), IS_ALIVE)
                                .and(customSpecification.likeSpecificationAtRoot(VoucherType.DEMAND.name(), VOUCHER_NO)))
        );

        return storeDemandRepository.findAll(specification, pageable);
    }

    private Map<Long, List<RequisitionProjection>> getRequisitionProjectionMap(Set<Long> storeDemandIds) {
        return procurementRequisitionService.getRequisitionProjection(storeDemandIds);
    }

    private Map<Long, List<StoreIssueProjection>> getIssuedProjectionMap(Set<Long> storeDemandIds) {
        return storeIssueService.getIssuedProjection(storeDemandIds);
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<StoreDemand> pagedData = storeDemandRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pagedData.getContent(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap(), false))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public StoreDemandResponseDto getSingle(Long id) {

        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(),
                        Helper.getAuthUserId());
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser, Collections.emptyMap(), Collections.emptyMap(), false).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    public StoreDemandResponseDto getSingleWithAlterPart(Long id) {

        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(),
                        Helper.getAuthUserId());
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser, Collections.emptyMap(), Collections.emptyMap(), true).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StoreDemand storeDemand = findById(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeDemand.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeDemand.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(storeDemand.getId(), STORE_DEMAND, storeDemand.getWorkFlowAction()));
            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(storeDemand.getId(), storeDemand.getWorkFlowAction().getId(), RemarkType.STORE_DEMAND_APPROVAL_REMARK, approvalRequestDto.getApprovalDesc());

            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, storeDemand.getWorkFlowAction());

            storeDemand.setWorkFlowAction(nextAction);
            if (storeDemand.getWorkFlowAction().equals(workFlowActionService.findFinalAction())) {
                updatePartAvailability(storeDemand);
            }

        } else {
            storeDemand.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storeDemand.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        StoreDemand entity = super.saveItem(storeDemand);
        List<StoreDemandItem> storeDemandItems = storeDemandDetailsService.findByStoreDemandId(id);


        storeDemandItems.forEach(storeDemandItem -> {
            demandStatusService.update(storeDemandItem.getPartId(),
                    id,
                    entity.getWorkFlowAction().getId(),
                    entity.getIsRejected(),
                    VoucherType.DEMAND,
                    RfqType.PROCUREMENT.name());
        });
    }

    public List<StoreDemandProjection> findByIdIn(Set<Long> collect) {
        return storeDemandRepository.findAircraftByIdIn(collect);
    }


    /**
     * Check dependency with aircraft
     */
    public boolean existByAircraft(Long aircraftId) {
        return storeDemandRepository.existsByAircraftIdAndIsActiveTrue(aircraftId);
    }

    @Override
    protected Specification<StoreDemand> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        return null;
    }

    @Override
    protected StoreDemand convertToEntity(StoreDemandsDto storeDemandsDto) {
        StoreDemand storeDemand = new StoreDemand();

        if (Objects.nonNull(storeDemandsDto.getAircraftId())) {
            storeDemand.setAircraft(aircraftService
                    .findById(storeDemandsDto.getAircraftId()));
        }

        if (Objects.nonNull(storeDemandsDto.getAirportId())) {
            storeDemand.setAirport(airportService.findActiveAirportById(storeDemandsDto.getAirportId()));
        }
        if (storeDemandsDto.getDepartmentType() == DepartmentType.INTERNAL && Objects.nonNull(storeDemandsDto.getDepartmentId())) {
            storeDemand.setInternalDepartment(departmentService.findById(storeDemandsDto.getDepartmentId()));
        }
        if (storeDemandsDto.getDepartmentType() == DepartmentType.EXTERNAL && Objects.nonNull(storeDemandsDto.getVendorId())) {
            storeDemand.setVendor(vendorService.findByIdAndVendorType(storeDemandsDto.getVendorId(), VendorType.OPERATOR));
        }

        storeDemand.setDepartmentType(storeDemandsDto.getDepartmentType());
        storeDemand.setUpdateDate(LocalDate.now());// UpdateDate means current day time
        storeDemand.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        storeDemand.setWorkOrderNo(storeDemandsDto.getWorkOrderNo());
        storeDemand.setValidTill(storeDemandsDto.getValidTill());
        storeDemand.setIsRequisition(false);
        storeDemand.setIsIssued(false);
        storeDemand.setRemarks(storeDemandsDto.getRemarks());
        return storeDemand;
    }

    @Override
    protected StoreDemand updateEntity(StoreDemandsDto dto, StoreDemand entity) {
        entity.setUpdateDate(LocalDate.now());// UpdateDate means current day time
        entity.setDepartmentType(dto.getDepartmentType());
        entity.getStoreDemandItemList().clear();
        entity.setRemarks(dto.getRemarks());
        entity.setValidTill(dto.getValidTill());
        entity.setWorkOrderNo(dto.getWorkOrderNo());
        if (StringUtils.isEmpty(entity.getVoucherNo())) {
            entity.setVoucherNo(storeVoucherTrackingService.generateUniqueNo(VoucherType.DEMAND));
        }
        if (Objects.nonNull(dto.getAircraftId()) && !dto.getAircraftId().equals(entity.getAircraftId())) {
            entity.setAircraft(aircraftService
                    .findByIdUnfiltered(dto.getAircraftId()));
        }
        if (Objects.nonNull(dto.getAirportId()) && !dto.getAirportId().equals(entity.getAirportId())) {
            entity.setAirport(airportService
                    .findById(dto.getAircraftId()));
        }
        if (entity.getDepartmentType() == DepartmentType.INTERNAL && Objects.nonNull(dto.getDepartmentId()) && !dto.getDepartmentId().equals(entity.getInternalDepartmentId())) {
            entity.setInternalDepartment(departmentService.findByIdUnfiltered(dto.getDepartmentId()));
        }
        if (entity.getDepartmentType() == DepartmentType.EXTERNAL && Objects.nonNull(dto.getVendorId())) {
            entity.setVendor(vendorService.findByIdAndVendorType(dto.getVendorId(), VendorType.OPERATOR));
        }
        return entity;
    }

    public Optional<StoreDemand> findByIdAndFinalWorkflow(Long id) {
        return storeDemandRepository.findByIdAndWorkFlowActionId(id, workFlowActionService.findFinalAction().getId());
    }

    @Override
    protected <T> T convertToResponseDto(StoreDemand storeDemand) {
        return null;
    }

    private List<StoreDemandResponseDto> getResponseData(List<StoreDemand> storeDemands,
                                                         List<WorkFlowActionProjection> approvedActions,
                                                         Map<Long, List<StoreIssueProjection>> issueProjectionMap,
                                                         Map<Long, List<RequisitionProjection>> requisitionProjectionMap, boolean withAlterPArt) {


        Map<DepartmentType, Set<Long>> internalExternalMap = storeDemands.stream().filter(storeDemand -> Objects.nonNull(storeDemand.getDepartmentType())).collect(Collectors.groupingBy(StoreDemand::getDepartmentType,
                Collectors.mapping(storeDemand -> storeDemand.getDepartmentType() == DepartmentType.INTERNAL ? storeDemand.getInternalDepartmentId() :
                        storeDemand.getVendorId(), Collectors.toSet())));

        Set<Long> collectionOfAircraftIds = storeDemands.stream()
                .map(StoreDemand::getAircraftId).collect(Collectors.toSet());

        Map<Long, AircraftProjection> aircraftProjectionMap =
                aircraftService.findByIdIn(collectionOfAircraftIds)
                        .stream()
                        .collect(Collectors
                                .toMap(AircraftProjection::getId, Function.identity()));

        Map<Long, AirportProjection> airportProjectionMap = airportService.findByIdIn(storeDemands.stream()
                .map(StoreDemand::getAirportId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(AirportProjection::getId, Function.identity()));

        Map<Long, DepartmentProjection> departmentProjectionMap =
                departmentService.findByIdIn(Objects.nonNull(internalExternalMap.get(DepartmentType.INTERNAL)) ? internalExternalMap.get(DepartmentType.INTERNAL) : Collections.emptySet())
                        .stream().collect(Collectors.toMap(DepartmentProjection::getId, Function.identity()));

        Map<Long, VendorProjection> vendorProjectionMap =
                vendorService.findByIdIn(Objects.nonNull(internalExternalMap.get(DepartmentType.EXTERNAL)) ? internalExternalMap.get(DepartmentType.EXTERNAL) : Collections.emptySet())
                        .stream().collect(Collectors.toMap(VendorProjection::getId, Function.identity()));

        Set<Long> demandIds = storeDemands.stream().map(StoreDemand::getId).collect(Collectors.toSet());

        Set<StoreDemandItem> items = storeDemandDetailsService.findByStoreDemandIdIn(demandIds);
        Map<Long, List<StoreDemandDetailsDto>> detailsByDemand;
        if (withAlterPArt) {
            detailsByDemand = storeDemandDetailsService.getResponseWithAlternatePart(items, RemarkType.STORE_DEMAND, demandIds).stream()
                    .collect(Collectors.groupingBy(StoreDemandDetailsDto::getStoreDemandId));
        } else {
            detailsByDemand = storeDemandDetailsService.getResponse(items, RemarkType.STORE_DEMAND, demandIds).stream()
                    .collect(Collectors.groupingBy(StoreDemandDetailsDto::getStoreDemandId));
        }

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(demandIds, approvedActions, STORE_DEMAND);

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(demandIds, RemarkType.STORE_DEMAND_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.STORE_DEMAND, demandIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        return storeDemands
                .stream().map(storeDemand ->
                        convertToResponseDto(storeDemand,
                                attachmentLinksMap.get(storeDemand.getId()),
                                aircraftProjectionMap.get(storeDemand.getAircraftId()),
                                airportProjectionMap.get(storeDemand.getAirportId()),
                                departmentProjectionMap.get(storeDemand.getInternalDepartmentId()),
                                vendorProjectionMap.get(storeDemand.getVendorId()),
                                detailsByDemand.get(storeDemand.getId()),
                                workFlowDto, issueProjectionMap.get(storeDemand.getId()),
                                requisitionProjectionMap.get(storeDemand.getId()),
                                partRemarkList.get(storeDemand.getId())))  //approval  remarks
                .collect(Collectors.toList());
    }

    private StoreDemandResponseDto convertToResponseDto(StoreDemand storeDemand,
                                                        Set<String> attachmentLinks,
                                                        AircraftProjection aircraftProjection,
                                                        AirportProjection airportProjection,
                                                        DepartmentProjection departmentProjection,
                                                        VendorProjection vendorProjection,
                                                        List<StoreDemandDetailsDto> items,
                                                        WorkFlowDto workFlowDto, List<StoreIssueProjection> storeIssueProjection,
                                                        List<RequisitionProjection> requisitionProjection,
                                                        List<PartRemark> partRemarks) {

        StoreDemandResponseDto responseDto = new StoreDemandResponseDto();
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeDemand.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeDemand.getWorkFlowActionId());
        if (Objects.nonNull(storeIssueProjection)) {
            responseDto.setIssued(storeIssueProjection.stream().map(issueProj -> IdVoucherDto.of(issueProj.getId(), issueProj.getVoucherNo()))
                    .collect(Collectors.toList()));
        }
        if (Objects.nonNull(requisitionProjection)) {
            responseDto.setRequisition(requisitionProjection.stream().map(requisitionPro ->
                    IdVoucherDto.of(requisitionPro.getId(), requisitionPro.getVoucherNo())).collect(Collectors.toList()));
        }
        responseDto.setId(storeDemand.getId());
        responseDto.setVoucherNo(storeDemand.getVoucherNo());

        if (CollectionUtils.isNotEmpty(items)) {
            items.stream().filter(Objects::nonNull).forEach(dto -> {
                dto.setRemark(dto.getParentWiseRemarks().getOrDefault(storeDemand.getId(), EMPTY_STRING));
            });
        }
        responseDto.setStoreDemandDetailsDtoList(items);
        responseDto.setRemarks(storeDemand.getRemarks());
        responseDto.setIsRejected(storeDemand.getIsRejected());
        responseDto.setRejectedDesc(storeDemand.getRejectedDesc());

        if (Objects.nonNull(aircraftProjection)) {
            responseDto.setAircraftId(aircraftProjection.getId());
            responseDto.setAircraftName(aircraftProjection.getAircraftName());
        }
        if (Objects.nonNull(airportProjection)) {
            responseDto.setAirportId(airportProjection.getId());
            responseDto.setAirportName(airportProjection.getName());
        }
        if (storeDemand.getDepartmentType() == DepartmentType.INTERNAL && Objects.nonNull(departmentProjection)) {
            responseDto.setDepartmentCode(departmentProjection.getCode());
            responseDto.setDepartmentId(departmentProjection.getId());
        }
        if (storeDemand.getDepartmentType() == DepartmentType.EXTERNAL && Objects.nonNull(vendorProjection)) {
            responseDto.setVendorId(vendorProjection.getId());
            responseDto.setVendorName(vendorProjection.getName());
        }
        responseDto.setDemandDate(storeDemand.getUpdateDate());
        responseDto.setValidTill(storeDemand.getValidTill());
        responseDto.setAttachment(attachmentLinks);
        responseDto.setDepartmentType(storeDemand.getDepartmentType());
        responseDto.setWorkFlowActionId(storeDemand.getWorkFlowActionId());
        responseDto.setWorkflowName(workFlowAction.getName());
        responseDto.setWorkOrderNo(storeDemand.getWorkOrderNo());
        responseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        responseDto.setActionEnabled(workFlowDto.getActionableIds().contains(storeDemand.getWorkFlowActionId()));
        responseDto.setEditable(workFlowDto.getEditableIds().contains(storeDemand.getWorkFlowActionId()));
        responseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            responseDto.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark -> partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        return responseDto;
    }

    private void updatePartAvailability(StoreDemand storeDemand) {
        storeDemandDetailsService.findByStoreDemandIdIn(Collections.singleton(storeDemand.getId())).forEach(storeDemandDetails -> {
            storePartAvailabilityService.updateDemandIssuedRequisitionQuantity(storeDemandDetails.getQuantityDemanded(),
                    storeDemandDetails.getPartId(), PartAvailabilityCountType.DEMAND);
        });
    }

    public List<DashboardProjection> getStoreDemandData(Integer month) {
        return storeDemandRepository.getStoreDemandDataForMonths(month);
    }
}
