package com.digigate.engineeringmanagement.storeinspector.repository.storeinspector;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspection;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.PlanningSiProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.StoreInspectionProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreInspectionRepository extends AbstractRepository<StoreInspection> {
     StoreInspectionProjection findByReturnPartsDetailId(Long id);
     Optional<PlanningSiProjection> findByPartIdAndSerialIdAndIsActiveTrue(Long partId, Long serialId);
     @Query(value = "select v.id as id, v.name as name " +
             "from StoreInspection as si " +
             "inner join StoreStockInward as ssi on si.stockInwardId = ssi.id " +
             "inner join PartsInvoice as pi on ssi.invoiceId = pi.id " +
             "inner join PartOrder as po on pi.partOrderId = po.id " +
             "inner join VendorQuotation as vq on po.id = vq.partOrderId " +
             "inner join QuoteRequestVendor as qrv on vq.quoteRequestVendorId = qrv.id " +
             "inner join Vendor as v on qrv.vendorId = v.id " +
             "where si.partId = :partId and si.partSerialId = :partSerialId "
     )
     VendorProjection findVendorByPartIdAndSerialId(@Param("partId") Long partId, @Param("partSerialId") Long partSerialId );
}