package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionItemDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandsDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcurementManualPoPopulateService {
    private final ProcurementRequisitionItemService requisitionItemService;
    private final String DEMAND = "DEMAND";
    private final String REQUISITION = "REQUISITION";

    public ProcurementManualPoPopulateService(ProcurementRequisitionItemService requisitionItemService) {
        this.requisitionItemService = requisitionItemService;
    }

    /**
     * ---- STORE DEMAND START ----
     */
    public StoreDemandsDto populateToStoreDemandDto(VendorQuotationDto vendorQuotationDto) {
        List<VendorQuotationInvoiceDetailDto> quotationDetailDtos = Objects.nonNull(vendorQuotationDto) ?
                vendorQuotationDto.getVendorQuotationDetails() : new ArrayList<>();

        return StoreDemandsDto.builder()
                .storeDemandDetailsDtoList(populateToStoreDemandItemDtos(quotationDetailDtos))
                .build();
    }

    private List<StoreDemandDetailsDto> populateToStoreDemandItemDtos(List<VendorQuotationInvoiceDetailDto> quotationDetailDtos) {
        Map<Long, Long> requisitionItemAndItemIdMap = getRequisitionItemIdAndItemIdMap(quotationDetailDtos, DEMAND);

        return quotationDetailDtos.stream().map(detail -> populateToStoreDemandItem(
                requisitionItemAndItemIdMap.get(detail.getItemId()),
                detail.getPartId(),
                detail.getUomId(),
                detail.getPartQuantity())).collect(Collectors.toList());
    }

    private StoreDemandDetailsDto populateToStoreDemandItem(Long itemId, Long partId,Long uomId, Integer quantity) {
        return StoreDemandDetailsDto.builder()
                .id(itemId)
                .partId(partId)
                .priorityType(PriorityType.NORMAL)
                .unitMeasurementId(uomId)
                .quantityDemanded(quantity)
                .remark(ApplicationConstant.INVISIBLE_REMARK)
                .build();
    }
    /** ---- STORE DEMAND END ---- */

    /**
     * ---- STORE REQUISITION START ----
     */
    public ProcurementRequisitionDto populateToRequisition(StoreDemand storeDemand, List<StoreDemandItem> storeDemandItems,
                                                            VendorQuotationDto vendorQuotationDto) {
        return ProcurementRequisitionDto.builder()
                .storeDemandId(storeDemand.getId())
                .procurementRequisitionItemDtoList(populateToRequisitionItemDtos(storeDemandItems,
                        vendorQuotationDto.getVendorQuotationDetails()))
                .build();
    }

    private List<ProcurementRequisitionItemDto> populateToRequisitionItemDtos(List<StoreDemandItem> storeDemandItems,
                                                                              List<VendorQuotationInvoiceDetailDto> quotationDetailDtos) {
        Map<Long, Long> itemIdAndRequisitionItemIdMap = getRequisitionItemIdAndItemIdMap(quotationDetailDtos, REQUISITION);

        return storeDemandItems.stream().map(item -> populateToRequisitionItemDto(item, itemIdAndRequisitionItemIdMap.get(item.getId())))
                .collect(Collectors.toList());
    }

    private ProcurementRequisitionItemDto populateToRequisitionItemDto(StoreDemandItem item, Long requisitionItemId) {
        return ProcurementRequisitionItemDto.builder()
                .id(requisitionItemId)
                .quantityRequested(item.getQuantityDemanded())
                .storeDemandItem(item)
                .inputType(InputType.MANUAL)
                .priorityType(PriorityType.NORMAL)
                .build();
    }

    /** ---- STORE REQUISITION END ---- */

    /**
     * ---- QUOTE REQUEST START ----
     */
    public RfqRequestDto populateToRfqDto(Long id,
                                          ProcurementRequisition requisition,
                                          RfqType rfqType, Long vendorId,
                                          Long quoteRequestVendorId,
                                          Long poId) {
        return RfqRequestDto.builder()
                .id(id)
                .requisition(requisition)
                .partOrderId(poId)
                .rfqType(rfqType)
                .quoteRequestVendorModelList(Collections.singletonList(populateToRfqVendorDto(vendorId, quoteRequestVendorId)))
                .build();
    }

    private QuoteRequestVendorDto populateToRfqVendorDto(Long vendorId, Long id) {
        return QuoteRequestVendorDto.builder()
                .id(id)
                .vendorId(vendorId)
                .requestDate(LocalDate.now())
                .build();
    }
    /** ---- QUOTE REQUEST END ---- */

    /**
     * ---- VENDOR QUOTATION START ----
     */
    public VendorQuotationDto populateToQuotationDto(PoInternalDto poInternalDto,
                                                      List<ProcurementRequisitionItem> requisitionItems,
                                                      QuoteRequest quoteRequest,
                                                      List<QuoteRequestVendor> quoteRequestVendors) {
        VendorQuotationDto vendorQuotationDto = poInternalDto.getVendorQuotationDto();
        vendorQuotationDto.setInputType(poInternalDto.getInputType());
        vendorQuotationDto.setRfqType(poInternalDto.getRfqType());
        vendorQuotationDto.setQuoteRequest(quoteRequest);
        vendorQuotationDto.setQuoteRequestVendor(quoteRequestVendors.stream().findFirst().get());

        Map<Long, ProcurementRequisitionItem> requisitionItemMap = new HashMap<>();
        requisitionItems.forEach(item -> requisitionItemMap.put(item.getDemandItem().getPart().getId(), item));
        vendorQuotationDto.setVendorQuotationDetails(vendorQuotationDto.getVendorQuotationDetails().stream().map(
                detail -> {
                    ProcurementRequisitionItem procurementRequisitionItem = requisitionItemMap.get(detail.getPartId());
                    return populateToQuotationDetailDto(detail, procurementRequisitionItem);
                }).collect(Collectors.toList()));

        return vendorQuotationDto;
    }

    private VendorQuotationInvoiceDetailDto populateToQuotationDetailDto(VendorQuotationInvoiceDetailDto detail,
                                                                         ProcurementRequisitionItem item) {
        detail.setRequisitionItem(item);
        return detail;
    }

    /** ---- VENDOR QUOTATION END ---- */

    /** ---- PART ORDER START ---- */
    public PoInternalDto populateToPoInternalDto(PoInternalDto dto, Pair<VendorQuotation,
            List<VendorQuotationInvoiceDetail>> vendorQuotationListPair) {

        dto.setVendorQuotation(vendorQuotationListPair.getLeft());
        dto.setIqItems(vendorQuotationListPair.getRight());
        return dto;
    }
    /** ---- PART ORDER END ---- */

    /**
     * ------------------- GLOBAL -------------------------
     */

    private Map<Long, Long> getRequisitionItemIdAndItemIdMap(List<VendorQuotationInvoiceDetailDto> quotationDetailDtos, String demandOrReq){
        Set<Long> requisitionItemIds = quotationDetailDtos.stream().map(VendorQuotationInvoiceDetailDto::getItemId).collect(Collectors.toSet());

        if(Objects.equals(demandOrReq, DEMAND)){
            return requisitionItemService.findRequisitionItemList(requisitionItemIds).stream()
                    .collect(Collectors.toMap(RequisitionItemProjection::getId, RequisitionItemProjection::getDemandItemId));
        }
        if(Objects.equals(demandOrReq, REQUISITION)){
            return requisitionItemService.findRequisitionItemList(requisitionItemIds).stream()
                    .collect(Collectors.toMap(RequisitionItemProjection::getDemandItemId, RequisitionItemProjection::getId));
        }
        return new HashMap<>();
    }
}
