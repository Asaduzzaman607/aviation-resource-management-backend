package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreDemandItemProjection;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StoreDemandDetailsRepository extends AbstractRepository<StoreDemandItem> {

    boolean existsByPartIdAndIsActiveTrue(Long id);

    Set<StoreDemandItem> findByStoreDemandIdInAndIsActiveTrue(Set<Long> demandIds);

    List<StoreDemandItemProjection> findByIdInAndIsActiveTrue(Set<Long> ids);

    List<StoreDemandItemProjection> findByStoreDemandIdAndIsActiveTrue(Long id);

    @Query(value = "select sum(sdi.quantity_demanded) as demand from store_demand_items\n" +
            "  as sdi where sdi.store_demand_id IN\n" +
            "  (select sd.id from store_demands as sd\n" +
            "  where sd.is_alive = 'true')", nativeQuery = true)
    Long findTotalAliveCountForDemand();

    @Query(value = "select sum(sdi.quantity_issued) as issued from store_demand_items\n" +
            "  as sdi where sdi.store_demand_id IN\n" +
            "  (select si.store_demand_id from store_issues as si)", nativeQuery = true)
    Long findTotalCountForIssued();

    @Query(value = "select sum(sdi.quantity_requested) as requisition from store_demand_items\n" +
            "  as sdi where sdi.store_demand_id IN\n" +
            "  (select pr.store_demand_id from procurement_requisitions as pr\n" +
            "  where pr.is_alive = 'true')", nativeQuery = true)
    Long findTotalAliveCountForRequisition();

    Optional<StoreDemandItem> findByIdAndIsActiveTrue(Long id);

    List<StoreDemandItem> findByIdIn(Set<Long> ids);

    List<StoreDemandItem> findByStoreDemandId(Long id);
    boolean existsByUomIdAndPartIdAndIsActiveTrue(Long id, Long partId);
}
