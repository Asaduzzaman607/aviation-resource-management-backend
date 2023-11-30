package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightDataForOilUpliftReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReport;
import com.digigate.engineeringmanagement.planning.payload.response.FlightDataInfoViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LatestAirTimeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AML Flight Data repository
 *
 * @author ashinisingha
 */
@Repository
public interface AmlFlightDataRepository extends AbstractRepository<AmlFlightData> {

    @Query("SELECT fd from AmlFlightData fd WHERE fd.amlId = :amlId ")
    Optional<AmlFlightData> findByAmlId(@Param("amlId") Long amlId);

    Optional<AmlFlightData> findAmlFlightDataByAmlId(Long amlId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response" +
            ".AmlFlightDataForOilUpliftReportViewModel(" +
            "afd.amlId, " +
            "afd.airTime ) " +
            "FROM AmlFlightData afd " +
            "WHERE afd.amlId in :amlIds")
    List<AmlFlightDataForOilUpliftReportViewModel> findAllByAmlIdIn(Set<Long> amlIds);
    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.LatestAirTimeResponse(" +
            "f.grandTotalAirTime," +
            "f.grandTotalLanding" +
            ") from AmlFlightData f " +
            "join AircraftMaintenanceLog  aml on aml.id = f.amlId " +
            "where aml.date <= :date and aml.amlAircraftId = :aircraftId order by aml.date desc")
    Page<LatestAirTimeResponse> findCloseAirTimeByAircraftId(Long aircraftId, LocalDate date, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.FlightDataInfoViewModel(" +
            "af.grandTotalAirTime, " +
            "af.grandTotalLanding, " +
            "aml.date " +
            ") from AmlFlightData af join AircraftMaintenanceLog aml on aml.id = af.amlId " +
            "where aml.id = (select max(subAml.id) " +
            "FROM AircraftMaintenanceLog subAml " +
            "WHERE subAml.date <= :date and subAml.amlAircraftId = :aircraftId) ")
    FlightDataInfoViewModel findAmlFlightDataByDate(LocalDate date, Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReport(" +
            "fd.airTime," +
            "fd.noOfLanding," +
            "fd.grandTotalAirTime," +
            "fd.grandTotalLanding," +
            "fd.apuHours," +
            "fd.apuCycles," +
            "aml.date," +
            "oil.engineOil1," +
            "oil.engineOil2" +
            ") " +
            " from AircraftMaintenanceLog aml join AmlFlightData fd on fd.amlId = aml.id" +
            " left join AmlOilRecord oil on oil.amlId = fd.amlId " +
            " where  aml.amlAircraftId = :aircraftId and aml.date <= :date")
    List<DailyUtilizationReport> findDailyUtilizationReports(Long aircraftId, LocalDate date);

}
