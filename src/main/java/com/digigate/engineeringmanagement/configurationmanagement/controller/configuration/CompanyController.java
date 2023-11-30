package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CompanyDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Company;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.CompanyService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController extends AbstractSearchController<Company, CompanyDto, IdQuerySearchDto> {

    /**
     * Autowired Constructor
     *
     * @param companyService {@link CompanyService}
     */
    public CompanyController(CompanyService companyService) {
        super(companyService);
    }
}
