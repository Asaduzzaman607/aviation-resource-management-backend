package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.DashboardItem;
import com.digigate.engineeringmanagement.planning.payload.response.DueResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DashboardItem Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface DashboardItemRepository extends JpaRepository<DashboardItem, Long> {


    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.DueResponse(" +
            "d.id," +
            "d.dueDate," +
            "d.calenderDueDate," +
            "d.itemType," +
            "d.nextDueHour" +
            ") " +
            "from DashboardItem d where d.aircraftId=:aircraftId")
    List<DueResponse> findDueListByAircraftId(Long aircraftId);

}
