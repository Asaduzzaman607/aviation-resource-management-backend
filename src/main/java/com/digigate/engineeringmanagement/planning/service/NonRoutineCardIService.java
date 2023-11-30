package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.NonRoutineCard;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardDto;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardSearchDto;

public interface NonRoutineCardIService extends ISearchService<NonRoutineCard, NonRoutineCardDto,
        NonRoutineCardSearchDto> {
}
