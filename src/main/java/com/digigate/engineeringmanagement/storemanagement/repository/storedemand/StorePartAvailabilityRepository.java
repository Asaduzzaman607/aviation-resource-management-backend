package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartAvailabilityProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardViewProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailabilitySearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StorePartAvailabilityRepository extends AbstractRepository<StorePartAvailability> {

    boolean existsByRoomIdAndIsActiveTrue(Long id);

    boolean existsByOfficeIdAndIsActiveTrue(Long id);

    boolean existsByRackIdAndIsActiveTrue(Long id);

    boolean existsByRackRowIdAndIsActiveTrue(Long id);

    boolean existsByPartIdAndIsActiveTrue(Long id);

    boolean existsByRackRowBinIdAndIsActiveTrue(Long id);

    @Modifying
    @Query("update StorePartAvailability spa set spa.quantity = (spa.quantity + :value ) where spa.id = :id")
    void updateStorePartAvailabilityQuantityById(@Param("id") Long id, @Param("value") Integer value);

    List<StorePartAvailability> findByPartIdInAndIsActiveTrue(Set<Long> partIdSet);
    List<StorePartAvailabilityProjection> findStorePartAvailabilitiesByPartIdIn(Set<Long> partIdSet);

    Optional<StorePartAvailability> findByPartId(Long id);

    Optional<StorePartAvailability> findByPartIdAndIsActiveTrue(Long partId);

    @Query(value = "SELECT p.part_no AS partNo, p.id AS partId, p.description AS nomenclature,sps.rack_life as shelfLife,sps.grn_no as grnNo,\n" +
            "sps.created_at AS inspDate, sps.quantity AS qty, sps.self_life AS expireDate,\n" +
            "a.name AS acType, s.serial_number AS serialNo, um.code AS uom\n" +
            "            FROM store_parts_availabilities AS spa\n" +
            "            INNER JOIN parts AS p ON spa.part_id = p.id\n" +
            "            INNER JOIN models AS m ON p.model_id = m.id\n" +
            "            INNER JOIN aircraft_models AS a ON m.aircraft_model_id = a.id\n" +
            "            INNER JOIN store_parts_serials AS sps ON (sps.self_life IS NOT NULL AND sps.self_life\n" +
            "\t\t\tBETWEEN :startDate AND :endDate) AND sps.avail_id = spa.id\n" +
            "            INNER JOIN serials AS s ON sps.serial_id = s.id\n" +
            "            INNER JOIN unit_measurements AS um ON sps.uom_id = um.id",
            nativeQuery = true)
    List<DashboardViewProjection> findPartInfoAndIsActiveTrue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query(value = "select spa.id as id,p.id as partId,p.part_no as partNo,spa.quantity as quantity,spa.demand_quantity as demandQuantity," +
            "  spa.issued_quantity as issuedQuantity,spa.requisition_quantity as requisitionQuantity ,um.id as uomId,um.code as uomCode," +
            "  ss.qty as uomWiseQuantity,o.id as officeId, o.code as officeCode, spa.min_stock as minStock, spa.max_stock as maxStock" +
            " from store_parts_availabilities as spa " +
            "  left join offices as o on spa.office_id = o.id" +
            "  left join parts as p on spa.part_id = p.id" +
            "  left join (select count(case when sps.parent_type = 'DEMAND' or sps.parent_type = 'RETURN' then sps.avail_id end)" +
            "  as qty,sps.avail_id, sps.uom_id, sps.part_status" +
            "  from store_parts_serials as sps where sps.part_status = 'SERVICEABLE' group by sps.uom_id, sps.avail_id, sps.part_status)" +
            "  as ss on ss.avail_id = spa.id" +
            "  left join unit_measurements as um on um.id= ss.uom_id" +
            "  where" +
            "  (:isActive is null or spa.is_active = :isActive )" +
            "  and (:partNo is null or p.part_no = :partNo)", nativeQuery = true)
    List<StorePartAvailabilitySearchProjection> findAlLGroupByUom(@Param("isActive") Boolean isActive, @Param("partNo") String partNo);








}
