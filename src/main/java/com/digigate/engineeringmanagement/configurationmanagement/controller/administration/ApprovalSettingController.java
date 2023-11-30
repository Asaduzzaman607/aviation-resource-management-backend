package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ApprovalSettingDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.ApprovalSettingSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.SelectedUserSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ApprovalSettingCustomResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/approval-settings")
public class ApprovalSettingController extends AbstractSearchController<ApprovalSetting, ApprovalSettingDto, ApprovalSettingSearchDto> {
    private final ApprovalSettingService approvalSettingService;

    public ApprovalSettingController(ApprovalSettingService service, ApprovalSettingService approvalSettingService) {
        super(service);
        this.approvalSettingService = approvalSettingService;
    }

    @PostMapping("/selected")
    public ResponseEntity<ApprovalSettingCustomResponseDto> getSelectedUsers(@RequestBody @Valid SelectedUserSearchDto searchDto) {
        return ResponseEntity.ok(approvalSettingService.getSelectedUsers(searchDto));
    }
}
