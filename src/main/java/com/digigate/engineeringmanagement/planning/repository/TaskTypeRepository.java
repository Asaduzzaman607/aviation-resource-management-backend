package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.TaskType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Task  Type repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface TaskTypeRepository extends AbstractRepository<TaskType> {

    @Query("select t.id from TaskType t where t.name = :name")
    Optional<Long> findByName(String name);

    @Query("select t from TaskType t where t.isActive = :isActive")
    List<TaskType> findAllActiveTaskTypes(Boolean isActive);
}
