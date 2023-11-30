package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.NrcControlList;
import com.digigate.engineeringmanagement.planning.payload.request.NrcControlListDto;
import com.digigate.engineeringmanagement.planning.payload.request.NrcControlListSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckIndexIdAndCheckViewModel;
import com.digigate.engineeringmanagement.planning.service.NrcControlListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Nrc ControlList Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/nrc-control-list")
public class NrcControlListController extends AbstractSearchController<NrcControlList, NrcControlListDto,
        NrcControlListSearchDto> {
    private final NrcControlListService nrcControlListService;

    public NrcControlListController(ISearchService<NrcControlList, NrcControlListDto, NrcControlListSearchDto>
                                            iSearchService, NrcControlListService nrcControlListService) {
        super(iSearchService);
        this.nrcControlListService = nrcControlListService;
    }

    @GetMapping("/get-aircraft-Check-Index-By-Aircraft-Id/{aircraftId}")
    public List<AircraftCheckIndexIdAndCheckViewModel> getAcCheckIndexByAircraftId(@PathVariable Long aircraftId){
        return nrcControlListService.getAircraftCheckIndexListByAircraftId(aircraftId);
    }

}
