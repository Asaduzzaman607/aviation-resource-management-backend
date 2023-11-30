package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationInvoiceDetailDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.OVERLOAD;

@Service
public class LogisticManualPoServiceImpl implements LogisticManualPoService{
    private final VendorQuotationService quotationService;
    private final QuoteRequestService quoteRequestService;
    private final LogisticManualPoPopulateService logisticManualPoPopulateService;
    private final PartOrderService partOrderService;
    private final PartOrderItemService partOrderItemService;

    public LogisticManualPoServiceImpl(VendorQuotationService quotationService,
                                       QuoteRequestService quoteRequestService,
                                       LogisticManualPoPopulateService logisticManualPoPopulateService,
                                       @Lazy PartOrderService partOrderService,
                                       PartOrderItemService partOrderItemService) {
        this.quotationService = quotationService;
        this.quoteRequestService = quoteRequestService;
        this.logisticManualPoPopulateService = logisticManualPoPopulateService;
        this.partOrderService = partOrderService;
        this.partOrderItemService = partOrderItemService;
    }

    /**
     * --------------- POPULATE LOGISTIC MANUAL PO FOR CREATE -------------
     */
    @Transactional
    @Override
    public PoInternalDto populateToManualEntity(PoInternalDto dto) {
        /** FIND REQUISITION */
        Pair<PartOrder, List<PartOrderItem>> partOrderListPair = findPartOrder(dto);

        /** CREATE QUOTE REQUEST */
        Pair<QuoteRequest, List<QuoteRequestVendor>> quoteRequestListPair = quoteRequestService.create(
                logisticManualPoPopulateService.populateToRfqDto(
                        dto.getVendorQuotationDto().getQuoteRequestId(),
                        partOrderListPair.getLeft(),
                        dto.getRfqType(),
                        dto.getVendorQuotationDto().getVendorId(),
                        dto.getVendorQuotationDto().getQuoteRequestVendorId(),
                        dto.getPPoId()),
                dto.getInputType());

        /** CREATE VENDOR QUOTATION */
        Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> vendorQuotationListPair = quotationService.create(
                logisticManualPoPopulateService.populateToQuotationDto(dto, partOrderListPair.getRight(), quoteRequestListPair.getLeft(),
                        quoteRequestListPair.getRight()), OVERLOAD);

        /** CREATE PART ORDER */
        return logisticManualPoPopulateService.populateToPoInternalDto(dto, vendorQuotationListPair);
    }

    /**
     * --------------- POPULATE LOGISTIC MANUAL PO FOR UPDATE -------------
     */
    @Transactional
    @Override
    public PoInternalDto populateToManualEntity(PoInternalDto dto, PartOrder partOrder) {
        /** FIND REQUISITION */
        Pair<PartOrder, List<PartOrderItem>> partOrderListPair = findPartOrder(dto);

        /** UPDATE QUOTE REQUEST */
        Pair<QuoteRequest, List<QuoteRequestVendor>> quoteRequestListPair = quoteRequestService.update(
                logisticManualPoPopulateService.populateToRfqDto(
                        dto.getVendorQuotationDto().getQuoteRequestId(),
                        partOrderListPair.getLeft(),
                        dto.getRfqType(),
                        dto.getVendorQuotationDto().getVendorId(),
                        dto.getVendorQuotationDto().getQuoteRequestVendorId(),
                        dto.getPPoId()),
                dto.getInputType(),
                partOrder.getId());

        /** UPDATE VENDOR QUOTATION */
        Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> vendorQuotationListPair = quotationService.update(
                logisticManualPoPopulateService.populateToQuotationDto(dto, partOrderListPair.getRight(), quoteRequestListPair.getLeft(),
                        quoteRequestListPair.getRight()), dto.getVendorQuotationDto().getId(), OVERLOAD);

        /** UPDATE PART ORDER */
        return logisticManualPoPopulateService.populateToPoInternalDto(dto, vendorQuotationListPair);
    }

    private Pair<PartOrder, List<PartOrderItem>> findPartOrder(PoInternalDto dto) {
        if(Objects.isNull(dto.getPPoId())){
            throw EngineeringManagementServerException.badRequest(ErrorId.PART_ORDER_ID_IS_REQUIRED_FOR_LOGISTIC_MANUAL_PO);
        }
        PartOrder partOrder = partOrderService.findById(dto.getPPoId());

        Set<Long> itemIds = dto.getVendorQuotationDto().getVendorQuotationDetails().stream()
                .map(VendorQuotationInvoiceDetailDto::getItemId).collect(Collectors.toSet());

        List<PartOrderItem> partOrderItems = partOrderItemService.getAllByDomainIdIn(itemIds, true);

        return Pair.of(partOrder, partOrderItems);
    }
}
