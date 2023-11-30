package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDemandResponseDto {
    private Long id;
    private DepartmentType departmentType;
    private String voucherNo;
    private LocalDate demandDate;
    private LocalDate validTill;
    private Set<String> attachment;
    private String aircraftName;
    private Long aircraftId;
    private String airportName;
    private Long airportId;
    private String departmentCode;
    private Long departmentId;
    private Long vendorId;
    private String vendorName;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private String workOrderNo;
    private List<StoreDemandDetailsDto> storeDemandDetailsDtoList;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Boolean editable;
    private Boolean actionEnabled;
    private List<IdVoucherDto> issued;
    private List<IdVoucherDto> requisition;
    private String remarks;
    private Boolean isRejected;
    private String rejectedDesc;
}
