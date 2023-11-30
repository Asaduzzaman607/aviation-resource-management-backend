package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftLocationDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftSearchLocationDto;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationIService;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * Aircraft Location Controller
 *
 * @author ashiniSingha
 */
@RestController
@RequestMapping("/api/aircraft-location")
public class AircraftLocationController extends AbstractSearchController<AircraftLocation, AircraftLocationDto, AircraftSearchLocationDto> {
    private final AircraftLocationService aircraftLocationService;
    private final AircraftLocationIService aircraftLocationIService;

    /**
     * Parameterized constructor
     *
     * @param service {@link IService<AircraftLocation, AircraftLocationDto>}
     * @param aircraftLocationService {@link  AircraftLocationService}
     * @param aircraftLocationIService {@link AircraftLocationIService}
     */
    public AircraftLocationController(ISearchService<AircraftLocation, AircraftLocationDto, AircraftSearchLocationDto> iSearchService, AircraftLocationService aircraftLocationService, AircraftLocationIService aircraftLocationIService) {
        super(iSearchService);
        this.aircraftLocationService = aircraftLocationService;
        this.aircraftLocationIService = aircraftLocationIService;
    }

    /**
     * This is an API endpoint for uploading Aircraft Location data via Excel file
     *
     * @param file {@link MultipartFile}
     * @return {@link  ResponseEntity<ExcelDataResponse>}
     * @throws IOException
     */
    @PostMapping("/upload")
    public ResponseEntity<ExcelDataResponse> importExcelFile(@RequestParam("file") MultipartFile file) {
        ExcelDataResponse excelDataResponse = aircraftLocationService.uploadExcel(file);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

}
