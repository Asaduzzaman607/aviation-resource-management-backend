package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CityDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.CityService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
public class CityController extends AbstractSearchController<City, CityDto, IdQuerySearchDto> {
    public CityController(CityService service) {
        super(service);
    }
}
