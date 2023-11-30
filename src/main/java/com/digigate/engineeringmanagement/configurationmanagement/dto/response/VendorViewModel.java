package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorViewModel {
    private Long id;
    private String name;
    private String address;
    private String officePhone;
    private String emergencyContact;
    private String email;
    private String website;
    private String skype;
    private String contactMobile;
    private String contactPhone;
    private String contactEmail;
    private String contactSkype;
    private String contactPerson;
    private Set<String> attachments;
    private List<VendorWiseClientListResponseDto> clientList;
    private String itemsBuild;
    private String loadingPort;
    private Boolean status;
    private Long workFlowActionId;
    private Boolean isRejected;
    private String rejectedDesc;
    private LocalDate validTill;
    private IdNameResponse countryOrigin;
    private CityResponseDto city;
    private Integer workflowOrder;
    private String workflowName;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Map<Long, ApprovalStatusViewModel> qualityApprovalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListQuality;
    private Boolean editable;
    private Boolean actionEnabled;
    private List<VendorCapabilityResponseDto> vendorCapabilityResponseDtoList;
}
