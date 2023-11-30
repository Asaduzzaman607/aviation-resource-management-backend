package com.digigate.engineeringmanagement.logistic.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.logistic.entity.PoTracker;
import com.digigate.engineeringmanagement.logistic.entity.PoTrackerLocation;
import com.digigate.engineeringmanagement.logistic.payload.request.PoTrackerRequestDto;
import com.digigate.engineeringmanagement.logistic.payload.response.PoTrackerLocationResponseDto;
import com.digigate.engineeringmanagement.logistic.payload.response.PoTrackerResponseDto;
import com.digigate.engineeringmanagement.logistic.repository.PoTrackerRepository;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartOrderItemService;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartOrderService;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PoTrackerService extends AbstractSearchService<PoTracker, PoTrackerRequestDto, IdQuerySearchDto> {
    private final PoTrackerLocationService poTrackerLocationService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final PartOrderItemService partOrderItemService;
    private final PartOrderService partOrderService;
    private final PoTrackerRepository poTrackerRepository;
    private final GenericAttachmentService genericAttachmentService;

    public PoTrackerService(AbstractRepository<PoTracker> repository, PoTrackerLocationService poTrackerLocationService,
                            StoreVoucherTrackingService storeVoucherTrackingService, PartOrderItemService partOrderItemService,
                            PartOrderService partOrderService, PoTrackerRepository poTrackerRepository, GenericAttachmentService genericAttachmentService) {
        super(repository);
        this.poTrackerLocationService = poTrackerLocationService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.partOrderItemService = partOrderItemService;
        this.partOrderService = partOrderService;
        this.poTrackerRepository = poTrackerRepository;
        this.genericAttachmentService = genericAttachmentService;
    }

    @Override
    public PoTracker create(PoTrackerRequestDto poTrackerRequestDto) {
        PoTracker poTracker = convertToEntity(poTrackerRequestDto);
        poTracker = saveItem(poTracker);
        poTrackerLocationService.saveAll(poTrackerRequestDto.getPoTrackerLocationList(), poTracker);
        if (CollectionUtils.isNotEmpty(poTrackerRequestDto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(poTrackerRequestDto.getAttachment(), FeatureName.PO_TRACKER, poTracker.getId());
        }
        return poTracker;
    }

    @Override
    public PoTracker update(PoTrackerRequestDto poTrackerRequestDto, Long id) {
        PoTracker poTracker = findByIdUnfiltered(id);
        poTracker = updateEntity(poTrackerRequestDto, poTracker);
        poTrackerLocationService.updateAll(poTrackerRequestDto.getPoTrackerLocationList(), poTracker);
        if(CollectionUtils.isNotEmpty(poTrackerRequestDto.getAttachment())){
            genericAttachmentService.updateByRecordId(FeatureName.PO_TRACKER, poTracker.getId(), poTrackerRequestDto.getAttachment());
        }
        return super.saveItem(poTracker);
    }

    @Override
    public PoTrackerResponseDto getSingle(Long id) {
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id))).stream().findFirst()
                .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }


    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Specification<PoTracker> poTrackerSpecification = buildSpecification(searchDto);
        Page<PoTracker> pageData = poTrackerRepository.findAll(poTrackerSpecification, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private List<PoTrackerResponseDto> getResponseData(List<PoTracker> trackerList) {
        Set<Long> trackerIds = trackerList.stream().map(PoTracker::getId).collect(Collectors.toSet());

        List<PoTrackerLocation> details = poTrackerLocationService.findByPoTrackerIdIn(trackerIds);

        Map<Long, List<PoTrackerLocationResponseDto>> locationMap = poTrackerLocationService.convertToResponse(details)
                .stream().collect(Collectors.groupingBy(PoTrackerLocationResponseDto::getTrackerId));

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.PO_TRACKER, trackerIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        return trackerList.stream().map(tracker -> convertToTrackerResponseDto(tracker, locationMap.get(tracker.getId()),
                attachmentLinksMap.getOrDefault(tracker.getId(), new HashSet<>()))).collect(Collectors.toList());


    }

    private PoTrackerResponseDto convertToTrackerResponseDto(PoTracker tracker, List<PoTrackerLocationResponseDto> poTrackerLocationResponseDto, Set<String> attachments) {
        PoTrackerResponseDto poTrackerResponseDto = new PoTrackerResponseDto();
        PartOrder partOrder = partOrderService.findById(tracker.getPartOrderItem().getPartOrderId());
        poTrackerResponseDto.setId(tracker.getId());
        poTrackerResponseDto.setPartOrderId(partOrder.getId());
        poTrackerResponseDto.setPartOrderNo(partOrder.getVoucherNo());
        poTrackerResponseDto.setPartOrderItemId(tracker.getPartOrderItemId());
        poTrackerResponseDto.setTrackerNo(tracker.getTrackerNo());
        poTrackerResponseDto.setTrackerStatus(tracker.getTrackerStatus());
        poTrackerResponseDto.setAttachment(attachments);

        if (!CollectionUtils.isEmpty(poTrackerLocationResponseDto)) {
            poTrackerResponseDto.setPoTrackerLocationList(poTrackerLocationResponseDto);
        }
        return poTrackerResponseDto;

    }

    @Override
    protected Specification<PoTracker> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<PoTracker> customSpecification = new CustomSpecification<>();

        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.TRACKER_NO)
                .and(customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD)));
    }

    @Override
    protected <T> T convertToResponseDto(PoTracker poTracker) {
        return null;
    }

    @Override
    protected PoTracker convertToEntity(PoTrackerRequestDto poTrackerRequestDto) {
        PoTracker poTracker = new PoTracker();
        if (StringUtils.isEmpty(poTrackerRequestDto.getTrackerNo())) {
            PartOrderItem partOrderItem = partOrderItemService.findById(poTrackerRequestDto.getPartOrderItemId());
            poTracker.setTrackerNo(storeVoucherTrackingService.generateUniqueVoucherNo(poTrackerRequestDto.getPartOrderItemId(),
                    VoucherType.TRACKER, partOrderItem.getPartOrder().getVoucherNo()));
        }
        poTracker.setPartOrderItem(partOrderItemService.findById(poTrackerRequestDto.getPartOrderItemId()));
        poTracker.setTrackerStatus(poTrackerRequestDto.getTrackerStatus());
        return poTracker;
    }

    @Override
    protected PoTracker updateEntity(PoTrackerRequestDto dto, PoTracker entity) {
        entity.setPartOrderItem(partOrderItemService.findById(dto.getPartOrderItemId()));
        entity.setTrackerStatus(dto.getTrackerStatus());
        return entity;
    }
}
