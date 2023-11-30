package com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AircraftModelRepository extends AbstractRepository<AircraftModel> {
    List<AircraftModel> findByAircraftModelName(String aircraftModelName);

    boolean existsByAircraftModelName(String aircraftModelName);

    @Query(value = "select am.id from AircraftModel am " +
            "join Aircraft a on am.id = a.aircraftModel.id " +
            "where a.id = :aircraftId")
    Optional<Long> findAircraftModelIdByAircraftId(@Param("aircraftId") long aircraftId);

    @Query(" SELECT am from AircraftModel am " +
            " WHERE am.id = :aircraftId AND am.isActive = true ")
    Optional<AircraftModel>findNameByAircraftId( @Param("aircraftId") Long aircraftId );

}
