package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionPoProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PartOrderRepository extends AbstractRepository<PartOrder> {

    List<PartOrder> findByCsDetailIdIn(Set<Long> idSet);

    List<PartOrderProjection> findByCsDetailIdInAndIsActiveTrue(Set<Long> idSet);
    List<PartOrder> findPartOrderByIdIn(Set<Long> ids);

    @Query(value = "select new com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionPoProjection(" +
            "pr.id, " +
            "pr.voucherNo," +
            "vq.partOrderId) " +
            "from ProcurementRequisition pr " +
            "inner join QuoteRequest qr on pr.id = qr.requisitionId " +
            "inner join VendorQuotation vq on qr.id = vq.quoteRequestId " +
            "where vq.partOrderId in (:poIds)")
    List<RequisitionPoProjection> findRequisitionByPartOrderIdIn(@Param("poIds") Set<Long> partOrderIds);
}
