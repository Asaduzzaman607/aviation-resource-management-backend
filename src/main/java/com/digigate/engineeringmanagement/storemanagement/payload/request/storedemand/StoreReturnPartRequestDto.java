package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReturnPartRequestDto implements IDto {
    private Long id;
    @Size(max = 8000)
    private String description;
    @NotNull
    @Min(0)
    private Long quantityReturn;
    @Size(max = 100)
    private String cardLineNo;
    @Size(max = 100)
    private String releaseNo;
    private Long partId; //removed part Id
    private Long installedPartId;
    private Long installedPartUomId;
    private Long removedPartUomId;
    private String remarks = EMPTY_STRING;
    @JsonIgnore
    private StoreReturnPart storeReturnPart;
    @Valid
    private ReturnPartsDetailDto returnPartsDetailDto;

    private Boolean caabEnabled = Boolean.FALSE;
    private String caabStatus;
    private String caabRemarks;
    private String caabCheckbox;
    private String approvalAuthNo;
    private LocalDate authorizedDate;
    private LocalDate authorizesDate;
    private String certApprovalRef;
    private Long authorizedUserId;
    private Long authorizesUserId;
    private Boolean isInactive = Boolean.FALSE;
}
