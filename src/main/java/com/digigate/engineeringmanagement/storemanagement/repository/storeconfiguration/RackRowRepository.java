package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRow;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRowBin;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowBinProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RackRowRepository extends AbstractRepository<RackRow> {
    boolean existsByRackIdAndIsActiveTrue(Long id);

    Set<RackRowBinProjection> findByIdIn(Set<Long> ids);

    List<RackRow> findByRackIdAndCodeIgnoreCaseAndIsActiveTrue(Long rackId, String rackRowCode);

    List<RackRow> findByIdIn(List<Long> ids);
}
