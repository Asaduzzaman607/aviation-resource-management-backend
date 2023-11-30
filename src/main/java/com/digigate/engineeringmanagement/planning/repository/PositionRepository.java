package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PositionProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PositionRepository extends AbstractRepository<Position> {
    @Query("select p from Position p where p.name = :name")
    Optional<Position> findByName(@Param("name") String name);

    @Query(" select p.name from Position p where p.isActive= true")
    Set<String> getAllNames();

    Set<Position> findAllByIsActiveTrue();

    List<PositionProjection> findAirportByIdIn(Set<Long> collectionsOfPositionIds);
}
