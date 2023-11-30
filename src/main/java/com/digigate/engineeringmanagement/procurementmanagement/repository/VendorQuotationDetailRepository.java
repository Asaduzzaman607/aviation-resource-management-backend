package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.VqDetailProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorQuotationDetailRepository extends AbstractRepository<VendorQuotationInvoiceDetail> {

    List<VendorQuotationInvoiceDetail> findByVendorQuotationInvoiceIdAndVendorRequestType(Long id, VendorRequestType vqType);

    List<VqDetailProjection> findVendorQuotationInvoiceDetailByVendorQuotationInvoiceIdAndVendorRequestType(Long id, VendorRequestType vendorRequestType);

    List<VendorQuotationInvoiceDetail> findByIdIn(List<Long> ids);

    @Query("SELECT NEW com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection(" +
            "viqd.id, " +
            "viqd.partQuantity, " +
            "viqd.vendorSerials, " +
            "pri.requisitionQuantity, " +
            "viqdum.id, " +
            "viqdum.code, " +
            "p.id, " +
            "p.partNo, " +
            "p.description, " +
            "um.id, " +
            "um.code, " +
            "alt.id, " +
            "alt.partNo, " +
            "alt.description, " +
            "altum.id, " +
            "altum.code, " +
            "sdi.id, " +
            "pri.priority) " +
            "FROM VendorQuotationInvoiceDetail viqd " +
            "LEFT JOIN UnitMeasurement viqdum ON (viqdum.id is null OR viqdum.id = viqd.uomId) " +
            "LEFT JOIN Part alt ON (alt.id IS NULL OR alt.id = viqd.alternatePartId) " +
            "LEFT JOIN UnitMeasurement altum ON (altum.id IS NULL OR altum.id = alt.unitMeasurementId) " +
            "INNER JOIN ProcurementRequisitionItem pri ON pri.id = viqd.requisitionItemId " +
            "INNER JOIN StoreDemandItem sdi ON sdi.id = pri.demandItemId " +
            "INNER JOIN Part p ON p.id = sdi.partId " +
            "INNER JOIN UnitMeasurement um ON um.id = sdi.uomId " +
            "WHERE viqd.isActive = true " +
            "AND viqd.vendorQuotationInvoiceId IN (:ids) " +
            "AND (:type IS NULL OR viqd.vendorRequestType = :type)")
    List<IqItemProjection> findByVendorQuotationInvoiceIdInAndVendorRequestType(@Param("ids") Set<Long> ids,
                                                                                @Param("type") VendorRequestType type);

    @Query("SELECT NEW com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection(" +
            "viqd.id, " +
            "viqd.partQuantity, " +
            "viqd.vendorSerials, " +
            "pri.requisitionQuantity, " +
            "viqdum.id, " +
            "viqdum.code, " +
            "p.id, " +
            "p.partNo, " +
            "p.description, " +
            "um.id, " +
            "um.code, " +
            "alt.id, " +
            "alt.partNo, " +
            "alt.description, " +
            "altum.id, " +
            "altum.code, " +
            "sdi.id, " +
            "pri.priority, " +
            "viqd.unitPrice, " +
            "viqd.condition, " +
            "viqd.leadTime," +
            "viqd.currencyId," +
            "c.code) " +
            "FROM VendorQuotationInvoiceDetail viqd " +
            "LEFT JOIN UnitMeasurement viqdum ON (viqdum.id is null OR viqdum.id = viqd.uomId) " +
            "LEFT JOIN Part alt ON (alt.id IS NULL OR alt.id = viqd.alternatePartId) " +
            "LEFT JOIN UnitMeasurement altum ON (altum.id IS NULL OR altum.id = alt.unitMeasurementId)" +
            "LEFT JOIN Currency c ON c.id = viqd.currencyId " +
            "INNER JOIN ProcurementRequisitionItem pri ON pri.id = viqd.requisitionItemId " +
            "INNER JOIN StoreDemandItem sdi ON sdi.id = pri.demandItemId " +
            "INNER JOIN Part p ON p.id = sdi.partId " +
            "INNER JOIN UnitMeasurement um ON um.id = p.unitMeasurementId " +
            "WHERE viqd.isActive = true " +
            "AND viqd.id IN (:ids) " +
            "AND (:type IS NULL OR viqd.vendorRequestType = :type)")
    List<IqItemProjection> findByIdInAndVendorRequestType(@Param("ids") Set<Long> ids,
                                                          @Param("type") VendorRequestType type);

    @Query("SELECT NEW com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection(" +
            "viqd.id, " +
            "viqd.partQuantity, " +
            "viqd.vendorSerials, " +
            "pri.requisitionQuantity, " +
            "pviqdum.id, " +
            "pviqdum.code, " +
            "p.id, " +
            "p.partNo, " +
            "p.description, " +
            "um.id, " +
            "um.code, " +
            "alt.id, " +
            "alt.partNo, " +
            "alt.description, " +
            "altum.id, " +
            "altum.code, " +
            "sdi.id, " +
            "pri.priority) " +
            "FROM VendorQuotationInvoiceDetail viqd " +
            "INNER JOIN PartOrderItem poi ON poi.id = viqd.poItemId " +
            "INNER JOIN VendorQuotationInvoiceDetail pviqd ON pviqd.id = poi.iqItemId " +
            "LEFT JOIN UnitMeasurement pviqdum ON (pviqdum.id is null OR pviqdum.id = viqd.uomId) " +
            "LEFT JOIN Part alt ON (alt.id IS NULL OR alt.id = pviqd.alternatePartId) " +
            "LEFT JOIN UnitMeasurement altum ON (altum.id IS NULL OR altum.id = alt.unitMeasurementId) " +
            "INNER JOIN ProcurementRequisitionItem pri ON pri.id = pviqd.requisitionItemId " +
            "INNER JOIN StoreDemandItem sdi ON sdi.id = pri.demandItemId " +
            "INNER JOIN Part p ON p.id = sdi.partId " +
            "INNER JOIN UnitMeasurement um ON um.id = sdi.uomId " +
            "WHERE viqd.isActive = true " +
            "AND viqd.vendorQuotationInvoiceId IN (:ids) " +
            "AND (:type IS NULL OR viqd.vendorRequestType = :type)")
    List<IqItemProjection> findByVendorQuotationInvoiceIdInAndVendorRequestTypeForLogistic(@Param("ids") Set<Long> ids,
                                                                                           @Param("type") VendorRequestType type);

    @Query("SELECT NEW com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection(" +
            "viqd.id, " +
            "viqd.partQuantity, " +
            "viqd.vendorSerials, " +
            "pri.requisitionQuantity, " +
            "pviqdum.id, " +
            "pviqdum.code, " +
            "p.id, " +
            "p.partNo, " +
            "p.description, " +
            "um.id, " +
            "um.code, " +
            "alt.id, " +
            "alt.partNo, " +
            "alt.description, " +
            "altum.id, " +
            "altum.code, " +
            "sdi.id, " +
            "pri.priority, " +
            "viqd.unitPrice, " +
            "viqd.condition, " +
            "viqd.leadTime," +
            "viqd.currencyId," +
            "c.code) " +
            "FROM VendorQuotationInvoiceDetail viqd " +
            "INNER JOIN PartOrderItem poi ON poi.id = viqd.poItemId " +
            "INNER JOIN VendorQuotationInvoiceDetail pviqd ON pviqd.id = poi.iqItemId " +
            "LEFT JOIN UnitMeasurement pviqdum ON (pviqdum.id is null OR pviqdum.id = viqd.uomId) " +
            "LEFT JOIN Part alt ON (alt.id IS NULL OR alt.id = pviqd.alternatePartId) " +
            "LEFT JOIN UnitMeasurement altum ON (altum.id IS NULL OR altum.id = alt.unitMeasurementId)" +
            "LEFT JOIN Currency c ON c.id = viqd.currencyId " +
            "INNER JOIN ProcurementRequisitionItem pri ON pri.id = pviqd.requisitionItemId " +
            "INNER JOIN StoreDemandItem sdi ON sdi.id = pri.demandItemId " +
            "INNER JOIN Part p ON p.id = sdi.partId " +
            "INNER JOIN UnitMeasurement um ON um.id = p.unitMeasurementId " +
            "WHERE viqd.isActive = true " +
            "AND viqd.id IN (:ids) " +
            "AND (:type IS NULL OR viqd.vendorRequestType = :type)")
    List<IqItemProjection> findByIdInAndVendorRequestTypeForLogistic(@Param("ids") Set<Long> ids,
                                                                     @Param("type") VendorRequestType type);

    boolean existsByCurrencyIdAndIsActiveTrue(Long currencyId);
    boolean existsByUomIdAndRequisitionItemDemandItemPartIdAndIsActiveTrue(Long uomId, Long partId);

    List<VendorQuotationInvoiceDetail> findByVendorQuotationInvoiceIdIn(Set<Long> quotationIds);
}
