package com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector;

import com.digigate.engineeringmanagement.storeinspector.constant.InspectionApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockInwardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreReturnDetailsViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInspectionResponseDto {
    private Long id;
    private String inspectionNo;
    private Long partId;
    private String partNo;
    private String partDescription;
    private StoreStockInwardProjection storeStockInward;
    private StoreReturnDetailsViewModel storeReturn;
    private StoreSerialIdNoDto serialIdNoDto;
    private String serialNo;
    private String grnNo;
    private LocalDate shelfLife;
    private LocalDate expireDate;
    private StoreInspectionGrnResponseDto grnResponseDto;
    private InspectionApprovalStatus status;
    private Integer quantity;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private String remarks;
    private List<String> partStateNameList;
    private String validUntil;
    private String lotNum;
    private String certiNo;
    private String inspectionAuthNo;
    private LocalDate createdDate;
    private List<InspectionCriterionResponseDto> inspectionCriterionList;
}
