package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.ForecastTask;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * ForecastTaskRepository
 *
 * @author Masud Rana
 */
@Repository
public interface ForecastTaskRepository extends JpaRepository<ForecastTask, Long> {
    List<ForecastTask> findByIdIn(Set<Long> ids);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskDto(" +
            "ft.id, ft.ldndId, ft.ldnd.taskId, ft.ldnd.task.taskNo, ft.dueDate, ft.comment, ft.forecastAircraftId) " +
            "from ForecastTask ft " +
            "where ft.forecastAircraftId in :forecastAircraftIds")
    Set<ForecastTaskDto> findByForecastAircraftIdIn(Set<Long> forecastAircraftIds);
}
