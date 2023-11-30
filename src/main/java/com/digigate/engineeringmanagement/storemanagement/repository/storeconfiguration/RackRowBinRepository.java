package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRowBin;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Room;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRowBinRepository extends AbstractRepository<RackRowBin> {
    boolean existsByRackRowIdAndIsActiveTrue(Long id);
    List<RackRowBin> findByRackRowIdAndCodeIgnoreCaseAndIsActiveTrue(Long rackRowId, String code);
    List<RackRowBin> findByIdIn(List<Long> ids);
}
