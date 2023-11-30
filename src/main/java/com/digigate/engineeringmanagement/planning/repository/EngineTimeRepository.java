package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.EngineTime;
import com.digigate.engineeringmanagement.planning.payload.response.EngineInfoViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineTimeViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Engine time repository
 *
 * @author Pranoy Das
 */
@Repository
public interface EngineTimeRepository extends JpaRepository<EngineTime, Long> {
    List<EngineTime> findAllByIdIn(Set<Long> engineTimeIds);

    @Query("select et.id from EngineTime et where et.aircraftBuildId = :aircraftBuildId")
    List<Long> findEngineShopIdsByAircraftBuildId(Long aircraftBuildId);

    @Query("select et from EngineTime et where et.aircraftBuildId = :aircraftBuildId")
    List<EngineTime> findEngineShopInfoByAircraftBuildId(Long aircraftBuildId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.EngineInfoViewModel(" +
            "ab.id, ab.aircraft.aircraftName, ab.part.description, ab.position.name) " +
            "FROM EngineTime et inner join AircraftBuild ab on et.aircraftBuildId = ab.id " +
            "where ab.aircraftId = :aircraftId")
    Set<EngineInfoViewModel> findEngineInfoByAircraftId(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.EngineTimeViewModel(" +
            "et.id, et.nameExtension, et.type, et.date, et.hour, et.cycle) " +
            "from EngineTime et where et.aircraftBuildId = :aircraftBuildId")
    List<EngineTimeViewModel> findAllByAircraftBuildId(Long aircraftBuildId);
}
