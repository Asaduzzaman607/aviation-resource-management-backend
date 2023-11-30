package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftEffectivityDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftEffectivity;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Aircraft Effectivity Repository
 *
 * @author Sayem Hasnat
 */
@Repository
public interface AircraftEffectivityRepository extends JpaRepository<AircraftEffectivity, Long> {
    @Query("select new com.digigate.engineeringmanagement.planning.dto.request.AircraftEffectivityDto(" +
            "a.task)" +
            "from AircraftEffectivity a join Task t on a.taskId = t.id  " +
            "where a.aircraft.id = :aircraftId " +
            "and a.effectivityType = :effectivityType and t.taskStatus <> :status ")
    List<AircraftEffectivityDto> findByAircraftIdAndTaskStatus(Long aircraftId, EffectivityType effectivityType,
                                                               TaskStatusEnum status);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto(" +
            "a.id," +
            "a.aircraftId," +
            "a.taskId," +
            "a.task.taskNo," +
            "a.remark," +
            "a.effectivityType)" +
            "from AircraftEffectivity a join Task t on a.taskId = t.id " +
            "where a.aircraftId = :aircraftId and t.isActive = true ")
    List<AircraftEffectivityTaskDto> findAllByAircraft(Long aircraftId);

    List<AircraftEffectivity> findAllByIdIn(Set<Long> aircraftEffectivityIds);

    Optional<AircraftEffectivity> findByAircraftIdAndTaskId(Long aircraftId, Long taskId);
}