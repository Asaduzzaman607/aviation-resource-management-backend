package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.WorkOrder;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAcCheckIndexViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Work Order Repository
 *
 * @author ashinisingha
 */
@Repository
public interface WorkOrderRepository extends AbstractRepository<WorkOrder> {
    @Query("select max(fd.updatedAt) from AmlFlightData fd where fd.amlId in " +
            "(select aml.id from AircraftMaintenanceLog aml where aml.amlAircraftId = :aircraftId and " +
            "aml.isActive = true)")
    Optional<LocalDate> getAircraftDataByAircraftId(Long aircraftId);

    @Query("select wo.woNo from WorkOrder wo where wo.id = ( select max(id) from  WorkOrder where aircraftId = :aircraftId ) ")
    Optional<String> getLastWorkOrderNo(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAcCheckIndexViewModel(" +
            "wo.id," +
            "wo.woNo" +
            ") from WorkOrder wo where wo.aircraftId = :aircraftId")
    List<WorkOrderAcCheckIndexViewModel> findAllWorkOrderDataByAircraftId(Long aircraftId);

    @Query("select wo from WorkOrder wo where wo.aircraftId = :aircraftId and wo.isActive = :isActive and " +
            " wo.date between :fromDate and :toDate")
    List<WorkOrder> findAllWorkOrderBetweenDateRange(Long aircraftId, Boolean isActive, LocalDate fromDate
            , LocalDate toDate);
}
