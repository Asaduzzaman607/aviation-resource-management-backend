package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Cabin;
import com.digigate.engineeringmanagement.planning.payload.request.CabinDto;
import com.digigate.engineeringmanagement.planning.payload.response.CabinViewModel;
import com.digigate.engineeringmanagement.planning.service.CabinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cabin Controller
 *
 * @author Pranoy Das
 */
@RestController
@RequestMapping("/api/cabin")
public class CabinController extends AbstractController<Cabin, CabinDto> {
    private final CabinService cabinService;

    /**
     * Parameterized constructor
     *
     * @param service         {@link IService}
     * @param cabinService    {@link CabinService}
     */
    public CabinController(IService<Cabin, CabinDto> service, CabinService cabinService) {
        super(service);
        this.cabinService = cabinService;
    }

    /**
     * Responsible for getting all cabins
     *
     * @return list of cabin info as view model
     */
    @GetMapping("/all")
    public ResponseEntity<List<CabinViewModel>> getAllCabins () {
        return new ResponseEntity<>(cabinService.getAllCabin(), HttpStatus.OK);
    }

    /**
     * responsible for changing cabin status
     *
     * @param cabinDto         {@link CabinDto}
     * @return                 cabin as view model
     */
    @PutMapping("/change/status")
    public ResponseEntity<CabinViewModel> changeActiveStatus (@RequestBody CabinDto cabinDto) {
        return new ResponseEntity<>(cabinService.changeActiveStatus(cabinDto), HttpStatus.OK);
    }


    @GetMapping("/info/{cabinId}")
    public ResponseEntity<Cabin>  findCabinByCabinId(@PathVariable Long cabinId) {
        return new ResponseEntity<>(cabinService.findCabinById(cabinId), HttpStatus.OK);
    }
}
