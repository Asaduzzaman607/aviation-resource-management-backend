package com.digigate.engineeringmanagement.procurementmanagement.util;

import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceFee;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceFeeService;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INVISIBLE;

@Service
public class PoDuplicateQuotation {
    private final VendorQuotationService vendorQuotationService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService;

    public PoDuplicateQuotation(VendorQuotationService vendorQuotationService,
                                VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                                VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService) {
        this.vendorQuotationService = vendorQuotationService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.vendorQuotationInvoiceFeeService = vendorQuotationInvoiceFeeService;
    }

    @Transactional
    public VendorQuotation duplicateVendorQuotation(VendorQuotation oldVendorQuotation, PoInternalDto poInternalDto){
        VendorQuotation vendorQuotation = new VendorQuotation();
        BeanUtils.copyProperties(oldVendorQuotation, vendorQuotation);
        final Long oldQuotationId = oldVendorQuotation.getId();

        /** Duplicate new vendor quotation */
        vendorQuotation.setId(null);
        /** Setting hidden quotation pattern */
        vendorQuotation.setQuotationNo(INVISIBLE + ZonedDateTime.now().toInstant().toEpochMilli());
        vendorQuotationService.saveItem(vendorQuotation);
        duplicateVqItem(vendorQuotation, oldQuotationId, poInternalDto);
        duplicateVqFee(vendorQuotation, oldQuotationId);

        return vendorQuotation;
    }

    @Transactional
    public VendorQuotation updateDuplicateVendorQuotation(VendorQuotation oldVendorQuotation,
                                                          Long partOrderId,
                                                          PoInternalDto poInternalDto){
        VendorQuotation newVendorQuotation = vendorQuotationService.findByPartOrderId(partOrderId);
        updateDuplicateVqItem(newVendorQuotation, oldVendorQuotation.getId(), poInternalDto);

        return newVendorQuotation;
    }

    private void duplicateVqItem(VendorQuotation vendorQuotation, Long oldQuotationId, PoInternalDto poInternalDto) {
        List<VendorQuotationInvoiceDetail> iqItems =
                vendorQuotationInvoiceDetailService.findByVendorQuotationInvoiceId(oldQuotationId);
        List<Long> iqItemList = poInternalDto.getItemIdList();

        List<Long> newIqItemList = iqItems.stream().filter(item -> iqItemList.contains(item.getId())).map(oldIqItem ->
                getNewListOfIqItemId(oldIqItem, vendorQuotation)).collect(Collectors.toList());

        /** Setting new id list */
        poInternalDto.setItemIdList(newIqItemList);
    }

    private void updateDuplicateVqItem(VendorQuotation vendorQuotation, Long oldQuotationId, PoInternalDto poInternalDto) {
        List<Long> newIqItemIds = vendorQuotationInvoiceDetailService.findByVendorQuotationInvoiceId(vendorQuotation.getId())
                .stream().map(this::getPartIdFromIqItemId).collect(Collectors.toList());

        List<VendorQuotationInvoiceDetail> iqItems =
                vendorQuotationInvoiceDetailService.findByVendorQuotationInvoiceId(oldQuotationId);
        List<Long> iqItemIdList = poInternalDto.getItemIdList();

        List<Long> newIqItemList = iqItems.stream().filter(iqItem -> checkAvailability(iqItem, iqItemIdList, newIqItemIds))
                .map(oldIqItem -> getNewListOfIqItemId(oldIqItem, vendorQuotation)).collect(Collectors.toList());

        /** Setting new id list */
        poInternalDto.setItemIdList(newIqItemList);
    }

    private Long getNewListOfIqItemId(VendorQuotationInvoiceDetail oldIqItem, VendorQuotation vendorQuotation){
        VendorQuotationInvoiceDetail iqItem = new VendorQuotationInvoiceDetail();
        BeanUtils.copyProperties(oldIqItem, iqItem);

        iqItem.setId(null);
        iqItem.setVendorQuotationInvoiceId(vendorQuotation.getId());
        iqItem.setVendorRequestType(VendorRequestType.QUOTATION);

        return vendorQuotationInvoiceDetailService.saveItem(iqItem).getId();
    }

    private void duplicateVqFee(VendorQuotation vendorQuotation, Long oldQuotationId) {
        List<VendorQuotationInvoiceFee> iqFees =
                vendorQuotationInvoiceFeeService.findByVendorQuotationInvoiceId(oldQuotationId);

        iqFees.forEach(oldIqFee -> {
            VendorQuotationInvoiceFee iqFee = new VendorQuotationInvoiceFee();
            BeanUtils.copyProperties(oldIqFee, iqFee);

            iqFee.setId(null);
            iqFee.setVendorQuotationInvoiceId(vendorQuotation.getId());
            iqFee.setVendorRequestType(VendorRequestType.QUOTATION);
            vendorQuotationInvoiceFeeService.saveItem(iqFee);
        });
    }

    private Long getPartIdFromIqItemId(VendorQuotationInvoiceDetail iqItem){
        if(Objects.nonNull(iqItem.getAlternatePartId())){
            return iqItem.getAlternatePartId();
        }
        return iqItem.getRequisitionItem().getDemandItem().getPartId();
    }

    private boolean checkAvailability(VendorQuotationInvoiceDetail iqItem, List<Long> iqItemIdList, List<Long> newIqItemIds) {
        return iqItemIdList.contains(iqItem.getId()) && !newIqItemIds.contains(getPartIdFromIqItemId(iqItem));
    }
}
