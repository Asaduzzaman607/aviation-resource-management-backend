package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Mel;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Mel Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface MelRepository extends AbstractRepository<Mel> {
    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MelReportModelView(" +
            "mel.id, " +
            "intAml.date, " +
            "airport.iataCode, " +
            "intAml.pageNo, " +
            "intAml.alphabet, " +
            "intDefRect.rectAta, " +
            "intDefRect.defectDescription, " +
            "intDefRect.rectDescription, " +
            "intDefRect.melCategory, " +
            "intDefRect.dueDate, " +
            "ctAml.date, " +
            "ctAml.pageNo, " +
            "ctAml.alphabet, " +
            "correctDefRect.rectDescription, " +
            "correctDefRect.rectPos, " +
            "correctDefRect.rectPnOff," +
            "correctDefRect.rectSnOff," +
            "correctDefRect.rectPnOn," +
            "correctDefRect.rectSnOn," +
            "correctDefRect.rectGrn" +
            ") " +
            "from Mel mel " +
            "join AMLDefectRectification intDefRect on intDefRect.id = mel.intDefRect.id " +
            "join AircraftMaintenanceLog intAml on intAml.id = intDefRect.aircraftMaintenanceLog.id " +
            "left join AMLDefectRectification correctDefRect on correctDefRect.id = mel.correctDefRect.id " +
            "left join AircraftMaintenanceLog ctAml on ctAml.id = correctDefRect.aircraftMaintenanceLog.id " +
            "left join Airport airport on airport.id = intDefRect.defectAirport.id " +
            "where intAml.date between :fromDate and :toDate and ctAml.date is not null " +
            " and ( :aircraftId is null or  mel.amlAircraftId = :aircraftId) ")
    Page<MelReportModelView> searchClosedMelByAmlDate(LocalDate fromDate,
                                                LocalDate toDate,
                                                Long aircraftId,
                                                Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MelReportModelView(" +
            "mel.id, " +
            "intAml.date, " +
            "airport.iataCode, " +
            "intAml.pageNo, " +
            "intAml.alphabet, " +
            "intDefRect.rectAta, " +
            "intDefRect.defectDescription, " +
            "intDefRect.rectDescription, " +
            "intDefRect.melCategory, " +
            "intDefRect.dueDate, " +
            "ctAml.date, " +
            "ctAml.pageNo, " +
            "ctAml.alphabet, " +
            "correctDefRect.rectDescription, " +
            "correctDefRect.rectPos, " +
            "correctDefRect.rectPnOff," +
            "correctDefRect.rectSnOff," +
            "correctDefRect.rectPnOn," +
            "correctDefRect.rectSnOn," +
            "correctDefRect.rectGrn" +
            ") " +
            "from Mel mel " +
            "join AMLDefectRectification intDefRect on intDefRect.id = mel.intDefRect.id " +
            "join AircraftMaintenanceLog intAml on intAml.id = intDefRect.aircraftMaintenanceLog.id " +
            "left join AMLDefectRectification correctDefRect on correctDefRect.id = mel.correctDefRect.id " +
            "left join AircraftMaintenanceLog ctAml on ctAml.id = correctDefRect.aircraftMaintenanceLog.id " +
            "left join Airport airport on airport.id = intDefRect.defectAirport.id " +
            "where intAml.date between :fromDate and :toDate and ctAml.date is null and " +
            "( :aircraftId is null or mel.amlAircraftId = :aircraftId) ")
    Page<MelReportModelView> searchOpenMelByAmlDate(LocalDate fromDate, LocalDate toDate, Long aircraftId,
                                                    Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AmlPageNoData(" +
            "intAml.pageNo," +
            "ctAml.pageNo," +
            "intAml.id," +
            "ctAml.id," +
            "intDefRect.id," +
            "correctDefRect.id," +
            "intDefRect.seqNo," +
            "correctDefRect.seqNo" +
            ") " +
            "from Mel mel " +
            "join AMLDefectRectification intDefRect on intDefRect.id = mel.intDefRect.id " +
            "join AircraftMaintenanceLog intAml on intAml.id = intDefRect.aircraftMaintenanceLog.id " +
            "left join AMLDefectRectification correctDefRect on correctDefRect.id = mel.correctDefRect.id " +
            "left join AircraftMaintenanceLog ctAml on ctAml.id = correctDefRect.aircraftMaintenanceLog.id " +
            "where mel.id = :melId")
    Optional<AmlPageNoData> findMelDataByMelId(Long melId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MelViewMode(" +
            "intDefRect.id," +
            "intAml.pageNo," +
            "intAml.alphabet," +
            "mel.id," +
            "mel.intDefRect.seqNo" +
            ") " +
            "from Mel mel " +
            "join AMLDefectRectification intDefRect on intDefRect.id = mel.intDefRect.id " +
            "join AircraftMaintenanceLog intAml on intAml.id = intDefRect.aircraftMaintenanceLog.id " +
            "where mel.correctDefRectId is null and intAml.amlAircraftId = :aircraftId")
    List<MelViewMode> findAllUnclearedMel(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DueResponse(" +
            "intAml.id," +
            "mel.intDefRect.dueDate" +
            ") " +
            "from Mel mel " +
            "join AMLDefectRectification intDefRect on intDefRect.id = mel.intDefRect.id " +
            "join AircraftMaintenanceLog intAml on intAml.id = intDefRect.aircraftMaintenanceLog.id " +
            "where mel.correctDefRectId is null and intDefRect.dueDate is not null " +
            "and intAml.amlAircraftId = :aircraftId order by intDefRect.dueDate asc")
    Page<DueResponse> findOpenClosestMel(Long aircraftId, Pageable pageable);

    Optional<Mel> findByIntDefRectId(Long id);
}
