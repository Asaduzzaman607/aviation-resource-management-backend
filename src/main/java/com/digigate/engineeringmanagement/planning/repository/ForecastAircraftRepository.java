package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.ForecastAircraft;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastAircraftDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * ForecastAircraftRepository
 *
 * @author Masud Rana
 */
@Repository
public interface ForecastAircraftRepository extends JpaRepository<ForecastAircraft, Long> {
    @Query("select new com.digigate.engineeringmanagement.planning.payload.request" +
            ".ForecastAircraftDto(fa.id, fa.aircraftId, fa.forecastId) " +
            "from ForecastAircraft fa where fa.forecastId= :forecastId")
    List<ForecastAircraftDto> findByForecastId(Long forecastId);
}
