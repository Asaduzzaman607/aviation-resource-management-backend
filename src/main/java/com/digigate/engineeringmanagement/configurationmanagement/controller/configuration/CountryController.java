package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CountryDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.CountryService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/countries")
public class CountryController extends AbstractSearchController<Country, CountryDto, IdQuerySearchDto> {
    public CountryController(CountryService service) { super(service); }
}
