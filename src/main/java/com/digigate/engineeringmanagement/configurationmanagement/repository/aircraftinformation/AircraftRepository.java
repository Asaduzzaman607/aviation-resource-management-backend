package com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ApuAvailableAircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AircraftProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AircraftRepository extends AbstractRepository<Aircraft> {
    boolean existsByAircraftModelIdAndIsActiveTrue(Long id);

    List<AircraftProjection> findAircraftByIdIn(Set<Long> aircraftIds);

    List<Aircraft> findAllByIsActive(Boolean isActive);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DailyHrsReportAircraftModel(" +
            "aircraft.aircraftName, " +
            "aircraft.airframeSerial, " +
            "aircraft.airFrameTotalTime, " +
            "aircraft.airframeTotalCycle, " +
            "aircraft.bdTotalTime, " +
            "aircraft.bdTotalCycle," +
            "aircraft.aircraftModelId," +
            "aircraft.aircraftCheckDoneHour," +
            "aircraft.aircraftCheckDoneDate" +
            ") " +
            "FROM Aircraft aircraft " +
            "where aircraft.id=:id")
    DailyHrsReportAircraftModel findByAircraftId(Long id);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.UtilizationReportResponse(" +
            "aircraft.aircraftName, " +
            "aircraft.airframeSerial ) " +
            "from Aircraft aircraft " +
            "where aircraft.id = :aircraftId " +
            "and aircraft.isActive = true")
    Optional<UtilizationReportResponse> findUtilizationReportHeaderByAircraftId(Long aircraftId);

    List<Aircraft> findAllByAircraftModelIdAndIsActive(Long acModelId, boolean isActive);

    Page<Aircraft> findAllByAircraftModelIdAndIsActive(Long acModelId, boolean isActive, Pageable pageable);

    @Query("select " +
            "aircraft.aircraftModel.id  " +
            "from Aircraft aircraft " +
            "where  aircraft.id = :aircraftId")
    Long findAircraftModelIdByAircraftId(Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlLastPageAndAircraftInfo(" +
            "ar.airFrameTotalTime, " +
            "ar.airframeTotalCycle," +
            "ar.totalApuHours) " +
            "from Aircraft ar where ar.id = :aircraftId")
    AmlLastPageAndAircraftInfo findAircraftInfo(Long aircraftId);

    @Query("SELECT aircraft.id FROM Aircraft aircraft WHERE aircraft.aircraftName = :name")
    Optional<Long> findAircraftIdByAircraftName(String name);
    @Query("SELECT aircraft.id  FROM Aircraft aircraft WHERE aircraft.airframeSerial = :serial")
    Optional<Long> findAircraftIdByAircraftSerial(String serial);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AdReportTitleDataViewModel(" +
            "ar.airframeTotalCycle," +
            "ar.airFrameTotalTime," +
            "ar.aircraftName," +
            "ar.airframeSerial,"+
            "ar.updatedAt) " +
            "from Aircraft ar where ar.id = :aircraftId")
    AdReportTitleDataViewModel fimdAdReportTitleDataByAircraft(Long aircraftId);

    List<AircraftProjection> findAircraftByIdInAndIsActiveTrue(Set<Long> aircraftIds);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftDropdownViewModel(" +
            "ar.id, " +
            "ar.aircraftName) " +
            "from Aircraft ar where ar.isActive = true")
    List<AircraftDropdownViewModel> findAllActiveAircraft();

    @Query("select new com.digigate.engineeringmanagement.configurationmanagement.dto.response.ApuAvailableAircraftViewModel(" +
            "ar.id, " +
            "ar.aircraftName, " +
            "ar.airframeSerial, " +
            "ar.airFrameTotalTime, " +
            "ar.bdTotalTime, " +
            "ar.airframeTotalCycle, " +
            "ar.bdTotalCycle, " +
            "ar.dailyAverageHours, " +
            "ar.dailyAverageCycle, " +
            "ar.dailyAverageApuHours, " +
            "ar.dailyAverageApuCycle, " +
            "ar.totalApuHours, " +
            "ar.totalApuCycle " +
            ") from Aircraft ar where ar.isActive = true and ar.totalApuHours >= 0")
    List<ApuAvailableAircraftViewModel> findAvailableApuAircraft();
    
    Long countByAircraftModelIdAndIsActiveTrue(Long aircraftModelId);

    @Query("select ar from Aircraft ar join AircraftBuild ab on ab.aircraftId = ar.id " +
            "where ab.id = :aircraftBuildId ")
    Aircraft getAircraftByAircraftBuildId(Long aircraftBuildId);

    @Query("select ar from Aircraft ar where ar.id in :aircraftIds")
    List<Aircraft> findByIds(Set<Long> aircraftIds);
}

