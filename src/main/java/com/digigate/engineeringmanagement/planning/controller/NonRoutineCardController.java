package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.NonRoutineCard;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardDto;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardSearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Non Routine Card Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/non-routine-card")
public class NonRoutineCardController extends AbstractSearchController<NonRoutineCard, NonRoutineCardDto,
        NonRoutineCardSearchDto> {

    /**
     * Parameterized Constructor
     *
     * @param iSearchService {@link ISearchService}
     */
    public NonRoutineCardController(ISearchService<NonRoutineCard, NonRoutineCardDto, NonRoutineCardSearchDto> iSearchService) {
        super(iSearchService);
    }
}

