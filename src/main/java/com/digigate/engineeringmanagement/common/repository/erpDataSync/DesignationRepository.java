package com.digigate.engineeringmanagement.common.repository.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Designation;
import com.digigate.engineeringmanagement.common.payload.projection.DesignationProjection;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;

import java.util.List;
import java.util.Set;

public interface DesignationRepository extends AbstractRepository<Designation> {
    Set<DesignationProjection> findByIdIn(Set<Long> ids);

    List<Designation> findByErpIdIn(Set<Long> ids);
    List<Designation> findBySectionIdAndNameAndIsActiveTrue(Long ids, String name);
}
