package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.AcAlertLevel;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftDefectListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AlertLevelByLocation;
import com.digigate.engineeringmanagement.planning.payload.response.AlertLevelListViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Ac Alert Level Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface AcAlertLevelRepository extends JpaRepository<AcAlertLevel, Long> {

    @Query("select al from AcAlertLevel al where al.month = :month and al.year= :year and " +
            " al.aircraftModel.id = :aircraftModelId " +
            " and al.aircraftLocation.id = :locationId ")
    AcAlertLevel findByMonthAndYear(Integer month, Integer year, Long aircraftModelId, Long locationId);

    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.AlertLevelListViewModel(" +
            " a.id, " +
            " a.year," +
            " a.month," +
            " a.aircraftModel.aircraftModelName, " +
            " a.aircraftLocation.name, " +
            " a.alertLevel " +
            ") " +
            " from AcAlertLevel a where a.aircraftModel.id = :aircraftModelId" +
            " and a.aircraftLocation.id = :locationId " +
            " and a.year=:year and (a.month >= :startMonth and a.month <= :endMonth) order by month asc ")
    List<AlertLevelListViewModel> findAlertLevelInSameYear(Long aircraftModelId, Long locationId, int year,
                                                           int startMonth, int endMonth);
    @Query("select new  com.digigate.engineeringmanagement.planning.payload.response.AlertLevelListViewModel(" +
            " a.id, " +
            " a.year," +
            " a.month," +
            " a.aircraftModel.aircraftModelName, " +
            " a.aircraftLocation.name, " +
            " a.alertLevel " +
            ") " +
            " from AcAlertLevel a where a.aircraftModel.id = :aircraftModelId" +
            " and a.aircraftLocation.id = :locationId " +
            " and ( (a.year = :startYear and a.month >= :startMonth) " +
            "or (a.year = :endYear and a.month <= :endMonth) ) order by year asc , month asc")
    List<AlertLevelListViewModel> findAlertLevelInDifferentYear(Long aircraftModelId, Long locationId, int startYear, int endYear,
                                                            int startMonth, int endMonth);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AlertLevelByLocation(" +
            "al.id," +
            "al.name, " +
            "a.alertLevel, " +
            "s.name " +
            ") from AcAlertLevel a " +
            " join AircraftLocation al on al.id = a.aircraftLocation.id " +
            " JOIN Systems s on s.locationId =  a.aircraftLocation.id " +
            " WHERE a.month = :month AND a.year = :year AND a.aircraftModel.id = :aircraftModelId " +
            " and a.isActive = true and s.isActive = true and al.isActive = true")
    List<AlertLevelByLocation> findAlertLevelByLocationId(Long aircraftModelId, int year, int month);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftDefectListViewModel(" +
            " d.aircraftId, " +
            " d.locationId, " +
            " a.aircraftName, " +
            " al.name," +
            " s.name, " +
            " COUNT(d.id) " +
            ") FROM Defect d " +
            "JOIN Aircraft a ON a.id = d.aircraftId " +
            "JOIN AircraftLocation al ON al.id = d.locationId " +
            "JOIN AircraftModel am ON am.id = a.aircraftModelId " +
            "JOIN Systems s on s.locationId =  d.locationId " +
            "WHERE d.date BETWEEN :startDate AND :endDate " +
            "AND am.id = :aircraftModelId  " +
            "and a.isActive = true and al.isActive = true and d.isActive = true and am.isActive = true " +
            "GROUP BY d.aircraftId, d.locationId, al.name, a.aircraftName, s.name")
    List<AircraftDefectListViewModel> aircraftDefectListViewModelList(Long aircraftModelId, LocalDate startDate,
                                                                      LocalDate endDate);
}
