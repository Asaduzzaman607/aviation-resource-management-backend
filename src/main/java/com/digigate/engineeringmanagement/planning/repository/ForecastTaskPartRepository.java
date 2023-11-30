package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.ForecastTaskPart;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskPartDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * ForecastTaskPartRepository
 *
 * @author Masud Rana
 */
@Repository
public interface ForecastTaskPartRepository extends JpaRepository<ForecastTaskPart, Long> {
    List<ForecastTaskPart> findByIdIn(Set<Long> ids);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskPartDto(" +
            "ftp.id, " +
            "ftp.partId, " +
            "ftp.quantity, " +
            "ftp.ipcRef, " +
            "ftp.forecastTaskId) " +
            "from ForecastTaskPart ftp where ftp.forecastTaskId in :forecastTaskIds")
    Set<ForecastTaskPartDto> findByForecastTaskIdIn(Set<Long> forecastTaskIds);
}
