package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import com.digigate.engineeringmanagement.planning.entity.AcCancellations;
import com.digigate.engineeringmanagement.planning.payload.response.AcCancellationsReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AcCancellationsViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * AcCancellations Repository
 *
 * @author Nafiul Islam
 */
@Repository
public interface AcCancellationsRepository extends AbstractRepository<AcCancellations> {

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AcCancellationsReportViewModel(" +
            "ac.id, " +
            "ac.cancellationTypeEnum, " +
            "ac.date, " +
            "ac.isActive, " +
            "ac.createdAt " +
            ") from AcCancellations ac" +
            " join AircraftModel am on am.id = ac.aircraftModelId " +
            " where ac.aircraftModelId = :aircraftModelId" +
            " and  ac.isActive = :isActive " +
            " and (:startDate is null or :endDate is null or (ac.date between :startDate and :endDate)) ")
    Page<AcCancellationsReportViewModel> searchAircraftCancellation(Long aircraftModelId, LocalDate startDate,
                                                                    LocalDate endDate, Boolean isActive,
                                                                    Pageable pageable);


    @Query("select ac " +
            "from AcCancellations ac" +
            " where ac.aircraftModelId = :aircraftModelId and ac.isActive = true " +
            " and (:startDate is null or :endDate is null or (ac.date between :startDate and :endDate)) ")
    List<AcCancellations> findDateWiseCancellation(Long aircraftModelId,LocalDate startDate, LocalDate endDate);


    @Query("select ac " +
            "from AcCancellations ac" +
            " join AircraftModel am on am.id = ac.aircraftModelId " +
            " where ac.aircraftModelId = :aircraftModelId" +
            " and  ac.isActive = true" +
            " and ac.cancellationTypeEnum = :initialCancellation" +
            " and (:startDate is null or :endDate is null or (ac.date between :startDate and :endDate)) ")
    List<AcCancellations> findDateWiseInitialCancellation(Long aircraftModelId, LocalDate startDate, LocalDate endDate,
                                                          CancellationTypeEnum initialCancellation);
}
