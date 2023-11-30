package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.OVERLOAD;

@Service
public class ProcurementManualPoServiceImpl implements ProcurementManualPoService {

    private final StoreDemandService storeDemandService;
    private final ProcurementRequisitionService requisitionService;
    private final VendorQuotationService quotationService;
    private final QuoteRequestService quoteRequestService;
    private final ProcurementManualPoPopulateService procurementManualPoPopulateService;

    public ProcurementManualPoServiceImpl(StoreDemandService storeDemandService,
                                          ProcurementRequisitionService requisitionService,
                                          VendorQuotationService quotationService,
                                          QuoteRequestService quoteRequestService,
                                          ProcurementManualPoPopulateService procurementManualPoPopulateService) {
        this.storeDemandService = storeDemandService;
        this.requisitionService = requisitionService;
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

        /** CREATE STORE DEMAND */
        Pair<StoreDemand, List<StoreDemandItem>> storeDemandListPair = storeDemandService.create(
                procurementManualPoPopulateService.populateToStoreDemandDto(dto.getVendorQuotationDto()), dto.getOrderType());

        /** CREATE REQUISITION */
        Pair<ProcurementRequisition,
                List<ProcurementRequisitionItem>> requisitionListPair = requisitionService.create(procurementManualPoPopulateService.populateToRequisition(
                storeDemandListPair.getLeft(), storeDemandListPair.getRight(), dto.getVendorQuotationDto()), dto.getOrderType());

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

        /** UPDATE STORE DEMAND */
        Pair<StoreDemand, List<StoreDemandItem>> storeDemandListPair = storeDemandService.update(
                procurementManualPoPopulateService.populateToStoreDemandDto(dto.getVendorQuotationDto()), dto.getOrderType(), partOrder.getId());

        /** UPDATE REQUISITION */
        Pair<ProcurementRequisition,
                List<ProcurementRequisitionItem>> requisitionListPair = requisitionService.update(procurementManualPoPopulateService.populateToRequisition(
                storeDemandListPair.getLeft(), storeDemandListPair.getRight(), dto.getVendorQuotationDto()), dto.getOrderType(), partOrder.getId());

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
}
