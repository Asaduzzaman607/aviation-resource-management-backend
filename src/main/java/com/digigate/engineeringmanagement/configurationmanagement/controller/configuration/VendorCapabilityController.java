package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorCapabilityRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapability;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorCapabilityService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/configuration/vendor-capabilities")
public class VendorCapabilityController extends AbstractSearchController<VendorCapability, VendorCapabilityRequestDto,
        IdQuerySearchDto> {
    public VendorCapabilityController(VendorCapabilityService vendorCapabilityService) {
        super(vendorCapabilityService);
    }
}
