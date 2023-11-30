package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnPartService;
import org.springframework.stereotype.Service;

@Service
public class PartWiseUomFacadeService {
    private final StoreDemandDetailsService storeDemandDetailsService;
    private final StoreReturnPartService storeReturnPartService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;

    public PartWiseUomFacadeService(StoreDemandDetailsService storeDemandDetailsService,
                                    StoreReturnPartService storeReturnPartService,
                                    VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService) {
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.storeReturnPartService = storeReturnPartService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
    }

    public boolean existsByUomIdAndIsActiveTrueInStoreDemandItem(Long uomId, Long partId) {
        return storeDemandDetailsService.existsByUomIdAndPartIdAndIsActiveTrue(uomId, partId);
    }

    public boolean existsByInstalledUomIdAndIsActiveTrueInStoreReturnPart(Long uomId, Long partId) {
        return storeReturnPartService.existsByInstallPartUomIdAndIsActiveTrue(uomId, partId);
    }

    public boolean existsByRemovedUomIdAndIsActiveTrueInStoreReturnPart(Long uomId, Long partId) {
        return storeReturnPartService.existsByRemovedPartUomIdAndIsActiveTrue(uomId, partId);
    }

    public boolean existsByUomIdAndIsActiveTrueInVendorQuotation(Long uomId, Long partId) {
        return vendorQuotationInvoiceDetailService.existsByUomIdAndIsActiveTrue(uomId, partId);
    }
}
