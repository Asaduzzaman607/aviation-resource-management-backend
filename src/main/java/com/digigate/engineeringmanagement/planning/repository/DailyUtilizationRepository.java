package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.DailyUtilization;
import com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReport;
import com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DailyUtilization Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface DailyUtilizationRepository extends JpaRepository<DailyUtilization, Long> {

    Optional<DailyUtilization> findByAircraftIdAndDate(Long aircraftId, LocalDate date);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReport(" +
            "d.usedHours," +
            "d.usedCycle," +
            "d.tat," +
            "d.tac," +
            "d.apuUsedHrs," +
            "d.apuUsedCycle," +
            "d.date," +
            "d.eng1OilUplift," +
            "d.eng2OilUplift" +
            ") from DailyUtilization d where d.aircraftId = :aircraftId and d.date between :fromDate and :toDate " +
            "and d.isActive = true order by d.date asc")
    List<DailyUtilizationReport> findDailyUtilizationReports(Long aircraftId, LocalDate fromDate, LocalDate toDate);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport(" +
            "d.usedHours," +
            "d.usedCycle," +
            "d.apuUsedHrs," +
            "d.apuUsedCycle," +
            "d.date" +
            ") from DailyUtilization d where d.aircraftId = :aircraftId and d.date < :date " +
            "and d.isActive = true order by d.date asc")
    List<MonthlyUtilizationReport> findDailyUtilizationByDate(Long aircraftId, LocalDate date);


    @Query("select d from DailyUtilization d where d.date > :date and d.aircraftId = :aircraftId")
    List<DailyUtilization> findNextDailyUtils(Long aircraftId, LocalDate date);
}
