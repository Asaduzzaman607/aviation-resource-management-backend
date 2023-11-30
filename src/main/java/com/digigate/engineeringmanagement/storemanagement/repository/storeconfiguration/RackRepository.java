package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Rack;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRow;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RackRepository extends AbstractRepository<Rack> {

    boolean existsByRoomIdAndIsActiveTrue(Long roomId);

    Set<RackRowProjection> findByIdIn(Set<Long> ids);

    List<Rack> findByRoomIdAndCodeIgnoreCaseAndIsActiveTrue(Long roomId, String rackCode);

    List<Rack> findByIdIn(List<Long> ids);
}
