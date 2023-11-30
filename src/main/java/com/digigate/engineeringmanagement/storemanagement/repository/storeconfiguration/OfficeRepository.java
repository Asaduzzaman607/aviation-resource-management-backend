package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Office;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OfficeRepository extends AbstractRepository<Office> {
    Optional<Office> findByIdAndIsActiveTrue(Long id);

    List<Office> findByCodeIgnoreCase(String code);

    boolean existsByLocationsIdAndIsActiveTrue(Long locationId);

    List<OfficeProjection> findByIdIn(List<Long> ids);

    Set<OfficeProjection> findByIdIn(Set<Long> ids);
}
