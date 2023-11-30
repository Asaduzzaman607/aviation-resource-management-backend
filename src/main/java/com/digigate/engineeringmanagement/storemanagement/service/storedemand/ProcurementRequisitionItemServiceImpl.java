package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.planning.payload.response.PartWiseUomResponseDto;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.ItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.converter.RequisitionItemConverter;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartWiseUomProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionItemDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ProcurementRequisitionItemViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.ProcurementRequisitionItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcurementRequisitionItemServiceImpl implements ProcurementRequisitionItemService {
    private static final String PROCUREMENT_REQUISITION_ITEM = "ProcurementRequisitionItem";
    private final ProcurementRequisitionItemRepository repository;
    private final StoreDemandDetailsService demandDetailsService;
    private final PartWiseUomService partWiseUomService;

    private final ProcurementRequisitionService procurementRequisitionService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    public ProcurementRequisitionItemServiceImpl(ProcurementRequisitionItemRepository repository,
                                                 StoreDemandDetailsService demandDetailsService,
                                                 PartWiseUomService partWiseUomService, @Lazy ProcurementRequisitionService procurementRequisitionService) {
        this.repository = repository;
        this.demandDetailsService = demandDetailsService;
        this.partWiseUomService = partWiseUomService;
        this.procurementRequisitionService = procurementRequisitionService;
    }

    @Override
    public ProcurementRequisitionItem create(ProcurementRequisitionItemDto dto, StoreDemandItem demandItem, ProcurementRequisition requisition) {
        ProcurementRequisitionItem requisitionItem = RequisitionItemConverter.convertToEntity(dto, demandItem, requisition);
        return this.saveItem(requisitionItem);
    }

    @Override
    public ProcurementRequisitionItem update(ProcurementRequisitionItemDto dto) {
        ProcurementRequisitionItem requisitionItem = Objects.nonNull(dto.getId()) ? findById(dto.getId()) :
                new ProcurementRequisitionItem();

        return saveItem(RequisitionItemConverter.updateEntity(requisitionItem, dto));
    }

    @Override
    public ProcurementRequisitionItem findById(Long id) {
        if (Objects.isNull(id)) {
            throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.ID_IS_REQUIRED_DYNAMIC,
                PROCUREMENT_REQUISITION_ITEM));
        }
        return repository.findById(id).orElseThrow(() ->
            EngineeringManagementServerException.notFound(Helper.createDynamicCode(ErrorId.DATA_NOT_FOUND_DYNAMIC,
                PROCUREMENT_REQUISITION_ITEM)));
    }

    @Override
    public List<RequisitionItemProjection> findRequisitionItemList(Set<Long> requisitionItemIds) {
        return repository.findProcurementRequisitionItemByIdIn(requisitionItemIds);
    }

    @Override
    public List<ProcurementRequisitionItem> findByRequisitionId(Long requisitionId) {
        return repository.findByRequisitionId(requisitionId);
    }

    @Override
    public List<ProcurementRequisitionItem> getAllByDomainIdIn(Set<Long> itemIdSet) {
        return repository.findAllByIdInAndIsActiveTrue(itemIdSet);
    }

    @Override
    public List<ItemProjection> findByProcurementRequisitionId(Long requisitionId) {
        return repository.findProcurementRequisitionItemByRequisitionId(requisitionId);
    }

    @Override
    public List<ItemProjection> findAllByIdIn(Set<Long> itemIdSet) {
        return repository.findByIdIn(itemIdSet);
    }

    @Override
    public List<ProcurementRequisitionItemViewModel> getAllResponseByViewModel(Set<Long> requisitionIds) {
        List<ProcurementRequisitionItem> requisitionItems = repository.findByRequisitionIdIn(requisitionIds);

        Set<StoreDemandItem> storeDemandItems = requisitionItems.stream().map(ProcurementRequisitionItem::getDemandItem).collect(Collectors.toSet());

        Map<Long, StoreDemandDetailsDto> demandDetailsDtoMap = demandDetailsService.getResponse(storeDemandItems,
                RemarkType.PROCUREMENT_REQUISITION, requisitionIds).stream().collect(Collectors.toMap(StoreDemandDetailsDto::getId, Function.identity()));

        return requisitionItems.stream().map(requisition -> populateToViewModel(requisition, demandDetailsDtoMap.get(requisition.getDemandItemId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemProjection> findRequisitionItemByRequisitionId(Long requisitionId) {
        return repository.findProcurementRequisitionItemByRequisitionId(requisitionId);
    }

    @Override
    public RfqPartViewModel findItemById(Long id) {
        return populateToRfqPartView(repository.findProcurementRequisitionItemById(id));
    }

    public RfqPartViewModel populateToRfqPartView(ItemProjection itemProjection) {
        if (Objects.isNull(itemProjection)) {
            return new RfqPartViewModel();
        }

        return RfqPartViewModel.builder()
                .id(itemProjection.getId())
                .demandItemId(itemProjection.getDemandItemId())
                .partId(itemProjection.getDemandItemPartId())
                .partNo(itemProjection.getDemandItemPartPartNo())
                .partDescription(itemProjection.getDemandItemPartDescription())
                .quantityRequested(itemProjection.getRequisitionQuantity())
                .unitMeasurementId(itemProjection.getDemandItemUnitMeasurementId())
                .unitMeasurementCode(itemProjection.getDemandItemUnitMeasurementCode())
                .priority(itemProjection.getDemandItemPriorityType()).build();

    }

    @Override
    public List<RfqPartViewModel> getRfqPartViewModelLIst(Long requisitionId) {
        return findRequisitionItemByRequisitionId(requisitionId).stream().map(this::populateToRfqPartView).collect(Collectors.toList());
    }

    private ProcurementRequisitionItemViewModel populateToViewModel(ProcurementRequisitionItem requisition,
                                                                    StoreDemandDetailsDto storeDemandDetailsDto) {
        ProcurementRequisitionItemViewModel requisitionItemViewModel = new ProcurementRequisitionItemViewModel();

        requisitionItemViewModel.setDemandItemId(storeDemandDetailsDto.getId());
        requisitionItemViewModel.setQuantityDemanded(storeDemandDetailsDto.getQuantityDemanded());
        requisitionItemViewModel.setIsActive(storeDemandDetailsDto.getIsActive());
        requisitionItemViewModel.setStoreDemandId(storeDemandDetailsDto.getStoreDemandId());
        requisitionItemViewModel.setUnitMeasurementCode(storeDemandDetailsDto.getUnitMeasurementCode());
        requisitionItemViewModel.setUnitMeasurementId(storeDemandDetailsDto.getUnitMeasurementId());
        requisitionItemViewModel.setPartId(storeDemandDetailsDto.getPartId());
        requisitionItemViewModel.setPartNo(storeDemandDetailsDto.getPartNo());
        requisitionItemViewModel.setPartDescription(storeDemandDetailsDto.getPartDescription());
        requisitionItemViewModel.setAvailablePart(storeDemandDetailsDto.getAvailablePart());
        requisitionItemViewModel.setParentWiseRemarks(storeDemandDetailsDto.getParentWiseRemarks());

        requisitionItemViewModel.setId(requisition.getId());
        requisitionItemViewModel.setRequisitionId(requisition.getRequisitionId());
        requisitionItemViewModel.setRequisitionQuantity(requisition.getRequisitionQuantity());
        requisitionItemViewModel.setRequisitionPriority(requisition.getPriority());
        requisitionItemViewModel.setRemark(requisition.getRemark());
        requisitionItemViewModel.setDepartment(storeDemandDetailsDto.getDepartment());
        requisitionItemViewModel.setAircraftName(storeDemandDetailsDto.getAirCraftName());
        requisitionItemViewModel.setIpcCmm(storeDemandDetailsDto.getIpcCmm());


        return requisitionItemViewModel;
    }

    public ProcurementRequisitionItem saveItem(ProcurementRequisitionItem entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
                    ErrorId.DATA_NOT_SAVED_DYNAMIC, name));
        }
    }

    public List<ProcurementRequisitionItem> findByDemandItemId(Long id) {
        return repository.findByDemandItemId(id);
    }
}
