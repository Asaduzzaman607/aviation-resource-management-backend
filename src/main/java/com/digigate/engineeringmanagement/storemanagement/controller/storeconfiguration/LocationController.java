package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Location;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.LocationDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.LocationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/locations")
public class LocationController extends AbstractSearchController<Location, LocationDto, IdQuerySearchDto> {
    public LocationController(LocationService service) {
        super(service);
    }
}
