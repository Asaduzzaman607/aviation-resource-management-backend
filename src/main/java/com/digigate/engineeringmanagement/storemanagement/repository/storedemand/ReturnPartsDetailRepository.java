package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RpdProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnPartDetailsProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.WorkOrderComponent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReturnPartsDetailRepository extends AbstractRepository<ReturnPartsDetail> {

    boolean existsByRemovedFromAircraftIdAndIsActiveTrue(Long id);

    @Query("select new com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.WorkOrderComponent(" +
            "r.airport.name," +
            "r.removalDate" +
            ") from ReturnPartsDetail r where r.id = :unserviceableId")
    Optional<WorkOrderComponent> findByUnserviceableId(Long unserviceableId);

    List<ReturnPartsDetail> findByStoreReturnPartIdInAndIsActiveTrue(Set<Long> collectionOfPartIds);

    @Query("select new com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto(" +
            "sps.id," +
            "s.id, " +
            "s.serialNumber," +
            "sps.price " +
            ") " +
            "from ReturnPartsDetail ab " +
            "inner join StorePartSerial sps on (sps.id in (ab.removedPartSerialId, ab.installedPartSerialId))" +
            "inner join Serial s on sps.serialId = s.id WHERE ab.id = :id order by sps.id asc"
    )
    List<StoreSerialIdNoDto> findRemovedAndInstalledSerialNo(@Param("id") Long id);

    Optional<ReturnPartsDetail> findByStoreReturnPartId(Long id);

    StoreReturnPartDetailsProjection findReturnPartsDetailById(Long id);

    List<StoreReturnPartDetailsProjection> findReturnPartsDetailByIdIn(Set<Long> ids);

    List<RpdProjection> findByRemovedPartSerialIdOrderByCreatedAtDesc(Long id);

    List<StoreReturnPartDetailsProjection> findReturnPartsDetailByRemovedPartSerialIdIn(Set<Long> ids);

    @Query("select rp from ReturnPartsDetail rp " +
            "inner join StoreReturnPart sr on sr.id = rp.storeReturnPartId " +
            "inner join StorePartSerial sp on sp.id = rp.removedPartSerialId " +
            "where rp.isActive = true and sr.partId = :partId and sp.serialId = :serialId")
    ReturnPartsDetail findByRemovedPartIdAndSerialId(@Param("partId") Long removePartId,
                                                     @Param("serialId") Long planningSerialId);
}
