package com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionApprovalStatus;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspectionGrn;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInspectionRequestDto implements IDto {
    private Long id;
    private Long inwardId;
    private Long detailsId;
    @NotNull
    private Long partId;
    private Long partReturnId;
    private Long serialId;
    @Size(max = 100)
    private String serialNo;
    private Integer quantity = INT_ONE;
    @Size(max = 100)
    private String grnNo;
    private LocalDate shelfLife;
    private LocalDate expireDate;
    private InspectionApprovalStatus status = InspectionApprovalStatus.NONE;
    private Long storeInspectionGrnId;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    @Size(max = 8000)
    private String remarks;
    private List<String> partStateNameList = new ArrayList<>();
    private String validUntil;
    private String lotNum;
    private String certiNo;
    private String inspectionAuthNo;
    private String partStateName;
    private Long  uomId;
    @Valid
    @NotEmpty
    List<InspectionCriterionRequestDto> inspectionCriterionList;

}
