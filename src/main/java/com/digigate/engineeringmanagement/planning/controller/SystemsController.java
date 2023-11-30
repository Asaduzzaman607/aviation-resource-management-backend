package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.request.AlertLevelSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.AlertLevelReport;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Systems;
import com.digigate.engineeringmanagement.planning.payload.request.AlertLevelViewSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemReliabilitySearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemsDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AlertLevelListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LocationViewModel;
import com.digigate.engineeringmanagement.planning.service.SystemsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Systems Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/systems")
public class SystemsController extends AbstractController<Systems, SystemsDto> {

    private final SystemsService systemsService;

    public SystemsController(IService<Systems, SystemsDto> service, SystemsService systemsService) {
        super(service);
        this.systemsService = systemsService;
    }

    @PostMapping("/search")
    public PageData searchSystems(@Valid @RequestBody SystemsSearchDto systemsSearchDto,
                                                @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return systemsService.searchSystems(systemsSearchDto,pageable);
    }

    @PostMapping("/alert-level")
    public AlertLevelReport alertLevelReport(@Valid @RequestBody AlertLevelSearchDto alertLevelSearchDto){
        return systemsService.alertLevelReport(alertLevelSearchDto);
    }

    @PostMapping("/alert-level-list")
    public List<AlertLevelListViewModel> alertLevelViewModelList(@Valid @RequestBody AlertLevelViewSearchDto alertLevelViewSearchDto){
        return systemsService.alertLevelListView(alertLevelViewSearchDto);
    }

    @PostMapping("/reliability")
    public List<LocationViewModel> createSystemReliability(@Valid @RequestBody SystemReliabilitySearchDto systemReliabilitySearchDto){
        return systemsService.createSystemReliability(systemReliabilitySearchDto);
    }
}
