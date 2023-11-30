package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Check;
import com.digigate.engineeringmanagement.planning.payload.request.CheckDto;
import com.digigate.engineeringmanagement.planning.payload.request.CheckSearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Check Controller
 *
 * @author Ashraful
 */
@RestController
@RequestMapping("/api/check")
public class CheckController extends AbstractSearchController<Check, CheckDto, CheckSearchDto> {

    /**
     * Parameterized constructor
     *
     * @param service {@link IService}
     */
    public CheckController(ISearchService<Check, CheckDto, CheckSearchDto> service) {
        super(service);
    }
}
