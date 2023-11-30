package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.planning.dto.request.AirportDto;
import com.digigate.engineeringmanagement.planning.dto.request.AirportSearchDto;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Airport Controller
 */
@RestController
@RequestMapping("/api/airport")
public class AirportController{
    private final AirportService airportService;
    private static final String ORDER_BY_AIRPORT_NAME = "name";

    /**
     * Autowired constructor
     *
     * @param airportService {@link  AirportService}
     */
    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    /**
     * This is an API endpoint to save Airport
     *
     * @param airportDto                {@link  AirportDto}
     * @return newly saved airport      {@link Airport}
     */
    @PostMapping("/")
    public ResponseEntity<Airport> saveAirport(@Valid @RequestBody AirportDto airportDto){
        return ResponseEntity.ok(airportService.saveOrUpdate(airportDto, null));
    }

    /**
     * This is an API endpoint to update Airport
     *
     * @param airportDto                 {@link AirportDto}
     * @param id                         {@link Long}
     * @return updated airport           {@link Airport}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Airport>updateAirport(@Valid @RequestBody AirportDto airportDto, @PathVariable Long id){
        return ResponseEntity.ok(airportService.saveOrUpdate(airportDto, id));
    }

    /**
     * This is an API endpoint to get Airport by id
     *
     * @param id                    {@link Long}
     * @return Airport entity       {@link Airport}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Airport>getAirportById(@PathVariable Long id){
        return ResponseEntity.ok(airportService.findById(id));
    }

    /**
     * This is an API endpoint to search Airports by search criteria
     *
     * @param airportSearchDto {@link  AirportDto}
     * @param page {@link Integer}
     * @param size {@link  Integer}
     * @return {@link PageData}
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> searchPointOfSales(@RequestBody AirportSearchDto airportSearchDto,
                                                       @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size){
        if(page > 0) page--;
        Sort sort = Sort.by(Sort.Direction.ASC,ORDER_BY_AIRPORT_NAME);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Airport> airportPage= airportService.searchAirports(airportSearchDto, pageable);

        PageData pageData = new PageData(airportPage.getContent(),
                airportPage.getTotalPages(),
                pageable.getPageNumber()+1,
                airportPage.getTotalElements());

        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }



    /**
     *This is an API endpoint to get all airports as a list
     *
     * @return {@link  List<Airport>}
     */
    @GetMapping("/all")
    public ResponseEntity<List<Airport>> getAll(){
        return ResponseEntity.ok(airportService.getAll());
    }

    /**
     * This is an API endpoint to toggle active status of an Airport
     *
     * @param id {@link Long}
     * @return
     */
    @PutMapping("/toggle/active/status/{id}")
    public ResponseEntity<Airport>toggleActiveStatus(@PathVariable Long id){
        return  ResponseEntity.ok(airportService.toggleActiveStatus(id));
    }

}
