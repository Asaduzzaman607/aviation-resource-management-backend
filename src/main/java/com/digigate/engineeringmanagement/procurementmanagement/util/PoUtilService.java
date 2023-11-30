package com.digigate.engineeringmanagement.procurementmanagement.util;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VqdQuantity;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VqdQuantityDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PoUtilService {
    private final VendorQuotationService vendorQuotationService;

    public PoUtilService(VendorQuotationService vendorQuotationService) {
        this.vendorQuotationService = vendorQuotationService;
    }

    public List<VendorQuotationInvoiceDetail> updateQuantity(Map<Long, VendorQuotationInvoiceDetail> vqDetailMap,
                                                             VqdQuantityDto vqdQuantityDto){
        return vqdQuantityDto.getVqdQuantities().stream().map(vqdQuantity -> populateQuantity(
                vqdQuantity, vqDetailMap.get(vqdQuantity.getId()), vqdQuantityDto)).collect(Collectors.toList());
    }

    private VendorQuotationInvoiceDetail populateQuantity(VqdQuantity vqdQuantity,
                                                          VendorQuotationInvoiceDetail vqDetail,
                                                          VqdQuantityDto vqdQuantityDto) {
        if(Objects.equals(vqDetail.getVendorQuotationInvoiceId(), vqdQuantityDto.getVqId())){
            VendorQuotation vendorQuotation = vendorQuotationService.findByIdUnfiltered(vqDetail.getVendorQuotationInvoiceId());
            if(Objects.equals(vendorQuotation.getPartOrderId(), vqdQuantityDto.getPoId())){
                vqDetail.setPartQuantity(vqdQuantity.getQuantity());
                vqDetail.setIsDiscount(vqdQuantity.getIsDiscount());
                vqDetail.setVendorSerials(vqdQuantity.getVendorSerials());
                return vqDetail;
            }else {
                throw EngineeringManagementServerException.badRequest(ErrorId.PART_ORDER_NOT_MATCHED);
            }
        } else {
            throw EngineeringManagementServerException.badRequest(ErrorId.VENDOR_QUOTATION_NOT_MATCHED);
        }
    }
}
