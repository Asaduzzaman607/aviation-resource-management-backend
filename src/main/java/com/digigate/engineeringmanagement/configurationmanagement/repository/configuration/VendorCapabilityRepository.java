package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapability;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorCapabilityRepository extends AbstractRepository<VendorCapability> {
    List<VendorCapability> findByIdInAndIsActiveTrue(Set<Long> ids);
}
