package com.digigate.engineeringmanagement.common.repository.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.payload.projection.EmployeeProjection;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;

import java.util.List;
import java.util.Set;

public interface EmployeeRepository extends AbstractRepository<Employee> {
    List<EmployeeProjection> findByIdIn(Set<Long> empIds);

    List<Employee> findByErpIdIn(Set<Long> ids);
    List<Employee> findByDesignationIdAndNameAndIsActiveTrue(Long id, String name);
}
