package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Airport location repository
 *
 * @author ashiniSingha
 */
@Repository
public interface AircraftLocationRepository extends AbstractRepository<AircraftLocation> {
    Boolean existsByName(String name);
    @Query(" SELECT al.name from AircraftLocation al where al.isActive = true")
    Set<String> getAllNames();
    Set<AircraftLocation> findAllByIsActiveTrue();
}
