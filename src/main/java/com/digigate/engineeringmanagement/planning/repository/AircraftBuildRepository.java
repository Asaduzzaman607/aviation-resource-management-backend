package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.entity.AircraftBuild;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.payload.response.AcPartSerialResponse;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftBuildPartSerialSearchViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftBuildViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnserviceablePartPositionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * AircraftBuild Repository
 *
 * @author Masud Rana
 */
@Repository
public interface AircraftBuildRepository extends AbstractRepository<AircraftBuild> {
    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftBuildViewModel(" +
            "ab.id, " +
            "ab.aircraftId, " +
            "ab.aircraft.aircraftName, " +
            "ab.higherModel.modelName, " +
            "ab.model.modelName, " +
            "ab.higherSerial.serialNumber, " +
            "ab.serial.serialNumber, " +
            "ab.isActive, " +
            "ab.positionId, " +
            "ab.part.partNo," +
            "ab.higherPart.partNo ) " +
            "FROM AircraftBuild ab " +
            "WHERE " +
            "(:aircraftId is null OR ab.aircraftId =:aircraftId) AND " +
            "(:modelName is null OR ab.model.modelName like %:modelName%) AND " +
            "(:partNo is null OR ab.part.partNo like :partNo% or replace(ab.part.partNo,'-','') LIKE :partNo%) AND " +
            "(:higherModelName is null OR ab.higherModel.modelName like  %:higherModelName%) AND " +
            "(:higherPartNo is null OR ab.higherPart.partNo like :higherPartNo% " +
            "or replace(ab.higherPart.partNo,'-','') LIKE :higherPartNo%) AND" +
            "(:isActive is null OR ab.isActive =:isActive)")
    Page<AircraftBuildViewModel> findBySearchCriteria(@Param("aircraftId") Long aircraftId,
                                                      @Param("modelName") String modelName,
                                                      @Param("partNo") String partNo,
                                                      @Param("higherModelName") String higherModelName,
                                                      @Param("higherPartNo") String higherPartNo,
                                                      @Param("isActive") Boolean isActive,
                                                      Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcBuildInHourInfo(" +
            "a.model.modelType," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.tsnHour," +
            "a.tsnCycle," +
            "a.position.name," +
            "a.outDate" +
            ") from AircraftBuild  a where a.aircraftId = :aircraftId and a.model.modelType in :modelTypes" +
            " and (a.outDate is null or a.outDate >= :fromDate) and (a.attachDate <= :toDate ) " +
            " order by a.model.modelType asc")
    List<AcBuildInHourInfo> findInHourInfoByModelTypes(Long aircraftId, EnumSet<ModelType> modelTypes,
                                                       LocalDate fromDate, LocalDate toDate);

    @Query("select ab.id from AircraftBuild ab where " +
            "ab.partId=:partId " +
            "and ab.serialId = :serialId " +
            "and ab.isActive = true")
    Optional<Long> findByPartIdAndSerialId(Long partId, Long serialId);

    @Query("select ab.id from AircraftBuild ab where " +
            "ab.aircraftId = :aircraftId and " +
            "ab.partId = :partId " +
            "and ab.positionId = :positionId " +
            "and ab.isActive = true")
    Optional<Long> findByPartIdAndPositionId(Long aircraftId, Long partId, Long positionId);

    AircraftBuild findTopByPartIdAndSerialId(Long partId, Long serialId);

    @Query("SELECT new com.digigate.engineeringmanagement.storemanagement.payload.projection.UnserviceablePartPositionDto(" +
            "a.partId," +
            "a.position.name) " +
            "from AircraftBuild a " +
            "where a.partId in :partIds")
    List<UnserviceablePartPositionDto> findPositionsByPartIds(List<Long> partIds);

    @Query("SELECT new com.digigate.engineeringmanagement.storemanagement.payload.projection.UnserviceablePartPositionDto(" +
            "a.partId," +
            "a.position.name) " +
            "from AircraftBuild a " +
            "where a.partId = :partId")
    UnserviceablePartPositionDto findByPartId(Long partId);

    @Query("select ab from AircraftBuild  ab where ab.aircraftId = :aircraftId and ab.isActive = true")
    List<AircraftBuild> findByAircraftId(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcBuildPartSerialResponse(" +
            "ab.partId," +
            "ab.serial.serialNumber" +
            ") from AircraftBuild  ab where ab.isActive = true")
    List<AcBuildPartSerialResponse> findAllExistingPartSerialList();

    @Query("SELECT new " +
            " com.digigate.engineeringmanagement.planning.payload.response.AircraftBuildPartSerialSearchViewModel(" +
            "ab.isTsnAvailable, " +
            "ab.tsnHour, " +
            "ab.tsnCycle, " +
            "ab.tsoHour, " +
            "ab.tsoCycle, " +
            "ab.isOverhauled, " +
            "ab.tslsvHour, " +
            "ab.tslsvCycle, " +
            "ab.isShopVisited, " +
            "ab.aircraftInHour," +
            "ab.aircraftInCycle," +
            "ab.aircraftOutHour," +
            "ab.aircraftOutCycle" +
            ")" +
            "FROM AircraftBuild ab where ab.createdAt = " +
            "(select max(abmx.createdAt) from AircraftBuild abmx where abmx.partId = :partId and " +
            "abmx.serialId = :serialId and abmx.isActive = false)")
    Optional<AircraftBuildPartSerialSearchViewModel> findByPartIdAndSerialIdIsActiveFalse(Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcSerialResponse(" +
            "ab.part.partNo," +
            "ab.serialId," +
            "ab.serial.serialNumber" +
            ") from AircraftBuild ab " +
            " left join Position p on p.id = ab.positionId " +
            "where ab.partId = :partId and ab.modelId = :modelId")
    Set<AcSerialResponse> findAcSerialResponseByPartIdAndModelId(Long partId, Long modelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcPartResponse(" +
            "ab.partId," +
            "ab.part.partNo" +
            ") from AircraftBuild ab where ab.modelId = :modelId ")
    Set<AcPartResponse> findAcPartResponseByModelId(Long modelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcPartSerialResponse(" +
            "ab.id," +
            "ab.partId," +
            "ab.part.partNo," +
            "ab.serialId," +
            "ab.serial.serialNumber," +
            "p.id," +
            "p.name" +
            ") from AircraftBuild ab " +
            " left join Position p on p.id = ab.positionId " +
            "where ab.aircraftId = :aircraftId and ab.modelId = :modelId " +
            "and ab.isActive = true")
    Set<AcPartSerialResponse> findAcPartSerialResponseByModel(Long aircraftId, Long modelId);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcPartSerialResponse(" +
            "ab.id," +
            "ab.higherPartId," +
            "ab.higherPart.partNo," +
            "ab.higherSerialId," +
            "ab.higherSerial.serialNumber" +
            ") from AircraftBuild ab where ab.aircraftId = :aircraftId " +
            "and ab.higherModelId = :modelId and ab.isActive = true")
    Set<AcPartSerialResponse> findAcPartSerialResponseByHigherModel(Long aircraftId, Long modelId);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PropellerReportQueryViewModel(" +
            "p.description, " +
            "p.partNo, " +
            "ab.serial.serialNumber, " +
            "ldnd.doneDate," +
            "ab.aircraftInHour, " +
            "ab.tsnHour, " +
            "ab.tsoHour, " +
            "ldnd.dueDate, " +
            "ldnd.dueHour," +
            "ldnd.remainingHour, " +
            "ldnd.estimatedDueDate," +
            "ab.isTsnAvailable, " +
            "ab.isOverhauled " +
            ") from AircraftBuild ab join Ldnd ldnd on ldnd.serialId = ab.serialId and " +
            "ldnd.partId = ab.partId  and ldnd.isActive = true " +
            "join Part p on p.id = ab.partId " +
            "where ( (ab.higherPartId = :partId and ab.higherSerialId = :serialId ) or " +
            " (ab.partId = :partId and ab.serialId = :serialId ) ) " +
            "and ab.aircraftId = :aircraftId and ab.isActive = true order by p.description asc ")
    List<PropellerReportQueryViewModel> getPropellerReport(Long aircraftId, Long partId, Long serialId);


    @Query("select " +
            "new com.digigate.engineeringmanagement.planning.payload.response.PropellerACBuildIdAndPositionViewModel(" +
            "ab.id, ab.position.name" +
            ") from AircraftBuild ab where ab.aircraftId = :aircraftId and ab.model.modelType = :modelType and " +
            "(ab.position is not null) and ab.isActive = true ")
    List<PropellerACBuildIdAndPositionViewModel> getPropellerPositionNameByAircraftId(Long aircraftId,
                                                                                      ModelType modelType);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.OCCMQueryViewModel( " +
            "ab.id, " +
            "al.name, " +
            "ab.part.description, " +
            "ab.part.partNo, " +
            "ab.serial.serialNumber, " +
            "p, " +
            "ab.attachDate, " +
            "ab.aircraftInHour, " +
            "ab.aircraftInCycle, " +
            "ab.isTsnAvailable, " +
            "ab.tsnHour, " +
            "ab.tsnCycle, " +
            "ab.isOverhauled, " +
            "ab.tsoHour, " +
            "ab.tsoCycle, " +
            "ab.isShopVisited, " +
            "ab.tslsvHour, " +
            "ab.tslsvCycle, " +
            "ab.part.countFactor) " +
            "from AircraftBuild ab left join Position p on p.id = ab.positionId " +
            "left join AircraftLocation al on al.id = ab.locationId " +
            "where ab.aircraftId = :aircraftId and ab.model.modelType in :modelTypes and ab.isActive = true " +
            "and (:description is null or ab.part.description LIKE  :description%) " +
            "and (:partNumber is null or ab.part.partNo like :partNumber% or replace(ab.part.partNo,'-','') LIKE :partNumber%) " +
            "and (:serialNumber is null or ab.serial.serialNumber LIKE :serialNumber% " +
            " or (replace(ab.serial.serialNumber,'-','') LIKE :serialNumber%)  ) " +
            "and (:installationDate is null or ab.attachDate = :installationDate)" +
            "and (:installationFH is null or ab.aircraftInHour = :installationFH)" +
            "and (:installationFC is null or ab.aircraftInCycle = :installationFC) order by al.name asc ")
    Page<OCCMQueryViewModel> findAircraftBuildByAircraftId(Long aircraftId, String description,
                                                           String partNumber, String serialNumber, LocalDate installationDate,
                                                           Double installationFH, Integer installationFC,
                                                           Set<ModelType> modelTypes,
                                                           Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.EngineViewModel(" +
            "ab.id," +
            "ab.position.name," +
            "ab.partId," +
            "ab.serialId," +
            "ab.serial.serialNumber" +
            ") " +
            "from AircraftBuild ab " +
            "where ab.position.name is not null and ab.aircraftId = :aircraftId and ab.model.modelType = :modelType " +
            "and ab.isActive = true")
    List<EngineViewModel> findAircraftEnginesByAircraftId(Long aircraftId, ModelType modelType);

    @Query("select ab from AircraftBuild ab " +
            "where " +
            "ab.higherSerialId = :higherSerialId and ab.higherPartId = :higherPartId " +
            "and (ab.model.modelType = :tmmModelType or ab.model.modelType = :rgbModelType) and ab.isActive = true")
    List<AircraftBuild> findAllTmmAndRgbByHigherSerial(Long higherSerialId, Long higherPartId, ModelType tmmModelType,
                                                       ModelType rgbModelType);

    @Query("select ab from AircraftBuild ab " +
            "where " +
            "ab.higherSerialId = :higherSerialId and ab.higherPartId = :higherPartId " +
            "and (ab.model.modelType = :tmmModelType or ab.model.modelType = :rgbModelType) and ab.isActive = false")
    List<AircraftBuild> findAllInactivateTmmAndRgbByHigherSerialAndPart(Long higherSerialId, Long higherPartId, ModelType tmmModelType,
                                                                        ModelType rgbModelType);

    @Query("select ab from AircraftBuild ab " +
            "where " +
            "ab.higherSerialId = :serialId and ab.higherPartId = :partId " +
            "and ab.model.modelType = :engineLlp and ab.isActive = true")
    List<AircraftBuild> findAllEngineLlpParts(Long serialId, Long partId, ModelType engineLlp);

    @Query("select ab from AircraftBuild ab " +
            "where " +
            "ab.higherSerialId = :serialId and ab.higherPartId = :partId " +
            "and ab.model.modelType = :engineLlp and ab.isActive = false")
    List<AircraftBuild> findAllInactivateEngineLlpParts(Long serialId, Long partId, ModelType engineLlp);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcComponentHistory(" +
            "a.aircraftName," +
            "p.name," +
            "ab.inRefMessage," +
            "ab.outRefMessage," +
            "ab.attachDate," +
            "ab.outDate," +
            "ab.tsnHour," +
            "ab.tsnCycle," +
            "ab.tsoHour," +
            "ab.tsoCycle," +
            "ab.aircraftInHour," +
            "ab.aircraftInCycle," +
            "ab.aircraftOutHour," +
            "ab.aircraftOutCycle," +
            "ab.removalReason," +
            "ab.higherSerial.serialNumber" +
            ")" +
            " from AircraftBuild  ab " +
            "join Aircraft a on a.id = ab.aircraftId " +
            "left join Position p on p.id = ab.positionId " +
            "where ab.partId = :partId and ab.serialId = :serialId order by ab.id asc")
    List<AcComponentHistory> getComponentHistoryList(Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AircraftEngineDetailsViewModel(" +
            "ab.aircraftInHour," +
            "ab.aircraftInCycle," +
            "ab.model.modelName," +
            "ab.aircraft.aircraftName," +
            "ab.aircraft.updatedAt," +
            "ab.aircraft.airFrameTotalTime," +
            "ab.aircraft.airframeTotalCycle," +
            "ab.tsnHour," +
            "ab.tsnCycle" +
            ") from AircraftBuild ab " +
            "where " +
            "ab.serialId = :serialId and ab.partId = :partId and ab.aircraftId=:aircraftId " +
            "and ab.isActive = true")
    Optional<AircraftEngineDetailsViewModel> findAircraftEngineDetailsByPartAndSerialId(Long serialId, Long partId, Long aircraftId);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcEnginePartSerialData(" +
            "ab.partId," +
            "ab.serial.serialNumber" +
            ") from AircraftBuild ab " +
            "where " +
            "( (ab.higherSerialId = :serialId and ab.higherPartId = :partId) or " +
            " (ab.serialId = :serialId and ab.partId = :partId ) ) " +
            " and ab.isActive = true")
    Set<AcEnginePartSerialData> findEnginePartSerials(Long serialId, Long partId);

    @Query("select ab.id from  AircraftBuild ab where ab.aircraftId = :aircraftId and ab.modelId = :modelId " +
            "and ab.higherModelId = :higherModelId and ab.locationId = :locationId and ab.positionId = :positionId " +
            "and ab.isActive = true")
    List<Long> checkDuplicate(Long aircraftId, Long modelId, Long higherModelId, Long locationId, Long positionId);

    @Query("select a from AircraftBuild a where a.aircraftId = :aircraftId and a.higherPartId = :higherPartId and " +
            " a.higherSerialId = :higherSerialId and a.isActive = true")
    List<AircraftBuild> findChildAcBuild(Long aircraftId,
                                         Long higherPartId,
                                         Long higherSerialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LandingGearViewModel(" +
            "p.description, " +
            "p.partNo, " +
            "a.serial.serialNumber, " +
            "a.attachDate, " +
            "a.aircraftInHour, " +
            "a.aircraftInCycle, " +
            "a.tsnCycle, " +
            "a.tsnHour, " +
            "a.tsoCycle, " +
            "a.tsoHour, " +
            "l.dueDate, " +
            "l.dueCycle, " +
            "l.estimatedDueDate, " +
            "p.lifeLimit, " +
            "p.lifeLimitUnit, " +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle, " +
            "l.intervalType " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on p.id = a.partId " +
            " where a.aircraftId = :aircraftId and " +
            " a.higherPartId = :higherPartId and a.higherSerialId = :higherSerialId " +
            " and l.isActive = true and a.isActive = true ")
    List<LandingGearViewModel> getAllMlgBuild(Long aircraftId, Long higherPartId, Long higherSerialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LandingGearViewModel(" +
            "p.description," +
            "p.partNo," +
            "a.serial.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "a.tsoCycle," +
            "a.tsoHour," +
            "l.dueDate," +
            "l.dueCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit," +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle, " +
            "l.intervalType " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on p.id = a.partId " +
            " where a.aircraftId = :aircraftId and " +
            " a.partId = :partId and a.serialId = :serialId " +
            " and l.isActive = true and a.isActive = true ")
    List<LandingGearViewModel> getMlgOwnBuilds(Long aircraftId, Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MlgPartSerialViewModel(" +
            "ab.part.description," +
            "ab.partId," +
            "ab.serialId" +
            ")" +
            " from AircraftBuild ab where ab.aircraftId = :aircraftId and " +
            " ab.model.modelType = :modelType and ab.isActive = true and ab.part.isActive = true ")
    List<MlgPartSerialViewModel> findByModelTypeAndAircraft(Long aircraftId, ModelType modelType);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LandingGearViewModel(" +
            "p.description," +
            "p.partNo," +
            "a.serial.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "a.tsoCycle," +
            "a.tsoHour," +
            "l.dueDate," +
            "l.dueCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit," +
            "a.model.modelType," +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle," +
            "l.intervalType  " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on a.partId = p.id " +
            " where a.aircraftId = :aircraftId and a.model.modelType in :ngModel" +
            " and l.isActive = true and a.isActive = true")
    List<LandingGearViewModel> getAllNgReport(Long aircraftId, Set<ModelType> ngModel);

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.ApuStatusModel(" +
            "p.description," +
            "p.partNo," +
            "s.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "l.dueDate," +
            "l.dueHour," +
            "l.dueCycle," +
            "l.remainingHour," +
            "l.remainingCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit" +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Part p on a.partId = p.id " +
            " join Serial s on s.id = a.serialId " +
            " where a.aircraftId = :aircraftId" +
            " and l.isApuControl = true" +
            " and l.isActive = true and a.isActive = true")
    List<ApuStatusModel> getApuStatusReport(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ApuStatusAircraftInfo(" +
            "p.partNo, " +
            "s.serialNumber, " +
            "a.aircraftInHour, " +
            "a.aircraftInCycle, " +
            "a.tsnHour, " +
            "a.tsnCycle," +
            "p.countFactor " +
            ") " +
            " from AircraftBuild a " +
            " join Part p on a.partId = p.id " +
            " join Serial s on s.id = a.serialId " +
            " join Model m on a.modelId = m.id " +
            " where a.aircraftId = :aircraftId and m.modelType = :apuModel " +
            " and a.isActive = true ")
    List<ApuStatusAircraftInfo> getApuStatusAircraftInfo(Long aircraftId, ModelType apuModel);

    @Query("select ab from AircraftBuild ab where ab.partId = :partId and ab.serialId = :serialId and ab.isActive = false")
    Optional<AircraftBuild> findInactiveAcBuildByPartSerial(Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.EngineViewModel(" +
            "ab.id," +
            "ab.position.name," +
            "ab.partId," +
            "ab.serialId," +
            "ab.serial.serialNumber" +
            ") " +
            "from AircraftBuild ab " +
            "where ab.position.name is not null and ab.aircraftId = :aircraftId and ab.model.modelType = :modelType " +
            "and ab.isActive = false")
    List<EngineViewModel> findInactivateAircraftEnginesByAircraftId(Long aircraftId, ModelType modelType);

    @Query("select ab from AircraftBuild ab where ab.id = :aircraftId and ab.isActive = false")
    AircraftBuild findByInactivateId(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.RemovedLandingGearViewModel(" +
            "p.description," +
            "p.partNo," +
            "a.serial.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.aircraftOutHour," +
            "a.aircraftOutCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "a.tsoCycle," +
            "a.tsoHour," +
            "l.dueDate," +
            "l.dueCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit," +
            "a.outDate," +
            "a.model.modelType, " +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle," +
            "l.intervalType  " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on a.partId = p.id " +
            " where a.aircraftId = :aircraftId and a.model.modelType in :ngModel" +
            " and l.isActive = false and a.isActive = false")
    List<RemovedLandingGearViewModel> getRemovedNlgReport(Long aircraftId, Set<ModelType> ngModel);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MlgPartSerialViewModel(" +
            "ab.part.description," +
            "ab.partId," +
            "ab.serialId" +
            ")" +
            " from AircraftBuild ab where ab.aircraftId = :aircraftId and " +
            " ab.model.modelType = :modelType and ab.isActive = false ")
    List<MlgPartSerialViewModel> findByRemovedModelTypeAndAircraft(Long aircraftId, ModelType modelType);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.RemovedLandingGearViewModel(" +
            "p.description," +
            "p.partNo," +
            "a.serial.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.aircraftOutHour," +
            "a.aircraftOutCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "a.tsoCycle," +
            "a.tsoHour," +
            "l.dueDate," +
            "l.dueCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit," +
            "a.outDate," +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle, " +
            "l.intervalType " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on p.id = a.partId " +
            " where a.aircraftId = :aircraftId and " +
            " a.partId = :partId and a.serialId = :serialId " +
            " and l.isActive = false and a.isActive = false ")
    List<RemovedLandingGearViewModel> getRemovedMlgOwnBuilds(Long aircraftId, Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.RemovedLandingGearViewModel(" +
            "p.description," +
            "p.partNo," +
            "a.serial.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.aircraftOutHour," +
            "a.aircraftOutCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "a.tsoCycle," +
            "a.tsoHour," +
            "l.dueDate," +
            "l.dueCycle," +
            "l.estimatedDueDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit," +
            "a.outDate," +
            "t.intervalDay, " +
            "t.intervalCycle, " +
            "t.thresholdDay, " +
            "t.thresholdCycle, " +
            "l.intervalType " +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Task t on l.taskId = t.id " +
            " join Part p on p.id = a.partId " +
            " where a.aircraftId = :aircraftId and " +
            " a.higherPartId = :higherPartId and a.higherSerialId = :higherSerialId " +
            " and l.isActive = false and a.isActive = false ")
    List<RemovedLandingGearViewModel> getAllRemovedMlgBuild(Long aircraftId, Long higherPartId, Long higherSerialId);

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.ApuRemovedStatusModel(" +
            "p.description," +
            "p.partNo," +
            "s.serialNumber," +
            "a.attachDate," +
            "a.aircraftInHour," +
            "a.aircraftInCycle," +
            "a.aircraftOutHour," +
            "a.aircraftOutCycle," +
            "a.tsnCycle," +
            "a.tsnHour," +
            "l.dueDate," +
            "l.dueHour," +
            "l.dueCycle," +
            "l.remainingHour," +
            "l.remainingCycle," +
            "l.estimatedDueDate," +
            "a.outDate," +
            "p.lifeLimit," +
            "p.lifeLimitUnit" +
            ")  " +
            " from AircraftBuild a " +
            " join Ldnd l on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId " +
            " join Part p on a.partId = p.id " +
            " join Serial s on s.id = a.serialId " +
            " where a.aircraftId = :aircraftId" +
            " and l.isApuControl = true" +
            " and l.isActive = false and a.isActive = false")
    List<ApuRemovedStatusModel> getApuRemovedStatusReport(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ApuRemovedStatusAircraftInfo(" +
            "p.partNo, " +
            "s.serialNumber, " +
            "a.aircraftInHour, " +
            "a.aircraftInCycle, " +
            "a.aircraftOutHour, " +
            "a.aircraftOutCycle, " +
            "a.outDate, " +
            "a.tsnHour, " +
            "a.tsnCycle," +
            "p.countFactor " +
            ") " +
            " from AircraftBuild a " +
            " join Part p on a.partId = p.id " +
            " join Serial s on s.id = a.serialId " +
            " join Model m on a.modelId = m.id " +
            " where a.aircraftId = :aircraftId and m.modelType = :apuModel " +
            " and a.isActive = false ")
    List<ApuRemovedStatusAircraftInfo> getApuRemovedStatusAircraftInfo(Long aircraftId, ModelType apuModel);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftBuildExcelViewModel(" +
            "a.aircraftName, " +
            "m.modelName, " +
            "p.partNo, " +
            "mo.modelName, " +
            "pa.partNo, " +
            "s.serialNumber, " +
            "po.name " +
            ")" +
            "FROM AircraftBuild ab " +
            "JOIN ab.aircraft a " +
            "JOIN ab.higherModel m " +
            "JOIN ab.higherPart p " +
            "JOIN ab.model mo " +
            "JOIN ab.part pa " +
            "JOIN ab.serial s " +
            "LEFT JOIN ab.position po " +
            "WHERE ab.isActive = true " +
            "AND a.isActive = true " +
            "AND m.isActive = true " +
            "AND p.isActive = true " +
            "AND mo.isActive = true " +
            "AND pa.isActive = true " +
            "AND s.isActive = true ")
    List<AircraftBuildExcelViewModel> findAllIsActiveAircraftBuild();
}

