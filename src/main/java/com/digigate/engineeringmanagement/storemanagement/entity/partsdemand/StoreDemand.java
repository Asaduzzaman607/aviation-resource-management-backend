package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_demands")
public class StoreDemand extends AbstractDomainBasedEntity {
    @Column(name = "internal_department")
    @Enumerated(EnumType.STRING)
    private DepartmentType departmentType = DepartmentType.INTERNAL;

    @Column(name = "voucher_no", unique = true, nullable = false)
    private String voucherNo;

    @Column(name = "valid_till")
    private LocalDate validTill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @OneToMany(mappedBy = "storeDemand", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<StoreDemandItem> storeDemandItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airport_id")
    private Airport airport;
    @Column(name = "airport_id", insertable = false, updatable = false)
    private Long airportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internal_department_id")
    private Department internalDepartment;
    @Column(name = "internal_department_id", insertable = false, updatable = false)
    private Long internalDepartmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    @Column(name = "vendor_id", insertable = false, updatable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_issued", columnDefinition = "bit default 0", nullable = false)
    private Boolean isIssued;
    @Column(name = "is_requisition", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRequisition;
    @Column(name = "is_alive", columnDefinition = "bit default 1", nullable = false)
    private Boolean isAlive = Boolean.TRUE;
    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
    @Column(length = 8000)
    private String remarks;
    @Column(name = "work_order_no", length = 100)
    private String workOrderNo;
    @Builder.Default
    private OrderType orderType = OrderType.PURCHASE;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreDemand)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((StoreDemand) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}