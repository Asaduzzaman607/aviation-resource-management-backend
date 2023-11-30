package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LogisticManualPoPopulateService {

    /**
     * ---- QUOTE REQUEST START ----
     */
    public RfqRequestDto populateToRfqDto(Long id,
                                          PartOrder partOrder,
                                          RfqType rfqType, Long vendorId,
                                          Long quoteRequestVendorId,
                                          Long poId) {
        return RfqRequestDto.builder()
                .id(id)
                .partOrder(partOrder)
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
                                                     List<PartOrderItem> partOrderItems,
                                                     QuoteRequest quoteRequest,
                                                     List<QuoteRequestVendor> quoteRequestVendors) {
        VendorQuotationDto vendorQuotationDto = poInternalDto.getVendorQuotationDto();
        vendorQuotationDto.setInputType(poInternalDto.getInputType());
        vendorQuotationDto.setRfqType(poInternalDto.getRfqType());
        vendorQuotationDto.setQuoteRequest(quoteRequest);
        vendorQuotationDto.setQuoteRequestVendor(quoteRequestVendors.stream().findFirst().get());

        Map<Long, PartOrderItem> partOrderItemMap = new HashMap<>();
        partOrderItems.forEach(item -> partOrderItemMap.put(item.getId(), item));
        vendorQuotationDto.setVendorQuotationDetails(vendorQuotationDto.getVendorQuotationDetails().stream().map(
                detail -> {
                    PartOrderItem partOrderItem = partOrderItemMap.get(detail.getItemId());
                    return populateToQuotationDetailDto(detail, partOrderItem);
                }).collect(Collectors.toList()));

        return vendorQuotationDto;
    }

    private VendorQuotationInvoiceDetailDto populateToQuotationDetailDto(VendorQuotationInvoiceDetailDto detail,
                                                                         PartOrderItem item) {
        detail.setPartOrderItem(item);
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
}
