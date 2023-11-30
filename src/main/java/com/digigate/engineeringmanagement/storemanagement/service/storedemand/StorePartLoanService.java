package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.ExternalDepartmentService;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoan;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoanDetails;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.ExternalDepartmentProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDetailDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartLoanResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StorePartLoanRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_PART_LOAN;
import static java.lang.Boolean.TRUE;

@Service
public class StorePartLoanService extends AbstractSearchService<StorePartLoan, StorePartLoanDto, CommonWorkFlowSearchDto> {
    private final StorePartLoanRepository storePartLoanRepository;
    private final ApprovalStatusService approvalStatusService;
    private final StoreDemandService storeDemandService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final ExternalDepartmentService externalDepartmentService;
    private final StorePartLoanDetailsService storePartLoanDetailsService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final WorkFlowUtil workFlowUtil;
    private final Helper helper;
    private final PartRemarkService partRemarkService;

    public StorePartLoanService(StorePartLoanRepository storePartLoanRepository,
                                ApprovalStatusService approvalStatusService,
                                StoreDemandService storeDemandService,
                                WorkFlowActionService workFlowActionService,
                                ApprovalEmployeeService approvalEmployeeService,
                                ExternalDepartmentService externalDepartmentService,
                                StorePartLoanDetailsService storePartLoanDetailsService,
                                StoreVoucherTrackingService storeVoucherTrackingService,
                                WorkFlowUtil workFlowUtil,
                                Helper helper,
                                PartRemarkService partRemarkService) {
        super(storePartLoanRepository);
        this.storePartLoanRepository = storePartLoanRepository;
        this.approvalStatusService = approvalStatusService;
        this.storeDemandService = storeDemandService;
        this.workFlowActionService = workFlowActionService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.externalDepartmentService = externalDepartmentService;
        this.storePartLoanDetailsService = storePartLoanDetailsService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.workFlowUtil = workFlowUtil;
        this.helper = helper;
        this.partRemarkService = partRemarkService;
    }

    /**
     * Custom create method
     *
     * @param storePartLoanDto {@link StorePartLoanDto}
     * @return {@link StorePartLoanDto}
     */
    @Transactional
    @Override
    public StorePartLoan create(StorePartLoanDto storePartLoanDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        StorePartLoan storePartLoan = convertToEntity(storePartLoanDto);
        storePartLoan.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        storePartLoan = super.saveItem(storePartLoan);

        approvalStatusService.create(ApprovalStatusDto.of(storePartLoan.getId(), STORE_PART_LOAN, workFlowAction));
        storePartLoanDetailsService.saveAll(storePartLoanDto.getStorePartLoanDetailDtoList(), storePartLoan);
        return storePartLoan;
    }

    /**
     * Custom update
     *
     * @param storePartLoanDto {@link StorePartLoanDto}
     * @param id               required id
     * @return {@link StorePartLoanDto}
     */
    @Transactional
    @Override
    public StorePartLoan update(StorePartLoanDto storePartLoanDto, Long id) {
        StorePartLoan storePartLoan = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storePartLoan.getWorkFlowActionId());

        WorkFlowAction currentAction = storePartLoan.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        StorePartLoan entity = updateEntity(storePartLoanDto, storePartLoan);
        storePartLoanDetailsService.updateAll(storePartLoanDto.getStorePartLoanDetailDtoList(), storePartLoan);
        return super.saveItem(entity);
    }

    /**
     * Make decision
     *
     * @param id                 required id
     * @param approvalRequestDto {@link ApprovalRequestDto}
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StorePartLoan storePartLoan = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storePartLoan.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storePartLoan.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(storePartLoan.getId(), STORE_PART_LOAN,
                    storePartLoan.getWorkFlowAction()));
            storePartLoan.setWorkFlowAction(workFlowActionService.getNavigatedAction(true, storePartLoan.getWorkFlowAction()));
        } else {
            storePartLoan.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storePartLoan.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(storePartLoan);
    }

    /**
     * Change active status
     *
     * @param id       required id
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StorePartLoan storePartLoan = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storePartLoan.getWorkFlowActionId());

        if (isActive == TRUE) {
            workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storePartLoan.getWorkFlowActionId()));

            WorkFlowAction prevAction = workFlowActionService.getNavigatedAction(false, storePartLoan.getWorkFlowAction());
            if (!prevAction.equals(workFlowActionService.findInitialAction())) {
                storePartLoan.setWorkFlowAction(prevAction);
                approvalStatusService.deleteByParentAndWorkflow(STORE_PART_LOAN, storePartLoan.getId(), prevAction.getId());
                storePartLoan.setIsRejected(false);
            }
        }
        super.updateActiveStatus(id, isActive);
        storePartLoan.setIsActive(isActive);
        super.saveItem(storePartLoan);
    }

    /**
     * Custom search
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable paged data
     * @return {@link StorePartLoanDto}
     */
    @Override
    public PageData search(CommonWorkFlowSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StorePartLoan> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = storePartLoanRepository.
                        findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndLoanNoContains(dto.getIsActive(),
                                pendingSearchWorkFlowIds, dto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = storePartLoanRepository.findAllByIsActiveAndWorkFlowActionIdAndLoanNoContains(
                        dto.getIsActive(), approvedId, dto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = storePartLoanRepository
                        .findAllByIsRejectedTrueAndLoanNoContains(dto.getQuery(), pageable);
                break;
            default:
                pageData = storePartLoanRepository.
                        findAllByIsActiveAndLoanNoContains(dto.getIsActive(), dto.getQuery(),
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

    /**
     * get single
     * @param id required id
     * @return {@link StorePartLoanDto}
     */
    @Override
    public StorePartLoanResponseDto getSingle(Long id) {

        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    @Override
    protected Specification<StorePartLoan> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(StorePartLoan storePartLoan) {
        return null;
    }

    @Override
    protected StorePartLoan convertToEntity(StorePartLoanDto storePartLoanDto) {
        StorePartLoan storePartLoan = new StorePartLoan();

        storePartLoan.setLoanExpires(storePartLoanDto.getLoanExpires());
        storePartLoan.setUpdateDate(storePartLoanDto.getUpdateDate());
        storePartLoan.setAttachment(storePartLoanDto.getAttachment());
        storePartLoan.setRemarks(storePartLoanDto.getRemarks());
        storePartLoan.setSubmittedById(User.withId(Helper.getAuthUserId()));
        if (Objects.nonNull(storePartLoanDto.getVendorId())) {
            storePartLoan.setVendor(externalDepartmentService.findById(storePartLoanDto.getVendorId()));
        }
        storePartLoan.setLoanNo(storeVoucherTrackingService.generateUniqueNo(VoucherType.STORE_PART_LOAN));
        return storePartLoan;
    }

    @Override
    protected StorePartLoan updateEntity(StorePartLoanDto dto, StorePartLoan entity) {
        entity.setUpdateDate(dto.getUpdateDate());
        entity.setLoanExpires(dto.getLoanExpires());
        entity.setAttachment(dto.getAttachment());
        entity.setRemarks(dto.getRemarks());
        if (Objects.nonNull(dto.getVendorId()) && !dto.getVendorId().equals(entity.getVendorId())) {
            entity.setVendor(externalDepartmentService.findById(dto.getVendorId()));
        }
        return entity;
    }

    private List<StorePartLoanResponseDto> getResponseData(List<StorePartLoan> storePartLoans,
                                                           List<WorkFlowActionProjection> approvedActions) {
        Set<Long> collectionOfExternalDepartmentIds = storePartLoans.stream()
                .map(StorePartLoan::getVendorId).collect(Collectors.toSet());

        Map<Long, ExternalDepartmentProjection> externalDepartmentProjectionMap = externalDepartmentService.
                findByIdIn(collectionOfExternalDepartmentIds)
                .stream().collect(Collectors.toMap(ExternalDepartmentProjection::getId, Function.identity()));

        Set<Long> loanIds = storePartLoans.stream().map(StorePartLoan::getId).collect(Collectors.toSet());

        List<StorePartLoanDetails> details = storePartLoanDetailsService.findByStorePartLoanIdIn(loanIds);

        Map<Long, List<StorePartLoanDetailDto>> detailsByLoan = storePartLoanDetailsService.convertToResponse(details)
                .stream().collect(Collectors.groupingBy(StorePartLoanDetailDto::getStorePartLoanId));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(loanIds, approvedActions, STORE_PART_LOAN);

        return storePartLoans
                .stream().map(storePartLoan ->
                        convertToResponseDto(storePartLoan,
                                externalDepartmentProjectionMap.get(storePartLoan.getVendorId()),
                                detailsByLoan.get(storePartLoan.getId()),
                                workFlowDto))
                .collect(Collectors.toList());
    }

    private StorePartLoanResponseDto convertToResponseDto(StorePartLoan storePartLoan,
                                                          ExternalDepartmentProjection externalDepartmentProjection,
                                                          List<StorePartLoanDetailDto> detailDtoList,
                                                          WorkFlowDto workFlowDto) {
        StorePartLoanResponseDto responseDto = new StorePartLoanResponseDto();

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storePartLoan.getId(), new ArrayList<>());
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storePartLoan.getWorkFlowActionId());

        responseDto.setId(storePartLoan.getId());
        responseDto.setLoanNo(storePartLoan.getLoanNo());
        responseDto.setRemarks(storePartLoan.getRemarks());
        responseDto.setAttachment(storePartLoan.getAttachment());
        responseDto.setUpdateDate(storePartLoan.getUpdateDate());
        responseDto.setLoanExpires(storePartLoan.getLoanExpires());
        responseDto.setStorePartLoanDetailDtoList(detailDtoList);
        if (Objects.nonNull(externalDepartmentProjection)) {
            responseDto.setExternalDepartmentId(externalDepartmentProjection.getId());
            responseDto.setExternalDepartmentName(externalDepartmentProjection.getName());
        }
        responseDto.setIsRejected(storePartLoan.getIsRejected());
        responseDto.setRejectedDesc(storePartLoan.getRejectedDesc());
        responseDto.setWorkFlowActionId(storePartLoan.getWorkFlowActionId());
        responseDto.setWorkflowName(workFlowAction.getName());
        responseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        responseDto.setActionEnabled(workFlowDto.getActionableIds().contains(storePartLoan.getWorkFlowActionId()));
        responseDto.setEditable(workFlowDto.getEditableIds().contains(storePartLoan.getWorkFlowActionId()));
        responseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        return responseDto;
    }
}
