package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.MonthlyUtilization;
import com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport;
import com.digigate.engineeringmanagement.planning.payload.response.YearlyUtilizationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

/**
 * MonthlyUtilization Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface MonthlyUtilizationRepository extends JpaRepository<MonthlyUtilization, Long> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport(" +
            "m.acHours," +
            "m.acCycle," +
            "m.apuHrs," +
            "m.apuCycle," +
            "m.yearMonth," +
            "m.ratio" +
            ") from MonthlyUtilization m where m.aircraftId = :aircraftId " +
            " and  m.yearMonth >= :from and m.yearMonth <= :to " +
            " order by m.yearMonth asc")
    List<MonthlyUtilizationReport> findMonthlyUtilizationReports(Long aircraftId, YearMonth from, YearMonth to);

}
