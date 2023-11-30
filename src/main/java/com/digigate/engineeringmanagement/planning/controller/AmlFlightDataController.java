package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.payload.request.AmlFlightDataDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightViewModel;
import com.digigate.engineeringmanagement.planning.service.AmlFlightDataIService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * AML flight controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/flight-data")
public class AmlFlightDataController extends AbstractController<AmlFlightData, AmlFlightDataDto> {

    private final AmlFlightDataIService amlFlightDataIService;

    /**
     * parameterized constructor
     *
     * @param service               {@link IService<AmlFlightData, AmlFlightDataDto>}
     * @param amlFlightDataIService {@link  AmlFlightDataIService}
     */
    public AmlFlightDataController(IService<AmlFlightData, AmlFlightDataDto> service,
                                   AmlFlightDataIService amlFlightDataIService) {
        super(service);
        this.amlFlightDataIService = amlFlightDataIService;
    }

    /**
     * This API is responsible for getting Flight data by Aml Id
     *
     * @param id {@link  Long}
     * @return {@link ResponseEntity<AmlFlightViewModel>}
     */
    @GetMapping("amlId/{id}")
    public ResponseEntity<AmlFlightViewModel> getFlightDatabyAmlId(@PathVariable Long id) {
        return ResponseEntity.ok(amlFlightDataIService.findByAmlId(id));
    }

    @GetMapping("/migrate-flight-data")
    public String migrateFlightData(@RequestParam(required = false, value = "date")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        amlFlightDataIService.migrateFlightDataIntoDailyUtilizationTable(date);
        return "data-migrated!";
    }

}
