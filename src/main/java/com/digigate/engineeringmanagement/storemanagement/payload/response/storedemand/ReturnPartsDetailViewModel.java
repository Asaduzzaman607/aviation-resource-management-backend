package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnPartsDetailViewModel {
    private Long id;
    private String authCodeNo;
    private String reasonRemoved;
    private LocalDate removalDate;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private Boolean isUsed;
    private Long positionId;
    private String position;
    private Long aircraftId;
    private String aircraftName;
    private Long airportId;
    private String airportName;
    private StoreSerialIdNoDto installedPartSerialNo;
    private StoreSerialIdNoDto removedPartSerialNo;

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
    private String authorizedUserName;
    private String authorizesUserName;
    private String partName;

    private String authNo;
    private String sign;
    private LocalDate createdDate;

    public static ReturnPartsDetailViewModel emptyModel() {
        return new ReturnPartsDetailViewModel();
    }
}
