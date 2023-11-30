package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorWiseClientListResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VendorWiseClientService {

    void saveAll(Vendor vendor, List<Long> clientList);

    void updateAll(List<Long> clientList, Vendor vendor);

    List<VendorWiseClientListResponseDto> getAllResponse(Set<Long> vendorIds);
}
