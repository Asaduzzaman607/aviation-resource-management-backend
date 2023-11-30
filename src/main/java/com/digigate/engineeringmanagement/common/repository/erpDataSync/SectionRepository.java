package com.digigate.engineeringmanagement.common.repository.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.payload.projection.SelectionProjection;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;

import java.util.List;
import java.util.Set;

public interface SectionRepository extends AbstractRepository<Section> {
    Set<SelectionProjection> findByIdIn(Set<Long> ids);

    List<Section> findByErpIdIn(Set<Long> ids);
    List<Section> findByDepartmentIdAndNameAndIsActiveTrue(Long deptId, String name);
}
