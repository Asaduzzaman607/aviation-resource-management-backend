package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Mel;
import com.digigate.engineeringmanagement.planning.payload.request.MelDto;
import com.digigate.engineeringmanagement.planning.payload.request.MelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.MelViewMode;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import com.digigate.engineeringmanagement.planning.service.MelIService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Model Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/mel")
public class MelController extends AbstractController<Mel, MelDto> {


    private final MelIService melIService;

    /**
     * Parameterized constructor
     *
     * @param melIService  {@link MelIService}
     */
    public MelController(IService<Mel, MelDto> service,
                         MelIService melIService) {
        super(service);
        this.melIService = melIService;
    }

    /**
     * Mel Search API
     *
     * @param melSearchDto {@link <MelSearchDto>}
     * @return dtos {@link ModelResponseByAircraftDto}
     */
    @PostMapping("/search")
    public PageData searchMelReport(@RequestBody @Valid MelSearchDto melSearchDto,
                                                        @PageableDefault(
                                                            sort = ApplicationConstant.DEFAULT_SORT,
                                                            direction = Sort.Direction.ASC) Pageable pageable) {
        return melIService.searchMelReport(melSearchDto, pageable);
    }

    @GetMapping("/find-all-mel/{aircraftId}")
    public List<MelViewMode> findAllUnclearedMel(@PathVariable Long aircraftId) {
        return melIService.findAllUnclearedMel(aircraftId);
    }
}
