package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.EngineShopVisit;
import com.digigate.engineeringmanagement.planning.payload.response.EngineShopVisitViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Engine shop visit repository
 *
 * @author Pranoy Das
 */
@Repository
public interface EngineShopVisitRepository extends JpaRepository<EngineShopVisit, Long> {
    List<EngineShopVisit> findAllByIdIn(Set<Long> engineShopVisitIds);

    @Query("select esv.id from EngineShopVisit esv where esv.aircraftBuildId = :aircraftBuildId")
    List<Long> findEngineShopIdsByAircraftBuildId(Long aircraftBuildId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.EngineShopVisitViewModel(" +
            "esv.id, esv.type, esv.date, esv.tsn, esv.csn, esv.tso, esv.cso, esv.status) " +
            "from EngineShopVisit esv where esv.aircraftBuildId = :aircraftBuildId")
    List<EngineShopVisitViewModel> findAllByAircraftBuildId(Long aircraftBuildId);

    @Query("select esv from EngineShopVisit esv where esv.aircraftBuildId = :aircraftBuildId")
    List<EngineShopVisit> findEngineShopInfoByAircraftBuildId(Long aircraftBuildId);
}
