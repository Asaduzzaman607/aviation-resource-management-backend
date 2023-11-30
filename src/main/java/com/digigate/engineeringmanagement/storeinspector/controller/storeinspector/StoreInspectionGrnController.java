package com.digigate.engineeringmanagement.storeinspector.controller.storeinspector;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspectionGrn;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionGrnRequestDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.StoreInspectionGrnService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store-inspector/grn")
public class StoreInspectionGrnController extends AbstractSearchController<StoreInspectionGrn, StoreInspectionGrnRequestDto, IdQuerySearchDto> {
    public StoreInspectionGrnController(StoreInspectionGrnService storeInspectionGrnService) {
        super(storeInspectionGrnService);
    }
}
