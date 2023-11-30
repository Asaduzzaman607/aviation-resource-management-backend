package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface VendorRepository extends AbstractRepository<Vendor> {
    List<VendorProjection> findByIdIn(Set<Long> vendorIdList);

    Vendor findByIdAndVendorType(Long id, VendorType vendorType);

    List<Vendor> findByValidTillBefore(LocalDate currentDate);
}
