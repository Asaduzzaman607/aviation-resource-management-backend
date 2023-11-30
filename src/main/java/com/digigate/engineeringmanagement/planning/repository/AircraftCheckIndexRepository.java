package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckIndex;
import com.digigate.engineeringmanagement.planning.entity.Ldnd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * AircraftCheckIndex Repository
 *
 * @author Ashraful
 */
public interface AircraftCheckIndexRepository extends AbstractRepository<AircraftCheckIndex> {
    List<AircraftCheckIndex>getAircraftCheckIndexByAircraftIdAndIsActiveTrue(Long aircraftId);
    Optional<AircraftCheckIndex> findByIdAndIsActiveTrue(Long acCheckIndexId);
    List<AircraftCheckIndex> findAllByIsActiveTrueAndAircraftId(Long aircraftId);
    @Query("SELECT ldnd FROM AircraftCheckIndex aci JOIN aci.ldndSet ldnd where aci.id = :acCheckIndexId")
    Page<Ldnd> getAllLdndByAircraftCheckIndexId(Long acCheckIndexId, Pageable pageable);
}
