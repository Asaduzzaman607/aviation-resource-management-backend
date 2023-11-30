package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationInvoiceDetailDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.OVERLOAD;

@Service
public class RequisitionToManualPoServiceImpl implements RequisitionToManualPoService {

    private final ProcurementRequisitionService requisitionService;
    private final ProcurementRequisitionItemService requisitionItemService;
    private final VendorQuotationService quotationService;
    private final QuoteRequestService quoteRequestService;
    private final ProcurementManualPoPopulateService procurementManualPoPopulateService;

    public RequisitionToManualPoServiceImpl(ProcurementRequisitionService requisitionService,
                                            ProcurementRequisitionItemService requisitionItemService,
                                            VendorQuotationService quotationService,
                                            QuoteRequestService quoteRequestService,
                                            ProcurementManualPoPopulateService procurementManualPoPopulateService) {
        this.requisitionService = requisitionService;
        this.requisitionItemService = requisitionItemService;
        this.quotationService = quotationService;
        this.quoteRequestService = quoteRequestService;
        this.procurementManualPoPopulateService = procurementManualPoPopulateService;
    }

    /**
     * --------------- POPULATE PROCUREMENT MANUAL PO FOR CREATE --------------
     */
    @Transactional
    @Override
    public PoInternalDto populateToManualEntity(PoInternalDto dto) {

        /** CREATE REQUISITION */
        Pair<ProcurementRequisition, List<ProcurementRequisitionItem>> requisitionListPair = findRequisition(dto);

        /** CREATE QUOTE REQUEST */
        Pair<QuoteRequest, List<QuoteRequestVendor>> quoteRequestListPair = quoteRequestService.create(
                procurementManualPoPopulateService.populateToRfqDto(
                        dto.getVendorQuotationDto().getQuoteRequestId(),
                        requisitionListPair.getLeft(),
                        dto.getRfqType(),
                        dto.getVendorQuotationDto().getVendorId(),
                        dto.getVendorQuotationDto().getQuoteRequestVendorId(),
                        null),
                dto.getInputType());

        /** CREATE VENDOR QUOTATION */
        Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> vendorQuotationListPair = quotationService.create(
                procurementManualPoPopulateService.populateToQuotationDto(dto, requisitionListPair.getRight(), quoteRequestListPair.getLeft(),
                        quoteRequestListPair.getRight()), OVERLOAD);

        /** CREATE PART ORDER */
        return procurementManualPoPopulateService.populateToPoInternalDto(dto, vendorQuotationListPair);
    }

    /**
     * --------------- POPULATE PROCUREMENT MANUAL PO FOR UPDATE -------------
     */
    @Transactional
    @Override
    public PoInternalDto populateToManualEntity(PoInternalDto dto, PartOrder partOrder) {

        /** UPDATE REQUISITION */
        Pair<ProcurementRequisition, List<ProcurementRequisitionItem>> requisitionListPair = findRequisition(dto);

        /** UPDATE QUOTE REQUEST */
        Pair<QuoteRequest, List<QuoteRequestVendor>> quoteRequestListPair = quoteRequestService.update(
                procurementManualPoPopulateService.populateToRfqDto(
                        dto.getVendorQuotationDto().getQuoteRequestId(),
                        requisitionListPair.getLeft(),
                        dto.getRfqType(),
                        dto.getVendorQuotationDto().getVendorId(),
                        dto.getVendorQuotationDto().getQuoteRequestVendorId(),
                        null),
                dto.getInputType(),
                partOrder.getId());

        /** UPDATE VENDOR QUOTATION */
        Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> vendorQuotationListPair = quotationService.update(
                procurementManualPoPopulateService.populateToQuotationDto(dto, requisitionListPair.getRight(), quoteRequestListPair.getLeft(),
                        quoteRequestListPair.getRight()), dto.getVendorQuotationDto().getId(), OVERLOAD);

        /** UPDATE PART ORDER */
        return procurementManualPoPopulateService.populateToPoInternalDto(dto, vendorQuotationListPair);
    }

    private Pair<ProcurementRequisition, List<ProcurementRequisitionItem>> findRequisition(PoInternalDto dto) {
        ProcurementRequisition procurementRequisition = requisitionService.findById(dto.getRequisitionId());

        Set<Long> itemIds = dto.getVendorQuotationDto().getVendorQuotationDetails().stream()
                .map(VendorQuotationInvoiceDetailDto::getItemId).collect(Collectors.toSet());

        List<ProcurementRequisitionItem> requisitionItems = requisitionItemService.getAllByDomainIdIn(itemIds);

        return Pair.of(procurementRequisition, requisitionItems);
    }
}
