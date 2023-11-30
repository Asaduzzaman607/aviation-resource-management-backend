package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorWiseClientList;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorWiseClientListRepository extends AbstractRepository<VendorWiseClientList> {
    List<VendorWiseClientList> findAllByVendorId(Long id);

    List<VendorWiseClientList> findAllByVendorIdInAndIsActiveTrue(Set<Long> vendorIds);
}
