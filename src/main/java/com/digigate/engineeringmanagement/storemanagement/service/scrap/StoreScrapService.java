package com.digigate.engineeringmanagement.storemanagement.service.scrap;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrap;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreScrapPartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.ScrapPartSerialDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapPartDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.scrap.StoreScrapPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.scrap.StoreScrapViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.scrap.StoreScrapRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.*;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.VALUE_ZERO;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_SCRAP;
import static java.lang.Boolean.TRUE;

@Service
public class StoreScrapService extends AbstractSearchService<StoreScrap, StoreScrapDto, CommonWorkFlowSearchDto> {
    private final StoreScrapRepository storeScrapRepository;
    private final ApprovalStatusService approvalStatusService;
    private final StoreScrapPartService storeScrapPartService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final WorkFlowUtil workFlowUtil;
    private final Helper helper;
    private final UserService userService;
    private final GenericAttachmentService genericAttachmentService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final StorePartAvailabilityLogService partAvailabilityLogService;
    private final StorePartSerialService storePartSerialService;
    private final StoreScrapPartSerialService storeScrapPartSerialService;
    private final PartRemarkService partRemarkService;

    /**
     * Constructors
     *
     * @param storeScrapRepository         {@link StoreScrapRepository}
     * @param approvalStatusService        {@link ApprovalStatusService}
     * @param storeScrapPartService        {@link StoreScrapPartService}
     * @param workFlowActionService        {@link WorkFlowActionService}
     * @param approvalEmployeeService      {@link ApprovalEmployeeService}
     * @param storeVoucherTrackingService  {@link StoreVoucherTrackingService}
     * @param workFlowUtil                 {@link WorkFlowUtil}
     * @param helper                       {@link Helper}
     * @param userService                  {@link UserService}
     * @param genericAttachmentService     {@link GenericAttachmentService}
     * @param storePartAvailabilityService
     * @param partAvailabilityLogService
     * @param storePartSerialService
     * @param storeScrapPartSerialService
     * @param partRemarkService
     */
    public StoreScrapService(StoreScrapRepository storeScrapRepository,
                             ApprovalStatusService approvalStatusService,
                             StoreScrapPartService storeScrapPartService,
                             WorkFlowActionService workFlowActionService,
                             ApprovalEmployeeService approvalEmployeeService,
                             StoreVoucherTrackingService storeVoucherTrackingService,
                             WorkFlowUtil workFlowUtil,
                             Helper helper,
                             UserService userService,
                             GenericAttachmentService genericAttachmentService,
                             StorePartAvailabilityService storePartAvailabilityService,
                             StorePartAvailabilityLogService partAvailabilityLogService,
                             StorePartSerialService storePartSerialService, StoreScrapPartSerialService storeScrapPartSerialService, PartRemarkService partRemarkService) {
        super(storeScrapRepository);
        this.storeScrapRepository = storeScrapRepository;
        this.approvalStatusService = approvalStatusService;
        this.storeScrapPartService = storeScrapPartService;
        this.workFlowActionService = workFlowActionService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.workFlowUtil = workFlowUtil;
        this.helper = helper;
        this.userService = userService;
        this.genericAttachmentService = genericAttachmentService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.partAvailabilityLogService = partAvailabilityLogService;
        this.storePartSerialService = storePartSerialService;
        this.storeScrapPartSerialService = storeScrapPartSerialService;
        this.partRemarkService = partRemarkService;
    }

    /**
     * Custom Save method
     *
     * @param storeScrapDto {@link StoreScrapDto}
     * @return Successfully saved message
     */
    @Transactional
    @Override
    public StoreScrap create(StoreScrapDto storeScrapDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        StoreScrap storeScrap = convertToEntity(storeScrapDto);
        storeScrap.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        super.saveItem(storeScrap);

        approvalStatusService.create(ApprovalStatusDto.of(storeScrap.getId(), STORE_SCRAP, workFlowAction));
        recordAttachments(storeScrapDto, storeScrap);
        storeScrapPartService.createOrUpdate(storeScrapDto.getScrapParts(), storeScrap, null);
        return storeScrap;
    }

    /**
     * Custom update method
     *
     * @param storeScrapDto {@link StoreScrapDto}
     * @param id            required id
     * @return successfully updated message
     */
    @Transactional
    @Override
    public StoreScrap update(StoreScrapDto storeScrapDto, Long id) {
        filterInactiveSerials(storeScrapDto);
        StoreScrap storeScrap = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeScrap.getWorkFlowActionId());

        WorkFlowAction currentAction = storeScrap.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        StoreScrap entity = updateEntity(storeScrapDto, storeScrap);
        StoreScrap storeScrapEntity = super.saveItem(entity);
        updateAttachments(storeScrapDto, storeScrapEntity);
        storeScrapPartService.createOrUpdate(storeScrapDto.getScrapParts(), entity, id);

        return entity;
    }

    /**
     * Change decision api
     *
     * @param id                 required id
     * @param approvalRequestDto {@link ApprovalRequestDto}
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StoreScrap storeScrap = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeScrap.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeScrap.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(storeScrap.getId(),storeScrap.getWorkFlowAction().getId(),
                    RemarkType.STORE_SCRAP_APPROVAL_REMARK, approvalRequestDto.getApprovalDesc());

            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, storeScrap.getWorkFlowAction());
            if (nextAction.equals(workFlowActionService.findFinalAction())) {
                updatePartAvailabilityLogAndQuantity(storeScrap);
            }
            approvalStatusService.create(ApprovalStatusDto.of(storeScrap.getId(), STORE_SCRAP,
                    storeScrap.getWorkFlowAction()));
            storeScrap.setWorkFlowAction(nextAction);
        } else {
            storeScrap.setIsRejected(true);
            if (!StringUtils.hasText(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storeScrap.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(storeScrap);
    }
    /**
     * Change active status
     *
     * @param id       required id
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreScrap storeScrap = findByIdUnfiltered(id);

        workFlowUtil.validateUpdatability(storeScrap.getWorkFlowActionId());

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, storeScrap.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(storeScrap.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(storeScrap.getId(), workFlowAction, RemarkType.STORE_SCRAP_APPROVAL_REMARK);
            storeScrap.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(storeScrap.getWorkFlowAction(), STORE_SCRAP,
                    storeScrap.getId()));
            storeScrap.setIsRejected(false);
        }

        storeScrap.setIsActive(isActive);
        super.saveItem(storeScrap);
    }

    /**
     * Custom search
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable paged data
     * @return required data
     */
    @Override
    public PageData search(CommonWorkFlowSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StoreScrap> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = storeScrapRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains(dto.getIsActive(),
                                pendingSearchWorkFlowIds, dto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = storeScrapRepository.
                        findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains(
                                dto.getIsActive(), approvedId, dto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = storeScrapRepository
                        .findAllByIsRejectedTrueAndVoucherNoContains(dto.getQuery(), pageable);
                break;
            default:
                pageData = storeScrapRepository.
                        findAllByIsActiveAndVoucherNoContains(dto.getIsActive(), dto.getQuery(),
                                pageable);
                break;
        }

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), approvedActionForUser))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public StoreScrapViewModel getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        StoreScrapViewModel storeScrapViewModel = getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
        storeScrapViewModel.setAttachmentList(genericAttachmentService.getLinksByFeatureNameAndId(FeatureName.STORE_SCRAP, id));

        return storeScrapViewModel;
    }

    @Override
    protected Specification<StoreScrap> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(StoreScrap storeScrap) {
        return null;
    }

    @Override
    protected StoreScrap convertToEntity(StoreScrapDto storeScrapDto) {
        StoreScrap storeScrap = new StoreScrap();
        storeScrap.setVoucherNo(storeVoucherTrackingService.generateUniqueNo(VoucherType.SCRAP));
        storeScrap.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        storeScrap.setRemarks(storeScrapDto.getRemarks());
        return storeScrap;
    }

    @Override
    protected StoreScrap updateEntity(StoreScrapDto dto, StoreScrap entity) {
        entity.setRemarks(dto.getRemarks());
        return entity;
    }

    private List<StoreScrapViewModel> getResponseData(List<StoreScrap> storeScrapList,
                                                      List<WorkFlowActionProjection> approvedActions) {
        Set<Long> loanIds = storeScrapList.stream().map(StoreScrap::getId).collect(Collectors.toSet());

        List<StoreScrapPart> details = storeScrapPartService.findByStoreScrapIdIn(loanIds);

        Map<Long, List<StoreScrapPartViewModel>> partMap = storeScrapPartService.convertToResponse(details)
                .stream().collect(Collectors.groupingBy(StoreScrapPartViewModel::getScrapId));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(loanIds, approvedActions, STORE_SCRAP);

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(loanIds,
                RemarkType.STORE_SCRAP_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        Set<Long> submittedByIds = storeScrapList.stream().map(StoreScrap::getSubmittedById).collect(Collectors.toSet());

        Map<Long, UsernameProjection>  usernameProjectionMap = userService.findUsernameByIdList(submittedByIds).stream()
                .collect(Collectors.toMap(UsernameProjection::getId, Function.identity()));

        return storeScrapList
                .stream().map(storeScrap ->
                        convertToResponseDto(storeScrap,
                                partMap.get(storeScrap.getId()),
                                workFlowDto, usernameProjectionMap.get(storeScrap.getSubmittedById()),
                                partRemarkList.get(storeScrap.getId())))
                .collect(Collectors.toList());
    }

    private void filterInactiveSerials(StoreScrapDto storeScrapDto) {
        List<StoreScrapPartDto> scrapParts = storeScrapDto.getScrapParts();
        scrapParts.forEach(dto -> {
            dto.setScrapPartSerialDtos(dto.getScrapPartSerialDtos().stream().filter(ScrapPartSerialDto::getIsActive).collect(Collectors.toList()));
        });
    }

    private StoreScrapViewModel convertToResponseDto(StoreScrap storeScrap,
                                                     List<StoreScrapPartViewModel> partList,
                                                     WorkFlowDto workFlowDto,
                                                     UsernameProjection usernameProjection,
                                                     List<PartRemark> partRemarks ) {
        StoreScrapViewModel viewModel = new StoreScrapViewModel();

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeScrap.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeScrap.getWorkFlowActionId());


        viewModel.setId(storeScrap.getId());
        viewModel.setVoucherNo(storeScrap.getVoucherNo());
        viewModel.setWorkFlowActionId(storeScrap.getWorkFlowActionId());
        viewModel.setRemarks(storeScrap.getRemarks());
        viewModel.setIsRejected(storeScrap.getIsRejected());
        viewModel.setRejectedDesc(storeScrap.getRejectedDesc());
        viewModel.setStoreScrapPartViewModels(partList);
        viewModel.setWorkflowName(workFlowAction.getName());
        viewModel.setWorkflowOrder(workFlowAction.getOrderNumber());
        viewModel.setActionEnabled(workFlowDto.getActionableIds().contains(storeScrap.getWorkFlowActionId()));
        viewModel.setEditable(workFlowDto.getEditableIds().contains(storeScrap.getWorkFlowActionId()));
        viewModel.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            viewModel.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark,workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }

        if(Objects.nonNull(usernameProjection)){
            viewModel.setSubmittedById(usernameProjection.getId());
            viewModel.setSubmittedBy(usernameProjection.getLogin());
        }
        return viewModel;
    }

    private void recordAttachments(StoreScrapDto storeScrapDto, StoreScrap storeScrap) {
        Set<String> attachmentList = storeScrapDto.getAttachmentList();
        if(CollectionUtils.isNotEmpty(attachmentList)){
            genericAttachmentService.saveAllAttachments(attachmentList, FeatureName.STORE_SCRAP, storeScrap.getId());
        }
    }
    private void updateAttachments(StoreScrapDto storeScrapDto, StoreScrap storeScrap) {
        Set<String> attachmentList = storeScrapDto.getAttachmentList();
        if(CollectionUtils.isNotEmpty(attachmentList)){
            genericAttachmentService.updateByRecordId(FeatureName.STORE_SCRAP, storeScrap.getId(), attachmentList);
        }
    }

    /**
     * This method will Updaate Part Availability, Availability Log and Serial Status
     *
     * @param storeScrap {@link StoreScrap}
     */
    private void updatePartAvailabilityLogAndQuantity(StoreScrap storeScrap) {
        List<StoreScrapPart> storeScrapPartSet = storeScrapPartService.findByStoreScrapIdIn(Collections.singleton(storeScrap.getId()));
        Set<Long> partIds = storeScrapPartSet.stream().map(StoreScrapPart::getPartId).collect(Collectors.toSet());
        Map<Long, StorePartAvailability> partAvailabilityMap = storePartAvailabilityService.findByPartIdIn(partIds).stream()
                .collect(Collectors.toMap(StorePartAvailability::getPartId, Function.identity()));

        for (StoreScrapPart scrapPart : storeScrapPartSet) {
            int subtractedQuantity = 0;
            StorePartAvailability storePartAvailability = partAvailabilityMap.get(scrapPart.getPartId());
            if (Objects.isNull(storePartAvailability)) {
                throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_AVAILABILITY_IS_NOT_FOUND);
            }
            List<StoreScrapPartSerial> storeScrapPartSerialList = storeScrapPartSerialService.findAllByStoreScrapPartId(scrapPart.getId());
            Set<Long> partSerialIds = storeScrapPartSerialList.stream().map(StoreScrapPartSerial::getStorePartSerialId)
                .collect(Collectors.toSet());
            List<StorePartSerial> storePartSerialList = storePartSerialService.findAllByIdIn(partSerialIds);
            List<StorePartAvailabilityLog> storePartAvailabilityLogList = new ArrayList<>();

            List<StoreScrapPartSerialProjection> scrapPartSerialList = storeScrapPartSerialService.findAllByStoreScrapPartIdIn(Collections.singleton(scrapPart.getId()));

            Map<Long, StoreScrapPartSerialProjection> serialIds = scrapPartSerialList.stream()
                    .collect(Collectors.toMap(StoreScrapPartSerialProjection::getStorePartSerialId, Function.identity(), (a, b) -> a));

            for (StorePartSerial partSerial : storePartSerialList) {

                if (partSerial.notExistsInStore()) {
                    throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_SERIAL_IS_NOT_FOUND);
                }
                StoreScrapPartSerialProjection storeScrapPartSerial = serialIds.get(partSerial.getId());
                int subQ = INT_ONE;

                if (partSerial.getPartStatus() == PartStatus.SERVICEABLE) {
                    subtractedQuantity += storeScrapPartSerial.getQuantity();
                }

                if (scrapPart.getPart().getClassification() == PartClassification.CONSUMABLE) {
                    int newLotQuantity = partSerial.getQuantity() - storeScrapPartSerial.getQuantity();
                    partSerial.setQuantity(newLotQuantity);
                    if (newLotQuantity == VALUE_ZERO) {
                        partSerial.setParentType(StorePartAvailabilityLogParentType.SCRAP);
                        partSerial.setPartStatus(PartStatus.SCRAP);
                    } else if (newLotQuantity < 0) {
                        throw EngineeringManagementServerException.notFound(ErrorId.STOCK_NOT_AVAILABLE);
                    }
                    subQ = storeScrapPartSerial.getQuantity();
                } else {
                    partSerial.setPartStatus(PartStatus.SCRAP);
                    partSerial.setParentType(StorePartAvailabilityLogParentType.SCRAP);
                }

                StorePartAvailabilityLog storePartAvailabilityLog = populateStorePartAvailabilityLog(partSerial, subQ,
                        storeScrap.getVoucherNo(), storeScrap.getSubmittedById(), storeScrap.getWorkFlowActionId(), storeScrap.getId());
                storePartAvailabilityLogList.add(storePartAvailabilityLog);
            }
            partAvailabilityLogService.saveItemList(storePartAvailabilityLogList);
            storePartAvailabilityService.updatePartQuantity(storePartAvailability, TransactionType.SCRAP, subtractedQuantity);
            storePartSerialService.saveItemList(storePartSerialList);
        }
    }

    /**
     * This Method will generate StorePartAvailabilityLog entity
     *
     * @param storePartSerial {@link StorePartSerial}
     * @param subQ
     * @return {@link StorePartAvailabilityLog}
     */
    private StorePartAvailabilityLog populateStorePartAvailabilityLog(StorePartSerial storePartSerial, int subQ,
                                                                      String voucherNo, Long initialUser,
                                                                      Long finalUser, Long ownId) {
        return StorePartAvailabilityLog.builder()
                .storePartSerial(storePartSerial)
                .partStatus(storePartSerial.getPartStatus())
                .grnNo(storePartSerial.getGrnNo())
                .quantity(subQ)
                .parentType(StorePartAvailabilityLogParentType.SCRAP)
                .receiveDate(LocalDate.now())
                .unitPrice(storePartSerial.getPrice())
                .currencyId(storePartSerial.getCurrencyId())
                .transactionType(TransactionType.SCRAP)
                .voucherNo(voucherNo)
                .parentId(ownId)
                .submittedBy(User.withId(initialUser))
                .workFlowAction(WorkFlowAction.withId(finalUser))
                .inStock(storePartSerial.getStorePartAvailability().getQuantity() - subQ)
                .build();
    }
}

