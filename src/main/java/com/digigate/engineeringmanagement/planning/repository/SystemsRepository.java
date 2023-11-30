package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Systems;
import com.digigate.engineeringmanagement.planning.payload.response.SystemsViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 * Systems repository
 *
 * @author Nafiul Islam
 */
public interface SystemsRepository extends AbstractRepository<Systems> {
    @Query("select locationId from Systems s where s.locationId = :locationId")
    Long getByLocationId(Long locationId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.SystemsViewModel(" +
            "s.id, " +
            "s.locationId, " +
            "s.aircraftLocation.name, " +
            "s.name, " +
            "s.isActive, " +
            "s.createdAt " +
            ") from Systems s where " +
            "(:locationId is null or s.locationId = :locationId)" +
            "and  s.isActive = :isActive")
    Page<SystemsViewModel> searchSystems(Long locationId, Boolean isActive, Pageable pageable);
}
