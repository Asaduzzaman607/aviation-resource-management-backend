package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AircraftIncidents;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftIncidentsViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
/**
 * Aircraft Interruptions repository
 *
 * @author Nafiul Islam
 */
public interface AircraftIncidentsRepository extends AbstractRepository<AircraftIncidents> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AircraftIncidentsViewModel(" +
            "ai.id, " +
            "ai.aircraftId," +
            "ai.aircraft.aircraftName, " +
            "ai.date, " +
            "ai.incidentTypeEnum, " +
            "ai.classificationTypeEnum, " +
            "ai.incidentDesc, " +
            "ai.actionDesc, " +
            "ai.referenceAtl," +
            "ai.seqNo, " +
            "ai.remarks, " +
            "ai.isActive, " +
            "ai.createdAt " +
            ") from AircraftIncidents ai where (:aircraftId is null or ai.aircraftId = :aircraftId)" +
            " and  ai.isActive = :isActive " +
            " and (:startDate is null or :endDate is null or (ai.date between :startDate and :endDate)) ")
    Page<AircraftIncidentsViewModel> searchAircraftIncidents(Long aircraftId, LocalDate startDate, LocalDate endDate,
                                                             Boolean isActive, Pageable pageable);

    @Query("select ai " +
            "from AircraftIncidents ai" +
            " where ai.aircraft.aircraftModelId = :aircraftModelId and ai.isActive = true " +
            " and (:fromDate is null or :toDate is null or (ai.date between :fromDate and :toDate)) ")
    List<AircraftIncidents> getIncidentList(Long aircraftModelId, LocalDate fromDate, LocalDate toDate);
}
