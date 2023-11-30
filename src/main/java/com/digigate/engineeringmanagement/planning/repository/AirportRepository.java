package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AirportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Airport repository
 *
 * @author ashiniSingha
 */
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport>findByIdAndIsActiveTrue(long id);
    Optional<Airport> findById(long id);
    Boolean existsByIataCode(String iataCode);

    @Query( "SELECT air FROM Airport air WHERE " +
            " (:name is null or :name = '' or air.name LIKE :name% )  " +
            " AND (:iataCode is null or :iataCode = '' or air.iataCode LIKE :iataCode% ) " +
            " AND ( :isActive is null or  air.isActive = :isActive )  ")
    Page<Airport> findAirportBySearchCriteria(
           @Param("name") String name,
           @Param("iataCode") String iataCode,
           @Param("isActive") Boolean isActive,
           Pageable pageable
    );

    List<Airport>findAllByIdInAndIsActiveTrue(Set<Long> ids);
    List<Airport> findAll();

    List<AirportProjection> findAirportByIdIn(Set<Long> airportIds);
}
