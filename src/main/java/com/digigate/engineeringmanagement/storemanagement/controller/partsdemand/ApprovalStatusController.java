package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/approval/status")
public class ApprovalStatusController extends AbstractController<ApprovalStatus, ApprovalStatusDto> {
    public ApprovalStatusController(ApprovalStatusService service) {
        super(service);
    }
}
