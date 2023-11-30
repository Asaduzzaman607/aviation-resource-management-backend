package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.constant.TaskSourceEnum;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.entity.Ldnd;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Ldnd repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface LdndRepository extends AbstractRepository<Ldnd> {

    Optional<Ldnd> findByTaskIdAndPartIdAndSerialIdAndIsActiveTrue(Long taskId, Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskComponentResponse(" +
            "t.id," +
            "t.taskNo," +
            "tp.jobProcedure," +
            "tt.name," +
            "CASE WHEN l.intervalType = :intervalType THEN t.intervalHour ELSE t.thresholdHour END ," +
            "CASE WHEN l.intervalType = :intervalType THEN t.intervalCycle ELSE t.thresholdCycle END ," +
            "CASE WHEN l.intervalType = :intervalType THEN t.intervalDay ELSE t.thresholdDay END ," +
            "t.isApuControl," +
            "l.isActive" +
            ") from Ldnd l " +
            "join Task t on t.id = l.taskId " +
            "left join TaskType tt on tt.id = t.taskTypeId " +
            "left join TaskProcedure tp on tp.id = l.taskProcedureId " +
            "where l.partId = :partId and l.serialId = :serialId order by l.isActive desc")
    List<TaskComponentResponse> findTaskResponseByPartIdAndSerialNo(Long partId, Long serialId, IntervalType intervalType);

    List<Ldnd> findAllByAircraftIdAndPartIdAndSerialIdAndIsActiveTrue(Long aircraftId, Long partId, Long serialId);

    @Query("select l.id from Ldnd l " +
            " join AircraftBuild a on a.partId = l.partId and a.serialId = l.serialId and a.aircraftId = l.aircraftId" +
            " where a.aircraftId = :aircraftId and a.higherPartId = :higherPartId and " +
            " a.higherSerialId = :higherSerialId and l.isActive=true")
    List<Long> findAllChildAcBuildLdnd(Long aircraftId, Long higherPartId, Long higherSerialId);

    @Modifying
    @Query("update Ldnd l set l.isActive=false where l.id in :ids")
    void makeInActive(List<Long> ids);

    @Query("select t.taskProcedure.position from Ldnd t where t.id = :id")
    Optional<Position> findTaskDonePositionById(Long id);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LdndDataViewModel(" +
            "t.id," +
            "t.taskId," +
            "t.task.taskNo," +
            "t.partId," +
            "t.part.partNo," +
            "t.serial.serialNumber," +
            "t.aircraftId," +
            "t.aircraft.aircraftName," +
            "t.estimatedDueDate" +
            ") from Ldnd t " +
            "where t.aircraftId in :aircraftIds " +
            "and t.estimatedDueDate between :fromDate and :toDate and t.isActive = true " +
            "order by t.aircraftId")
    List<LdndDataViewModel> getLdndListByAircraftAndDueDate(List<Long> aircraftIds, LocalDate fromDate, LocalDate toDate);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskReportViewModel(" +
            "t.id," +
            "ae.aircraftId," +
            "t.taskNo," +
            "t.taskSource," +
            "t.manHours," +
            "t.description," +
            "t.isApuControl," +
            "t.intervalDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.thresholdDay," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.taskStatus," +
            "m.modelType," +
            "ae.remark," +
            "ld.id," +
            "p.partNo," +
            "s.serialNumber," +
            "ld.doneDate," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.dueDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ld.estimatedDueDate," +
            "ld.isActive," +
            "tt.name," +
            "pos.name" +
            ") from Task t " +
            "join AircraftEffectivity ae on ae.taskId = t.id and ae.aircraftId = :aircraftId " +
            "left join TaskType tt on tt.id = t.taskTypeId " +
            "left join Model m on m.id = t.modelId " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId and ld.isActive = true " +
            "and ld.taskStatus <> :closeStatus " +
            "left join Part p on p.id = ld.partId " +
            "left join Serial s on s.id = ld.serialId " +
            "left join TaskProcedure tp on tp.id = ld.taskProcedureId " +
            "left join Position pos on pos.id = tp.positionId " +
            "where (ae.effectivityType = 1 or (ae.effectivityType = 0 and t.taskSource = :taskSource)) " +
            "and t.taskStatus <> :closeStatus and t.isActive = true " +
            "and ( (:fromDate is null and :toDate is null) or " +
            " (ld.estimatedDueDate is not null and  ld.estimatedDueDate between :fromDate and :toDate) ) " +
            "and (:intervalCycle is null  or t.intervalCycle = :intervalCycle) " +
            "and (:intervalHour is null  or t.intervalHour = :intervalHour) " +
            "and (:intervalDay is null  or t.intervalDay = :intervalDay) " +
            "and (:thCycle is null  or t.thresholdCycle = :thCycle) " +
            "and (:thHour is null  or t.thresholdHour = :thHour) " +
            "and (:thDay is null  or t.thresholdDay = :thDay)" +
            "and (:ampTaskNo is null  or t.taskNo like %:ampTaskNo%) order by t.taskNo asc ")
    Page<TaskReportViewModel> getLdndReport(Long aircraftId, TaskStatusEnum closeStatus, LocalDate fromDate,
                                            LocalDate toDate, String taskSource,
                                            Integer intervalCycle, Double intervalHour, Integer intervalDay,
                                            Integer thCycle, Double thHour, Integer thDay, String ampTaskNo,
                                            Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskReportViewModel(" +
            "ld.task.taskNo," +
            "td.id," +
            "td.doneDate," +
            "td.doneHour," +
            "td.doneCycle," +
            "td.remark," +
            "td.isActive" +
            ") from TaskDone td " +
            " join Ldnd ld on ld.id = td.ldndId " +
            "where (:aircraftId is null or ld.aircraftId = :aircraftId) " +
            "and (:taskNo is null or ld.task.taskNo like :taskNo%  ) " +
            "and (:remark is null or td.remark like :remark%  ) and td.isActive = :isActive ")
    Page<TaskReportViewModel> searchTaskDone(Long aircraftId, String taskNo, Boolean isActive, String remark,
                                             Pageable pageable);

    List<Ldnd> findAllByIsActiveTrue();

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LdndForTaskViewModel(" +
            "l.task.taskNo," +
            "l.id," +
            "l.task.description," +
            "l.part.partNo," +
            "l.serial.serialNumber" +
            ") from Ldnd l " +
            "where l.taskId in :taskIds and l.aircraftId = :aircraftId and l.isActive = true")
    List<LdndForTaskViewModel> findAllLdndTaskByTaskIdIn(Set<Long> taskIds, Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LdndForTaskViewModel(" +
            "t.taskNo," +
            "l.id," +
            "t.description," +
            "l.part.partNo," +
            "l.serial.serialNumber," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "t.isApuControl" +
            ") from Ldnd l join Task t on t.id = l.taskId " +
            "where l.aircraftId = :aircraftId and l.isActive = true")
    List<LdndForTaskViewModel> findAllLdndTaskByAircraftId(Long aircraftId);

    @Modifying
    @Query("update Ldnd ldnd set ldnd.isActive = false " +
            "where ldnd.partId = :partId and ldnd.serialId = :serialId and ldnd.isActive = true ")
    void updateLdndByPartAndSerialIsActiveFalse(Long partId, Long serialId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AdReportViewModel(" +
            "ld.id," +
            "t.taskNo," +
            "tp.jobProcedure," +
            "t.description," +
            "t.effectiveDate," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "ld.taskStatus," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl" +
            ") from Task t " +
            " join Model m on  m.id = t.modelId " +
            "left join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join TaskProcedure tp on tp.id = ld.taskProcedureId " +
            "where ae.aircraftId = :aircraftId and t.taskSource = :taskSource " +
            "and   m.modelType in :modelTypes " +
            "and (ld.isActive = true or ld.isActive is null) and t.isActive = true  " +
            "and ( (:fromDate is null and :toDate is null) or " +
            " (ld.estimatedDueDate is not null and ld.estimatedDueDate between :fromDate and :toDate) )  " +
            "and (:intervalCycle is null  or t.intervalCycle = :intervalCycle) " +
            "and (:intervalHour is null  or t.intervalHour = :intervalHour) " +
            "and (:intervalDay is null  or t.intervalDay = :intervalDay) " +
            "and (:thCycle is null  or t.thresholdCycle = :thCycle) " +
            "and (:thHour is null  or t.thresholdHour = :thHour) " +
            "and (:thDay is null  or t.thresholdDay = :thDay) order by t.effectiveDate asc")
    Page<AdReportViewModel> getAdReportData(Long aircraftId, EnumSet<ModelType> modelTypes,
                                            LocalDate fromDate, LocalDate toDate, String taskSource,
                                            Integer intervalCycle, Double intervalHour, Integer intervalDay,
                                            Integer thCycle, Double thHour, Integer thDay, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.SbReportViewModel(" +
            "t.taskNo," +
            "t.description," +
            "tp.jobProcedure," +
            "ld.taskStatus," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ae.remark," +
            "t.comment" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join TaskProcedure tp on tp.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.taskProcedureId = tp.id " +
            "and ld.aircraftId = ae.aircraftId and ld.isActive = true " +
            "left join AircraftBuild ab on ab.partId = ld.partId and ab.serialId = ld.serialId and ab.isActive = true " +
            "left join AircraftLocation al on al.id = ab.locationId " +
            "where ae.aircraftId = :aircraftId and t.taskSource = :taskSource " +
            "and (:taskNo is null or t.taskNo = :taskNo)" +
            "and t.isActive = true order by ld.doneDate asc ")
    Page<SbReportViewModel> getSbReport(Long aircraftId, String taskNo, String taskSource, Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.StcAndModViewModel(" +
            "t.comment," +
            "t.taskNo," +
            "t.description," +
            "tp.jobProcedure," +
            "ld.doneDate," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.dueDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "t.taskStatus," +
            "ae.remark" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join TaskProcedure tp on tp.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.taskProcedureId = tp.id " +
            "and ld.aircraftId = ae.aircraftId and ld.isActive = true " +
            "left join AircraftBuild ab on ab.partId = ld.partId and ab.serialId = ld.serialId and ab.isActive = true " +
            "left join AircraftLocation al on al.id = ab.locationId " +
            "where ae.aircraftId = :aircraftId and t.taskSource not in :taskSource " +
            "and ( (:fromDate is null and :toDate is null) or " +
            " (ld.estimatedDueDate is not null and ld.estimatedDueDate between :fromDate and :toDate) )  " +
            "and (:taskNo is null or t.taskNo = :taskNo)" +
            "and  t.isActive = true order by ld.doneDate asc")
    Page<StcAndModViewModel> getStcReport(Long aircraftId, String taskNo, Set<String> taskSource, LocalDate fromDate,
                                          LocalDate toDate, Pageable pageable);

    List<Ldnd> findAllByAircraftIdAndIsActiveTrue(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.HardTimeReportViewModel(" +
            "t.taskNo," +
            "m.modelName," +
            "t.isApuControl," +
            "t.intervalDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.thresholdDay," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.taskStatus," +
            "t.model.modelType," +
            "p.partNo," +
            "s.serialNumber," +
            "ld.doneDate," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.dueDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ld.estimatedDueDate," +
            "tt.name," +
            "al.name," +
            "pos.name" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id and ae.aircraftId = :aircraftId " +
            "join Model m on m.id = t.modelId " +
            "left join TaskType tt on tt.id = t.taskTypeId " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId and ld.isActive = true " +
            "and ld.taskStatus <> :closeStatus " +
            "left join Part p on p.id = ld.partId " +
            "left join Serial s on s.id = ld.serialId " +
            "left join TaskProcedure tp on tp.id = ld.taskProcedureId " +
            "left join Position pos on pos.id = tp.positionId " +
            "left join AircraftBuild ab on ab.partId = ld.partId and ab.serialId = ld.serialId and ab.isActive = true " +
            "left join AircraftLocation al on al.id = ab.aircraftLocation.id " +
            "where ( ae.effectivityType = 1 or (ae.effectivityType = 0 and t.taskSource = :taskSource) )" +
            "and t.taskStatus <> :closeStatus and t.isActive = true and m.modelType in :hardTimeTypes " +
            "and ( (:fromDate is null and :toDate is null) or " +
            " (ld.estimatedDueDate is not null and ld.estimatedDueDate between :fromDate and :toDate) ) " +
            "and (:intervalCycle is null  or t.intervalCycle = :intervalCycle) " +
            "and (:intervalHour is null  or t.intervalHour = :intervalHour) " +
            "and (:intervalDay is null  or t.intervalDay = :intervalDay) " +
            "and (:thCycle is null  or t.thresholdCycle = :thCycle) " +
            "and (:thHour is null  or t.thresholdHour = :thHour) " +
            "and (:model is null or m.modelName LIKE %:model%) " +
            "and (:thDay is null  or t.thresholdDay = :thDay) " +
            "and (:partNo is null  or p.partNo = :partNo)" +
            "and (:serialNumber is null  or s.serialNumber = :serialNumber) " +
            "order by al.name asc ")
    Page<HardTimeReportViewModel> getLdndHardTimeReport(Long aircraftId, TaskStatusEnum closeStatus, Set<ModelType> hardTimeTypes,
                                                        LocalDate fromDate, LocalDate toDate, String taskSource,
                                                        Integer intervalCycle, Double intervalHour, Integer intervalDay,
                                                        Integer thCycle, Double thHour, Integer thDay, String model,String partNo, String serialNumber,
                                                        Pageable pageable);

    Optional<Ldnd> findByIdAndIsActiveTrue(Long ldndId);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AdReportViewModel(" +
            "ld.id," +
            "t.taskNo," +
            "tp.jobProcedure," +
            "t.description," +
            "t.effectiveDate," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "ld.taskStatus," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl," +
            "ld.partId," +
            "s.serialNumber" +
            ") from Task t " +
            " join Model m on  m.id = t.modelId " +
            "left join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join Serial s on s.id = ld.serialId " +
            "left join TaskProcedure tp on tp.id = ld.taskProcedureId " +
            "where ae.aircraftId = :aircraftId and t.taskSource = :taskSource and m.modelType in :modelTypes " +
            "and (ld.isActive = true or ld.isActive is null) and (ld.doneDate <= :date or :date is null " +
            "or ld.doneDate is null)" +
            "and t.isActive = true")
    Page<AdReportViewModel> getAdEngineReportData(Long aircraftId, LocalDate date, EnumSet<ModelType> modelTypes,
                                                  String taskSource, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LdndViewModelForForecast(" +
            "ldnd.id," +
            "ldnd.partId, " +
            "ldnd.taskId, " +
            "ldnd.task.taskNo " +
            ") from Ldnd ldnd " +
            "where ldnd.id in :ldndIds and ldnd.isActive = true")
    List<LdndViewModelForForecast> findByIdIn(Set<Long> ldndIds);

    @Modifying
    @Query("update Ldnd set isActive = false where taskId = :taskId and isActive = true")
    void makeInActiveByTaskId(Long taskId);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.SearchedTasks(" +
            "t.id," +
            "t.taskNo" +
            ") from Task t where t.taskNo like :taskNo% ")
    Page<SearchedTasks> searchTaskListByTaskSourceType(String taskNo, Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskStatusReport(" +
            "ld.id," +
            "t.taskNo," +
            "a.airframeSerial," +
            "t.description," +
            "t.effectiveDate," +
            "t.issueDate," +
            "t.revisionNumber," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join Aircraft a on a.id = ld.aircraftId " +
            "where t.taskNo in :taskNo and t.isActive = true and ld.isActive = true")
    Page<TaskStatusReport> getTaskStatusReportByTaskNo(List<String> taskNo, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskStatusReport(" +
            "ld.id," +
            "t.taskNo," +
            "a.airframeSerial," +
            "t.description," +
            "t.effectiveDate," +
            "t.issueDate," +
            "t.revisionNumber," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join Aircraft a on a.id = ld.aircraftId " +
            "where t.taskNo in :taskNo and ae.aircraftId= :aircraftId and t.isActive = true and ld.isActive = true ")
    Page<TaskStatusReport> getTaskStatusReportByTaskNoAndAircraft(List<String> taskNo, Long aircraftId, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskStatusReport(" +
            "ld.id," +
            "t.taskNo," +
            "a.airframeSerial," +
            "t.description," +
            "t.effectiveDate," +
            "t.issueDate," +
            "t.revisionNumber," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join Aircraft a on a.id = ld.aircraftId " +
            "where t.id in :taskId and t.isActive = true and ld.isActive = true")
    Page<TaskStatusReport> getTaskStatusReportByTaskId(List<Long> taskId, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskStatusReport(" +
            "ld.id," +
            "t.taskNo," +
            "a.airframeSerial," +
            "t.description," +
            "t.effectiveDate," +
            "t.issueDate," +
            "t.revisionNumber," +
            "ae.effectivityType," +
            "t.taskStatus," +
            "t.thresholdHour," +
            "t.thresholdCycle," +
            "t.thresholdDay," +
            "t.intervalHour," +
            "t.intervalCycle," +
            "t.intervalDay," +
            "ld.doneHour," +
            "ld.doneCycle," +
            "ld.doneDate," +
            "ld.dueHour," +
            "ld.dueCycle," +
            "ld.dueDate," +
            "ld.remainingHour," +
            "ld.remainingCycle," +
            "ae.remark," +
            "ld.isApuControl" +
            ") from Task t " +
            "join AircraftEffectivity ae on  ae.taskId = t.id " +
            "left join Ldnd ld on ld.taskId = t.id and ld.aircraftId = ae.aircraftId " +
            "left join Aircraft a on a.id = ld.aircraftId " +
            "where t.id in :taskId and ae.aircraftId = :aircraftId and t.isActive = true and ld.isActive = true ")
    Page<TaskStatusReport> getTaskStatusReportByTaskIdAndAircraft(List<Long> taskId, Long aircraftId, Pageable pageable);
}
