package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.AmlBook;
import com.digigate.engineeringmanagement.planning.payload.request.AmlBookDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlBookSearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AML Book Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/aml-book")
public class AmlBookController extends AbstractSearchController<AmlBook, AmlBookDto, AmlBookSearchDto> {

    /**
     * Parameterized constructor
     *
     * @param iSearchService {@link ISearchService<AmlBook, AmlBookDto, AmlBookSearchDto>}
     */
    public AmlBookController(ISearchService<AmlBook, AmlBookDto, AmlBookSearchDto> iSearchService) {
        super(iSearchService);
    }
}
