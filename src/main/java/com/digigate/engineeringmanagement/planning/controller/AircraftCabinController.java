package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.planning.entity.AircraftCabin;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCabinDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCabinSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCabinViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftCabinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aircraft/cabin")
public class AircraftCabinController extends AbstractController<AircraftCabin, AircraftCabinDto>  {
    private final AircraftCabinService aircraftCabinService;

    /**
     *
     * @param service                {@link IService}
     * @param aircraftCabinService   {@link AircraftCabinService}
     */
    @Autowired
    public AircraftCabinController(IService<AircraftCabin, AircraftCabinDto> service,
                                   AircraftCabinService aircraftCabinService) {
        super(service);
        this.aircraftCabinService = aircraftCabinService;
    }

    /**
     * This is an API endpoint to search aircraft cabin by search criteria
     *
     * @param aircraftCabinSearchDto {@link AircraftCabinSearchDto}
     * @param page                   page number
     * @param size                   page size
     * @return                       aircraft cabin as page data
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> searchPointOfSales(@RequestBody AircraftCabinSearchDto aircraftCabinSearchDto,
                                                       @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        page = NumberUtil.getValidPageNumber(page);
        size = NumberUtil.getValidPageSize(size);
        Pageable pageable = PageRequest.of(page, size);

        Page<AircraftCabinViewModel> aircraftCabinPage = aircraftCabinService.searchAircraftCabins(aircraftCabinSearchDto, pageable);

        PageData pageData = new PageData(aircraftCabinPage.getContent(), aircraftCabinPage.getTotalPages(), page,
                aircraftCabinPage.getTotalElements());

        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }
}
