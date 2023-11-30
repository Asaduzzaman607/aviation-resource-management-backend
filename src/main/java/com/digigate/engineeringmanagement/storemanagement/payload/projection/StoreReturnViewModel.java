package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReturnViewModel {
    private Long id;
    private String voucherNo;
    private Boolean isInternalDept;
    private Set<String> attachment;
    private String aircraftRegistration;
    private Boolean isActive;
    private Long departmentId;
    private String departmentName;
    private Long externalDepartmentId;
    private String externalDepartmentName;
    private String storeReturnStatusType;
    private Long locationId;
    private String stockRoomType;
    private String locationCode;
    private Long stockRoomId;
    private String stockRoomName;
    private Long storeIssueId;
    private String storeIssueVoucherNo;
    private Long submittedById;
    private String submittedByName;
    private Long returningOfficerId;
    private String remarks;
    private String storeLocation;

    public StoreReturnViewModel(Long id, String voucherNo, Boolean isInternalDept, String aircraftRegistration, Boolean isActive,
                                Long departmentId, String departmentName, Long externalDepartmentId, String externalDepartmentName,
                                Long locationId, String locationCode, Long stockRoomId, String stockRoomName,
                                Long storeIssueId, String storeIssueVoucherNo, Long submittedById,
                                String submittedByName, Long returningOfficerId, String remarks, String storeLocation) {
        this.id = id;
        this.voucherNo = voucherNo;
        this.isInternalDept = isInternalDept;
        this.aircraftRegistration = aircraftRegistration;
        this.isActive = isActive;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.externalDepartmentId = externalDepartmentId;
        this.externalDepartmentName = externalDepartmentName;
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.stockRoomId = stockRoomId;
        this.stockRoomName = stockRoomName;
        this.storeIssueId = storeIssueId;
        this.storeIssueVoucherNo = storeIssueVoucherNo;
        this.submittedById = submittedById;
        this.submittedByName = submittedByName;
        this.returningOfficerId = returningOfficerId;
        this.remarks = remarks;
        this.storeLocation = storeLocation;
    }
}
