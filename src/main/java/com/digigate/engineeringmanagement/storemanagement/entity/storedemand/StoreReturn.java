package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import com.digigate.engineeringmanagement.storemanagement.constant.StoreReturnStatusType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Location;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.StoreStockRoom;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_return")
public class StoreReturn extends AbstractDomainBasedEntity {

    @Column(name = "voucher_no", unique = true)
    private String voucherNo;

    @Column(name = "stock_room_type")
    @Enumerated(EnumType.STRING)
    private StockRoomType stockRoomType;

    @Column(name = "part_classification")
    private PartClassification partClassification;

    @Column(name = "unserviceable_status")
    private String unserviceableStatus;

    @Column(name = "serviceable_status_type")
    @Enumerated(EnumType.STRING)
    private StoreReturnStatusType storeReturnStatusType;

    @Column(name = "is_internal_dept")
    private Boolean isInternalDept = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private StoreIssue storeIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private AircraftLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_stock_room_id")
    private StoreStockRoom storeStockRoom;

    @Column(name = "aircraft_reg", length = 100)
    private String aircraftRegistration;

    @Column(name = "active_part_count")
    private Integer activePartCount; /** This Field is for active part count coming from planning */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returning_officer_id")
    private User returningOfficer; //TODO: confirmation if it will be user or employee

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @Column(name = "submitted_by", updatable = false, insertable = false)
    private Long submittedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @Column(name = "department_Id", insertable = false, updatable = false)
    private Long departmentId;

    @Column(name = "vendor_id", insertable = false, updatable = false)
    private Long vendorId;

    @Column(name = "issue_id", insertable = false, updatable = false)
    private Long storeIssueId;

    @Column(name = "store_stock_room_id", insertable = false, updatable = false)
    private Long storeStockRoomId;

    @Column(name = "returning_officer_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = Boolean.FALSE;

    @Column(name = "rejected_desc")
    private String rejectedDesc;

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;

    @Column(length = 8000)
    private String remarks;

    @Column(length = 8000)
    private String storeLocation;

    @Column(name = "is_serviceable", columnDefinition = "bit default 0", nullable = false)
    private Boolean serviceable;
    @Column(name = "work_order_number")
    private String workOrderNumber;
    @Column(name = "work_order_serial")
    private String workOrderSerial;

    @OneToMany(mappedBy = "storeReturn", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<StoreReturnPart> returnParts;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreReturn)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreReturn) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
