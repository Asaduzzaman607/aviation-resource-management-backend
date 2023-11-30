package com.digigate.engineeringmanagement.configurationmanagement.controller.aircraftinformation;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftModelDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aircraft/models")
public class AircraftModelController extends AbstractSearchController<
        AircraftModel,
        AircraftModelDto,
        IdQuerySearchDto> {

    private final AircraftModelService aircraftModelService;

    /**
     * Autowired constructor
     *
     * @param aircraftModelService {@link AircraftModelService}
     */
    public AircraftModelController(AircraftModelService aircraftModelService) {
        super(aircraftModelService);
        this.aircraftModelService = aircraftModelService;
    }
}
