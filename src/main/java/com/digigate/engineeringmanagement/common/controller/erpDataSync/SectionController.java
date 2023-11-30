package com.digigate.engineeringmanagement.common.controller.erpDataSync;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.payload.request.erp.SectionDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.service.erpDataSync.SectionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/section")
public class SectionController extends AbstractSearchController<Section, SectionDto, ERPSearchRequestDto> {
    public SectionController(SectionService sectionService) {
        super(sectionService);
    }
}
