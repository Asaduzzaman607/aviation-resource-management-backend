
package com.digigate.engineeringmanagement.storeinspector.controller.storeinspector;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspection;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionRequestDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.StoreInspectionService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/store-inspector/store-inspection")
public class StoreInspectorController extends AbstractSearchController<StoreInspection, StoreInspectionRequestDto, IdQuerySearchDto> {
    public StoreInspectorController(StoreInspectionService storeInspectionService) {
        super(storeInspectionService);
    }
}