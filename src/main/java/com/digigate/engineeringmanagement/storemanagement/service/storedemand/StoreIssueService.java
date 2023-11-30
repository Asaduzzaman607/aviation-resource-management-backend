package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.MapUtil;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.PartAvailabilityCountType;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.*;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.*;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreIssueViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreIssueRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.StoreStockRoomService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_ISSUE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;

@Service
public class StoreIssueService extends AbstractSearchService<StoreIssue, StoreIssueDto, CommonWorkFlowSearchDto> {
    private final StoreIssueRepository storeIssueRepository;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final StoreStockRoomService storeStockRoomService;
    private final StoreDemandService storeDemandService;
    private final StoreDemandDetailsService demandDetailsService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final Helper helper;
    private final WorkFlowUtil workFlowUtil;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final StorePartSerialService storePartSerialService;
    private final StorePartAvailabilityLogService storePartAvailabilityLogService;
    private final StoreIssueItemDetailsServiceImpl storeIssueItemDetailsService;
    private final StoreIssueSerialService storeIssueSerialService;
    private final PartService partService;
    private final UnitMeasurementService unitMeasurementService;
    private final PartWiseUomService partWiseUomService;
    private final DemandStatusService demandStatusService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final PartRemarkService partRemarkService;

    public StoreIssueService(StoreIssueRepository storeIssueRepository,
                             ApprovalStatusService approvalStatusService,
                             WorkFlowActionService workFlowActionService,
                             ApprovalEmployeeService approvalEmployeeService,
                             StoreStockRoomService storeStockRoomService,
                             StoreDemandService storeDemandService,
                             @Lazy StoreDemandDetailsService demandDetailsService,
                             StorePartAvailabilityService storePartAvailabilityService,
                             StorePartSerialService storePartSerialService,
                             StorePartAvailabilityLogService storePartAvailabilityLogService,
                             StoreVoucherTrackingService storeVoucherTrackingService,
                             Helper helper,
                             WorkFlowUtil workFlowUtil,
                             StoreIssueItemDetailsServiceImpl storeIssueItemDetailsService,
                             StoreIssueSerialService storeIssueSerialService,
                             PartService partService,
                             DemandStatusServiceImpl demandStatusServiceImpl,
                             UnitMeasurementService unitMeasurementService,
                             PartWiseUomService partWiseUomService,
                             DemandStatusService demandStatusService,
                             PartRemarkService partRemarkService) {
        super(storeIssueRepository);
        this.storeIssueRepository = storeIssueRepository;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.storeStockRoomService = storeStockRoomService;
        this.storeDemandService = storeDemandService;
        this.demandDetailsService = demandDetailsService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.helper = helper;
        this.workFlowUtil = workFlowUtil;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.storePartSerialService = storePartSerialService;
        this.storePartAvailabilityLogService = storePartAvailabilityLogService;
        this.storeIssueItemDetailsService = storeIssueItemDetailsService;
        this.storeIssueSerialService = storeIssueSerialService;
        this.partService = partService;
        this.unitMeasurementService = unitMeasurementService;
        this.partWiseUomService = partWiseUomService;
        this.demandStatusService = demandStatusService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.partRemarkService = partRemarkService;
    }

    public void updateReturnApprovedStatus(Long issueId) {
        StoreIssue storeIssue = findByIdUnfiltered(issueId);
        storeIssue.setIsReturnApproved(true);
        saveItem(storeIssue);
    }

    /**
     * Create method
     *
     * @param storeIssueDto {@link StoreIssueDto}
     * @return Successfully created message
     */
    @Transactional
    @Override
    public StoreIssue create(StoreIssueDto storeIssueDto) {
        validate(storeIssueDto);
        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService
                .getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService
                .getByIndex(INITIAL_ORDER, sortedWorkflowAction);

        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections
                .singletonList(workFlowAction.getId()));

        StoreIssue storeIssue = convertToEntity(storeIssueDto);
        storeIssue.setWorkFlowAction(workFlowActionService.
                getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        storeIssue = super.saveItem(storeIssue);

        validateAndUpdateItems(storeIssueDto, storeIssue);
        approvalStatusService.create(ApprovalStatusDto.of(storeIssue.getId(), STORE_ISSUE, workFlowAction));

        List<StoreIssueItemDto> storeIssueItems = storeIssueDto.getStoreIssueItems();
        Set<Long> demandItemIds = storeIssueItems.stream().map(StoreIssueItemDto::getDemandItemId).collect(Collectors.toSet());

        List<StoreIssueItemProjection> storeIssueItemProjections = storeIssueItemDetailsService.findByStoreIssueId(storeIssue.getId());

        for (StoreIssueItemProjection item : storeIssueItemProjections) {
            demandStatusService.create(
                    item.getStoreDemandItemPartId(),
                    item.getStoreDemandItemStoreDemandId(),
                    item.getStoreDemandItemStoreDemandId(),
                    storeIssue.getId(),
                    item.getIssuedQuantity(),
                    storeIssue.getWorkFlowAction().getId(),
                    VoucherType.ISSUE,
                    storeIssue.getIsActive(),
                    ApplicationConstant.STORE
            );
        }

        return storeIssue;
    }

    /**
     * Change active status
     *
     * @param id       which user want to change status
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreIssue storeIssue = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(storeIssue.getWorkFlowActionId());
        storeIssue.setIsActive(isActive);

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction= workFlowActionService.getNavigatedAction(false, storeIssue.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(storeIssue.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(storeIssue.getId(), workFlowAction ,RemarkType.STORE_ISSUE_APPROVAL_REMARK);
            storeIssue.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(storeIssue.getWorkFlowAction(), STORE_ISSUE,
                    storeIssue.getId()));
            storeIssue.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(storeIssue);
            setDemandStatusRejectedUpdatedValue(storeIssue);
            updateStoreIssueSerial(storeIssue,true);
        }else{
            updateStoreIssueSerial(storeIssue,false);
        }
        setDemandStatusIsActiveUpdatedValue(storeIssue);
        super.saveItem(storeIssue);

        StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(storeIssue.getStoreDemandId());
        storeDemand.setIsIssued(storeIssueRepository.existsByStoreDemandIdAndIsActiveTrue(storeIssue.getStoreDemandId()));
        storeDemandService.saveItem(storeDemand);
    }

    /**
     * Update entity
     *
     * @param storeIssueDto {@link StoreIssueDto}
     * @param id            which user want to update
     * @return successfully update message
     */
    @Transactional
    @Override
    public StoreIssue update(StoreIssueDto storeIssueDto, Long id) {
        StoreIssue storeIssue = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        validate(storeIssueDto);
        workFlowUtil.validateUpdatability(storeIssue.getWorkFlowActionId());

        WorkFlowAction currentAction = storeIssue.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        StoreIssue updatedStoreIssue = updateEntity(storeIssueDto, storeIssue);
        validateAndUpdateItems(storeIssueDto, updatedStoreIssue);

        boolean isDeleted = true;
        for (StoreIssueItemDto issueItemDto : storeIssueDto.getStoreIssueItems()) {
            if (isDeleted) {
                demandStatusServiceImpl.deleteAllDemandStatus(storeIssue.getStoreDemand().getId(), storeIssue.getId(), VoucherType.ISSUE);
                isDeleted = false;
            }
            demandStatusService.entityUpdate(
                    storeIssueItemDetailsService.findById(issueItemDto.getId()).getStoreDemandItem().getPart().getId(),
                    storeIssue.getStoreDemand().getId(),
                    storeIssue.getStoreDemand().getId(),
                    storeIssue.getId(),
                    issueItemDto.getIssuedQuantity(),
                    storeIssue.getWorkFlowAction().getId(),
                    VoucherType.ISSUE,
                    storeIssue.getIsActive(),
                    STORE);
        }
        return super.saveItem(updatedStoreIssue);
    }

    /**
     * Make decision for change approval status
     *
     * @param id                 which user want to change approval
     * @param approvalRequestDto ApprovalRequestDto type
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StoreIssue storeIssue = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeIssue.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeIssue.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(storeIssue.getId(), STORE_ISSUE, storeIssue.getWorkFlowAction()));

            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(storeIssue.getId(),storeIssue.getWorkFlowAction().getId(),
                    RemarkType.STORE_ISSUE_APPROVAL_REMARK, approvalRequestDto.getApprovalDesc());

            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, storeIssue.getWorkFlowAction());

            storeIssue.setWorkFlowAction(nextAction);
            if (storeIssue.getWorkFlowAction().equals(workFlowActionService.findFinalAction())) {
                updatePartAvailability(storeIssue.getId());
                executeIssueProcedure(storeIssue);
            }
        } else {
            StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(storeIssue.getStoreDemandId());
            storeDemand.setIsIssued(false);
            storeDemandService.saveItem(storeDemand);
            storeIssue.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storeIssue.setRejectedDesc(approvalRequestDto.getRejectedDesc());
            updateStoreIssueSerial(storeIssue,false);
        }
        super.saveItem(storeIssue);
        List<StoreIssueItemProjection> procurementRequisitionItems = storeIssueItemDetailsService.findByStoreIssueId(id);
        procurementRequisitionItems.forEach(storeIssueItemProjection -> {
            demandStatusService.update(
                    storeIssueItemProjection.getStoreDemandItemPartId(),
                    id,
                    storeIssue.getWorkFlowAction().getId(),
                    storeIssue.getIsRejected(),
                    VoucherType.ISSUE,
                    STORE);
        });
    }

    private void updateStoreIssueSerial(StoreIssue storeIssue, boolean isActive) {
        List<StoreIssueItemProjection> storeIssueItems = storeIssueItemDetailsService.findByStoreIssueId(storeIssue.getId());
        Set<Long> itemIds = storeIssueItems.stream().map(StoreIssueItemProjection::getId).collect(Collectors.toSet());

        List<StoreIssueSerial> storeIssueSerials = storeIssueSerialService.findAllByStoreIssueItemIdIn(itemIds);

        storeIssueSerialService.updateActiveStatus(storeIssueSerials, isActive);
    }

    /**
     * Custom search method
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable page no
     * @return searched result
     */
    @Override
    public PageData search(CommonWorkFlowSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StoreIssue> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = storeIssueRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains(dto.getIsActive(),
                                pendingSearchWorkFlowIds, dto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = storeIssueRepository.
                        findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains(
                                dto.getIsActive(), approvedId, dto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = storeIssueRepository
                        .findAllByIsRejectedTrueAndVoucherNoContains(dto.getQuery(), pageable);
                break;
            default:
                pageData = storeIssueRepository.
                        findAllByIsActiveAndVoucherNoContains(dto.getIsActive(), dto.getQuery(),
                                pageable);
                break;
        }

        return PageData.builder()
                .model(getSearchResponseData(pageData.getContent(), approvedActionForUser))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public Set<StoreIssueProjection> findByIdIn(Set<Long> collect) {
        return storeIssueRepository.findByIdIn(collect);
    }

    /**
     * Get all method
     *
     * @param isActive boolean field
     * @param pageable page number
     * @return all the active data
     */
    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<StoreIssue> pageData = storeIssueRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), emptyList()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * Get single data
     *
     * @param id which user want to get
     * @return data
     */
    @Override
    public StoreIssueViewModel getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());
        StoreIssue storeIssue = findByIdUnfiltered(id);

        StoreIssueViewModel storeIssueViewModel = getResponseData(Collections.singletonList(storeIssue),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));

        Set<Long> storeIssueItemIds = storeIssue.getStoreIssueItemSet().stream().map(StoreIssueItem::getId).collect(Collectors.toSet());

        Map<Long, List<GrnAndSerialDto>> serialGrnMap = storeIssueSerialService.findStoreIssueSerialByStoreIssueItemIdIn(storeIssueItemIds)
                .stream().map(GrnAndSerialDto::from).collect(Collectors.groupingBy(GrnAndSerialDto::getIssueItemId));

        storeIssueViewModel.getStoreIssueItemResponseDtos().forEach(dto -> dto.setGrnAndSerialDtoList(getGrnAndSerial(
                serialGrnMap.get(dto.getId()), storeIssue.getWorkFlowAction())));

        return storeIssueViewModel;
    }

    @Override
    public Boolean validateClientData(StoreIssueDto storeIssueDto, Long id) {
        return super.validateClientData(storeIssueDto, id);
    }

    public Map<Long, List<StoreIssueProjection>> getIssuedProjection(Set<Long> demandIds) {
        return storeIssueRepository.findByStoreDemandIdInAndIsActiveTrue(demandIds).stream().
                collect(Collectors.groupingBy(StoreIssueProjection::getStoreDemandId));
    }

    @Override
    protected Specification<StoreIssue> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        CustomSpecification<StoreIssue> customSpecification = new CustomSpecification<>();

        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(),
                ApplicationConstant.VOUCHER_NO));
    }

    @Override
    protected <T> T convertToResponseDto(StoreIssue storeIssue) {
        return null;
    }

    @Override
    protected StoreIssue convertToEntity(StoreIssueDto storeIssueDto) {
        return populateChange(storeIssueDto, new StoreIssue());
    }

    @Override
    protected StoreIssue updateEntity(StoreIssueDto dto, StoreIssue entity) {
        return populateChange(dto, entity);
    }

    private void updatePartAvailability(Long issueId) {
        storeIssueItemDetailsService.findByStoreIssueId(issueId).forEach(storeIssueDetails -> {
            storePartAvailabilityService.updateDemandIssuedRequisitionQuantity(storeIssueDetails.getIssuedQuantity(),
                    storeIssueDetails.getStoreDemandItemPartId(), PartAvailabilityCountType.ISSUE);
        });
    }

    private void validateSerials(Set<StorePartSerial> storePartSerials, Set<Long> serialIds) {
        if (CollectionUtils.isEmpty(serialIds) || serialIds.size() == storePartSerials.size()) {
            return;
        }
        throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_SERIAL_IS_NOT_FOUND);
    }

    private void executeIssueProcedure(StoreIssue storeIssue) {

        List<StorePartAvailabilityLog> storePartAvailabilityLogs = new ArrayList<>();

        List<StoreIssueItem> storeIssueItemList = storeIssueItemDetailsService.getAllStoreIssueItemByStoreIssueId(
                storeIssue.getId());

        Set<Long> storeIssueItemIds = storeIssueItemList.stream().map(StoreIssueItem::getId).collect(Collectors.toSet());

        List<StoreIssueSerialProjection> storeIssueSerialList = storeIssueSerialService.
                findStoreIssueSerialByStoreIssueItemIdIn(storeIssueItemIds);

        Map<Long, StoreIssueSerialProjection> serialIds = storeIssueSerialList.stream()
                .collect(Collectors.toMap(StoreIssueSerialProjection::getStorePartSerialId, Function.identity(), (a, b) -> a));

        List<StorePartSerial> storePartSerialList = storePartSerialService.findAllByIdIn(serialIds.keySet());

        Map<Long, List<StorePartSerial>> serialsByAvailMap = storePartSerialList.stream().collect(Collectors.groupingBy(
                StorePartSerial::getStorePartAvailabilityId));

        Map<Long, StorePartAvailability> storePartAvailabilityMap = storePartAvailabilityService.
                getAllByDomainIdIn(serialsByAvailMap.keySet(), true)
                .stream().collect(Collectors.toMap(StorePartAvailability::getId, Function.identity()));

        serialsByAvailMap.keySet().forEach(key -> {
            StorePartAvailability storePartAvailability = MapUtil.getOrElseThrow(storePartAvailabilityMap, key,
                    EngineeringManagementServerException.notFound(ErrorId.STORE_PART_AVAILABILITY_IS_NOT_FOUND));

            if (Objects.equals(storePartAvailability.getQuantity(), NUMBERS.ZERO)) {
                throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_AVAILABILITY_QUANTITY_IS_ZERO);
            }
            List<StorePartSerial> storePartSerials = serialsByAvailMap.get(key);

            storePartSerials.forEach(partSerial -> {
                if (partSerial.notExistsInStore()) {
                    throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_SERIAL_IS_NOT_FOUND);
                }
                StoreIssueSerialProjection storeIssueSerial = serialIds.get(partSerial.getId());

                if (storeIssue.getPartClassification() == PartClassification.CONSUMABLE) {
                    int newLotQuantity = partSerial.getQuantity() - storeIssueSerial.getQuantity();
                    partSerial.setQuantity(newLotQuantity);
                    if (newLotQuantity == VALUE_ZERO) {
                        partSerial.setParentType(StorePartAvailabilityLogParentType.ISSUE);
                    } else if (newLotQuantity < 0) {
                        throw EngineeringManagementServerException.notFound(ErrorId.STOCK_NOT_AVAILABLE);
                    }
                    storePartAvailabilityService.updateStorePartAvailabilityQuantityById(
                            storePartAvailability.getId(), -storeIssueSerial.getQuantity());
                } else {
                    partSerial.setParentType(StorePartAvailabilityLogParentType.ISSUE);
                    storePartAvailabilityService.updateStorePartAvailabilityQuantityById(
                            storePartAvailability.getId(), ApplicationConstant.INT_NEGATIVE_ONE);
                }
                storePartAvailabilityLogs.add(generateStorePartAvailabilityLog(storeIssue.getId(), partSerial,
                        storeIssue.getVoucherNo(), storeIssueSerial.getQuantity(),
                        storeIssue.getSubmittedById(), storeIssue.getWorkFlowActionId()));

                if (StringUtils.isNotBlank(storeIssueSerial.getGrnNo()) && StringUtils.isBlank(partSerial.getGrnNo())) {
                    partSerial.setGrnNo(storeIssueSerial.getGrnNo());
                }
                storePartSerialService.saveItem(partSerial);
            });
        });

        if (CollectionUtils.isNotEmpty(storePartAvailabilityLogs)) {
            storePartAvailabilityLogService.saveItemList(storePartAvailabilityLogs);
        }
    }

    private StorePartAvailabilityLog generateStorePartAvailabilityLog(Long parentId, StorePartSerial storePartSerial,
                                                                      String voucherNo, int issueQty,
                                                                      Long initialAction, Long finalUser) {
        StorePartAvailabilityLog storePartAvailabilityLog = new StorePartAvailabilityLog();
        Integer inStockValue = storePartSerial.getStorePartAvailability().getQuantity() - issueQty;
        storePartAvailabilityLog.setQuantity(INT_ONE);
        storePartAvailabilityLog.setStorePartSerial(storePartSerial);
        storePartAvailabilityLog.setParentType(StorePartAvailabilityLogParentType.ISSUE);
        storePartAvailabilityLog.setTransactionType(TransactionType.ISSUE);
        storePartAvailabilityLog.setPartStatus(storePartSerial.getPartStatus());
        storePartAvailabilityLog.setParentId(parentId);
        storePartAvailabilityLog.setIssuedQty(issueQty);
        storePartAvailabilityLog.setVoucherNo(voucherNo);
        storePartAvailabilityLog.setSubmittedBy(User.withId(initialAction));
        storePartAvailabilityLog.setWorkFlowAction(WorkFlowAction.withId(finalUser));
        storePartAvailabilityLog.setInStock(inStockValue);
        return storePartAvailabilityLog;
    }

    private StoreIssue populateChange(StoreIssueDto storeIssueDto, StoreIssue storeIssue) {
        if (StringUtils.isEmpty(storeIssue.getVoucherNo())) {
            StoreDemand storeDemand = storeDemandService.findById(storeIssueDto.getDemandId());
            storeIssue.setVoucherNo(storeVoucherTrackingService.generateUniqueVoucherNo(storeIssueDto.getDemandId(),
                    VoucherType.ISSUE, storeDemand.getVoucherNo()));
        }
        storeIssue.setRegistration(storeIssueDto.getRegistration());
        storeIssue.setStockRoomType(storeIssueDto.getStockRoomType());
        storeIssue.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        storeIssue.setRequestedBy(User.withId(Helper.getAuthUserId()));
        storeIssue.setRemarks(storeIssueDto.getRemarks());
        storeIssue.setPartClassification(storeIssueDto.getPartClassification());
        storeIssue.setUpdateDate(LocalDate.now());// update locale date time in current day time
        if (Objects.nonNull(storeIssueDto.getDemandId()) &&
                !storeIssueDto.getDemandId().equals(storeIssue.getStoreDemandId())) {
            StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(storeIssueDto.getDemandId());
            storeDemand.setIsIssued(true);
            storeIssue.setStoreDemand(storeDemandService.saveItem(storeDemand));
        }
        if (Objects.nonNull(storeIssueDto.getStoreStockRoomId()) &&
                !storeIssueDto.getStoreStockRoomId().equals(storeIssue.getStoreStockRoomId())) {
            storeIssue.setStoreStockRoom(storeStockRoomService.findByIdUnfiltered(storeIssueDto.getStoreStockRoomId()));
        }
        return storeIssue;
    }

    /**
     * This method is responsible for validating unique serial no.
     *
     * @param storeIssueDto {@link StoreIssueDto}
     */
    private void validate(StoreIssueDto storeIssueDto) {
        storeDemandService.findByIdAndFinalWorkflow(storeIssueDto.getDemandId()).orElseThrow(() ->
                EngineeringManagementServerException.badRequest(ErrorId.INVALID_OLD_DEMAND));
    }

    private void validateAndUpdateItems(StoreIssueDto storeIssueDto, StoreIssue storeIssue) {
        List<StoreIssueItemDto> storeIssueItems = storeIssueDto.getStoreIssueItems();
        validateQuantity(storeIssueDto);

        Set<Long> demandItemIds = storeIssueItems.stream().map(StoreIssueItemDto::getDemandItemId).collect(Collectors.toSet());
        Set<Long> issueItemIds = storeIssueDto.getStoreIssueItems().stream().map(StoreIssueItemDto::getId).collect(Collectors.toSet());
        storeIssueSerialService.deleteAllByStoreIssueItemIdIn(issueItemIds);
        Map<Long, StoreDemandItem> storeDemandItemMap = demandDetailsService.getAllByDomainIdIn(demandItemIds, true).stream()
                .collect(Collectors.toMap(StoreDemandItem::getId, Function.identity()));
        Set<Long> uomIds = storeIssueItems.stream().map(StoreIssueItemDto::getUomId).collect(Collectors.toSet());
        Map<Long, UnitMeasurement> unitMeasurementMap = unitMeasurementService.getAllByDomainIdIn(uomIds, true).stream()
                .collect(Collectors.toMap(UnitMeasurement::getId, Function.identity()));
        Set<Long> partIds = storeDemandItemMap.values().stream().map(StoreDemandItem::getPartId).collect(Collectors.toSet());

        Map<Long, StorePartAvailability> storePartAvailabilityMap = storePartAvailabilityService.findByPartIdIn(partIds)
                .stream().collect(Collectors.toMap(StorePartAvailability::getPartId, Function.identity()));

        Map<Long, Integer> mainPartWiseIssuedQuantity = storeIssueItems.stream().filter(partWiseIssued -> Objects.nonNull(
                partWiseIssued.getParentPartId())).collect(Collectors.groupingBy(StoreIssueItemDto::getParentPartId,
                Collectors.summingInt(StoreIssueItemDto::getIssuedQuantity)));

        Map<Long, Integer> alreadyIssuedQuantity = storeIssueItems.stream().filter(alreadyIssued -> Objects.nonNull(
                alreadyIssued.getParentPartId())).collect(Collectors.groupingBy(StoreIssueItemDto::getParentPartId,
                Collectors.summingInt(StoreIssueItemDto::getAlreadyIssuedQuantity)));

        Map<Long, Integer> alternatePartWiseIssuedQuantity = storeDemandItemMap.values().stream().filter(alternatePartWiseIssued -> Objects.nonNull(
                alternatePartWiseIssued.getParentPartId())).collect(Collectors.groupingBy(StoreDemandItem::getParentPartId,
                Collectors.summingInt(StoreDemandItem::getIssuedQty)));

        storeIssueItems.forEach(item -> {
            StoreDemandItem storeDemandItem = storeDemandItemMap.get(item.getDemandItemId());

            if (Objects.isNull(storeDemandItem)) {
                return;
            }
            if (!Objects.equals(storeIssueDto.getDemandId(), storeDemandItem.getStoreDemandId())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DEMAND_ITEM);
            }

            if (Objects.isNull(item.getParentPartId())) {
                Long partId = storeDemandItem.getPartId();
                Integer previousIssuedQty = ((alternatePartWiseIssuedQuantity.getOrDefault(partId, VALUE_ZERO) + storeDemandItem.getIssuedQty())
                        - (alreadyIssuedQuantity.getOrDefault(partId, VALUE_ZERO) + item.getAlreadyIssuedQuantity()));

                Integer totalIssuedQuantity = mainPartWiseIssuedQuantity.getOrDefault(partId, VALUE_ZERO) + item.getIssuedQuantity();

                if ((totalIssuedQuantity + previousIssuedQty) > storeDemandItem.getQuantityDemanded()) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.ISSUED_QUANTITY_MUST_NOT_GREATER_THAN_ITEM_DEMAND_QUANTITY);
                }
                storeDemandItem.setIssuedQty(totalIssuedQuantity + previousIssuedQty);
                demandDetailsService.saveItem(storeDemandItem);
            }
            updateSingleIssueItem(storeDemandItem, storeIssue,
                    storePartAvailabilityMap, item,
                    unitMeasurementMap.get(item.getUomId()));
        });
    }

    private void updateSingleIssueItem(StoreDemandItem storeDemandItem, StoreIssue storeIssue,
                                       Map<Long, StorePartAvailability> storePartAvailabilityMap, StoreIssueItemDto item,
                                       UnitMeasurement unitMeasurement) {
        Part part = storeDemandItem.getPart();
        StorePartAvailability partAvailability = storePartAvailabilityMap.get(storeDemandItem.getPartId());
        if (Objects.isNull(partAvailability) || partAvailability.getQuantity() < item.getIssuedQuantity()) {
            throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_AVAILABILITY_IS_NOT_FOUND);
        }
        Set<Long> serialIds = item.getGrnAndSerialDtoList().stream().map(GrnAndSerialDto::getSerialId).collect(Collectors.toSet());
        Set<StorePartSerial> storePartSerials = storePartSerialService.getStorePartSerialNos(serialIds,
                partAvailability.getId(), PartStatus.SERVICEABLE);

        validateSerials(storePartSerials, serialIds);
        Map<Long, StorePartSerial> storePartSerialMap = storePartSerials.stream().collect(Collectors.toMap(StorePartSerial::getId,
                Function.identity()));

        if (Objects.isNull(item.getId())) {
            storeIssueItemDetailsService.create(item, storeDemandItem, storeIssue, item.getGrnAndSerialDtoList(), storePartSerialMap, unitMeasurement);
        } else {
            storeIssueItemDetailsService.update(item, storeDemandItem, item.getGrnAndSerialDtoList(), storePartSerialMap, unitMeasurement);
        }
        partWiseUomService.updateAll(List.of(item.getUomId()), part, OTHER);
    }

    private void validateQuantity(StoreIssueDto storeIssueDto) {
        List<StoreIssueItemDto> storeIssueItems = storeIssueDto.getStoreIssueItems();

        int totalIssued = storeIssueItems.stream().mapToInt(StoreIssueItemDto::getIssuedQuantity).sum();
        if (totalIssued < 1) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AT_LEAST_ONE_ITEM_MUST_BE_ISSUED);
        }

        storeIssueItems.forEach(this::validateIndividualSum);
    }

    private void validateIndividualSum(StoreIssueItemDto storeIssueItemDto) {
        int sum = storeIssueItemDto.getGrnAndSerialDtoList().stream().mapToInt(GrnAndSerialDto::getQuantity).sum();
        if (!storeIssueItemDto.getIssuedQuantity().equals(sum)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.COUNT_OF_SERIAL_NO_AND_QUANTITY_MUST_SAME);
        }
    }

    private List<StoreIssueViewModel> getResponseData(List<StoreIssue> storeIssues,
                                                      List<WorkFlowActionProjection> approvedActions) {

        Set<Long> issueIds = storeIssues.stream().map(StoreIssue::getId).collect(Collectors.toSet());

        Set<Long> collectionsOfStoreDemandIds = storeIssues.stream().map(StoreIssue::getStoreDemandId).collect(Collectors.toSet());

        Map<Long, StoreDemandProjection> storeDemandProjectionMap = storeDemandService.findByIdIn(collectionsOfStoreDemandIds)
                .stream().collect(Collectors.toMap(StoreDemandProjection::getId, Function.identity()));

        Set<Long> collectionsOfStoreStockRoomIds = storeIssues.stream().map(StoreIssue::getStoreStockRoomId).collect(Collectors.toSet());

        Map<Long, StoreStockRoomProjection> storeStockRoomProjectionMap = storeStockRoomService.findByIdIn(collectionsOfStoreStockRoomIds)
                .stream().collect(Collectors.toMap(StoreStockRoomProjection::getId, Function.identity()));

        Set<StoreIssueItem> items = storeIssueItemDetailsService.getAllStoreIssueItemByStoreIssueIdIn(issueIds);

        Map<Long, List<StoreIssueItemResponseDto>> detailsByIssue = storeIssueItemDetailsService.getResponse(items, issueIds).stream()
                .collect(Collectors.groupingBy(StoreIssueItemResponseDto::getIssueId));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(issueIds, approvedActions, STORE_ISSUE);

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(issueIds, RemarkType.STORE_ISSUE_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        return storeIssues
                .stream().map(storeIssue ->
                        convertToResponseDto(storeIssue,
                                storeDemandProjectionMap.get(storeIssue.getStoreDemandId()),
                                storeStockRoomProjectionMap.get(storeIssue.getStoreStockRoomId()),
                                detailsByIssue.getOrDefault(storeIssue.getId(), emptyList()),
                                workFlowDto, partRemarkList.get(storeIssue.getId())))
                .collect(Collectors.toList());
    }

    private List<StoreIssueViewModel> getSearchResponseData(List<StoreIssue> storeIssues,
                                                            List<WorkFlowActionProjection> approvedActions) {

        Set<Long> issueIds = storeIssues.stream().map(StoreIssue::getId).collect(Collectors.toSet());
        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(issueIds, approvedActions, STORE_ISSUE);
        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(issueIds,
                        RemarkType.STORE_ISSUE_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        return storeIssues.stream().map(storeIssue -> convertToMultipleResponseDto(workFlowDto, storeIssue, partRemarkList.get(storeIssue.getId())))
                .collect(Collectors.toList());
    }

    private  StoreIssueViewModel convertToMultipleResponseDto(WorkFlowDto workFlowDto, StoreIssue storeIssue, List<PartRemark> partRemarks) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeIssue.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeIssue.getWorkFlowActionId());
        StoreIssueViewModel storeIssueViewModel = new StoreIssueViewModel();
        storeIssueViewModel.setId(storeIssue.getId());
        storeIssueViewModel.setVoucherNo(storeIssue.getVoucherNo());
        storeIssueViewModel.setWorkflowOrder(workFlowAction.getOrderNumber());
        storeIssueViewModel.setActionEnabled(workFlowDto.getActionableIds().contains(storeIssue.getWorkFlowActionId()));
        storeIssueViewModel.setEditable(workFlowDto.getEditableIds().contains(storeIssue.getWorkFlowActionId()));
        storeIssueViewModel.setWorkFlowActionId(storeIssue.getWorkFlowActionId());
        storeIssueViewModel.setWorkflowName(workFlowAction.getName());
        storeIssueViewModel.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            storeIssueViewModel.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark,workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        storeIssueViewModel.setIsReturnApproved(storeIssue.getIsReturnApproved());
        return storeIssueViewModel;
    }

    private StoreIssueViewModel convertToResponseDto(StoreIssue storeIssue,
                                                     StoreDemandProjection storeDemandProjection,
                                                     StoreStockRoomProjection storeStockRoomProjection,
                                                     List<StoreIssueItemResponseDto> storeIssueItemDtoList,
                                                     WorkFlowDto workFlowDto, List<PartRemark> partRemarks) {
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeIssue.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeIssue.getWorkFlowActionId());

        StoreIssueViewModel storeIssueViewModel = new StoreIssueViewModel();
        storeIssueViewModel.setId(storeIssue.getId());
        storeIssueViewModel.setVoucherNo(storeIssue.getVoucherNo());
        storeIssueViewModel.setPartClassification(storeIssue.getPartClassification());
        storeIssueViewModel.setRemarks(storeIssue.getRemarks());
        storeIssueViewModel.setRegistration(storeIssue.getRegistration());
        storeIssueViewModel.setWorkflowName(workFlowAction.getName());
        storeIssueViewModel.setStockRoomType(storeIssue.getStockRoomType().getValue());
        storeIssueViewModel.setWorkflowOrder(workFlowAction.getOrderNumber());
        storeIssueViewModel.setActionEnabled(workFlowDto.getActionableIds().contains(storeIssue.getWorkFlowActionId()));
        storeIssueViewModel.setEditable(workFlowDto.getEditableIds().contains(storeIssue.getWorkFlowActionId()));
        storeIssueViewModel.setWorkFlowActionId(storeIssue.getWorkFlowActionId());
        if (Objects.nonNull(storeDemandProjection)) {
            storeIssueViewModel.setStoreDemandId(storeDemandProjection.getId());
            storeIssueViewModel.setStoreDemandNo(storeDemandProjection.getVoucherNo());
        }
        if (Objects.nonNull(storeStockRoomProjection)) {
            storeIssueViewModel.setStoreStockRoomId(storeStockRoomProjection.getId());
            storeIssueViewModel.setStoreStockRoom(storeStockRoomProjection.getCode());
        }
        storeIssueViewModel.setIsRejected(storeIssue.getIsRejected());
        storeIssueViewModel.setCreatedDate(storeIssue.getCreatedAt().toLocalDate());
        storeIssueViewModel.setRejectedDesc(storeIssue.getRejectedDesc());
        storeIssueViewModel.setStoreIssueItemResponseDtos(storeIssueItemDtoList);
        storeIssueViewModel.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            storeIssueViewModel.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark,workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        storeIssueViewModel.setIsReturnApproved(storeIssue.getIsReturnApproved());
        return storeIssueViewModel;
    }

    /**
     * This method will generate Approved PartIssue Preview Model
     *
     * @param issueId  {@link  Long }
     * @param pageable {@link Pageable}
     */
    public PageData generateStoreIssuePrintPreview(Long issueId, Pageable pageable) {
        StoreIssue storeIssue = findByIdUnfiltered(issueId);
        Set<StoreIssueItem> storeIssueItemList = storeIssue.getStoreIssueItemSet();

        List<StoreDemandItem> storeDemandItemList = storeIssueItemList.stream().map(StoreIssueItem::getStoreDemandItem)
                .collect(Collectors.toList());

        Map<Long, StoreDemandItem> storeDemandItemMap = storeDemandItemList.stream().collect(Collectors.toMap(StoreDemandItem::getId, Function.identity()));

        Set<Long> collectionsOfPartIds = storeDemandItemList.stream().map(StoreDemandItem::getPartId).collect(Collectors.toSet());

        Map<Long, UnitMeasurement> uom = unitMeasurementService.findAllUnitOfMeasures().stream().collect(Collectors.toMap(UnitMeasurement::getId, Function.identity()));

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionsOfPartIds).stream()
                .collect(Collectors.toMap(PartProjection::getId, Function.identity()));

        Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap =
                storePartAvailabilityService.findPartQuantityByPartIdIn(collectionsOfPartIds).stream()
                        .collect(Collectors.toMap(StorePartAvailabilityProjection::getPartId, Function.identity()));

        Set<Long> storeIssueItemIds = storeIssueItemList.stream().map(StoreIssueItem::getId).collect(Collectors.toSet());

        Map<Long, List<GrnAndSerialDto>> serialGrnMap = storeIssueSerialService.findStoreIssueSerialByStoreIssueItemIdIn(storeIssueItemIds)
                .stream().map(GrnAndSerialDto::from).collect(Collectors.groupingBy(GrnAndSerialDto::getIssueItemId));


        List<List<StoreIssueItemPrintResponseDto>> storeIssueItemResponseDtoList = storeIssueItemList.stream().map(storeIssueItem -> {
            StoreDemandItem storeDemandItem = storeDemandItemMap.get(storeIssueItem.getStoreDemandItemId());

            return convertToStoreIssuePrintReportViewModel(storeIssueItem, partProjectionMap, storePartAvailabilityProjectionMap, storeDemandItem, serialGrnMap, uom);
        }).collect(Collectors.toList());

        List<StoreIssueItemPrintResponseDto> listData = storeIssueItemResponseDtoList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return Helper.buildCustomPagedData(listData, pageable);
    }

    private void setDemandStatusIsActiveUpdatedValue(StoreIssue storeIssue) {
        List<StoreIssueItem> storeIssueItems = storeIssueItemDetailsService.getAllStoreIssueItemByStoreIssueId(storeIssue.getId());
        storeIssueItems.forEach(storeIssueItem -> {
            demandStatusService.updateActiveStatus(
                    storeIssue.getStoreDemand().getId(),
                    storeIssue.getId(),
                    storeIssueItem.getStoreDemandItem().getPart().getId(),
                    VoucherType.ISSUE,
                    storeIssue.getIsActive(),
                    storeIssue.getWorkFlowAction().getId());
        });
    }

    private void setDemandStatusRejectedUpdatedValue(StoreIssue storeIssue) {
        List<StoreIssueItem> storeIssueItems = storeIssueItemDetailsService.getAllStoreIssueItemByStoreIssueId(storeIssue.getId());
        storeIssueItems.forEach(storeIssueItem -> {
            demandStatusService.updateRejectedStatus(
                    storeIssue.getStoreDemand().getId(),
                    storeIssue.getId(),
                    storeIssueItem.getStoreDemandItem().getPart().getId(),
                    VoucherType.ISSUE,
                    storeIssue.getIsRejected());
        });
    }


    private List<StoreIssueItemPrintResponseDto> convertToStoreIssuePrintReportViewModel(StoreIssueItem storeIssueItem,
                                                                               Map<Long, PartProjection> partProjectionMap,
                                                                               Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap,
                                                                               StoreDemandItem storeDemandItem,
                                                                               Map<Long, List<GrnAndSerialDto>> serialGrnMap,
                                                                                         Map<Long,UnitMeasurement> unitMeasurement) {
        PartProjection partProjection = partProjectionMap.get(storeDemandItem.getPartId());
        StorePartAvailabilityProjection availabilityProjection = storePartAvailabilityProjectionMap.get(storeDemandItem.getPartId());
        List<GrnAndSerialDto> grnAndSerialDtos = serialGrnMap.get(storeIssueItem.getId());
        UnitMeasurement issuedUom = unitMeasurement.get(storeIssueItem.getUomId());
        UnitMeasurement demandedUom = unitMeasurement.get(storeDemandItem.getUomId());
        List<StoreIssueItemPrintResponseDto> storeIssueItemPrintResponseDtoList = new ArrayList<>();

        grnAndSerialDtos.forEach(grnAndSerialDto -> {
            StoreIssueItemPrintResponseDto issueItemPrintResponseDto = StoreIssueItemPrintResponseDto.builder()
                    .id(storeIssueItem.getId())
                    .issueId(storeIssueItem.getStoreIssueId())
                    .isActive(storeIssueItem.getIsActive())
                    .cardLineNo(storeIssueItem.getCardLineNo())
                    .remark(storeIssueItem.getRemark())
                    .issuedQuantity(storeIssueItem.getIssuedQuantity())
                    .quantityDemanded(storeDemandItem.getQuantityDemanded())
                    .storeDemandId(storeDemandItem.getStoreDemandId())
                    .priorityType(storeDemandItem.getPriorityType())
                    .availablePart(Objects.nonNull(availabilityProjection) ? availabilityProjection.getQuantity() : 0)
                    .grnNo(grnAndSerialDto.getGrnNo())
                    .quantity(grnAndSerialDto.getQuantity())
                    .serialId(grnAndSerialDto.getSerialId())
                    .serialNo(grnAndSerialDto.getSerialNo())
                    .issueItemId(grnAndSerialDto.getIssueItemId())
                    .price(grnAndSerialDto.getPrice())
                    .build();

            if (Objects.nonNull(partProjection)) {
                issueItemPrintResponseDto.setPartNo(partProjection.getPartNo());
                issueItemPrintResponseDto.setPartClassification(partProjection.getClassification());
                issueItemPrintResponseDto.setPartId(partProjection.getId());
                issueItemPrintResponseDto.setPartDescription(partProjection.getDescription());
            }
            if (Objects.nonNull(issuedUom)) {
                issueItemPrintResponseDto.setUnitMeasurementId(issuedUom.getId());
                issueItemPrintResponseDto.setUnitMeasurementCode(issuedUom.getCode());
            }
            if (Objects.nonNull(demandedUom)) {
                issueItemPrintResponseDto.setDemandedUomId(demandedUom.getId());
                issueItemPrintResponseDto.setDemandedUomCode(demandedUom.getCode());
            }
            storeIssueItemPrintResponseDtoList.add(issueItemPrintResponseDto);
        });

        return storeIssueItemPrintResponseDtoList;
    }

    private List<GrnAndSerialDto> getGrnAndSerial(List<GrnAndSerialDto> grnAndSerialDtos, WorkFlowAction workFlowAction) {
        List<PartSerialGrnProjection> storePartSerialByIdIn = storePartSerialService.findGrnByPartSerialByIdIn(grnAndSerialDtos.stream()
                .map(GrnAndSerialDto::getSerialId).collect(Collectors.toSet()));
        Map<Long, PartSerialGrnProjection> partSerialGrnProjectionMap = storePartSerialByIdIn.stream()
                .collect(Collectors.toMap(PartSerialGrnProjection::getId, Function.identity()));
        grnAndSerialDtos.forEach(grnAndSerialDto -> {
            if (workFlowAction.equals(workFlowActionService.findFinalAction()) ||
                    StringUtils.isBlank(grnAndSerialDto.getGrnNo())) {
                PartSerialGrnProjection partSerialGrnProjection = partSerialGrnProjectionMap.get(grnAndSerialDto.getSerialId());
                if (Objects.nonNull(partSerialGrnProjection)) {
                    grnAndSerialDto.setGrnNo(partSerialGrnProjection.getGrnNo());
                }
            }
        });
        return grnAndSerialDtos;
    }

    public List<DashboardProjection> getStoreIssueData(Integer month) {
        return storeIssueRepository.getStoreIssueDataForMonths(month);
    }
}
