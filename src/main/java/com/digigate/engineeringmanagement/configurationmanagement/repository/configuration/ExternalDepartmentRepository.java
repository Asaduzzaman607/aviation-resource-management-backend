package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.ExternalDepartmentProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ExternalDepartmentRepository extends AbstractRepository<Vendor> {
    List<ExternalDepartmentProjection> findExternalDepartmentByIdIn(Set<Long> ids);
}
