package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.dto.FlyingDayFlyingHourDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheck;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckForAircraftViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AircraftCheck Repository
 *
 * @author Ashraful
 */
@Repository
public interface AircraftCheckRepository extends AbstractRepository<AircraftCheck> {

    @Query("select ac.id from AircraftCheck ac where ac.checkId = :checkId and ac.aircraftModelId = :aircraftModelId")
    Optional<Long> findByCheckIdAndAircraftModelId(Long checkId, Long aircraftModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response" +
            ".AircraftCheckForAircraftViewModel(" +
            "ac.id, " +
            "ac.check.title)" +
            "from AircraftCheck ac join Aircraft a on a.aircraftModelId = ac.aircraftModelId " +
            "where a.id = :aircraftId ")
    List<AircraftCheckForAircraftViewModel> findAllAircraftCheckByAircraft(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.dto" +
            ".FlyingDayFlyingHourDto(" +
            "ac.flyingHour, " +
            "ac.flyingDay)" +
            "from AircraftCheck ac " +
            "where ac.id in :acCheckIds ")
    List<FlyingDayFlyingHourDto> findAllFlyingHourAndFlyingDayByAcCheckIdsIn(Set<Long> acCheckIds);
}
