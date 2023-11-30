package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.CheckType;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckDone;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckDoneViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LatestAirTimeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * AircraftCheckDone repository
 *
 * @author Nafiul Islam
 */
@Repository
public interface AircraftCheckDoneRepository extends AbstractRepository<AircraftCheckDone> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckDoneViewModel(" +
            "ad.id," +
            "ad.aircraftId, " +
            "ad.aircraft.aircraftName, " +
            "ad.aircraftCheckDoneHour, " +
            "ad.aircraftCheckDoneDate, " +
            "ad.isActive," +
            "ad.checkType " +
            ") from AircraftCheckDone ad " +
            "where ad.aircraftId = :aircraftId " +
            "and  ad.isActive = :isActive " +
            "and (:date is null or ad.aircraftCheckDoneDate = :date)")
    Page<AircraftCheckDoneViewModel> findAllByDate(Long aircraftId, LocalDate date, Boolean isActive,
                                                   Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LatestAirTimeResponse(" +
            "a.aircraftCheckDoneHour," +
            "a.aircraftCheckDoneDate" +
            ") from AircraftCheckDone a " +
            "where a.aircraftCheckDoneDate <= :date and a.isActive = true and a.aircraftId = :aircraftId " +
            "order by a.aircraftCheckDoneDate desc")
    Page<LatestAirTimeResponse> findCloseAirTimeByAircraftId(Long aircraftId, LocalDate date, Pageable pageable);


    Optional<AircraftCheckDone> findTopByAircraftIdAndCheckTypeAndIsActiveTrue(Long aircraftId, CheckType checkType);
}
