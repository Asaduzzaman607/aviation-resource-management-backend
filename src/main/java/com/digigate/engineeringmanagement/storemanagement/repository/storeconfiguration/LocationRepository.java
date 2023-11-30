package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Location;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.LocationProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LocationRepository extends AbstractRepository<Location> {

    boolean existsByCityIdAndIsActiveTrue(Long cityId);

    List<Location> findByCode(String code);

    Set<LocationProjection> findByIdIn(Set<Long> locationIds);
}
