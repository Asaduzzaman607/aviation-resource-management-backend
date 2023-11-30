package com.digigate.engineeringmanagement.planning.controller;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftApusDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftApusViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftApusService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;

/**
 * AircraftApus Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/aircraft-apu")
public class AircraftApusController {

    private static final String CREATED_SUCCESSFULLY_MESSAGE = "Created Successfully";
    private static final String UPDATED_SUCCESSFULLY_MESSAGE = "Updated Successfully";

    private final AircraftApusService aircraftApusService;

    public AircraftApusController(AircraftApusService aircraftApusService) {
        this.aircraftApusService = aircraftApusService;
    }

    @PostMapping("")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody AircraftApusDto aircraftApusDto) {
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE,
                aircraftApusService.create(aircraftApusDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody AircraftApusDto aircraftApusDto,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE,
                aircraftApusService.update(aircraftApusDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftApusViewModel>getAircraftApu(@PathVariable Long id){
        return ResponseEntity.ok(aircraftApusService.getAircraftApuDetailsById(id));
    }

    @GetMapping("")
    public PageData getAllAircraftApuDetails(@PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                    direction = Sort.Direction.ASC) Pageable pageable) {
        return aircraftApusService.getAllAircraftApuDetails(pageable);
    }
}
