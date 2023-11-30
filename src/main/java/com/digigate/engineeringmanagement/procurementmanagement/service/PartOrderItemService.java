package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.ItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PoItemAirCraftProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PoItemResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.repository.PartOrderItemRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.CsPartUtilService;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PartOrderItemService extends AbstractSearchService<PartOrderItem, IDto, IdQuerySearchDto> {
    private final ProcurementRequisitionItemService requisitionItemService;
    private final PartOrderItemRepository partOrderItemRepository;
    private final QuoteRequestService quoteRequestService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final CsPartUtilService csPartUtilService;
    private final DemandStatusService demandStatusService;

    public PartOrderItemService(ProcurementRequisitionItemService requisitionItemService,
                                PartOrderItemRepository partOrderItemRepository,
                                QuoteRequestService quoteRequestService,
                                @Lazy VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                                CsPartUtilService csPartUtilService, DemandStatusService demandStatusService) {
        super(partOrderItemRepository);
        this.requisitionItemService = requisitionItemService;
        this.partOrderItemRepository = partOrderItemRepository;
        this.quoteRequestService = quoteRequestService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.csPartUtilService = csPartUtilService;
        this.demandStatusService = demandStatusService;
    }

    public List<PartOrderItemProjection> findPoItemByIdIn(Set<Long> poItemIds) {
        return partOrderItemRepository.findPartOrderItemByIdIn(poItemIds);
    }

    @Override
    public PoItemResponseDto getSingle(Long id) {
        return convertToResponseDto(findByIdUnfiltered(id));
    }

    public List<PartOrderItem> findByPartOrderId(Long poId) {
        return partOrderItemRepository.findByPartOrderId(poId);
    }

    public List<PartOrderItemProjection> findAllByPartOrderId(Long poId) {
        return partOrderItemRepository.findPartOrderItemByPartOrderId(poId);
    }

    @Override
    public PageData search(IdQuerySearchDto dto, Pageable pageable) {
        Specification<PartOrderItem> partOrderItemSpecification = buildSpecification(dto);
        Page<PartOrderItem> pageData = partOrderItemRepository.findAll(partOrderItemSpecification, pageable);
        return PageData.builder()
                .model(getAllPart(pageData.getContent()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected Specification<PartOrderItem> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<PartOrderItem> customSpecification = new CustomSpecification<>();
        Long partOrderId = quoteRequestService.findQuoteRequestById(searchDto.getId()).get().getPartOrderId();
        if (Objects.isNull(partOrderId)) {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(partOrderId, ApplicationConstant.PART_ORDER_ID)
        );
    }

    @Override
    protected PoItemResponseDto convertToResponseDto(PartOrderItem partOrderItem) {
        PoItemResponseDto poItemResponseDto = new PoItemResponseDto();
        poItemResponseDto.setId(partOrderItem.getId());
        poItemResponseDto.setItemId(partOrderItem.getIqItemId());
        poItemResponseDto.setPartOrderId(partOrderItem.getPartOrderId());
        getResponseData(partOrderItem.getIqItemId(), poItemResponseDto);
        return poItemResponseDto;
    }

    @Override
    protected PartOrderItem convertToEntity(IDto iDto) {
        return null;
    }

    @Override
    protected PartOrderItem updateEntity(IDto dto, PartOrderItem entity) {
        return null;
    }

    public void saveOrUpdate(PoInternalDto poInternalDto, PartOrder partOrder) {
        Set<Long> iqItemIdSet = new HashSet<>(poInternalDto.getInputType() == InputType.MANUAL ? poInternalDto.getIqItems()
                .stream().map(VendorQuotationInvoiceDetail::getId).collect(Collectors.toList()) :
                CollectionUtils.isNotEmpty(poInternalDto.getItemIdList()) ? poInternalDto.getItemIdList() : new ArrayList<>());

        List<VendorQuotationInvoiceDetail> iqItems = poInternalDto.getInputType() == InputType.MANUAL ?
                poInternalDto.getIqItems() : vendorQuotationInvoiceDetailService.getAllByDomainIdIn(iqItemIdSet, true);

        Map<Long, VendorQuotationInvoiceDetail> iqItemMap = iqItems.stream().collect(Collectors.toMap(
                VendorQuotationInvoiceDetail::getId, Function.identity(), (a, b) -> a));

        Set<Long> poIqItemIdSet = partOrderItemRepository.findPartOrderItemByPartOrderId(partOrder.getId()).stream().map(
                PartOrderItemProjection::getIqItemId).collect(Collectors.toSet());

        saveItemList(iqItemIdSet.stream().filter(id -> !poIqItemIdSet.contains(id)).map(id ->
                populateToEntityForCs(iqItemMap.get(id), partOrder)).collect(Collectors.toList()));
        //Demand status tracking for po
    }

    public List<PoItemResponseDto> getAllResponse(List<PartOrder> partOrderList,
                                                  RfqType rfqType) {
        List<PartOrderItem> partOrderItemList = partOrderList.stream().flatMap(partOrder -> partOrder.getPartOrderItemList()
                .stream()).collect(Collectors.toList());

        Set<Long> iqItemIdSet = partOrderItemList.stream().map(PartOrderItem::getIqItemId).collect(Collectors.toSet());

        Map<Long, IqItemProjection> iqItemProjectionMap;
        if (rfqType == RfqType.PROCUREMENT) {
            iqItemProjectionMap = vendorQuotationInvoiceDetailService.findDetailsByIdIn(iqItemIdSet, VendorRequestType.QUOTATION)
                    .stream().collect(Collectors.toMap(IqItemProjection::getId, Function.identity()));
        } else {
            iqItemProjectionMap = vendorQuotationInvoiceDetailService.findDetailsByIdInForLogistic(iqItemIdSet, VendorRequestType.QUOTATION)
                    .stream().collect(Collectors.toMap(IqItemProjection::getId, Function.identity()));
        }

        return partOrderList.stream().flatMap(partOrder -> populateToViewModel(partOrder.getPartOrderItemList(),
                iqItemProjectionMap, rfqType).stream()).collect(Collectors.toList());
    }

    private List<PoItemResponseDto> populateToViewModel(List<PartOrderItem> partOrderItemList,
                                                        Map<Long, IqItemProjection> iqItemProjectionMap, RfqType rfqType) {

        return partOrderItemList.stream().map(partOrderItem -> convertToResponseDto(partOrderItem,
                iqItemProjectionMap.getOrDefault(partOrderItem.getIqItemId(), null), rfqType)).collect(Collectors.toList());
    }

    private void getResponseData(Long requisitionItemId, PoItemResponseDto poItemResponseDto) {
        Set<Long> requisitionItemIds = new HashSet<>();
        requisitionItemIds.add(requisitionItemId);
        List<ItemProjection> itemProjectionList = requisitionItemService.findAllByIdIn(requisitionItemIds);

        if (Objects.nonNull(itemProjectionList)) {
            itemProjectionList.forEach(itemProjection -> {
                poItemResponseDto.setPartId(itemProjection.getDemandItemPartId());
                poItemResponseDto.setPartNo(itemProjection.getDemandItemPartPartNo());
                poItemResponseDto.setPartDescription(itemProjection.getDemandItemPartDescription());
                poItemResponseDto.setQuantity(itemProjection.getRequisitionQuantity());
                poItemResponseDto.setUomId(itemProjection.getDemandItemUnitMeasurementId());
                poItemResponseDto.setUomCode(itemProjection.getDemandItemUnitMeasurementCode());
            });
        }
    }

    private PoItemResponseDto convertToResponseDto(PartOrderItem partOrderItem,
                                                   IqItemProjection iqItemProjection, RfqType rfqType) {

        PoItemResponseDto poItemResponseDto = new PoItemResponseDto();

        poItemResponseDto.setId(partOrderItem.getId());
        poItemResponseDto.setItemId(partOrderItem.getIqItemId());
        poItemResponseDto.setPartOrderId(partOrderItem.getPartOrderId());
        PoItemAirCraftProjection airCraftProjection;
        if (rfqType == RfqType.PROCUREMENT) {
            airCraftProjection = partOrderItemRepository
                    .findAircraftNameForProcurement(partOrderItem.getPartOrderId());
        } else {
            airCraftProjection = partOrderItemRepository
                    .findAircraftNameForLogistics(partOrderItem.getPartOrderId());
        }
        if (airCraftProjection != null) {
            poItemResponseDto.setAircraftName(airCraftProjection.getAircraftName());
            poItemResponseDto.setAircraftId(airCraftProjection.getAirCraftId());
        }

        if (Objects.nonNull(iqItemProjection)) {
            poItemResponseDto.setPartId(csPartUtilService.getPartId(iqItemProjection));
            poItemResponseDto.setPartNo(csPartUtilService.getPartNo(iqItemProjection));
            poItemResponseDto.setPartDescription(csPartUtilService.getPartDescription(iqItemProjection));
            poItemResponseDto.setRequisitionPriority(iqItemProjection.getPriorityType());
            poItemResponseDto.setQuantity(iqItemProjection.getPartQuantity());
            poItemResponseDto.setVendorSerials(iqItemProjection.getVendorSerials());
            poItemResponseDto.setUomId(csPartUtilService.getUomId(iqItemProjection));
            poItemResponseDto.setUomCode(csPartUtilService.getUomCode(iqItemProjection));
            poItemResponseDto.setUnitPrice(iqItemProjection.getUnitPrice());
            poItemResponseDto.setCd(iqItemProjection.getCondition());
            poItemResponseDto.setLt(iqItemProjection.getLeadTime());
            poItemResponseDto.setCurrencyId(iqItemProjection.getCurrencyId());
            poItemResponseDto.setCurrencyCode(iqItemProjection.getCurrencyCode());
        }
        return poItemResponseDto;
    }

    private List<RfqPartViewModel> getAllPart(List<PartOrderItem> partOrderItemList) {
        Set<Long> reqIqItemIds = partOrderItemList.stream().map(PartOrderItem::getIqItemId).collect(Collectors.toSet());
        return getAllPart(reqIqItemIds);
    }

    private List<RfqPartViewModel> getAllPart(Set<Long> reqIqItemIds) {
        return vendorQuotationInvoiceDetailService.findDetailsByIdIn(reqIqItemIds, VendorRequestType.QUOTATION).stream()
                .map(this::convertPartView).collect(Collectors.toList());
    }

    private RfqPartViewModel convertPartView(IqItemProjection part) {
        if (Objects.isNull(part)) {
            return new RfqPartViewModel();
        }

        RfqPartViewModel rfqPartViewModel = new RfqPartViewModel();
        rfqPartViewModel.setId(part.getId());
        rfqPartViewModel.setPartId(csPartUtilService.getPartId(part));
        rfqPartViewModel.setPartNo(csPartUtilService.getPartNo(part));
        rfqPartViewModel.setPartDescription(csPartUtilService.getPartDescription(part));
        return rfqPartViewModel;
    }

    private PartOrderItem populateToEntityForCs(VendorQuotationInvoiceDetail detail, PartOrder partOrder) {

        if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
            demandStatusService.createPO(
                    Objects.nonNull(detail.getAlternatePart())?detail.getAlternatePart().getId():detail.getRequisitionItem().getDemandItem().getPart().getId(),
                    getParentId(partOrder, detail),
                    detail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                    partOrder.getId(),
                    detail.getId(),
                    detail.getPartQuantity(),
                    partOrder.getWorkFlowAction().getId(),
                    getVoucherType(partOrder),
                    partOrder.getRfqType().name(),
                    partOrder.getIsActive(),
                    partOrder.getInputType(),
                    partOrder.getIsRejected()
            );
        } else {
            demandStatusService.createPO(
                    Objects.nonNull(detail.getPoItem().getIqItem().getAlternatePart())?detail.getPoItem().getIqItem().getAlternatePart().getId():
                            detail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                    getParentId(partOrder, detail),
                    detail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                    partOrder.getId(),
                    detail.getId(),
                    detail.getPartQuantity(),
                    partOrder.getWorkFlowAction().getId(),
                    getVoucherType(partOrder),
                    partOrder.getRfqType().name(),
                    partOrder.getIsActive(),
                    partOrder.getInputType(),
                    partOrder.getIsRejected()
            );
        }
        return PartOrderItem
                .builder()
                .iqItem(detail)
                .partOrder(partOrder)
                .build();
    }

    private Long getParentId(PartOrder partOrder, VendorQuotationInvoiceDetail detail) {
        if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                return detail.getRequisitionItem().getProcurementRequisition().getId();
            }
        } else {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                return partOrder.getId();
            }
        }
        return partOrder.getCsDetail().getComparativeStatement().getId();

    }

    private VoucherType getVoucherType(PartOrder partOrder) {


        VoucherType voucherType;
        switch (partOrder.getOrderType())
        {
            case REPAIR:
                voucherType = VoucherType.RO;
                break;
            case LOAN:
                voucherType = VoucherType.LO;
                break;
            case EXCHANGE:
                voucherType = VoucherType.PO;
                break;
            case PURCHASE:
                voucherType = VoucherType.ORDER;
                break;
            default:
                voucherType = VoucherType.ORDER;
                break;
        }
        return voucherType;

    }

    public List<RfqPartViewModel> getRfqPartViewModelsByIdIn(Set<Long> poItemIds) {
        List<PartOrderItemProjection> partOrderItemProjections = findPoItemByIdIn(poItemIds);

        Set<Long> iqItemIds = partOrderItemProjections.stream().map(PartOrderItemProjection::getIqItemId).collect(Collectors.toSet());

        Map<Long, IqItemProjection> iqItemProjectionMap = vendorQuotationInvoiceDetailService.findDetailsByIdIn(iqItemIds, VendorRequestType.QUOTATION)
                .stream().collect(Collectors.toMap(IqItemProjection::getId, Function.identity()));

        return partOrderItemProjections.stream().map(poItem -> populateToRfqPartView(poItem, iqItemProjectionMap.get(poItem.getIqItemId()))).collect(Collectors.toList());
    }

    public RfqPartViewModel populateToRfqPartView(PartOrderItemProjection projection, IqItemProjection iqItemProjection) {
        if (Objects.isNull(projection) || Objects.isNull(iqItemProjection)) {
            return new RfqPartViewModel();
        }

        return RfqPartViewModel.builder()
                .id(projection.getId())
                .iqItemId(iqItemProjection.getId())
                .partId(csPartUtilService.getPartId(iqItemProjection))
                .partNo(csPartUtilService.getPartNo(iqItemProjection))
                .partDescription(csPartUtilService.getPartDescription(iqItemProjection))
                .quantityRequested(iqItemProjection.getPartQuantity())
                .unitMeasurementId(csPartUtilService.getUomId(iqItemProjection))
                .unitMeasurementCode(csPartUtilService.getUomCode(iqItemProjection))
                .priority(iqItemProjection.getPriorityType()).build();
    }
}
