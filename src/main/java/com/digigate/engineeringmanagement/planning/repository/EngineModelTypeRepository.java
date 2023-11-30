package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineModelTypeRepository extends JpaRepository<EngineModelType, Integer> {
    Boolean existsByName(String name);
    Boolean existsByNameAndIdIsNot(String name, Integer id);
    Optional<EngineModelType> findByIdAndIsActiveTrue(Integer engineModelTypeId);
}
