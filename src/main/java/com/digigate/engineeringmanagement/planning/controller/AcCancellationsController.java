package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AcCancellations;
import com.digigate.engineeringmanagement.planning.payload.request.AcCancellationsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AcCancellationsSearchDto;
import com.digigate.engineeringmanagement.planning.service.AcCancellationsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;

/**
 * AcCancellations Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/ac-cancellations")
public class AcCancellationsController extends AbstractController<AcCancellations, AcCancellationsDto> {

    private final AcCancellationsService acCancellationsService;

    public AcCancellationsController(IService<AcCancellations, AcCancellationsDto> service,
                                     AcCancellationsService acCancellationsService) {
        super(service);
        this.acCancellationsService = acCancellationsService;
    }

    @PostMapping("/search")
    public PageData searchAircraftCancellation(@Valid @RequestBody AcCancellationsSearchDto acCancellationsSearchDto,
                                                @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return acCancellationsService.searchAircraftCancellation(acCancellationsSearchDto,pageable);
    }
}
