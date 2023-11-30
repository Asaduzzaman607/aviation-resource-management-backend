package com.digigate.engineeringmanagement.storemanagement.repository.partsreceive;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.InwardPartOrderProjection;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockInwardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreStockInwardRepository extends AbstractRepository<StoreStockInward> {
    List<StoreStockInwardProjection> findByIdIn(Set<Long> idList);
    StoreStockInwardProjection findStoreStockInwardById(Long id);


    @Query("select new com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection(" +
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
            "from StoreStockInward ssi " +
            "inner join PartsInvoice pi on pi.id = ssi.invoiceId " +
            "inner join PartOrder po on po.id = pi.partOrderId " +
            "inner join PartOrderItem poi on poi.partOrderId = po.id " +
            "inner join VendorQuotationInvoiceDetail viqd on viqd.id = poi.iqItemId " +
            "left join UnitMeasurement viqdum on (viqdum.id is null or viqdum.id = viqd.uomId) " +
            "left join Part alt on (alt.id is null or alt.id = viqd.alternatePartId) " +
            "left join UnitMeasurement altum on (altum.id is null or altum.id = alt.unitMeasurementId) " +
            "inner join ProcurementRequisitionItem pri on pri.id = viqd.requisitionItemId " +
            "inner join StoreDemandItem sdi on sdi.id = pri.demandItemId " +
            "inner join Part p on p.id = sdi.partId " +
            "inner join UnitMeasurement um on um.id = sdi.uomId " +
            "where viqd.isActive = true " +
            "and ssi.id = :id")
            List<IqItemProjection> getPartsFromPartOrder(@Param("id") Long id);

    @Query("select po.id as id, po.orderNo as orderNo, ssi.id as inwardId " +
            "from StoreStockInward ssi " +
            "inner join PartsInvoice pi on pi.id = ssi.invoiceId " +
            "inner join PartOrder po on po.id = pi.partOrderId " +
            "where ssi.id in (:inwardIdSet)"
    )
    List<InwardPartOrderProjection> getPoFromStockInwardIdIn(Set<Long> inwardIdSet);
}
