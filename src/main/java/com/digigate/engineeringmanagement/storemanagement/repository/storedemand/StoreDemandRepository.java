package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreDemandProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StoreDemandRepository extends AbstractRepository<StoreDemand> {
    boolean existsByAircraftIdAndIsActiveTrue(Long id);

    List<StoreDemandProjection> findAircraftByIdIn(Set<Long> collect);

    Optional<StoreDemand> findByIdAndWorkFlowActionId(Long id, Long finalAction);

    @Query(value = "SELECT sd.* FROM store_demands sd " +
            "INNER JOIN procurement_requisitions pr ON pr.store_demand_id = sd.id " +
            "INNER JOIN quote_requests qr ON qr.requisition_id = pr.id " +
            "INNER JOIN vendor_quotations vq ON vq.rfq_id = qr.id " +
            "WHERE vq.part_order_id = :id", nativeQuery = true)
    StoreDemand findStoreDemandByPartOrderId(@Param("id") Long id);

    @Query(value = "SELECT COUNT(id) AS total, yr, mnth\n" +
            "FROM (\n" +
            "    SELECT id, YEAR(created_at) AS yr, MONTH(created_at) AS mnth\n" +
            "    FROM store_demands ss\n" +
            "    WHERE ss.voucher_no NOT LIKE 'INVISIBLE%' \n" +
            "      AND ss.created_at >= DATEADD(MONTH, :month, GETDATE())\n" +
            ") AS subquery\n" +
            "GROUP BY yr, mnth\n" +
            "ORDER BY yr , mnth ", nativeQuery = true)
    List<DashboardProjection> getStoreDemandDataForMonths(@Param("month") Integer month);
}
