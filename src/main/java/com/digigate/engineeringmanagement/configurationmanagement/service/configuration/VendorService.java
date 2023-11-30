package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleItemEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CountryProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.QualitySaveValidityDateReqDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.*;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapability;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapabilityLog;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.VendorRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.VENDOR;
import static com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType.OWN_DEPARTMENT;
import static com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType.QUALITY;
import static com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType.SHIPMENT_PROVIDER;
import static com.digigate.engineeringmanagement.storemanagement.constant.RemarkType.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class VendorService extends AbstractSearchService<Vendor, VendorDto, VendorSearchDto> {
    private final CityService cityService;
    private final VendorCapabilityService vendorCapabilityService;
    private final VendorRepository vendorRepository;
    private final WorkFlowUtil workFlowUtil;
    private final CountryService countryService;
    private final ApprovalStatusService approvalStatusService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final WorkFlowActionService workFlowActionService;
    private final VendorCapabilityLogService vendorCapabilityLogService;
    private final GenericAttachmentService genericAttachmentService;
    private final PartRemarkService partRemarkService;
    private final Helper helper;
    private final VendorWiseClientService vendorWiseClientService;

    public VendorService(CityService cityService,
                         VendorCapabilityService vendorCapabilityService,
                         VendorRepository vendorRepository,
                         WorkFlowUtil workFlowUtil,
                         CountryService countryService,
                         ApprovalStatusService approvalStatusService,
                         ApprovalEmployeeService approvalEmployeeService,
                         WorkFlowActionService workFlowActionService,
                         VendorCapabilityLogService vendorCapabilityLogService,
                         GenericAttachmentService genericAttachmentService,
                         PartRemarkService partRemarkService, Helper helper, VendorWiseClientService vendorWiseClientService) {
        super(vendorRepository);
        this.cityService = cityService;
        this.vendorCapabilityService = vendorCapabilityService;
        this.vendorRepository = vendorRepository;
        this.workFlowUtil = workFlowUtil;
        this.countryService = countryService;
        this.approvalStatusService = approvalStatusService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.workFlowActionService = workFlowActionService;
        this.vendorCapabilityLogService = vendorCapabilityLogService;
        this.genericAttachmentService = genericAttachmentService;
        this.partRemarkService = partRemarkService;
        this.helper = helper;
        this.vendorWiseClientService = vendorWiseClientService;
    }

    public List<VendorProjection> findVendorByIds(Set<Long> vendorIdSet) {
        return vendorRepository.findByIdIn(vendorIdSet);
    }

    @Transactional
    public Vendor create(VendorDto vendorDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(workFlowAction.getId()));
        Vendor vendor = populateEntity(vendorDto, new Vendor(), vendorDto.getVendorType());
        vendor.setWorkflowType(OWN_DEPARTMENT);
        vendor.setSubmoduleItemId(subModuleItemId);
        vendor.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        Vendor manufacturerLog = super.saveItem(vendor);
        if (!CollectionUtils.isEmpty(vendorDto.getAttachments())) {
            genericAttachmentService.saveAllAttachments(vendorDto.getAttachments(), FeatureName.VENDOR, manufacturerLog.getId());
        }
        if (vendor.isManufacturerOrSupplier()) {
            vendorCapabilityLogService.saveAll(vendorDto.getVendorCapabilityLogRequestDtoList(),
                    manufacturerLog.getId());
        }
        approvalStatusService.create(ApprovalStatusDto.of(manufacturerLog.getId(), VENDOR, workFlowAction));
        if (CollectionUtils.isNotEmpty(vendorDto.getClientList())) {
            vendorWiseClientService.saveAll(manufacturerLog, vendorDto.getClientList());
        }
        return manufacturerLog;
    }

    @Transactional
    @Override
    public Vendor update(VendorDto vendorDto, Long id) {
        Vendor vendor = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        WorkFlowAction currentAction = vendor.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        Vendor entity = populateEntity(vendorDto, vendor, vendorDto.getVendorType());
        genericAttachmentService.updateByRecordId(FeatureName.VENDOR, entity.getId(), vendorDto.getAttachments());

        if (vendorDto.getVendorType() != SHIPMENT_PROVIDER) {
            vendorCapabilityLogService.saveAll(vendorDto.getVendorCapabilityLogRequestDtoList(), id);
        }

        if (vendor.getWorkFlowAction().equals(workFlowActionService.findFinalAction())) {
            updateWorkflowActionToInitialStage(vendor);
        }
        Vendor savedEntity = saveItem(entity);

        if (CollectionUtils.isNotEmpty(vendorDto.getClientList())) {
            vendorWiseClientService.updateAll(vendorDto.getClientList(), savedEntity);
        }
        return savedEntity;
    }
    /** Run at 12:01 AM every day*/
    public void updateExpiredVendors() {
        List<Vendor> expiredVendor = vendorRepository.findByValidTillBefore(LocalDate.now());
        if (ObjectUtils.isNotEmpty(expiredVendor)) {
            saveItemList(expiredVendor.stream().map(this::prepareExpiredVendor).collect(Collectors.toList()));
        }
    }

    private Vendor prepareExpiredVendor(Vendor vendor) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, findInitialSubModuleItem(vendor.getVendorType()));
        if (Objects.equals(sortedWorkflowActions.get(sortedWorkflowActions.size() - VALUE_ONE).getId(), vendor.getWorkFlowActionId())) {

            prepareVendorWorkflowForInitialStage(vendor, sortedWorkflowActions);
            approvalStatusService.createApprovalStatusForManualUser(ApprovalStatusDto.of(vendor.getId(), VENDOR,
                    workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions)), vendor.getSubmittedById().getId());
        }
        return vendor;
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, VendorWorkFlowType workFlowType) {
        Vendor vendor = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(vendor.getWorkFlowAction().getId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(vendor.getWorkFlowAction().getId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, vendor.getWorkFlowAction());
            saveApprovalRemarks(approvalRequestDto, vendor, workFlowType);

            if (vendor.getWorkflowType().equals(QUALITY) && vendor.isManufacturerOrSupplier() && Objects.isNull(vendor.getValidTill())) {
                throw EngineeringManagementServerException.notFound(ErrorId.VENDOR_VALIDITY_DATE_MISSING);
            }
            if (nextAction.equals(workFlowActionService.findFinalAction())) {
                if (vendor.getWorkflowType().equals(OWN_DEPARTMENT)
                        && vendor.isManufacturerOrSupplier()) {
                    approvalStatusService.create(ApprovalStatusDto.of(vendor.getId(), VENDOR, vendor.getWorkFlowAction()));
                    Long nextSubModuleItemId = findNextSubModuleItemId(vendor);
                    List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, nextSubModuleItemId);

                    WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
                    vendor.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
                    vendor.setWorkflowType(VendorWorkFlowType.QUALITY);
                    vendor.setSubmoduleItemId(nextSubModuleItemId);
                    approvalStatusService.create(ApprovalStatusDto.of(vendor.getId(), VENDOR_QUALITY, workFlowAction));
                } else {
                    approvalStatusService.create(ApprovalStatusDto.of(vendor.getId(), workflowType(workFlowType), vendor.getWorkFlowAction()));
                    vendor.setWorkFlowAction(nextAction);
                }
            } else {
                approvalStatusService.create(ApprovalStatusDto.of(vendor.getId(), workflowType(workFlowType), vendor.getWorkFlowAction()));
                vendor.setWorkFlowAction(nextAction);
            }

            vendor.setIsRejected(false);
        } else {
            vendor.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            if (vendor.isManufacturerOrSupplier()) {
                updateWorkflowActionToInitialStage(vendor);
            }
            vendor.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(vendor);
    }

    private Long findInitialSubModuleItem(VendorType vendorType) {
        return vendorType.equals(VendorType.MANUFACTURER) ? SubModuleItemEnum.CONFIGURATION_MANUFACTURE.getSubModuleItemId()
                : SubModuleItemEnum.MATERIAL_MANAGEMENT_SUPPLIER.getSubModuleItemId();
    }

    private void saveApprovalRemarks(ApprovalRequestDto approvalRequestDto, Vendor vendor, VendorWorkFlowType workFlowType) {
        if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
            throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
        }
        partRemarkService.saveApproveRemark(vendor.getId(), vendor.getWorkFlowAction().getId(), getRemarkType(workFlowType), approvalRequestDto.getApprovalDesc());// save approval remarks
    }

    public RemarkType getRemarkType(VendorWorkFlowType workflowType) {
        RemarkType remarkType = null;

        switch (workflowType) {
            case OWN_DEPARTMENT:
                remarkType = VENDOR_OWN_DEPARTMENT_APPROVAL_REMARK;
                break;
            case QUALITY:
                remarkType = RemarkType.VENDOR_QUALITY_APPROVAL_REMARK;
                break;
        }

        return remarkType;
    }

    public void updateActiveStatus(Long id, Boolean isActive, VendorWorkFlowType workFlowType) {
        Vendor vendor = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(vendor.getWorkFlowActionId());

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, vendor.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(vendor.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(vendor.getId(), workFlowAction, getRemarkType(workFlowType));
            vendor.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(vendor.getWorkFlowAction(),
                    workflowType(workFlowType), vendor.getId()));
            vendor.setIsRejected(FALSE);
        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(vendor.getWorkFlowAction().getId()));
        }
        vendor.setIsActive(isActive);
        saveItem(vendor);
    }

    @Override
    public VendorViewModel getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    @Override
    public PageData search(VendorSearchDto dto, Pageable pageable) {
        CustomSpecification<Vendor> customSpecification = new CustomSpecification<>();
        Specification<Vendor> vendorSpecification = Specification.where(customSpecification
                        .active(Objects.nonNull(dto.getIsActive()) ? dto.getIsActive() : true, IS_ACTIVE_FIELD))
                .and(buildSpecification(dto));

        Page<Vendor> pagedData;
        List<WorkFlowActionProjection> approvedActionsForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionsForUser);
                vendorSpecification = vendorSpecification.and(customSpecification.inSpecificationAtRoot(
                                pendingSearchWorkFlowIds, WORKFLOW_ACTION_ID).and(customSpecification.equalSpecificationAtRoot(dto.getWorkflowType(), WORKFLOW_TYPE))
                        .and(customSpecification.notEqualSpecificationAtRoot(TRUE, IS_REJECTED))
                        .and(customSpecification.equalSpecificationAtRoot(dto.getVendorType(), VENDOR_TYPE)));
                break;
            case APPROVED:
                Long approvedId = workFlowActionService
                        .findFinalAction().getId();
                vendorSpecification = vendorSpecification.and(
                        customSpecification.equalSpecificationAtRoot(approvedId, ApplicationConstant.WORKFLOW_ACTION_ID)
                                .and(customSpecification.equalSpecificationAtRoot(dto.getVendorType(), VENDOR_TYPE)));
                break;
            case REJECTED:
                vendorSpecification = Specification.where(
                                customSpecification.equalSpecificationAtRoot(true, ApplicationConstant.IS_REJECTED))
                        .and(customSpecification.equalSpecificationAtRoot(dto.getVendorType(), VENDOR_TYPE))
                        .and(buildSpecification(dto)).and(customSpecification.equalSpecificationAtRoot(dto.getWorkflowType(), WORKFLOW_TYPE));
                break;
            case ALL:
                vendorSpecification = vendorSpecification.and(customSpecification.equalSpecificationAtRoot(dto.getVendorType(), VENDOR_TYPE));
                break;
        }

        pagedData = vendorRepository.findAll(vendorSpecification, pageable);
        return PageData.builder()
                .model(getResponseData(pagedData.getContent(), approvedActionsForUser))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected Specification<Vendor> buildSpecification(VendorSearchDto searchDto) {
        CustomSpecification<Vendor> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.VENDOR_NAME));
    }

    @Override
    protected Vendor convertToEntity(VendorDto vendorDto) {
        return null;
    }

    @Override
    protected Vendor updateEntity(VendorDto dto, Vendor entity) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(Vendor vendor) {
        return null;
    }

    private Vendor populateEntity(VendorDto dto, Vendor vendor, VendorType vendorType) {
        vendor.setVendorType(vendorType);
        vendor.setName(dto.getName());
        vendor.setAddress(dto.getAddress());
        vendor.setOfficePhone(dto.getOfficePhone());
        vendor.setEmergencyContact(dto.getEmergencyContact());
        vendor.setEmail(dto.getEmail());
        vendor.setWebsite(dto.getWebsite());
        vendor.setSkype(dto.getSkype());
        vendor.setItemsBuild(dto.getItemsBuild());
        vendor.setLoadingPort(dto.getLoadingPort());
        vendor.setValidTill(dto.getValidTill());
        vendor.setUpdateDate(LocalDate.now());
        vendor.setContactSkype(dto.getContactSkype());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setSubmittedById(User.withId(Helper.getAuthUserId()));
        if (!dto.getCityId().equals(vendor.getCityId())) {
            vendor.setCity(cityService.findById(dto.getCityId()));
        }
        if (Objects.nonNull(dto.getCountryOriginId())) {
            vendor.setCountryOrigin(countryService.findById(dto.getCountryOriginId()));
        }
        vendor.setWebsite(dto.getWebsite());
        return vendor;
    }

    private List<VendorViewModel> getResponseData(List<Vendor> vendors,
                                                  List<WorkFlowActionProjection> approvedActions) {
        Set<Long> cityIds = vendors.stream()
                .map(Vendor::getCityId).collect(Collectors.toSet());

        Map<Long, CityProjection> cityProjectionMap = cityService.findByIdIn(cityIds)
                .stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));

        Set<Long> suppliersCountryIds = vendors.stream()
                .map(Vendor::getCountryOriginId).collect(Collectors.toSet());

        Set<Long> cityProjectionsCountryIds = cityProjectionMap.values().stream()
                .map(CityProjection::getCountryId).collect(Collectors.toSet());

        suppliersCountryIds.addAll(cityProjectionsCountryIds);
        Map<Long, CountryProjection> countryProjectionMap = countryService.findByIdIn(suppliersCountryIds)
                .stream()
                .collect(Collectors.toMap(CountryProjection::getId, Function.identity()));

        Set<Long> vendorIds = vendors.stream().map(Vendor::getId).collect(Collectors.toSet());

        Map<Long, List<VendorWiseClientListResponseDto>> vendorWiseClientResponseMap =
                vendorWiseClientService.getAllResponse(vendorIds).stream().collect(Collectors.groupingBy(VendorWiseClientListResponseDto::getVendorId));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(vendorIds, approvedActions, VENDOR);
        WorkFlowDto qualityWorkFlowDto = workFlowUtil.prepareResponseData(vendorIds, approvedActions, VENDOR_QUALITY);

        Map<Long, List<PartRemark>> partRemarkListVendorOwn = partRemarkService.findByParentIdAndRemarkType(vendorIds,
                VENDOR_OWN_DEPARTMENT_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId)); //vendor own department approval  remarks

        Map<Long, List<PartRemark>> partRemarkListVendorQuality = partRemarkService.findByParentIdAndRemarkType(vendorIds,
                RemarkType.VENDOR_QUALITY_APPROVAL_REMARK).stream().collect(Collectors.groupingBy(PartRemark::getParentId));//vendor quality approval  remarks

        List<VendorCapabilityLog> vendorCapabilityLogList = vendorCapabilityLogService.findAllByVendorIdIn(vendorIds);
        Set<Long> capabilityIds = vendorCapabilityLogList.stream().map(VendorCapabilityLog::getVendorCapabilityId).collect(Collectors.toSet());
        Map<Long, String> nameMap = vendorCapabilityService.getAllByDomainIdInUnfiltered(capabilityIds).stream()
                .collect(Collectors.toMap(VendorCapability::getId, VendorCapability::getName));
        Map<Long, List<VendorCapabilityLog>> logMap = vendorCapabilityLogList.stream().collect(Collectors.groupingBy(VendorCapabilityLog::getVendorId));

        return vendors
                .stream().map(vendor ->
                        convertToResponseDto(vendor,
                                cityProjectionMap,
                                countryProjectionMap,
                                workFlowDto, qualityWorkFlowDto, logMap.getOrDefault(vendor.getId(), Collections.emptyList()), nameMap,
                                partRemarkListVendorOwn.get(vendor.getId()), partRemarkListVendorQuality.get(vendor.getId()),
                                vendorWiseClientResponseMap.getOrDefault(vendor.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    private VendorViewModel convertToResponseDto(Vendor vendor,
                                                 Map<Long, CityProjection> cityProjectionMap,
                                                 Map<Long, CountryProjection> countryProjectionMap,
                                                 WorkFlowDto workFlowDto, WorkFlowDto qualityWorkFlowDto,
                                                 List<VendorCapabilityLog> logs, Map<Long, String> nameMap,
                                                 List<PartRemark> partRemarkListVendorOwn,
                                                 List<PartRemark> partRemarkListVendorQuality,
                                                 List<VendorWiseClientListResponseDto> vendorWiseClientListResponseDtoList) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(vendor.getId(), new ArrayList<>());
        List<ApprovalStatus> qualityApprovalStatuses = qualityWorkFlowDto.getStatusMap().getOrDefault(vendor.getId(), new ArrayList<>());

        Map<Long, ApprovalStatus> workFlowActionMapVendorOwn = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        Map<Long, ApprovalStatus> workFlowActionMapVendorQuality = qualityApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(vendor.getWorkFlowActionId());

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.VENDOR, vendor.getId())
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));
        CityProjection city = cityProjectionMap.get(vendor.getCityId());
        CountryProjection countryOriginProjection = countryProjectionMap.get(vendor.getCountryOriginId());

        VendorViewModel viewModel = VendorViewModel.builder()
                .id(vendor.getId())
                .address(vendor.getAddress())
                .name(vendor.getName())
                .officePhone(vendor.getOfficePhone())
                .email(vendor.getEmail())
                .skype(vendor.getSkype())
                .website(vendor.getWebsite())
                .attachments(attachmentLinksMap.get(vendor.getId()))
                .contactSkype(vendor.getContactSkype())
                .contactPerson(vendor.getContactPerson())
                .skype(vendor.getSkype())
                .loadingPort(vendor.getLoadingPort())
                .itemsBuild(vendor.getItemsBuild())
                .emergencyContact(vendor.getEmergencyContact())
                .status(vendor.getStatus())
                .validTill(vendor.getValidTill())
                .rejectedDesc(vendor.getRejectedDesc())
                .isRejected(vendor.getIsRejected())
                .workFlowActionId(vendor.getWorkFlowActionId())
                .workflowName(workFlowAction.getName())
                .workflowOrder(workFlowAction.getOrderNumber())
                .actionEnabled(workFlowDto.getActionableIds().contains(vendor.getWorkFlowActionId()))
                .editable(workFlowDto.getEditableIds().contains(vendor.getWorkFlowActionId()))
                .approvalStatuses(approvalStatuses.stream().map(approvalStatus ->
                                ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                        .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                                Function.identity(), (a, b) -> b)))
                .qualityApprovalStatuses(qualityApprovalStatuses.stream().map(approvalStatus ->
                                ApprovalStatusViewModel.from(approvalStatus, qualityWorkFlowDto.getNamesFromApprovalStatuses()))
                        .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                                Function.identity(), (a, b) -> b)))
                .build();
        if (Objects.nonNull(city)) {
            CountryProjection country = countryProjectionMap.get(city.getCountryId());
            viewModel.setCity(CityResponseDto.builder().id(city.getId()).name(city.getName())
                    .countryId(city.getCountryId())
                    .countryName(Objects.nonNull(country) ? country.getName() : null)
                    .dialingCode(country.getDialingCode())
                    .zipCode(city.getZipCode())
                    .build());
        }
        if (CollectionUtils.isNotEmpty(vendorWiseClientListResponseDtoList)) {
            viewModel.setClientList(vendorWiseClientListResponseDtoList);
        }
        List<VendorCapabilityResponseDto> vendorCapabilityResponseDtoList = logs.stream().filter(VendorCapabilityLog::isStatus)
                .map(log -> viewVendorCapabilityData(log, nameMap.get(log.getVendorCapabilityId()))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(vendorCapabilityResponseDtoList)) {
            viewModel.setVendorCapabilityResponseDtoList(vendorCapabilityResponseDtoList);
        }

        if (Objects.nonNull(countryOriginProjection)) {
            viewModel.setCountryOrigin(IdNameResponse.of(countryOriginProjection.getId(), countryOriginProjection.getName()));
        }

        if (CollectionUtils.isNotEmpty(partRemarkListVendorOwn)) {
            viewModel.setApprovalRemarksResponseDtoList(partRemarkListVendorOwn.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapVendorOwn, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListVendorQuality)) {
            viewModel.setApprovalRemarksResponseDtoListQuality(partRemarkListVendorQuality.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapVendorQuality, qualityWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        return viewModel;
    }

    private VendorCapabilityResponseDto viewVendorCapabilityData(VendorCapabilityLog vendorCapabilityLog, String name) {
        VendorCapabilityResponseDto vendorCapabilityResponseDto = new VendorCapabilityResponseDto();
        vendorCapabilityResponseDto.setId(vendorCapabilityLog.getId());
        vendorCapabilityResponseDto.setVendorCapabilityId(vendorCapabilityLog.getVendorCapabilityId());
        vendorCapabilityResponseDto.setName(name);
        return vendorCapabilityResponseDto;
    }

    private ApprovalStatusType workflowType(VendorWorkFlowType workFlowType) {
        return workFlowType == VendorWorkFlowType.QUALITY ? VENDOR_QUALITY : VENDOR;
    }

    private Long findNextSubModuleItemId(Vendor vendor) {
        Long nextSubModuleId = null;
        switch (vendor.getVendorType()) {
            case MANUFACTURER:
                nextSubModuleId = SubModuleItemEnum.QUALITY_MANUFACTURER_PENDING_LIST.getSubModuleItemId();
                break;
            case SUPPLIER:
                nextSubModuleId = SubModuleItemEnum.QUALITY_SUPPLIER_PENDING_LIST.getSubModuleItemId();
                break;
            default:
                break;
        }
        return nextSubModuleId;
    }

    private void updateWorkflowActionToInitialStage(Vendor vendor) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, findInitialSubModuleItem(vendor.getVendorType()));
        prepareVendorWorkflowForInitialStage(vendor, sortedWorkflowActions);
        approvalStatusService.create(ApprovalStatusDto.of(vendor.getId(), VENDOR, workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions)));
    }

    private void prepareVendorWorkflowForInitialStage(Vendor vendor, List<WorkFlowAction> sortedWorkflowActions){
        vendor.setWorkflowType(OWN_DEPARTMENT);
        vendor.setSubmoduleItemId(findNextSubModuleItemId(vendor));
        List<ApprovalStatusType> approvalStatusTypeList = Arrays.asList(VENDOR_QUALITY, VENDOR);
        helper.deleteAllByParentIdAndApprovalStatusTypes(vendor.getId(), approvalStatusTypeList);

        List<RemarkType> remarkTypeList = Arrays.asList(VENDOR_QUALITY_APPROVAL_REMARK, VENDOR_OWN_DEPARTMENT_APPROVAL_REMARK);
        helper.deleteByParentIdAndRemarkTypeIn(vendor.getId(), remarkTypeList);
        vendor.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
    }

    public Vendor findByIdAndVendorType(Long id, VendorType vendorType) {
        return vendorRepository.findByIdAndVendorType(id, vendorType);
    }

    public List<VendorProjection> findByIdIn(Set<Long> collect) {
        return vendorRepository.findByIdIn(collect);
    }

    public Vendor saveValidityDate(Long id, QualitySaveValidityDateReqDto qualitySaveValidityDateReqDto) {
        Vendor vendor = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        WorkFlowAction currentAction = vendor.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));
        vendor.setValidTill(qualitySaveValidityDateReqDto.getValidTill());
        return saveItem(vendor);
    }
}
