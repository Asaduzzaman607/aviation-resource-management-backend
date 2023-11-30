package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.EngineIncidents;
import com.digigate.engineeringmanagement.planning.payload.response.EngineIncidentsViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Engine Incidents Repository
 *
 * @author Nafiul Islam
 */
public interface EngineIncidentsRepository extends AbstractRepository<EngineIncidents> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.EngineIncidentsViewModel(" +
            "ei.id, " +
            "ei.aircraftModelId, " +
            "ei.aircraftModel.aircraftModelName, " +
            "ei.engineIncidentsEnum, " +
            "ei.date, " +
            "ei.isActive " +
            ") from EngineIncidents ei" +
            " where ei.aircraftModelId = :aircraftModelId and ei.isActive = :isActive" +
            " and (:fromDate is null or :toDate is null or (ei.date between :fromDate and :toDate)) ")
    Page<EngineIncidentsViewModel> searchEngineIncidents(Long aircraftModelId, LocalDate fromDate, LocalDate toDate,
                                                         Boolean isActive, Pageable pageable);

    @Query("select ei " +
            " from EngineIncidents ei" +
            " where ei.aircraftModelId = :aircraftModelId and ei.isActive = true " +
            " and (:fromDate is null or :toDate is null or (ei.date between :fromDate and :toDate)) ")
    List<EngineIncidents> engineIncidentsList(Long aircraftModelId, LocalDate fromDate, LocalDate toDate);
}
