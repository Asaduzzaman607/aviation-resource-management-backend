package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.AcStatistics;
import com.digigate.engineeringmanagement.planning.payload.response.MonthData;
import com.digigate.engineeringmanagement.planning.payload.response.TotalCycleByDateViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TotalHoursByDateViewModel;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AcStatistics Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface AcStatisticsRepository extends JpaRepository<AcStatistics, Long> {


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MonthData("+
            "a.month," +
            "a.year" +
            ") from AcStatistics a where a.aircraftModel.id = :aircraftModelId")
    Set<MonthData> findMonths(Long aircraftModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.MonthData("+
            "a.month," +
            "a.year" +
            ") from AcStatistics a where a.aircraftModel.id = :aircraftModelId " +
            "and ( ( a.month = :startMonth and a.year = :startYear) or ( a.month = :endMonth and a.year = :endYear) ) ")
    Set<MonthData> getExistingMonths(Long aircraftModelId, Integer startMonth, Integer startYear,
                                     Integer endMonth, Integer endYear);
    Boolean existsByAircraftModelIdAndMonthAndYear(Long aircraftModelId, Integer month, Integer year);

    @Query("select a from AcStatistics a where a.aircraftModelId = :aircraftModelId " +
            "and a.month = :month and a.year = :year")
    Optional<AcStatistics> findByAcModelIdAndMonthAndYear(Long aircraftModelId, Integer month, Integer year);

    @Query("select a from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and a.year=:year and (a.month >= :startMonth and a.month <= :endMonth) order by year asc , month asc ")
    List<AcStatistics> findAcStatInSameYear(Long aircraftModelId, Integer year, Integer startMonth, Integer endMonth);

    @Query("select a from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and ( (a.year = :startYear and a.month >= :startMonth) " +
            "or (a.year = :endYear and a.month <= :endMonth) ) order by year asc , month asc")
    List<AcStatistics> findAcStatInDifferentYear(Long aircraftModelId, Integer startYear, Integer endYear,
                                                 Integer startMonth, Integer endMonth );

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.TotalCycleByDateViewModel(" +
            " a.totalFlightCycle, " +
            " a.year, " +
            " a.month" +
            ") " +
            "from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and ( (a.year = :startYear and a.month >= :startMonth) " +
            "or (a.year = :endYear and a.month <= :endMonth) ) order by year asc , month asc")
    List<TotalCycleByDateViewModel> findTotalCycleInDifferentYear(Long aircraftModelId, Integer startYear, Integer endYear,
                                          Integer startMonth, Integer endMonth );

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.TotalCycleByDateViewModel(" +
            " a.totalFlightCycle, " +
            " a.year, " +
            " a.month" +
            ") " +
            "from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and a.year=:year and (a.month >= :startMonth and a.month <= :endMonth) order by month asc ")
    List<TotalCycleByDateViewModel> findTotalCycleInSameYear(Long aircraftModelId, Integer year, Integer startMonth,
                                                             Integer endMonth);

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.TotalHoursByDateViewModel(" +
            " a.totalFlightHour, " +
            " a.year, " +
            " a.month" +
            ") " +
            "from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and a.year=:year and (a.month >= :startMonth and a.month <= :endMonth) order by month asc ")
    List<TotalHoursByDateViewModel> findTotalHoursInSameYear(Long aircraftModelId, Integer year, Integer startMonth,
                                                             Integer endMonth);

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.TotalHoursByDateViewModel(" +
            " a.totalFlightHour, " +
            " a.year, " +
            " a.month" +
            ") " +
            "from AcStatistics a where a.aircraftModel.id = :aircraftModelId" +
            " and ( (a.year = :startYear and a.month >= :startMonth) " +
            "or (a.year = :endYear and a.month <= :endMonth) ) order by year asc , month asc")
    List<TotalHoursByDateViewModel> findTotalHoursInDifferentYear(Long aircraftModelId, Integer startYear, Integer endYear,
                                                                  Integer startMonth, Integer endMonth );

}
