package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AlternatePartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Part repository
 *
 * @author ashinisingha
 */
@Repository
public interface PartRepository extends AbstractRepository<Part> {
    @Query("select part from Part part where part.model.id = :modelId  and part.partNo = :partNo ")
    Optional<Part> findByModelIdAndPartNo(Long modelId, String partNo);

    @Query("select part from Part part where part.model.aircraftModelId = :aircraftModelId and part.partNo = :partNo " +
            "and part.isActive=true")
    Optional<Part> findByPartNoAndAcModelId(String partNo, Long aircraftModelId);

    @Query("select part from Part part where part.partNo = :partNo")
    Optional<Part> findPartByPartNo(String partNo);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            " part.id, part.partNo " +
            ") from Part part where part.model.id = :modelId and part.isActive = true")
    List<PartViewModelLite> getAllByModelId(@Param("modelId") Long modelId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            "part.id, part.modelId, m.modelName, " +
            "part.partNo, part.description, part.classification,um.id, um.code, part.isActive" +
            ") " +
            " from Part part " +
            "left join Model m on part.modelId = m.id " +
            "left join UnitMeasurement um on um.id = part.unitMeasurementId " +
            "where " +
            " (:modelId is null or :modelId = m.id) " +
            " and (:partNo is null or  part.partNo LIKE :partNo% or replace(part.partNo,'-','') LIKE :partNo%   ) " +
            " and (:partClassification is null or  part.classification = :partClassification) " +
            " and ( :isActive is null or part.isActive = :isActive )  ")
    Page<PartViewModel> findPartBySearchCriteria(
            @Param("modelId") Long modelId,
            @Param("partNo") String partNo,
            @Param("partClassification") PartClassification partClassification,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    List<PartProjection> findPartByIdIn(Set<Long> idSet);


    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            "part.id, part.partNo " +
            ") " +
            " from Part part " +
            "left join StorePartAvailability spa on part.id = spa.partId " +
            "where " +
            "(spa.id is null) " +
            " and (:partNo is null or  part.partNo LIKE :partNo%)")
    Page<PartViewModel> findPartBySearchCriteria(
            @Param("partNo") String partNo,
            Pageable pageable
    );

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            "part.id, aps.partNo, aps.id) " +
            "from Part part " +
            "join part.alternatePartSet aps " +
            "where (:partIds is null or part.id in :partIds) " +
            "and part.isActive = true")
    List<PartViewModel> findAlternatePartByPartId(@Param("partIds") Set<Long> partIds);

    @Query("select p from Part p where p.modelId in :modelIds and p.isActive = true")
    Set<Part> findAllParteByModelIdIn(Set<Long> modelIds);

    List<Part> findPartByModelIdInAndIsActiveTrue(Set<Long> modelIds);

    List<PartProjection> findByIdIn(List<Long> partIds);

    Optional<PartProjection> findPartById(Long partId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            "part.id, part.partNo) " +
            "from Part part where " +
            "(:partClassification is null or  part.classification = :partClassification) " +
            "and part.isActive = true")
    List<PartViewModel> findAllPartByPartType(@Param("partClassification") PartClassification partClassification);

    @Query("SELECT part " +
            "from Part part where part.classification = :partClassification " +
            "and part.isActive = true")
    List<Part> findAllPartsByPartType(@Param("partClassification") PartClassification partClassification);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            " part.id, part.partNo " +
            ") from Part part where part.partNo like :partNo% and part.isActive = true")
    List<PartViewModelLite> searchByPartNo(String partNo);

    @Query("select p from Part p " +
            "join Model m on m.id = p.modelId " +
            "join AircraftModel am on am.id = m.aircraftModelId " +
            "where m.aircraftModelId=:aircraftModelId and p.isActive=true")
    List<Part> findAllByAircraftModelId(Long aircraftModelId);

    Part findByPartNoAndIsActiveTrue(String partNo);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            " p.id, p.partNo " +
            ") " +
            "FROM Part p where p.partNo = :partNo"
    )
    PartViewModelLite getByPartNo(String partNo);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            " p.id, p.partNo " +
            ") " +
            "FROM Model m inner join Part p on m.id = p.modelId and p.partNo = :partNo " +
            "and m.aircraftModelId = :aircraftModelId "
    )
    PartViewModelLite findByPartNoAndAircraftModelId(String partNo, Long aircraftModelId);


    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            " p.id, p.partNo " +
            ") " +
            "from Part p inner join Model m ON p.modelId = m.id OR (p.modelId is null AND p.classification = 1) " +
            "WHERE m.aircraftModelId = (SELECT a.aircraftModelId FROM Aircraft a WHERE a.id = :id) " +
            "AND (LEN(:partNo) = 0 OR p.partNo LIKE :partNo%) GROUP BY p.id, p.partNo"

    )
    Page<PartViewModel> findPartByUniqueAircraftId(Pageable pageable, Long id, String partNo);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            " p.id, p.partNo " +
            ") " +
            "from Part p JOIN Model m ON p.modelId = m.id " +
            "WHERE p.classification = :partClassification AND m.aircraftModelId = :acType " +
            "AND (LEN(:partNo) = 0 OR p.partNo LIKE :partNo%) GROUP BY p.id, p.partNo"
    )
    Page<PartViewModel> findPartByPartAndAcTypeId(@Param("partClassification") PartClassification partClassification, @Param("acType") Long acType, @Param("partNo") String partNo, Pageable pageable);


    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModel(" +
            " p.id, p.partNo" +
            ") " +
            "FROM Part p WHERE p.modelId is NULL AND p.classification = :partClassification " +
            "AND (LEN(:partNo) = 0 OR p.partNo LIKE :partNo%) GROUP BY p.id, p.partNo"
    )
    Page<PartViewModel> findPartByPartId(@Param("partClassification") PartClassification partClassification, @Param("partNo") String partNo, Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.DashboardPartDemandViewModel(" +
            "p.id, p.partNo, " +
            "sd.id, sd.voucherNo, " +
            "wad.name" +
            ") " +
            "from Part as p " +
            "inner join StoreDemandItem as sdi " +
            "on sdi.partId = p.id " +
            "inner join StoreDemand as sd " +
            "on sdi.storeDemandId = sd.id " +
            "inner join WorkFlowAction as wad " +
            "on sd.workFlowActionId = wad.id " +
            "where p.id = :id " +
            "AND (:voucherNo is null or sd.voucherNo LIKE :voucherNo% )"
    )
    Page<DashboardPartDemandViewModel> findPartDetailsFromDemand(@Param("id") Long id,
                                                                 @Param("voucherNo") String voucherNo,
                                                                 Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.DashboardPartIssueViewModel(" +
            "p.id, p.partNo, " +
            "si.id, si.voucherNo, " +
            "wai.name" +
            ") " +
            "from Part as p " +
            "inner join StoreDemandItem as sdi " +
            "on sdi.partId = p.id " +
            "inner join StoreDemand as  sd " +
            "on sdi.storeDemandId = sd.id " +
            "inner join StoreIssue as si " +
            "on si.storeDemandId = sd.id " +
            "inner join WorkFlowAction as wai " +
            "on si.workFlowActionId = wai.id " +
            "where p.id = :id " +
            "AND (:voucherNo is null or si.voucherNo LIKE :voucherNo% )"
    )
    Page<DashboardPartIssueViewModel> findPartDetailsFromIssue(@Param("id") Long id,
                                                               @Param("voucherNo") String voucherNo,
                                                               Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.DashboardPartRequisitionViewModel(" +
            "p.id, p.partNo, " +
            "pr.id, pr.voucherNo, " +
            "wapr.name" +
            ") " +
            "from Part as p " +
            "inner join StoreDemandItem as sdi " +
            "on sdi.partId = p.id " +
            "inner join StoreDemand as  sd " +
            "on sdi.storeDemandId = sd.id " +
            "inner join ProcurementRequisition as pr " +
            "on pr.storeDemandId = sd.id " +
            "inner join WorkFlowAction as wad " +
            "on sd.workFlowActionId = wad.id " +
            "inner join WorkFlowAction as wapr " +
            "on pr.workFlowActionId = wapr.id " +
            "where p.id = :id " +
            "AND (:voucherNo is null or pr.voucherNo LIKE :voucherNo% )"
    )
    Page<DashboardPartRequisitionViewModel> findPartDetailsFromRequisition(@Param("id") Long id,
                                                                           @Param("voucherNo") String voucherNo,
                                                                           Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.DashboardPartScrapViewModel(" +
            "p.id, p.partNo, " +
            "ss.id, ss.voucherNo, " +
            "wass.name" +
            ") " +
            "from Part as p " +
            "inner join StorePartAvailability as pa " +
            "on pa.partId = p.id " +
            "left join StoreScrapPart as ssp " +
            "on ssp.partId = p.id " +
            "left join StoreScrap as ss " +
            "on ssp.storeScrapId = ss.id " +
            "left join WorkFlowAction as wass " +
            "on ss.workFlowActionId = wass.id " +
            "where p.id = :id " +
            "AND ssp.storeScrapId IS NOT NULL " +
            "AND (:voucherNo is null or ss.voucherNo LIKE :voucherNo% )"
    )
    Page<DashboardPartScrapViewModel> findPartDetailsFromScrap(@Param("id") Long id,
                                                               @Param("voucherNo") String voucherNo,
                                                               Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.DashboardPartAvailabilityViewModel(" +
            "p.id, p.partNo, " +
            "pa.quantity , pa.demandQuantity, " +
            "pa.issuedQuantity, pa.requisitionQuantity, " +
            "uom.code, ofc.code, pa.minStock, pa.maxStock " +
            ") " +
            "from Part as p " +
            "inner join StorePartAvailability as pa " +
            "on pa.partId = p.id " +
            "inner join UnitMeasurement as uom " +
            "on p.unitMeasurementId = uom.id " +
            "left join Office as ofc " +
            "on pa.officeId = ofc.id " +
            "where p.id = :id "
    )
    Page<DashboardPartAvailabilityViewModel> findPartDetailsFromAvailability(@Param("id") Long id,
                                                                             Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.StockCardViewModel(" +
            "p.id, p.partNo, p.description, um.code, " +
            "sps.id, sps.partStatus, sps.parentType, " +
            "r.code, rr.code, rrb.code, o.code, " +
            "spa.otherLocation, am.aircraftModelName, spa.minStock, spa.maxStock, spa.stockRoomId, ssr.code, " +
            "s.id, s.serialNumber, " +
            "spal.id, spal.unitPrice, spal.issuedQty, spal.receivedQty, spal.inStock, spal.voucherNo, spal.createdAt, " +
            "wfa.id, wfa.name, u.login, spal.transactionType, spal.parentId" +
            ") " +
            "from StorePartAvailabilityLog as spal " +
            "inner join StorePartSerial as sps on spal.storePartSerialId = sps.id " +
            "inner join StorePartAvailability as spa on sps.storePartAvailabilityId = spa.id " +
            "left join StoreStockRoom as ssr on ssr.id = spa.stockRoomId " +
            "inner join Serial as s on sps.serialId = s.id " +
            "left join Rack as r on spa.rackId = r.id " +
            "left join Office as o on spa.officeId = o.id " +
            "left join RackRow as rr on spa.rackRowId = rr.id " +
            "left join RackRowBin as rrb on spa.rackRowBinId = rrb.id " +
            "inner join Part as p on spa.partId = p.id " +
            "inner join UnitMeasurement as um on p.unitMeasurementId = um.id " +
            "inner join Model as m on p.modelId = m.id " +
            "inner join AircraftModel as am on m.aircraftModelId = am.id " +
            "left join WorkFlowAction as wfa on spal.workFlowActionId = wfa.id " +
            "left join User as u on spal.submittedById = u.id " +
            "where p.id = :id"
    )
    List<StockCardViewModel> findDataForStockCard(@Param("id") Long id);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.BinCardViewModel(" +
            "p.id,p.partNo,p.classification,spal.grnNo,p.description, " +
            "sps.id, rpd.tso," +
            "r.code,rr.code,rrb.code, o.code, " +
            "spa.issuedQuantity,spa.otherLocation, " +
            "s.id,s.serialNumber,am.aircraftModelName,um.code, " +
            "spal.id,spal.inStock,spal.voucherNo,spal.createdAt," +
            "spal.receivedQty,spa.minStock,sps.rackLife,u.login " +
            ") " +
            "from StorePartAvailabilityLog as spal " +
            "inner join StorePartSerial as sps on spal.storePartSerialId = sps.id " +
            "left join StoreReturnPart as srp on srp.partId = :partId " +
            "left join ReturnPartsDetail as rpd on (srp.id = rpd.storeReturnPartId and sps.id = rpd.removedPartSerialId) " +
            "inner join StorePartAvailability as spa on sps.storePartAvailabilityId = spa.id " +
            "inner join Serial as s on sps.serialId = s.id " +
            "left join Office as o on spa.officeId = o.id " +
            "left join Rack as r on spa.rackId = r.id " +
            "left join RackRow as rr on spa.rackRowId = rr.id " +
            "left join RackRowBin as rrb on spa.rackRowBinId = rrb.id " +
            "inner join Part as p on spa.partId = p.id " +
            "inner join UnitMeasurement as um on p.unitMeasurementId = um.id " +
            "inner join Model as m on p.modelId = m.id " +
            "inner join AircraftModel as am on m.aircraftModelId = am.id " +
            "left join User as u on spal.submittedById = u.id " +
            "where p.id = :partId and sps.id = :serialId"
    )
    List<BinCardViewModel> findDataForBinCard(@Param("partId") Long id, @Param("serialId") Long serialId);

    @Query(value = "select pa.part_no as partNo, pa.id " +
            "from parts p inner join alternate_parts ap on p.id = ap.part_id " +
            "inner join parts as pa on pa.id = ap.alternate_part_id where p.id = :id ", nativeQuery = true
    )
    List<AlternatePartProjection> findAlternatePartById(@Param("id") Long id);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite(" +
            " p.id, p.partNo " +
            ") " +
            "FROM Part p " +
            "Join Model m on m.id = p.modelId " +
            "Join Aircraft a on a.aircraftModelId = m.aircraftModelId " +
            "and a.id = :aircraftId and p.isActive = true ")
    List<PartViewModelLite> getPartListByAcTypeOfAircraftId(Long aircraftId);

    Optional<Part> findById(Long partId);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.PartListViewModel(" +
            "m.modelName, " +
            "p.partNo, " +
            "p.description, " +
            "p.classification, " +
            "um.code)" +
            "FROM Part p " +
            "left Join Model m on m.id = p.modelId " +
            "left Join UnitMeasurement um on um.id = p.unitMeasurementId " +
            "and p.isActive = true " +
            "and (m.isActive = true or m.id is null) " +
            "and (um.isActive = true or um.id is null) ")
    List<PartListViewModel> findAllPartByList();

}

