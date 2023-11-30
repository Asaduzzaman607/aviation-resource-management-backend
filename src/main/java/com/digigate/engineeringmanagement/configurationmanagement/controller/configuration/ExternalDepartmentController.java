package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.ExternalDepartmentDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.ExternalDepartmentService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/config/external/departments")
public class ExternalDepartmentController extends AbstractSearchController<Vendor, ExternalDepartmentDto, IdQuerySearchDto> {
    public ExternalDepartmentController(ExternalDepartmentService service) {
        super(service);
    }
}
