package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProcurementRequisitionRepository extends AbstractRepository<ProcurementRequisition> {
    boolean existsByStoreDemandIdAndIsActiveTrue(Long id);

    RequisitionProjection findProcurementRequisitionById(Long id);

    List<RequisitionProjection> findProcurementRequisitionByIdIn(Set<Long> idSet);

    List<RequisitionProjection> findByStoreDemandIdInAndIsActiveTrue(Set<Long> demandIds);

    @Query(value = "SELECT pr.* FROM procurement_requisitions pr " +
            "INNER JOIN quote_requests qr ON qr.requisition_id = pr.id " +
            "INNER JOIN vendor_quotations vq ON vq.rfq_id = qr.id " +
            "WHERE vq.part_order_id = :id", nativeQuery = true)
    ProcurementRequisition findProcurementRequisitionByPartOrderId(@Param("id") Long id);

    @Query(value = "SELECT COUNT(id) AS total, yr, mnth\n" +
            "FROM (\n" +
            "    SELECT id, YEAR(created_at) AS yr, MONTH(created_at) AS mnth\n" +
            "    FROM procurement_requisitions pr\n" +
            "    WHERE pr.voucher_no NOT LIKE 'INVISIBLE%' \n" +
            "      AND pr.created_at >= DATEADD(MONTH, :month, GETDATE())\n" +
            ") AS subquery\n" +
            "GROUP BY yr, mnth\n" +
            "ORDER BY yr , mnth  ", nativeQuery = true)
    List<DashboardProjection> getProcurementRequisitionDataForMonths(@Param("month") Integer month);
}
