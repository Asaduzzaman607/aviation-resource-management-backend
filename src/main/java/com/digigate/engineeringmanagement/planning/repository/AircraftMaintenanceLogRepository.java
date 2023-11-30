package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.payload.request.DailyAirtimeCycle;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Aircraft maintenance log repository
 *
 * @author Pranoy Das
 */
@Repository
public interface AircraftMaintenanceLogRepository extends AbstractRepository<AircraftMaintenanceLog> {
    @Query("SELECT aml.id from AircraftMaintenanceLog aml where aml.pageNo = :pageNo " +
            "and aml.amlAircraftId = :aircraftId and aml.alphabet = :alphabet")
    List<Long> findAllAmlIdsByPageNoAndAircraftAndAlphabet(Integer pageNo, Long aircraftId, Character alphabet);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlDropdownViewModel(aml.id, " +
            "aml.pageNo, aml.alphabet) FROM AircraftMaintenanceLog aml WHERE aml.isActive = true")
    List<AmlDropdownViewModel> findAllActiveAml();

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DailyHrsReportAircraftModel(" +
            "df.aircraft.airFrameTotalTime, " +
            "df.aircraft.airframeTotalCycle, " +
            "df.aircraft.bdTotalTime, " +
            "df.aircraft.bdTotalCycle )" +
            " FROM AircraftMaintenanceLog df" +
            " where df.date = :date and df.aircraft.id = :id")
    List<DailyHrsReportAircraftModel> findAircraftByDateAndAmlId(LocalDate date, Long id);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DailyHrsReportBfDto(" +
            "df.flightData.grandTotalAirTime, " +
            "df.flightData.grandTotalLanding )" +
            " FROM AircraftMaintenanceLog df" +
            " where df.date = :date and df.aircraft.id = :aircraftId")
    List<DailyHrsReportBfDto> findLastByDateAndAircraftIdOrderByIdDesc(LocalDate date, Long aircraftId);

    Page<AircraftMaintenanceLog> findByDateAndAircraftId(LocalDate date, Long id, Pageable pageable);

    List<AircraftMaintenanceLog> findByDateAndAircraftId(LocalDate date, Long id);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.SectorWiseUtilizationReportDto(" +
            "aml.fromAirport.iataCode, " +
            "aml.toAirport.iataCode, " +
            "aml.flightNo, " +
            "aml.flightData.airTime, " +
            "aml.flightData.noOfLanding )" +
            "from AircraftMaintenanceLog aml " +
            "where aml.aircraft.id = :aircraftId " +
            "and aml.date IS NOT NULL and aml.flightData IS NOT NULL " +
            "and aml.fromAirport IS NOT NULL and aml.toAirport IS NOT NULL " +
            "and aml.date between :startDate and :endDate ")
    List<SectorWiseUtilizationReportDto> findAllAmlByAircraftIdAndDate(Long aircraftId,
                                                                       LocalDate startDate,
                                                                       LocalDate endDate);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlDropdownViewModel(" +
            "aml.pageNo, aml.alphabet) " +
            "from AircraftMaintenanceLog aml where " +
            "aml.createdAt = (select max(aml2.createdAt) " +
            "from AircraftMaintenanceLog aml2 where aml2.amlAircraftId = :aircraftId)")
    AmlDropdownViewModel findMaxAmlPageByAircraftId(Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlDropdownViewModel(" +
            "aml.pageNo, aml.alphabet) " +
            "from AircraftMaintenanceLog aml where " +
            "aml.createdAt = (select max(aml2.createdAt) " +
            "from AircraftMaintenanceLog aml2 where aml2.amlAircraftId = :aircraftId and aml2.alphabet is null)")
    AmlDropdownViewModel findMaxAmlPageWithoutAlphabetByAircraftId(Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlFlightDataVerify(" +
            "aml.id, aml.pageNo, fd.id) " +
            "from AircraftMaintenanceLog aml left join AmlFlightData fd on fd.amlId = aml.id where " +
            "aml.createdAt = (select max(aml2.createdAt) " +
            "from AircraftMaintenanceLog aml2 where aml2.amlAircraftId = :aircraftId)")
    AmlFlightDataVerify findMaxAmlPageWithFlightDataAndAmlType(Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlFlightDataVerify(" +
            "fd.id, aml.amlType) " +
            "from AircraftMaintenanceLog aml left join AmlFlightData fd on fd.amlId = aml.id where " +
            "aml.id = :amlId")
    AmlFlightDataVerify findAmlTypeWithFlightDataId(Long amlId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.OilUpLiftReportViewModel(" +
            "aml.id, " +
            "aml.fromAirportId, " +
            "aml.date, " +
            "aml.pageNo, " +
            "aml.alphabet, " +
            "aml.amlType) " +
            "FROM AircraftMaintenanceLog aml " +
            "where aml.amlAircraftId = :aircraftId " +
            "and aml.date between :startDate and :endDate ")
    Page<OilUpLiftReportViewModel> findAllByAmlAircraftIdAndDate(Long aircraftId,
                                                                 LocalDate startDate, LocalDate endDate,
                                                                 Pageable pageable);

    @Query("SELECT aml.id from AircraftMaintenanceLog aml where aml.pageNo = :pageNo " +
            "and aml.amlAircraftId = :aircraftId")
    Set<Long> findAllByAircraftIdAndPageNo(Long aircraftId, Integer pageNo);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlLastPageAndAircraftInfo(" +
            "aml.pageNo, aml.alphabet) " +
            "from AircraftMaintenanceLog aml " +
            "where aml.pageNo = :pageNo and aml.amlAircraftId = :aircraftId " +
            "and aml.alphabet is not null order by aml.createdAt asc")
    List<AmlLastPageAndAircraftInfo> findAllAmlByPageNo(Integer pageNo, Long aircraftId);

    @Query("SELECT aml from AircraftMaintenanceLog aml " +
            "where aml.pageNo >= :pageNo and aml.amlAircraftId = :aircraftId " +
            "and aml.isActive = true order by aml.pageNo asc, aml.alphabet asc")
    List<AircraftMaintenanceLog> findAllNextAmlsWithCurrentAml(Integer pageNo, Long aircraftId);

    @Query("SELECT aml from AircraftMaintenanceLog aml where aml.pageNo > :pageNo " +
            " and aml.amlAircraftId=:aircraftId and aml.isActive = true order by aml.pageNo asc, aml.alphabet asc")
    List<AircraftMaintenanceLog> findAllNextAmls(Integer pageNo, Long aircraftId);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AmlLastPageAndAircraftInfo(" +
            "aml.flightData.grandTotalAirTime, aml.flightData.grandTotalLanding) " +
            "from AircraftMaintenanceLog aml where " +
            "aml.createdAt = (select max(aml2.createdAt) " +
            "from AircraftMaintenanceLog aml2 where aml2.pageNo = :pageNo and aml2.amlAircraftId = :aircraftId)")
    AmlLastPageAndAircraftInfo findAirframeInfoByPageNo(Integer pageNo, Long aircraftId);

    @Query("SELECT aml.date " +
            "from AircraftMaintenanceLog aml where " +
            "aml.pageNo < :pageNo and aml.amlAircraftId = :aircraftId and aml.alphabet is null and aml.isActive = true")
    Page<LocalDate> findPreviousAmlDate(Long aircraftId, Integer pageNo, Pageable pageable);

    @Query("SELECT aml.date " +
            "from AircraftMaintenanceLog aml where " +
            "aml.pageNo = :pageNo and aml.amlAircraftId = :aircraftId " +
            "and (aml.alphabet is null or aml.alphabet < :alphabet) and aml.isActive = true")
    Page<LocalDate> findPreviousAmlDate(Long aircraftId, Integer pageNo, Character alphabet, Pageable pageable);

    @Query("SELECT aml.date " +
            "from AircraftMaintenanceLog aml where " +
            "aml.pageNo >= :pageNo and aml.amlAircraftId = :aircraftId " +
            "and ((aml.alphabet is null and aml.pageNo > :pageNo) or aml.alphabet > :alphabet) and aml.isActive = true")
    Page<LocalDate> findNextAmlDate(Long aircraftId, Integer pageNo, Character alphabet, Pageable pageable);

    @Query("SELECT aml.date " +
            "from AircraftMaintenanceLog aml where " +
            "aml.amlAircraftId = :aircraftId " +
            "and ((aml.alphabet is null and aml.pageNo > :pageNo) or " +
            "(aml.alphabet is not null and aml.pageNo = :pageNo)) and aml.isActive = true")
    Page<LocalDate> findNextAmlDate(Long aircraftId, Integer pageNo, Pageable pageable);

    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftMaintenanceViewModel(" +
            "aml.id,aml.pageNo, aml.alphabet, aml.date) " +
            "from AircraftMaintenanceLog aml " +
            "where aml.amlAircraftId = :aircraftId " +
            "and  aml.isActive is true order by aml.pageNo desc, aml.alphabet desc ")
    Page<AircraftMaintenanceViewModel> findTopAml(Long aircraftId, Pageable pageable);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcFlightStatisticData(" +
            "a.date," +
            "a.amlAircraftId," +
            "fd.airTime," +
            "fd.noOfLanding" +
            ") " +
            "" +
            "from AircraftMaintenanceLog a" +
            " join AircraftModel am on am.id = a.aircraft.aircraftModelId " +
            " join AmlFlightData fd on fd.amlId = a.id " +
            " where  a.amlType = :amlType and a.date between :from and :to " +
            "and am.id = :aircraftIdModelId order by a.date asc")
    List<AcFlightStatisticData> getAcFlightStatData(LocalDate from, LocalDate to, Long aircraftIdModelId, AmlType amlType);

    @Query(value = "select new com.digigate.engineeringmanagement.planning.payload.response.MonthData(" +
            " month(a.date)," +
            " year(a.date)" +
            ")" +
            " from AircraftMaintenanceLog a" +
            " join AircraftModel am on am.id = a.aircraft.aircraftModelId " +
            " where a.amlType = :amlType and  a.date between :from and :to " +
            "and am.id = :aircraftIdModelId group by year(a.date), month(a.date) order by year(a.date) asc")
    Set<MonthData> getMonths(LocalDate from, LocalDate to, Long aircraftIdModelId, AmlType amlType);

    @Query("SELECT aml FROM AircraftMaintenanceLog aml WHERE aml.amlAircraftId = :aircraftId")
    List<AircraftMaintenanceLog> findAmls(Long aircraftId);


    @Query(" select new com.digigate.engineeringmanagement.planning.payload.request.DailyAirtimeCycle(" +
            "fd.totalApuHours," +
            "fd.totalApuCycles," +
            "fd.id" +
            ") from AircraftMaintenanceLog aml join AmlFlightData fd on fd.amlId = aml.id  " +
            " where  aml.amlAircraftId = :aircraftId and aml.date <= :date order by fd.id asc")
    List<DailyAirtimeCycle> findFlightDataApuHourCyclesByAircraftId(Long aircraftId, LocalDate date);

    @Query("SELECT CASE WHEN COUNT(aml) > 0 THEN true ELSE false END " +
            "FROM AircraftMaintenanceLog aml " +
            "WHERE aml.date = :date and aml.amlAircraftId = :amlAircraftId ")
    Boolean findAmlByDate(Long amlAircraftId, LocalDate date);


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AmlTatTacDto(" +
            "max (fd.grandTotalAirTime)," +
            "max (fd.grandTotalLanding)" +
            ") from AmlFlightData fd join AircraftMaintenanceLog a on fd.amlId = a.id " +
            " where a.amlAircraftId = :aircraftId and a.date = :date and fd.isActive = true")
    AmlTatTacDto finMaxTatTacByDate(Long aircraftId, LocalDate date);
    @Query("select aml from AircraftMaintenanceLog aml where aml.amlAircraftId = :aircraftId and aml.id > :lastAmlId")
    List<AircraftMaintenanceLog> findNextAmlIds(Long lastAmlId, Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AmlPageViewModel(" +
            "aml.id, " +
            "aml.pageNo, " +
            "aml.alphabet, " +
            "aml.date " +
            ") from AircraftMaintenanceLog aml where aml.amlAircraftId = :aircraftId ")
    List<AmlPageViewModel> getAmlPageAndAlphabets(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DefectRectViewModel(" +
            "ad.seqNo, " +
            "ad.defectDescription, " +
            "ad.rectDescription " +
            ") from AMLDefectRectification ad where ad.aircraftMaintenanceLogId = :amlId")
    List<DefectRectViewModel> getInterruptionInfo(Long amlId);
}
