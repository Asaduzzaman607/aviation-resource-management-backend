package com.digigate.engineeringmanagement.logistic.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.logistic.entity.DutyFee;
import com.digigate.engineeringmanagement.logistic.payload.response.DutyPartViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DutyFeeRepository extends AbstractRepository<DutyFee> {


    @Query(value = "Select new com.digigate.engineeringmanagement.logistic.payload.response.DutyPartViewModel(" +
            "p.id, " +
            "p.partNo " +
            ") " +
            "from DutyFee as df " +
            "LEFT join PartsInvoiceItem as pii on df.partsInvoiceItemId = pii.id " +
            "LEFT JOIN PartOrderItem lpoi ON  lpoi.id = pii.poItemId " +
            "LEFT JOIN VendorQuotationInvoiceDetail lvqid ON lvqid.id = lpoi.iqItemId " +
            "LEFT JOIN PartOrderItem ppoi ON  ppoi.id = lvqid.poItemId " +
            "LEFT JOIN VendorQuotationInvoiceDetail pvqid ON  pvqid.id = ppoi.iqItemId " +
            "LEFT JOIN ProcurementRequisitionItem  pri ON pri.id = pvqid.requisitionItemId " +
            "LEFT JOIN StoreDemandItem sdi ON  sdi.id = pri.demandItemId " +
            "LEFT JOIN Part p ON sdi.partId = p.id " +
            "  where df.id = :id ")
    DutyPartViewModel findByDutyFeeId(Long id);

}
