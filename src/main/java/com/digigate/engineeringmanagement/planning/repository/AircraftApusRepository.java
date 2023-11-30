package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.AircraftApus;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftApusViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AircraftApus Repository
 *
 * @author Nafiul Islam
 */
@Repository
public interface AircraftApusRepository extends JpaRepository<AircraftApus,Long> {

    @Query("select a.id from AircraftApus a where a.aircraftId = :aircraftId")
    Optional<Long> findDuplicateAircraftApuId(Long aircraftId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.AircraftApusViewModel(" +
            "ap.id," +
            "ap.aircraftId," +
            "ap.aircraft.aircraftName," +
            "ap.model," +
            "ap.date," +
            "ap.tsn," +
            "ap.csn," +
            "ap.tsr," +
            "ap.csr," +
            "ap.status" +
            ") from AircraftApus ap")
    Page<AircraftApusViewModel> findAllApu(Pageable pageable);

    AircraftApus findByAircraftId(Long aircraftId);
}
