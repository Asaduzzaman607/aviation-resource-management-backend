package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoiceItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PartInvoiceItemRepository extends AbstractRepository<PartsInvoiceItem> {
    @Query("SELECT New com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionItemProjection(" +
            "pii.id, pii.poItemId, pii.partInvoiceId, pii.paymentMode, pii.remarks, pii.isPartiallyApproved," +
            "pii.paymentCurrencyId, pc.code, pii.approvedQuantity, vqid.partQuantity, vqid.uomId, pviqdum.code, p.id, p.partNo," +
            "p.description, pri.priority, vqid.unitPrice, vqid.condition, vqid.leadTime, vqid.currencyId," +
            "vqidc.code, sd.aircraftId, a.aircraftName, vqid.alternatePartId, ap.partNo, ap.description, v.id, v.name)" +
            "from PartsInvoiceItem pii " +
            "LEFT JOIN Currency pc ON  pc.id = pii.paymentCurrencyId " +
            "LEFT JOIN PartOrderItem poi ON pii.poItemId = poi.id " +
            "LEFT JOIN PartOrder po ON po.id = poi.partOrderId " +
            "LEFT JOIN VendorQuotation vq ON vq.partOrderId = po.id " +
            "LEFT JOIN QuoteRequestVendor qrv ON vq.quoteRequestVendorId = qrv.id " +
            "LEFT JOIN Vendor v ON v.id = qrv.vendorId " +
            "LEFT JOIN VendorQuotationInvoiceDetail vqid ON vqid.id= poi.iqItemId " +
            "LEFT JOIN Part ap ON vqid.alternatePartId = ap.id " +
            "LEFT JOIN UnitMeasurement pviqdum ON pviqdum.id = vqid.uomId " +
            "LEFT JOIN Currency vqidc ON vqidc.id = vqid.currencyId " +
            "LEFT JOIN ProcurementRequisitionItem  pri ON pri.id = vqid.requisitionItemId " +
            "LEFT JOIN StoreDemandItem sdi ON  sdi.id = pri.demandItemId " +
            "LEFT JOIN StoreDemand sd ON  sd.id = sdi.storeDemandId " +
            "LEFT JOIN Aircraft a ON  a.id = sd.aircraftId " +
            "LEFT JOIN Part p ON sdi.partId = p.id " +
            "WHERE  pii.partInvoiceId IN(:invoiceIds)")
    List<RequisitionItemProjection> getProcurementRequisitionInfoByPiIds(@Param("invoiceIds") Set<Long> invoiceIds);

    @Query("SELECT New com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionItemProjection(" +
            "pii.id, pii.poItemId, pii.partInvoiceId, pii.paymentMode, pii.remarks, pii.isPartiallyApproved," +
            "pii.paymentCurrencyId, pc.code, pii.approvedQuantity, lvqid.partQuantity, lvqid.uomId, lviqdum.code, p.id, p.partNo," +
            "p.description, pri.priority, lvqid.unitPrice, lvqid.condition, lvqid.leadTime, lvqid.currencyId," +
            "lvqidc.code, sd.aircraftId, a.aircraftName, pvqid.alternatePartId, ap.partNo, ap.description, lv.id, lv.name)" +
            "from PartsInvoiceItem pii " +
            "LEFT JOIN Currency pc ON  pc.id = pii.paymentCurrencyId " +
            "LEFT JOIN PartOrderItem lpoi ON  lpoi.id = pii.poItemId " +
            "LEFT JOIN PartOrder lpo ON lpo.id = lpoi.partOrderId " +
            "LEFT JOIN VendorQuotation lvq ON lvq.partOrderId = lpo.id " +
            "LEFT JOIN QuoteRequestVendor lqrv ON lvq.quoteRequestVendorId = lqrv.id " +
            "LEFT JOIN Vendor lv ON lv.id = lqrv.vendorId " +
            "LEFT JOIN VendorQuotationInvoiceDetail lvqid ON lvqid.id = lpoi.iqItemId " +
            "LEFT JOIN UnitMeasurement lviqdum ON lviqdum.id = lvqid.uomId " +
            "LEFT JOIN Currency lvqidc ON lvqidc.id = lvqid.currencyId " +
            "LEFT JOIN PartOrderItem ppoi ON  ppoi.id = lvqid.poItemId " +
            "LEFT JOIN VendorQuotationInvoiceDetail pvqid ON  pvqid.id = ppoi.iqItemId " +
            "LEFT JOIN Part ap ON pvqid.alternatePartId = ap.id " +
            "LEFT JOIN ProcurementRequisitionItem  pri ON pri.id = pvqid.requisitionItemId " +
            "LEFT JOIN StoreDemandItem sdi ON  sdi.id = pri.demandItemId " +
            "LEFT JOIN StoreDemand sd ON  sd.id = sdi.storeDemandId " +
            "LEFT JOIN Aircraft a ON  a.id = sd.aircraftId " +
            "LEFT JOIN Part p ON sdi.partId = p.id " +
            "WHERE  pii.partInvoiceId IN(:invoiceIds)")
    List<RequisitionItemProjection> getProcurementRequisitionInfoByPiIdsForLogistic(@Param("invoiceIds") Set<Long> invoiceIds);
}
