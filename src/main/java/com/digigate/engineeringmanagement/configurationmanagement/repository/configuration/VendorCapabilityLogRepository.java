package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapabilityLog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorCapabilityLogRepository extends AbstractRepository<VendorCapabilityLog> {
    List<VendorCapabilityLog> findAllByVendorId(Long id);

    List<VendorCapabilityLog> findAllByVendorIdIn(Set<Long> ids);
}
