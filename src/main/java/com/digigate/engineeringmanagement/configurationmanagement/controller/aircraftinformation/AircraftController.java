package com.digigate.engineeringmanagement.configurationmanagement.controller.aircraftinformation;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.AircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ApuAvailableAircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.AircraftInfoViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftIService;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftDropdownViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftEffectivityTypeViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
public class AircraftController extends AbstractController<Aircraft, AircraftDto> {

    private final AircraftIService aircraftIService;

    /**
     * Constructor parameterized
     *
     * @param aircraftIService {@link AircraftIService}
     * @param service          {@link IService }
     */
    public AircraftController(AircraftIService aircraftIService,
                              ISearchService<Aircraft, AircraftDto,
                                      IdQuerySearchDto> service) {
        super(service);
        this.aircraftIService = aircraftIService;
    }

    /**
     * All Aircraft API
     *
     * @return List of Aircraft {@link AircraftViewModel}
     */
    @GetMapping("/all")
    public List<AircraftViewModel> getAllAircraft() {
        return aircraftIService.getAllAircraft();
    }

    @GetMapping("/find-all-apu_available_aircraft")
    public List<ApuAvailableAircraftViewModel> getAllApuAvailableAircraft(){
        return aircraftIService.getAllApuAvailableAircraft();
    }

    /**
     * All Aircraft By AircraftModelId API
     *
     * @return List of Aircraft {@link AircraftViewModel}
     */
    @GetMapping("/all/{acModelId}")
    public List<AircraftEffectivityTypeViewModel> getAllAircraftByModelId(@PathVariable Long acModelId) {
        return aircraftIService.getAllAircraftByAcModelId(acModelId);
    }


    /**
     * find AircraftInfo aircraftId  API
     *
     * @return  {@link AircraftInfoViewModel}
     */
    @GetMapping("/info/{aircraftId}")
    public AircraftInfoViewModel getAircraftInfo(@PathVariable Long aircraftId) {
        return aircraftIService.findAircraftInfoData(aircraftId);
    }

    /**
     * All Aircraft API
     *
     * @return List of Aircraft {@link AircraftDropdownViewModel}
     */
    @GetMapping("/find-all-active_aircraft")
    public List<AircraftDropdownViewModel> getAllActiveAircraft() {
        return aircraftIService.getAllActiveAircraft();
    }
    @PostMapping("/search")
    public PageData search(@Valid @RequestBody IdQuerySearchDto searchDto,
                                                @PageableDefault(sort = ApplicationConstant.AIRCRAFT_NAME,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return aircraftIService.search(searchDto,pageable);
    }

}
