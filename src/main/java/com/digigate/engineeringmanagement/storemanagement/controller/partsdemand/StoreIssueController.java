package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreIssueService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/store/issues")
public class StoreIssueController extends AbstractSearchController<
        StoreIssue,
        StoreIssueDto,
        CommonWorkFlowSearchDto> {
    private static final String RETURN_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Return status changed successfully!";
    private final StoreIssueService storeIssueService;
    public StoreIssueController(StoreIssueService storeIssueService) {
        super(storeIssueService);
        this.storeIssueService = storeIssueService;
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody
    ApprovalRequestDto approvalRequestDto) {
        storeIssueService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }

    @GetMapping("print/{issueId}")
    public PageData generatePartIssueReport(@PathVariable Long issueId,
                                            @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                    direction = Sort.Direction.ASC) Pageable pageable) {
        return storeIssueService.generateStoreIssuePrintPreview(issueId, pageable);
    }

    @PutMapping("return/{id}")
    public ResponseEntity<MessageResponse> returnApproved(@PathVariable("id") Long issueId){
        storeIssueService.updateReturnApprovedStatus(issueId);
        return ResponseEntity.ok(new MessageResponse(RETURN_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }
}
