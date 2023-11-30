package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.constant.DiscountType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PoResponseDto {
    private Long id;
    private String voucherNo;
    private String orderNo;
    private Long csDetailId;
    private Long csId;
    private String csNo;
    private Long submittedById;
    private String submittedByName;
    private Long employeeId;
    private String employeeName;
    private Long designationId;
    private String designationName;
    private String tac;
    private String remark;
    private InputType inputType;
    private OrderType orderType;
    private RfqType rfqType;
    private String shipTo;
    private String invoiceTo;
    private String vendorResponse;
    private Long requisitionId;
    private String requisitionNo;

    private VendorQuotationViewModel vendorQuotationViewModel;
    private DiscountType discountType;
    private Double discount;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Boolean editable;
    private Boolean actionEnabled;
    private Boolean isRejected;
    private String rejectedDesc;
    private List<PoItemResponseDto> poItemResponseDtoList;
    private String companyName;
    private String pickUpAddress;
}
