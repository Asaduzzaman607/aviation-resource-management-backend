package com.digigate.engineeringmanagement.planning.controller;


import com.digigate.engineeringmanagement.planning.payload.response.LandingGearReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.MlgPartSerialViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.RemovedLandingGearReportViewModel;
import com.digigate.engineeringmanagement.planning.service.LandingGearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Landing Gear Report Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/landing-gear-report")
public class LandingGearReportController {

    private final LandingGearService landingGearService;

    public LandingGearReportController(LandingGearService landingGearService) {
        this.landingGearService = landingGearService;
    }


    /**
     * @param aircraftId          {@link Long}
     * @param partId          {@link Long}
     * @param serialId          {@link Long}
     * @return {@link ResponseEntity < ForecastAircraftDto >}
     */
    @GetMapping("/mlg-report")
    public ResponseEntity<LandingGearReportViewModel> getMlgReport(@RequestParam("aircraftId") Long aircraftId,
                                                                   @RequestParam("partId") Long partId,
                                                                   @RequestParam("serialId") Long serialId,
                                                                   @RequestParam(value = "date", required = false)
                                                                   String dateString) {
        LocalDate date = null;
        if(Objects.nonNull(dateString)){
            date = LocalDate.parse(dateString);
        }
        return new ResponseEntity<>(landingGearService.getMlgGearReport(partId, serialId, aircraftId, date),
                HttpStatus.OK);
    }

    /**
     * @param aircraftId          {@link Long}
     * @return {@link ResponseEntity < ForecastAircraftDto >}
     */
    @GetMapping("/mlg-part-serial")
    public ResponseEntity<List<MlgPartSerialViewModel>> getMlgPartSerial(@RequestParam("aircraftId") Long aircraftId
    ) {
        return new ResponseEntity<>(landingGearService.getMlgPartSerial(aircraftId), HttpStatus.OK);
    }

    @GetMapping("/nlg-report")
    public ResponseEntity<LandingGearReportViewModel> getNlgReport( @RequestParam("aircraftId") Long aircraftId,
                                                                    @RequestParam(value = "date",required = false)
                                                                    String dateString){
        LocalDate date = null;
        if(Objects.nonNull(dateString)){
            date = LocalDate.parse(dateString);
        }
        return new ResponseEntity<>(landingGearService.getNlgGearReport(aircraftId,date), HttpStatus.OK);
    }

    @GetMapping("/removed-nlg-report")
    public ResponseEntity<RemovedLandingGearReportViewModel> getRemovedNlgReport(@RequestParam("aircraftId") Long aircraftId){
        return new ResponseEntity<>(landingGearService.getRemovedNlgGearReport(aircraftId), HttpStatus.OK);
    }

    @GetMapping("/removed-mlg-part-serial")
    public ResponseEntity<List<MlgPartSerialViewModel>> getRemovedMlgPartSerial(@RequestParam("aircraftId")
                                                                                    Long aircraftId) {
        return new ResponseEntity<>(landingGearService.getRemovedMlgPartSerial(aircraftId), HttpStatus.OK);
    }

    @GetMapping ("/removed-mlg-report")
    public ResponseEntity<RemovedLandingGearReportViewModel> getRemovedMlgReport(@RequestParam("aircraftId") Long aircraftId,
                                                                   @RequestParam("partId") Long partId,
                                                                   @RequestParam("serialId") Long serialId
    ) {
        return new ResponseEntity<>(landingGearService.getRemovedMlgGearReport(partId, serialId, aircraftId),
                HttpStatus.OK);
    }

}
