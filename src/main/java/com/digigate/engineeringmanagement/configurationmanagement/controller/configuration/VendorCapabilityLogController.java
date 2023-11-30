package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.CapabilitySearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorCapabilityLogRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapabilityLog;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorCapabilityLogService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/configuration/vendor-capabilities-logs")
public class VendorCapabilityLogController extends AbstractSearchController<VendorCapabilityLog,
        VendorCapabilityLogRequestDto, CapabilitySearchDto> {
    public VendorCapabilityLogController(VendorCapabilityLogService vendorCapabilityLogService) {
        super(vendorCapabilityLogService);
    }
}
