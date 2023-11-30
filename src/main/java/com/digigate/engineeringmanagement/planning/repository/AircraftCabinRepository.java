package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AircraftCabin;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCabinViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *  Aircraft cabin repository
 *
 * @author Pranoy Das
 */
@Repository
public interface AircraftCabinRepository extends AbstractRepository<AircraftCabin> {
    @Query("SELECT new com.digigate.engineeringmanagement.planning.payload.response.AircraftCabinViewModel(ac.id, " +
            "ac.cabin.id, CONCAT(ac.cabin.code, ' - ', ac.cabin.title), ac.aircraft.id, ac.aircraft.aircraftName, " +
            "ac.noOfSeats, ac.isActive) " +
            "FROM AircraftCabin ac WHERE " +
            "(:aircraftId is null OR ac.aircraft.id = :aircraftId) AND " +
            "(:cabinId is null OR ac.cabin.id = :cabinId) AND " +
            "(:activeStatus is null OR ac.isActive = :activeStatus)"
    )
    Page<AircraftCabinViewModel> findAircraftCabinBySearchCriteria
            (Long aircraftId, Long cabinId, Boolean activeStatus, Pageable pageable);

    @Query("SELECT ac.id FROM AircraftCabin ac where ac.aircraftId = :aircraftId and ac.cabinId = :cabinId ")
    Optional<Long> findByAircraftIdAndCabinId(Long aircraftId, Long cabinId);
}
