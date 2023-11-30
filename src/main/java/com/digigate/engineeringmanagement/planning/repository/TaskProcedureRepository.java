package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.TaskProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskProcedureRepository extends JpaRepository<TaskProcedure, Long> {
    List<TaskProcedure> findAllByTaskId(Long taskId);
    Optional<TaskProcedure> findByTaskIdAndPositionId(Long taskId, Long positionId);

}

