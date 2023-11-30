package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Store Work Order Entity
 *
 * @author Sayem Hasnat
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreWorkOrderResponse {
    private Long id;
    private String workOrderNo;
    private Long unserviceablePartId;
    private String reasonRemark;
    private LocalDate updateDate;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private Boolean actionEnabled;
    private Boolean editable;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Boolean isRejected;
    private String rejectedDesc;
}
