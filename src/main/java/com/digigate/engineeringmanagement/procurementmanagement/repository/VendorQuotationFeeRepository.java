package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceFee;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorQuotationFeeRepository extends AbstractRepository<VendorQuotationInvoiceFee> {
    List<VendorQuotationInvoiceFee> findByVendorQuotationInvoiceIdAndVendorRequestType(Long id, VendorRequestType quotation);
}