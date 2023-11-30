package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.WorkFLowActionRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.WorkFlowActionSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.WorkFLowActionResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.WorkFlowActionRepository;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class WorkFlowActionService extends AbstractSearchService<WorkFlowAction, WorkFLowActionRequestDto, WorkFlowActionSearchDto> {
    private final WorkFlowActionRepository workFlowActionRepository;
    private final ApprovalSettingService approvalSettingService;
    private final Helper helper;

    public WorkFlowActionService(WorkFlowActionRepository workFlowActionRepository,
                                 @Lazy ApprovalSettingService approvalSettingService,
                                 Helper helper) {
        super(workFlowActionRepository);
        this.workFlowActionRepository = workFlowActionRepository;
        this.approvalSettingService = approvalSettingService;
        this.helper = helper;
    }

    @Override
    @SneakyThrows
    public PageData search(WorkFlowActionSearchDto searchDto, Pageable pageable) {

        Specification<WorkFlowAction> specification = buildSpecification(searchDto);
        Page<WorkFlowAction> pagedData = workFlowActionRepository.findAll(specification, pageable);
        Optional<WorkFlowAction> finalAction = findNonActionableItem();

        List<Object> models = pagedData.getContent()
            .stream()
            .map(entity -> {
                entity.setFinalItem(finalAction.isPresent() && entity.equals(finalAction.get()));
                return convertToResponseDto(entity);
            }).collect(Collectors.toList());

        return PageData.builder()
            .model(models)
            .totalPages(pagedData.getTotalPages())
            .totalElements(pagedData.getTotalElements())
            .currentPage(pageable.getPageNumber() + 1)
            .build();
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        WorkFlowAction entity = findByIdUnfiltered(id);
        if (entity.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        if (isActive == Boolean.FALSE) {
            validateIfEditable(entity);
            approvalSettingService.makeAllInactiveByWfa(id);
        }
        entity.setIsActive(isActive);
        saveItem(entity);
    }

    private Optional<WorkFlowAction> findNonActionableItem() {
        return workFlowActionRepository.findTop1ByShowFalseOrderByOrderNumberDesc();
    }

    @Override
    public WorkFlowAction create(WorkFLowActionRequestDto requestDto) {
        validate(requestDto, null);
        if (Objects.isNull(requestDto.getLabel())){
            requestDto.setLabel(requestDto.getName());
        }
        return super.create(requestDto);
    }

    public WorkFlowAction findByOrderAsc(Integer index) {
        return findByOrder(index, Sort.Direction.ASC);
    }

    public WorkFlowAction findByOrder(Integer index, Sort.Direction direction) {
        List<WorkFlowAction> workFlowActions = getSortedWorkflowActions(direction);
        return getByIndex(index, workFlowActions);
    }

    public List<WorkFlowAction> getSortedWorkflowActions(Sort.Direction direction) {
        Long subModuleItemId = helper.getSubModuleItemId();
        return getSortedWorkflowActions(direction, subModuleItemId);
    }

    public List<WorkFlowAction> getSortedWorkflowActions(Sort.Direction direction, Long subModuleItemId) {
        List<WorkFlowAction> workFlowActions = findWorkFlowActionsBySubmoduleItem(subModuleItemId);
        Comparator<WorkFlowAction> comparator = direction.isAscending() ? getOrderComparator() :
                getOrderComparator().reversed();

        workFlowActions.sort(comparator);
        return workFlowActions;
    }

    public WorkFlowAction findInitialAction(Long subModuleItemId) {
        List<WorkFlowAction> sortedWorkflowActions = getSortedWorkflowActions(Sort.Direction.ASC, subModuleItemId);
        return getByIndex(0, sortedWorkflowActions);
    }

    public WorkFlowAction getEditableActionsByIndex(int index, List<WorkFlowAction> workFlowActions) {
        int lastIndex = workFlowActions.size() - 1;
        if (index == lastIndex) {
            return getByIndex(lastIndex - 1, workFlowActions);
        }
        return getByIndex(index, workFlowActions);
    }

    public WorkFlowAction getByIndex(Integer index, List<WorkFlowAction> workFlowActions) {
        if (index < 0) {
            return findInitialAction();
        }
        if (index >= workFlowActions.size()) {
            return findFinalAction();
        }
        return workFlowActions.get(index);
    }

    public WorkFlowAction findInitialAction() {
        return findByOrderAsc(ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER);
    }

    public WorkFlowAction findFinalAction() {
        return findByOrder(ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER, Sort.Direction.DESC);
    }

    public WorkFlowAction getNavigatedAction(boolean isNext, WorkFlowAction workFlowAction) {
        List<WorkFlowAction> sortedWorkflowActions = getSortedWorkflowActions(Sort.Direction.ASC);
        return getNavigatedAction(isNext, workFlowAction, sortedWorkflowActions);
    }

    public WorkFlowAction getNavigatedAction(boolean isNext, WorkFlowAction workFlowAction, Long smiId) {
        List<WorkFlowAction> sortedWorkflowActions = getSortedWorkflowActions(Sort.Direction.ASC, smiId);
        return getNavigatedAction(isNext, workFlowAction, sortedWorkflowActions);
    }

    public Set<Long> findByPendingWorkFlowIds(Integer order) {
        List<Long> ids = getSortedWorkflowActions(Sort.Direction.ASC).stream().filter(workFlowAction ->
                        workFlowAction.getOrderNumber() >= order)
                .map(WorkFlowAction::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ids)) {
            ids.remove(ids.size() - INT_ONE);
        }
        return new HashSet<>(ids);
    }

    @Override
    public WorkFlowAction update(WorkFLowActionRequestDto requestDto, Long id) {
        WorkFlowAction workFlowAction = findByIdUnfiltered(id);
        validate(requestDto, workFlowAction);
        final WorkFlowAction updatedWorkFlowAction = updateEntity(requestDto, workFlowAction);
        return super.saveItem(updatedWorkFlowAction);
    }

    public Comparator<WorkFlowAction> getOrderComparator() {
        return Comparator.comparing(WorkFlowAction::getOrderNumber);
    }

    @Override
    protected Specification<WorkFlowAction> buildSpecification(WorkFlowActionSearchDto searchDto) {
        CustomSpecification<WorkFlowAction> customSpecification = new CustomSpecification<>();

        Specification<WorkFlowAction> specification = Specification.where(customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ENTITY_NAME)));

        if (Objects.nonNull(searchDto.getSubModuleItemId())) {
            Set<Long> flowActionIdBySubmoduleId = getFlowActionIdBySubmoduleId(searchDto.getSubModuleItemId());
            if (CollectionUtils.isEmpty(flowActionIdBySubmoduleId)) {
                specification = specification.and(customSpecification.equalSpecificationAtRoot(false, SHOW_FIELD));
            } else {
                specification = specification.and(customSpecification.inSpecificationAtRoot(flowActionIdBySubmoduleId, ID));
            }
        }
        return specification;
    }

    @Override
    protected WorkFLowActionResponseDto convertToResponseDto(WorkFlowAction entity) {
        return WorkFLowActionResponseDto.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .orderNumber(entity.getOrderNumber())
                .name(entity.getName())
                .actionable(!entity.isFinalItem())
                .editable(isEditableWorkflow(entity))
                .build();
    }

    @Override
    protected WorkFlowAction convertToEntity(WorkFLowActionRequestDto requestDto) {
        return WorkFlowAction.builder()
                .name(requestDto.getName())
                .orderNumber(requestDto.getOrderNumber())
                .label(requestDto.getLabel())
                .show(true)
                .build();
    }

    @Override
    protected WorkFlowAction updateEntity(WorkFLowActionRequestDto dto, WorkFlowAction entity) {
        entity.setName(dto.getName());
        entity.setOrderNumber(dto.getOrderNumber());
        if (Objects.nonNull(dto.getLabel())){
            entity.setLabel(dto.getLabel());
        }
        return entity;
    }

    private WorkFlowAction getNavigatedAction(boolean isNext, WorkFlowAction workFlowAction, List<WorkFlowAction> sortedWorkflowActions) {
        int currentIndex = sortedWorkflowActions.indexOf(workFlowAction);
        int adder = isNext ? INT_ONE : ApplicationConstant.INT_NEGATIVE_ONE;
        return getByIndex(currentIndex + adder, sortedWorkflowActions);
    }

    private List<WorkFlowAction> findWorkFlowActionsBySubmoduleItem(Long subModuleItemId) {
        Set<Long> workFlowActionIds = getFlowActionIdBySubmoduleId(subModuleItemId);
        return workFlowActionRepository.findByIsActiveTrueAndIdInOrShowFalse(workFlowActionIds);
    }

    private Set<Long> getFlowActionIdBySubmoduleId(Long subModuleItemId) {
        return Objects.isNull(subModuleItemId) ? Collections.emptySet() : approvalSettingService.findBySubmoduleId(subModuleItemId).stream()
            .map(ApprovalSetting::getWorkFlowActionId).collect(Collectors.toSet());
    }

    private void validate(WorkFLowActionRequestDto dto, WorkFlowAction old) {
        boolean isNew = Objects.isNull(old);

        if (isNew) {
            checkUniqueConstraints(dto);
        } else {
            validateIfEditable(old);
            checkUniqueConstraints(dto, old);
        }
    }

    private void checkUniqueConstraints(WorkFLowActionRequestDto dto) {
        if (workFlowActionRepository.existsByNameAndIsActiveTrue(dto.getName()) ||
            workFlowActionRepository.existsByOrderNumberAndIsActiveTrue(dto.getOrderNumber())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.WORK_FLOW_ACTION_ALREADY_EXIST);
        }
        if (dto.getOrderNumber() >= Short.MAX_VALUE || dto.getOrderNumber() <= Short.MIN_VALUE) {
                throw EngineeringManagementServerException.badRequest(ErrorId.WORK_FLOW_ACTION_ORDER_OUT_OF_RANGE);
        }
    }

    private void checkUniqueConstraints(WorkFLowActionRequestDto dto, WorkFlowAction old) {
        if (workFlowActionRepository.existsByNameAndIsActiveTrueAndIdNot(dto.getName(), old.getId()) ||
                workFlowActionRepository.existsByOrderNumberAndIsActiveTrueAndIdNot(dto.getOrderNumber(), old.getId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.WORK_FLOW_ACTION_ALREADY_EXIST);
        }
        if (dto.getOrderNumber() >= Short.MAX_VALUE || dto.getOrderNumber() <= Short.MIN_VALUE) {
            throw EngineeringManagementServerException.badRequest(ErrorId.WORK_FLOW_ACTION_ORDER_OUT_OF_RANGE);
        }
    }

    public List<WorkFlowAction> getAllWorkFLowActionByIdIn(Set<Long> ids){
        return workFlowActionRepository.findAllByIdIn(ids);
    }

    private void validateIfEditable(WorkFlowAction entity) {
        if (!isEditableWorkflow(entity)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.NOT_EDITABLE);
        }
    }

    private boolean isEditableWorkflow(WorkFlowAction entity) {
        return entity.isShow() && entity.getOrderNumber() >= 0;
    }

}
