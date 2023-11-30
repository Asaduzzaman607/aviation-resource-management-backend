package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.payload.response.PartViewModel;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturn;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.OfficeIdNameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreReturnRepository extends AbstractRepository<StoreReturn> {
    boolean existsByLocationIdAndIsActiveTrue(Long id);

    Page<StoreReturn> findAllByIsActiveAndVoucherNoContains
            (Boolean isActive, String query, Pageable pageable);
    Page<StoreReturn> findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains
            (Boolean isActive, Long approvedId, String query, Pageable pageable);

    boolean existsByDepartmentIdAndIsActiveTrue(Long id);

    boolean existsByStoreStockRoomIdAndIsActiveTrue(Long id);

    List<StoreReturnProjection> findByIdIn(Set<Long> ids);

    Page<StoreReturn> findAllByIsRejectedTrueAndVoucherNoContains(String query, Pageable pageable);

    Page<StoreReturn>findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains
            (Boolean isActive, Set<Long> workflowIds, String query, Pageable pageable);

    @Query(value = "Select new com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnViewModel(" +
            "sr.id, sr.voucherNo,sr.isInternalDept," +
            " sr.aircraftRegistration, sr.isActive," +
            " sr.departmentId, dep.name," +
            "sr.vendorId, ven.name, " +
            " loc.id, loc.name, " +
            " room.id, room.stockRoomNo," +
            " si.id, si.voucherNo," +
            " sr.submittedById, usr.login," +
            " sr.userId, sr.remarks, sr.storeLocation" +
            ")" +
            " from StoreReturn sr inner join AircraftLocation loc on sr.locationId = loc.id " +
            "left join StoreStockRoom room on sr.storeStockRoomId = room.id left join User usr on usr.id = sr.submittedById" +
            " left join Department dep  on sr.departmentId = dep.id left join Vendor ven on ven.id = sr.vendorId " +
            " left join StoreIssue si on sr.storeIssueId = si.id " +
            " where sr.id in :returnIds")
    List<StoreReturnViewModel> findJoinedProjectionByIdIn(@Param("returnIds") Set<Long> returnIds);

    @Query("select new com.digigate.engineeringmanagement.storemanagement.payload.response.OfficeIdNameDto(" +
            "p.id," +
            "p.code " +
            ")" +
            "FROM StoreReturn sr " +
            "inner join StoreStockRoom r on sr.storeStockRoomId = r.id " +
            "inner join Office p on r.officeId = p.id where sr.id = :id"
    )
    OfficeIdNameDto findOfficeCodeDependingNoRoom(@Param("id") Long id);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            "p.id, " +
            "p.partNo " +
            ")" +
            "from StoreReturn as sr " +
            "inner join StoreReturnPart as srp on sr.id = srp.storeReturnId " +
            "inner join ReturnPartsDetail as rpd on srp.id = rpd.storeReturnPartId " +
            "inner join StorePartSerial as sps on rpd.id = sps.serialId " +
            "inner join Serial as s on sps.id = s.partId " +
            "inner join Part as p on s.id = p.id where sr.id = :id "
    )
    List<PartViewModel> findPartOrderName(@Param("id") Long id);
}
