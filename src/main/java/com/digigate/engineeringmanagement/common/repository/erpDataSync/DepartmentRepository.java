package com.digigate.engineeringmanagement.common.repository.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.DepartmentProjection;

import java.util.List;
import java.util.Set;

public interface DepartmentRepository extends AbstractRepository<Department> {
    Set<DepartmentProjection> findByIdIn(Set<Long> departmentIds);

    List<Department> findByErpIdIn(Set<Long> ids);
    List<Department> findByNameAndIsActiveTrue(String name);
}
