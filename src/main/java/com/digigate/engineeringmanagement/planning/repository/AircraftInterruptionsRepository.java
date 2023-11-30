package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AircraftInterruptions;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftInterruptionsViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DispatchInterruptionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Aircraft Interruptions repository
 *
 * @author Nafiul Islam
 */
@Repository
public interface AircraftInterruptionsRepository extends AbstractRepository<AircraftInterruptions> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AircraftInterruptionsViewModel(" +
            "ai.id, " +
            "ai.aircraftId, " +
            "ai.aircraft.aircraftName, " +
            "ai.locationId, " +
            "ai.aircraftLocation.name, " +
            "ai.date, " +
            "ai.defectDescription, " +
            "ai.rectDescription, " +
            "ai.duration," +
            "ai.amlPageNo," +
            "ai.seqNo, " +
            "ai.isActive, " +
            "ai.createdAt " +
            ") from AircraftInterruptions ai where (:aircraftId is null or ai.aircraftId = :aircraftId)" +
            " and  ai.isActive = :isActive " +
            " and (:startDate is null or :endDate is null or (ai.date between :startDate and :endDate)) ")
    Page<AircraftInterruptionsViewModel> searchAircraftInterruptions(Long aircraftId, LocalDate startDate,
                                                                     LocalDate endDate, Boolean isActive,
                                                                     Pageable pageable);

    @Query("select ai.date" +
            " from AircraftInterruptions ai" +
            " where ai.aircraft.aircraftModelId = :aircraftModelId" +
            " and ai.isActive =true " +
            " and (:startDate is null or :endDate is null or (ai.date between :startDate and :endDate))")
    List<LocalDate> delayList(Long aircraftModelId, LocalDate startDate, LocalDate endDate);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DispatchInterruptionData(" +
            "ai.locationId, " +
            "ai.aircraftLocation.name, " +
            "ai.date, " +
            "ai.aircraft.aircraftName, " +
            "ai.defectDescription, " +
            "ai.rectDescription, " +
            "ai.duration " +
            ") from AircraftInterruptions ai " +
            " where ai.aircraft.aircraftModelId = :aircraftModelId" +
            " and ai.isActive =true " +
            " and (:startDate is null or :endDate is null or (ai.date between :startDate and :endDate))")
    List<DispatchInterruptionData> getInterruptionDataByDate(Long aircraftModelId, LocalDate startDate,
                                                             LocalDate endDate);


}
