package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Defect;
import com.digigate.engineeringmanagement.planning.payload.response.CrrReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DefectSearchViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TopAtaViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Defect repository
 *
 * @author Asif
 */
@Repository
public interface DefectRepository extends AbstractRepository<Defect> {


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DefectSearchViewModel(" +
            "d.id," +
            "d.aircraftId," +
            "d.aircraft.aircraftName," +
            "d.date," +
            "d.defectType," +
            "d.defectDesc," +
            "d.actionDesc," +
            "d.reference," +
            "d.locationId," +
            "l.name," +
            "d.partId," +
            "p.partNo," +
            "p.description," +
            "s.name," +
            "d.isActive" +
            ")" +
            " from Defect d " +
            "left join AircraftLocation l on l.id = d.locationId " +
            "left join Systems s on s.locationId = d.locationId " +
            "left join Part p on p.id = d.partId " +
            "where (:aircraftId is null  or d.aircraftId = :aircraftId) " +
            "and (:partId is null or d.partId = :partId) " +
            "and (:locationId is null or d.locationId = :locationId) " +
            "and ((:fromDate is null and :toDate is null)  or d.date between :fromDate and :toDate) " +
            "and d.isActive = :isActive order by d.id desc")
    Page<DefectSearchViewModel> searchDefects(Long aircraftId, Long locationId, Long partId,
                                              LocalDate fromDate, LocalDate toDate, Boolean isActive, Pageable pageable);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.TopAtaViewModel(" +
            "l.name," +
            "s.name," +
            "count (defects.id)," +
            "sum(case when defects.defectType = 0 then 1 else 0 end )," +
            "sum(case when defects.defectType = 1 then 1 else 0 end ) " +
            ") from Defect defects " +
            "join AircraftLocation l on l.id = defects.locationId " +
            "join Systems s on s.locationId = defects.locationId " +
            " where defects.aircraftId = :aircraftId and defects.date between :fromDate and :toDate " +
            "group by l.name, s.name order by count(defects.id) desc")
    Page<TopAtaViewModel> findTopTenAta(Long aircraftId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.CrrReportViewModel(" +
            "l.name," +
            "p.partNo," +
            "p.description," +
            "count(defects.id)" +
            ") from Defect defects " +
            "join Aircraft a on a.id = defects.aircraftId " +
            "join AircraftLocation l on l.id = defects.locationId " +
            "join Part p on p.id = defects.partId " +
            " where a.aircraftModelId = :aircraftModelId and defects.date between :fromDate and :toDate " +
            "group by l.name, p.partNo, p.description order by count(defects.id) desc")
    Page<CrrReportViewModel> findCrrReport(Long aircraftModelId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    @Query("select d from Defect d where d.aircraft.aircraftModelId = :aircraftModelId and d.isActive = true " +
            " and d.locationId = :locationId " +
            " and (:fromDate is null or :toDate is null or (d.date between :fromDate and :toDate)) ")
    List<Defect> findDefectByDate(Long aircraftModelId, Long locationId, LocalDate fromDate, LocalDate toDate);
}
