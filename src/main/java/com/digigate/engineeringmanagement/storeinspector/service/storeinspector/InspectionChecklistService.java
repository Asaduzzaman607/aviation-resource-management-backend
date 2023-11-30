package com.digigate.engineeringmanagement.storeinspector.service.storeinspector;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleItemEnum;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionChecklist;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.InspectionChecklistProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.InspectionChecklistRequestDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.InspectionChecklistResponseDto;
import com.digigate.engineeringmanagement.storeinspector.repository.storeinspector.InspectionChecklistRepository;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;
import static com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType.OWN_DEPARTMENT;
import static com.digigate.engineeringmanagement.storemanagement.constant.RemarkType.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class InspectionChecklistService extends AbstractSearchService<InspectionChecklist, InspectionChecklistRequestDto, VendorSearchDto> {

    private final InspectionChecklistRepository inspectionChecklistRepository;
    private final WorkFlowActionService workFlowActionService;
    private final WorkFlowUtil workFlowUtil;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final Helper helper;
    private final ApprovalStatusService approvalStatusService;
    private final InspectionCriterionService inspectionCriterionService;
    private final PartRemarkService partRemarkService;

    @Autowired
    public InspectionChecklistService(AbstractRepository<InspectionChecklist> repository,
                                      InspectionChecklistRepository inspectionChecklistRepository,
                                      WorkFlowActionService workFlowActionService,
                                      WorkFlowUtil workFlowUtil, Helper helper,
                                      ApprovalEmployeeService approvalEmployeeService,
                                      ApprovalStatusService approvalStatusService,
                                      @Lazy InspectionCriterionService inspectionCriterionService,
                                      PartRemarkService partRemarkService) {
        super(repository);
        this.inspectionChecklistRepository = inspectionChecklistRepository;
        this.workFlowActionService = workFlowActionService;
        this.workFlowUtil = workFlowUtil;
        this.approvalEmployeeService = approvalEmployeeService;
        this.helper = helper;
        this.approvalStatusService = approvalStatusService;
        this.inspectionCriterionService = inspectionCriterionService;
        this.partRemarkService = partRemarkService;
    }

    public List<InspectionChecklistProjection> findDescriptionByIdIn(Set<Long> descriptionIdSet) {
        return inspectionChecklistRepository.findDescriptionByIdIn(descriptionIdSet);
    }

    /**
     * This method is responsible for create Inspection Checklist
     *
     * @param dto {@link InspectionChecklistRequestDto}
     * @return successfully created message
     */
    @Override
    public InspectionChecklist create(InspectionChecklistRequestDto dto) {

        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);

        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowAction);

        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        InspectionChecklist inspectionChecklist = convertToEntity(dto);
        inspectionChecklist.setWorkflowType(VendorWorkFlowType.OWN_DEPARTMENT);

        inspectionChecklist.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        inspectionChecklist.setSubmoduleItemId(helper.getSubModuleItemId());
        inspectionChecklist = super.saveItem(inspectionChecklist);

        approvalStatusService.create(ApprovalStatusDto.of(inspectionChecklist.getId(), STORE_INSPECTOR_CHECKLIST, workFlowAction));

        return inspectionChecklist;
    }

    /**
     * This method is responsible for update Inspection Checklist
     *
     * @param inspectionChecklistRequestDto {@link InspectionChecklistRequestDto}
     * @param id                            which checklist want to update
     * @return successfully updated message
     */
    @Override
    public InspectionChecklist update(InspectionChecklistRequestDto inspectionChecklistRequestDto, Long id) {
        InspectionChecklist inspectionChecklist = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        validateUpdatability(inspectionChecklist);

        WorkFlowAction currentAction = inspectionChecklist.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(), workFlowActionService
                .getNavigatedAction(false, currentAction).getId()));

        InspectionChecklist entity = updateEntity(inspectionChecklistRequestDto, inspectionChecklist);
        return super.saveItem(entity);
    }

    /**
     * Change active status
     *
     * @param id       which user want to change status
     * @param isActive boolean field
     * @return status changed  message
     */

    public void updateActiveStatus(Long id, Boolean isActive, VendorWorkFlowType workFlowType) {

        InspectionChecklist inspectionChecklist = findByIdUnfiltered(id);

        if (isActive == FALSE && inspectionCriterionService.existsByDescriptionIdAndIsActiveTrue(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }

        workFlowUtil.validateUpdatability(inspectionChecklist.getWorkFlowActionId());

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, inspectionChecklist.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(inspectionChecklist.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(inspectionChecklist.getId(), workFlowAction, getRemarkType(workFlowType));
            inspectionChecklist.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(inspectionChecklist.getWorkFlowAction(),
                    workflowType(workFlowType), inspectionChecklist.getId()));
            inspectionChecklist.setIsRejected(false);
        }
        inspectionChecklist.setIsActive(isActive);
        saveItem(inspectionChecklist);
    }

    public RemarkType getRemarkType(VendorWorkFlowType workflowType) {
        RemarkType remarkType = null;

        switch (workflowType) {
            case OWN_DEPARTMENT:
                remarkType = RemarkType.CHECKLIST_OWN_DEPARTMENT_APPROVAL_REMARK;
                break;
            case QUALITY:
                remarkType = RemarkType.CHECKLIST_QUALITY_APPROVAL_REMARK;
                break;
        }

        return remarkType;
    }
    /**
     * This method is responsible for get single active data
     *
     * @param id which user want to get that id data
     * @return all active data of that id
     */
    @Override
    public InspectionChecklistResponseDto getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));

    }

    /**
     * This method is responsible for get ALl active data
     *
     * @param isActive boolean field
     * @param pageable Pageable
     * @return all active data
     */
    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<InspectionChecklist> pageData = inspectionChecklistRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), Collections.emptyList()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }


    /**
     * Make decision for change approval status
     *
     * @param id                 which user want to change approval
     * @param approvalRequestDto {@link ApprovalRequestDto}
     * @return successfully status changed message
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, VendorWorkFlowType workFlowType) {
        InspectionChecklist inspectionChecklist = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(inspectionChecklist.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(inspectionChecklist.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == TRUE) {
            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, inspectionChecklist.getWorkFlowAction());
            saveApprovalRemarks(approvalRequestDto, inspectionChecklist, workFlowType);
            if (nextAction.equals(workFlowActionService.findFinalAction()) && inspectionChecklist.getWorkflowType().equals(VendorWorkFlowType.OWN_DEPARTMENT)) {

                approvalStatusService.create(ApprovalStatusDto.of(inspectionChecklist.getId(), STORE_INSPECTOR_CHECKLIST,
                        inspectionChecklist.getWorkFlowAction()));
                Long qualitySmiId = SubModuleItemEnum.QUALITY_PENDING_INSPECTION_CHECKLIST.getSubModuleItemId();
                List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, qualitySmiId);
                WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);

                inspectionChecklist.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
                inspectionChecklist.setWorkflowType(VendorWorkFlowType.QUALITY);
                inspectionChecklist.setSubmoduleItemId(qualitySmiId);

                approvalStatusService.create(ApprovalStatusDto.of(inspectionChecklist.getId(), STORE_INSPECTOR_CHECKLIST_QUALITY, workFlowAction));
            } else {
                approvalStatusService.create(ApprovalStatusDto.of(inspectionChecklist.getId(), workflowType(workFlowType), inspectionChecklist.getWorkFlowAction()));
                inspectionChecklist.setWorkFlowAction(nextAction);
            }

        } else {
            inspectionChecklist.setIsRejected(true);

            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            updateWorkflowActionToInitialStage(inspectionChecklist);
            inspectionChecklist.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(inspectionChecklist);
    }

    private void saveApprovalRemarks(ApprovalRequestDto approvalRequestDto, InspectionChecklist inspectionChecklist, VendorWorkFlowType workFlowType) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
            throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
        }
        partRemarkService.saveApproveRemark(inspectionChecklist.getId(), inspectionChecklist.getWorkFlowAction().getId(), getRemarkType(workFlowType), approvalRequestDto.getApprovalDesc());// save approval remarks
    }

    /**
     * This method is responsible for search Inspection checklist
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable page number
     * @return required search result
     */
    @Override
    public PageData search(VendorSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<InspectionChecklist> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();

        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.
                        findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = inspectionChecklistRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndWorkflowTypeAndDescriptionContains
                                (dto.getIsActive(), pendingSearchWorkFlowIds, dto.getWorkflowType(), dto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = inspectionChecklistRepository.findAllByIsActiveAndWorkFlowActionIdAndDescriptionContains(
                        dto.getIsActive(), approvedId, dto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = inspectionChecklistRepository.findAllByIsRejectedTrueAndWorkflowTypeAndDescriptionContains(dto.getWorkflowType(), dto.getQuery(), pageable);
                break;
            default:
                pageData = inspectionChecklistRepository.findAllByIsActiveAndDescriptionContains(dto.getIsActive(), dto.getQuery(),
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


    @Override
    protected InspectionChecklistResponseDto convertToResponseDto(InspectionChecklist inspectionChecklist) {
        return null;
    }

    @Override
    protected InspectionChecklist convertToEntity(InspectionChecklistRequestDto inspectionChecklistRequestDto) {
        return populateEntity(inspectionChecklistRequestDto, new InspectionChecklist());
    }

    @Override
    protected InspectionChecklist updateEntity(InspectionChecklistRequestDto dto, InspectionChecklist entity) {
        return populateEntity(dto, entity);
    }

    @Override
    protected Specification<InspectionChecklist> buildSpecification(VendorSearchDto searchDto) {
        CustomSpecification<InspectionChecklist> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }

    /**
     * Custom response data method
     *
     * @return response data
     */
    private List<InspectionChecklistResponseDto> getResponseData(List<InspectionChecklist> inspectionChecklist,
                                                                 List<WorkFlowActionProjection> approvedAction) {
        Set<Long> inspectionChecklistIds = inspectionChecklist.stream().map(InspectionChecklist::getId).collect(Collectors.toSet());

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(inspectionChecklistIds, approvedAction, STORE_INSPECTOR_CHECKLIST);
        WorkFlowDto qualityWorkFlowDto = workFlowUtil.prepareResponseData(inspectionChecklistIds, approvedAction, STORE_INSPECTOR_CHECKLIST_QUALITY);

        Map<Long, List<PartRemark>> partRemarkListCheckListOwn = partRemarkService.findByParentIdAndRemarkType(inspectionChecklistIds,
                RemarkType.CHECKLIST_OWN_DEPARTMENT_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId)); //CheckList own department approval  remarks

        Map<Long, List<PartRemark>> partRemarkListCheckListQuality = partRemarkService.findByParentIdAndRemarkType(inspectionChecklistIds,
                RemarkType.CHECKLIST_QUALITY_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId));//CheckList quality approval  remarks

        return inspectionChecklist.stream().map(checklist -> convertToResponseDto(checklist, workFlowDto, qualityWorkFlowDto,
                partRemarkListCheckListOwn.get(checklist.getId()), partRemarkListCheckListQuality.get(checklist.getId()))).collect(Collectors.toList());
    }

    private InspectionChecklistResponseDto convertToResponseDto(InspectionChecklist inspectionChecklist,
                                                                WorkFlowDto workFlowDto,
                                                                WorkFlowDto qualityWorkFlowDto,
                                                                List<PartRemark> partRemarkListCheckListOwn,
                                                                List<PartRemark> partRemarkListCheckListQuality) {
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(inspectionChecklist.getId(), new ArrayList<>());
        List<ApprovalStatus> qualityApprovalStatuses = qualityWorkFlowDto.getStatusMap().getOrDefault(inspectionChecklist.getId(), new ArrayList<>());
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(inspectionChecklist.getWorkFlowActionId());

        Map<Long, ApprovalStatus> workFlowActionMapCheckListOwn = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        Map<Long, ApprovalStatus> workFlowActionMapCheckListQuality = qualityApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        InspectionChecklistResponseDto inspectionChecklistResponseDto = new InspectionChecklistResponseDto();
        inspectionChecklistResponseDto.setId(inspectionChecklist.getId());
        inspectionChecklistResponseDto.setDescription(inspectionChecklist.getDescription());
        inspectionChecklistResponseDto.setWorkflowName(workFlowAction.getName());
        inspectionChecklistResponseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        inspectionChecklistResponseDto.setActionEnabled(workFlowDto.getActionableIds().contains(inspectionChecklist.getWorkFlowActionId()));
        inspectionChecklistResponseDto.setEditable(workFlowDto.getEditableIds().contains(inspectionChecklist.getWorkFlowActionId()));
        inspectionChecklistResponseDto.setWorkFlowActionId(inspectionChecklist.getWorkFlowActionId());

        inspectionChecklistResponseDto.setApprovalStatuses(approvalStatuses.stream().
                map(approvalStatus -> ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId, Function.identity(), (a, b) -> b)));
        inspectionChecklistResponseDto.setQualityApprovalStatuses(qualityApprovalStatuses.stream().
                map(qualityApprovalStatus -> ApprovalStatusViewModel.from(qualityApprovalStatus, qualityWorkFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId, Function.identity(), (a, b) -> b)));

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(partRemarkListCheckListOwn)) {
            inspectionChecklistResponseDto.setApprovalRemarksResponseDtoList(partRemarkListCheckListOwn.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapCheckListOwn, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(partRemarkListCheckListQuality)) {
            inspectionChecklistResponseDto.setApprovalRemarksResponseDtoListQuality(partRemarkListCheckListQuality.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapCheckListQuality, qualityWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        return inspectionChecklistResponseDto;
    }

    private InspectionChecklist populateEntity(InspectionChecklistRequestDto inspectionChecklistRequestDto,
                                               InspectionChecklist inspectionChecklist) {
        inspectionChecklist.setDescription(inspectionChecklistRequestDto.getDescription());
        inspectionChecklist.setUpdateDate(LocalDate.now());
        return inspectionChecklist;
    }

    private void validateUpdatability(InspectionChecklist inspectionChecklist) {
        if (inspectionChecklist.getWorkFlowActionId().equals(workFlowActionService.findFinalAction().getId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ALREADY_APPROVED);
        }
    }

    private ApprovalStatusType workflowType(VendorWorkFlowType workFlowType) {
        return workFlowType == VendorWorkFlowType.QUALITY ? STORE_INSPECTOR_CHECKLIST_QUALITY : STORE_INSPECTOR_CHECKLIST;
    }

    private void updateWorkflowActionToInitialStage(InspectionChecklist inspectionChecklist) {
        inspectionChecklist.setWorkflowType(OWN_DEPARTMENT);
        inspectionChecklist.setSubmoduleItemId(SubModuleItemEnum.QUALITY_PENDING_INSPECTION_CHECKLIST.getSubModuleItemId());
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC,
                SubModuleItemEnum.STORE_INSPECTOR_INSPECTION_CHECKLIST.getSubModuleItemId());


        List<ApprovalStatusType> approvalStatusTypeList = Arrays.asList(STORE_INSPECTOR_CHECKLIST, STORE_INSPECTOR_CHECKLIST_QUALITY);
        helper.deleteAllByParentIdAndApprovalStatusTypes(inspectionChecklist.getId(), approvalStatusTypeList);

        List<RemarkType> remarkTypeList = Arrays.asList(CHECKLIST_QUALITY_APPROVAL_REMARK, CHECKLIST_OWN_DEPARTMENT_APPROVAL_REMARK);
        helper.deleteByParentIdAndRemarkTypeIn(inspectionChecklist.getId(), remarkTypeList);

        approvalStatusService.create(ApprovalStatusDto.of(inspectionChecklist.getId(), STORE_INSPECTOR_CHECKLIST, workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions)));
        inspectionChecklist.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
    }
}
