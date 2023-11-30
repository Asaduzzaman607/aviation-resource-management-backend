package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderProjection;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AlternatePartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialViewModelLite;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailablityViewModelLite;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreReturnPartRepository extends AbstractRepository<StoreReturnPart> {
    Set<StoreReturnPart> findByIdIn(Set<Long> ids);

    List<StoreReturnPart> findByStoreReturnIdInAndIsActiveTrue(Set<Long> demandIds);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            "s.partId, " +
            "s.serialNumber " +
            ")" +
            "from StoreReturnPart as srp " +
            "inner join ReturnPartsDetail as rpd on srp.id = rpd.storeReturnPartId " +
            "inner join StorePartSerial as sps on rpd.id = sps.serialId " +
            "inner join Serial as s on sps.serialId = s.id where srp.id = :id "
    )
    List<PartViewModelLite> findLotNumber(@Param("id") Long id);

    @Query("select new com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialViewModelLite(" +
            "sps.selfLife, " +
            "sps.rackLife, " +
            "sps.grnNo " +
            ")" +
            "from StoreReturnPart as srp " +
            "inner join ReturnPartsDetail as rpd on srp.id = rpd.storeReturnPartId " +
            "inner join StorePartSerial as sps on sps.id = rpd.id where srp.id = :id "
    )
    List<StorePartSerialViewModelLite> findGrnShelfAndRackLife(@Param("id") Long id);

    @Query("select new com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailablityViewModelLite(" +
            "spa.id, " +
            "spa.otherLocation " +
            ")" +
            "from StoreReturnPart as srp " +
            "inner join ReturnPartsDetail as rpd on srp.id = rpd.storeReturnPartId " +
            "inner join StorePartSerial as sps on rpd.id = sps.serialId " +
            "inner join StorePartAvailability as spa on sps.serialId = spa.id where srp.id = :id "
    )
    List<StorePartAvailablityViewModelLite> findOtherLocationById(@Param("id") Long id);

    @Query(value = "select po.order_no as orderNo, po.id from return_parts_details rpd " +
            "inner join store_inspections si on rpd.removed_part_serial_id = si.part_serial_id " +
            "inner join store_stock_inwards ssi on si.stock_inward_id = ssi.id " +
            "inner join parts_invoices pin on ssi.invoice_id = pin.id " +
            "inner join part_orders po on pin.prats_order_id = po.id where rpd.id = :id " +
            "order by si.created_at desc OFFSET 0 ROWS FETCH FIRST 1 ROWS ONLY ", nativeQuery = true
    )
    PartOrderProjection findPartOrderNoById(@Param("id") Long id);

    @Query(value = "select pa.part_no as partNo, pa.id from store_return_parts srp " +
            "inner join parts p on srp.part_id = p.id " +
            "inner join alternate_parts ap on p.id = ap.part_id " +
            "inner join parts as pa on pa.id = ap.alternate_part_id where srp.id = :id ", nativeQuery = true
    )
    Set<AlternatePartProjection> findAlternatePartNameById(@Param("id") Long id);

    @Query(value = "SELECT COUNT(srp.id) as total, MONTH(GETDATE()) as mnth, \n" +
            "CASE WHEN sr.is_serviceable = 1 THEN 'SERVICEABLE' ELSE 'UNSERVICEABLE' END as partStatus\n" +
            "FROM store_return_parts as srp \n" +
            "INNER JOIN store_return as sr ON sr.id = srp.return_id \n" +
            "GROUP BY sr.is_serviceable", nativeQuery = true)
    List<DashboardProjection> getStoreReturnPartDataForLastOneMonth(@Param("month") Integer month);

    Integer countByStoreReturnIdAndIsInactiveFalse(Long storeReturnId);
    boolean existsByInstallPartUomIdAndInstalledPartIdAndIsActiveTrue(Long uomId, Long installPartId);
    boolean existsByRemovedPartUomIdAndPartIdAndIsActiveTrue(Long uomId, Long removePartId);
}
