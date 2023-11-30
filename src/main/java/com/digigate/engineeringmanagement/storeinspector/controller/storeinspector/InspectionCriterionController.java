package com.digigate.engineeringmanagement.storeinspector.controller.storeinspector;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionCriterion;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.InspectionCriterionRequestDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.InspectionCriterionService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store-inspector/inspection-criterions")
public class InspectionCriterionController extends AbstractSearchController<InspectionCriterion, InspectionCriterionRequestDto, IdQuerySearchDto> {
    public InspectionCriterionController(InspectionCriterionService inspectionCriterionService) {
        super(inspectionCriterionService);
    }
}
