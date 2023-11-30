package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.entity.Task;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;
import com.digigate.engineeringmanagement.planning.payload.response.ConsumablePartTaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskModelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.TaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskViewModelForAcCheck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Task repository
 *
 * @author ashinisingha
 */
@Repository
public interface TaskRepository extends AbstractRepository<Task> {

    @Query(" SELECT task.id from Task task WHERE task.taskNo LIKE :taskNo% ")
    Optional<Long> findTaskByTaskNo(@Param("taskNo") String taskNo);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskViewModel(" +
            "t.id," +
            "t.taskNo" +
            ") from Task t where t.aircraftModelId = :aircraftModelId")
    List<TaskViewModel> findTasksByAircraftModelId(Long aircraftModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto(" +
            "t.id," +
            "t.taskNo" +
            ") from Task t where t.aircraftModelId = :aircraftModelId")
    List<AircraftEffectivityTaskDto> getTaskByAircraftModelId(Long aircraftModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskViewModelForAcCheck(" +
            "t.id," +
            "t.taskNo" +
            ") from Task t where t.aircraftModelId = :acModelId and t.isActive = true and t.taskStatus <> :status")
    List<TaskViewModelForAcCheck> findAllTaskByAircraftModelId(Long acModelId, TaskStatusEnum status);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskViewModelForAcCheck(" +
            "t.id," +
            "t.taskNo" +
            ") from Task t " +
            "where t.aircraftModelId = :acModelId " +
            "and ( ( (:hour is null or t.thresholdHour = :hour) and (:day is null or t.thresholdDay = :day) ) " +
            "or ( (:hour is null or t.intervalHour = :hour) and (:day is null or t.intervalDay = :day) ) ) " +
            "and t.isActive = true and t.taskStatus <> :status")
    List<TaskViewModelForAcCheck> findAllTaskByAircraftModelId(Long acModelId, TaskStatusEnum status,
                                                               Double hour, Integer day);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TaskModelResponseDto(" +
            "t.id," +
            "t.taskNo," +
            "t.modelId," +
            "t.model.modelName" +
            ") from Task t where t.aircraftModelId = :aircraftModelId and t.isActive = true")
    List<TaskModelResponseDto> getTaskModelByAircraftModelId(Long aircraftModelId);


    Set<Task> findAllByModelIdInAndIsActiveTrue(Collection<Long> modelId);

    List<Task> findAllByAircraftModelIdAndIsActiveTrue(Long aircraftModelId);

    @Query("select t.taskNo from Task t where t.aircraftModelId = :aircraftModelId")
    Set<String> findAllTaskNoByAircraftModelId(Long aircraftModelId);
}
