package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AMLDefectRectification;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * AMLDefectRectification Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface AmlDefectRectificationRepository extends AbstractRepository<AMLDefectRectification> {

    List<AMLDefectRectification> findAllByAircraftMaintenanceLogId(Long id);

    void deleteAllByAircraftMaintenanceLogId(Long aircraftMaintenanceLogId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationReportViewModel( " +
            "aml.date," +
            "nrc.issueDate," +
            "a.iataCode," +
            "afd.totalAirTime, " +
            "afd.totalLanding," +
            "aml.pageNo, " +
            "aml.alphabet, " +
            "nrc.nrcNo, " +
            "amlDef.defectDescription, " +
            "amlDef.rectDescription, " +
            "amlDef.rectAta, " +
            "amlDef.rectPos,  " +
            "amlDef.rectPnOff, " +
            "amlDef.rectSnOff, " +
            "amlDef.rectPnOn, " +
            "amlDef.rectSnOn, " +
            "amlDef.rectGrn, " +
            "amlDef.reasonForRemoval, " +
            "amlDef.remark," +
            "amlDef.woNo" +
            ") from AMLDefectRectification amlDef " +
            "left join AircraftMaintenanceLog aml on aml.id = amlDef.aircraftMaintenanceLogId " +
            "left join NonRoutineCard nrc on nrc.id = amlDef.nonRoutineCardId " +
            "left join Airport a on a.id = amlDef.defectAirportId " +
            "left join AmlFlightData afd on afd.amlId = aml.id " +
            "where (aml.amlAircraftId = :aircraftId or nrc.aircraftId = :aircraftId) " +
            "and amlDef.rectDescription is not null and " +
            "(:startDate is null or :endDate is null or (aml.date between :startDate and :endDate) " +
            "or (nrc.issueDate between :startDate and :endDate)) and " +
            "(:airportId is null or a.id = :airportId) and " +
            "(:rectDescription is null  or amlDef.rectDescription LIKE :rectDescription%) and " +
            "(:defDescription is null or amlDef.defectDescription LIKE :defDescription%) and " +
            "(:position is null or amlDef.rectPos LIKE :position%) and " +
            "(:rectPnOff is null or amlDef.rectPnOff LIKE :rectPnOff%) and " +
            "(:rectSnOff is null or amlDef.rectSnOff LIKE :rectSnOff%) and " +
            "(:rectPnOn is null or amlDef.rectPnOn LIKE :rectPnOn%) and " +
            "(:rectSnOn is null or amlDef.rectSnOn LIKE :rectSnOn%) and " +
            "(:rectAta is null or amlDef.rectAta LIKE :rectAta%) and " +
            "(:reasonForRemoval is null or amlDef.reasonForRemoval LIKE :reasonForRemoval%) and " +
            "(:remark is null or amlDef.remark LIKE :remark%) and " +
            "amlDef.isActive = true order by aml.date asc")
    Page<AmlDefectRectificationReportViewModel> generateReport(Long aircraftId, LocalDate startDate, LocalDate endDate,
                                                               Long airportId, String rectDescription,
                                                               String defDescription, String position, String rectPnOff,
                                                               String rectSnOff, String rectPnOn, String rectSnOn,
                                                               String rectAta, String reasonForRemoval, String remark,
                                                               Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel( " +
            "amlDef.id," +
            "aml.date," +
            "aml.pageNo, " +
            "aml.alphabet, " +
            "amlDef.defectDescription, " +
            "amlDef.rectDescription, " +
            "amlDef.rectPnOff," +
            "amlDef.rectAta " +
            ") from AMLDefectRectification amlDef " +
            " join AircraftMaintenanceLog aml on aml.id = amlDef.aircraftMaintenanceLogId " +
            "where aml.amlAircraftId = :aircraftId " +
            "and amlDef.rectDescription is not null and " +
            "aml.date between :startDate and :endDate " +
            "and amlDef.isActive = true")
    List<DefRectSearchViewModel> searchDefectRect(Long aircraftId, LocalDate startDate, LocalDate endDate);

    List<AMLDefectRectification> findAllByIdIn(List<Long> correctiveRectificationId);

    AMLDefectRectification findByNonRoutineCardId(Long nrcId);

    Optional<AMLDefectRectification> findByAircraftMaintenanceLogId(Long id);
}
