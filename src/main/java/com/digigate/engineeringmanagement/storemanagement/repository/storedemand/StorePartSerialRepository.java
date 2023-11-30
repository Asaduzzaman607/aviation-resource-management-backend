package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartSerialGrnProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.UnserviceableComponentListViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StorePartSerialRepository extends AbstractRepository<StorePartSerial> {

    Optional<StorePartSerial> findByStorePartAvailabilityIdAndSerialId(Long storePartAvailabilityId, Long serialId);
    Optional<StorePartSerial> findByStorePartAvailabilityPartIdAndSerialIdAndIsActiveTrue(Long storePartAvailabilityId, Long planingSerialId);

    List<StorePartSerialProjection> findStorePartSerialByIdIn(Set<Long> ids);

    void deleteBySerialIdAndStorePartAvailabilityId(Long serialId, Long availId);

    List<StorePartSerialProjection> findBySerialSerialNumberIn(List<String> serialList);

    Set<StorePartSerial> findAllByIdInAndStorePartAvailabilityIdAndPartStatusAndIsActiveTrue(Set<Long> ids,
                                                                                             Long storePartsAvailabilityId, PartStatus partStatus);

    List<PartSerialGrnProjection> findDataByIdIn(Set<Long> ids);

    Optional<StorePartSerial> findBySerialIdAndStorePartAvailabilityPartIdAndIsActiveTrue(Long removedPartSerialId, Long partId);

    boolean existsBySerialIdAndIsActiveTrue(Long serialId);

    boolean existsByCurrencyIdAndIsActiveTrue(Long id);
    List<StorePartSerial> findAllByStorePartAvailabilityIdInAndIsActiveTrue(Set<Long> availIds);

    @Query("SELECT new com.digigate.engineeringmanagement.storemanagement.payload.response.UnserviceableComponentListViewModel(" +
            "p.partNo, " +
            "p.description, " +
            "MAX(s.serialNumber), " +
            "a.aircraftName, " +
            "MAX(rp.removalDate), " +
            "rp.reasonRemoved, " +
            "u.login, " +
            "us.login, " +
            "sr.storeLocation, " +
            "sr.remarks " +
            ") " +
            "FROM StorePartSerial as srp " +
            "JOIN ReturnPartsDetail rp ON rp.removedPartSerialId = srp.id " +
            "JOIN StoreReturnPart sp ON sp.id = rp.storeReturnPartId " +
            "JOIN Aircraft a ON a.id = rp.removedFromAircraftId " +
            "JOIN Serial s ON s.id = srp.serialId " +
            "JOIN Part p ON p.id = sp.partId " +
            "JOIN StoreReturn sr ON sp.storeReturnId = sr.id " +
            "JOIN ApprovalStatus approvalStatus ON approvalStatus.parentId = sr.id " +
            "JOIN User us ON us.id = approvalStatus.updatedBy " +
            "JOIN User u ON u.id = sr.submittedById " +
            "WHERE srp.partStatus = 'UNSERVICEABLE' " +
            "AND approvalStatus.workFlowActionId = 3L " +
            "AND approvalStatus.approvalStatusType = 'STORE_RETURN' " +
            "GROUP BY p.partNo, p.description, srp.id, a.aircraftName, rp.reasonRemoved, u.login, us.login, sr.storeLocation, sr.remarks " +
            "ORDER BY MAX(rp.removalDate) DESC")
    Page<UnserviceableComponentListViewModel> getAllUnserviceableComponentList(Pageable pageable);


    @Query("SELECT new com.digigate.engineeringmanagement.storemanagement.payload.response.UnserviceableComponentListViewModel(" +
            "p.partNo, " +
            "p.description, " +
            "MAX(s.serialNumber), " +
            "a.aircraftName, " +
            "MAX(rp.removalDate), " +
            "rp.reasonRemoved, " +
            "u.login, " +
            "us.login, " +
            "sr.storeLocation, " +
            "sr.remarks " +
            ") " +
            "FROM StorePartSerial as srp " +
            "JOIN ReturnPartsDetail rp ON rp.removedPartSerialId = srp.id " +
            "JOIN StoreReturnPart sp ON sp.id = rp.storeReturnPartId " +
            "JOIN Aircraft a ON a.id = rp.removedFromAircraftId " +
            "JOIN Serial s ON s.id = srp.serialId " +
            "JOIN Part p ON p.id = sp.partId " +
            "JOIN StoreReturn sr ON sp.storeReturnId = sr.id " +
            "JOIN ApprovalStatus approvalStatus ON approvalStatus.parentId = sr.id " +
            "JOIN User us ON us.id = approvalStatus.updatedBy " +
            "JOIN User u ON u.id = sr.submittedById " +
            "WHERE srp.partStatus = 'UNSERVICEABLE' " +
            "AND approvalStatus.workFlowActionId = 3L " +
            "AND approvalStatus.approvalStatusType = 'STORE_RETURN' " +
            "GROUP BY p.partNo, p.description, srp.id, a.aircraftName, rp.reasonRemoved, u.login, us.login, sr.storeLocation, sr.remarks " +
            "ORDER BY MAX(rp.removalDate) DESC")
    List<UnserviceableComponentListViewModel> getAllUnserviceableComponentListAllData();
    List<StorePartSerial> findBySelfLifeBefore(LocalDate currentDate);
}
