package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.EngineModel;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelSearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Engine model controller
 *
 * @author Pranoy Das
 */
@RestController
@RequestMapping("/api/engine/model")
public class EngineModelController extends AbstractSearchController<EngineModel, EngineModelDto, EngineModelSearchDto> {

    /**
     * Parameterized constructor
     *
     * @param service {@link ISearchService}
     */
    public EngineModelController(ISearchService<EngineModel, EngineModelDto, EngineModelSearchDto> service) {
        super(service);
    }
}
