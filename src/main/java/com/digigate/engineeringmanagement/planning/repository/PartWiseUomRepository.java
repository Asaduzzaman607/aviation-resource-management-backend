package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.PartWiseUom;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartWiseUomProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PartWiseUomRepository extends AbstractRepository<PartWiseUom> {
    List<PartWiseUomProjection> findAllByPartIdInAndIsActiveTrue(Set<Long> ids);
    List<PartWiseUom> findAllByPartId(Long id);
}
