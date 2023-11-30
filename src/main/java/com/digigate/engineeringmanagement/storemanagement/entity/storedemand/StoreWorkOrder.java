package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Store Work Order Entity
 *
 * @author Sayem Hasnat
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_work_orders")
public class StoreWorkOrder extends AbstractDomainBasedEntity {
    @Column(name = "workorder_no", unique = true)
    private String workOrderNo;

    @Column(name = "chk_svc_ohrn_no")
    private String chkSvcOhrnNo;

    @Column(name = "reason_remark")
    private String reasonRemark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unserviceable_part_id")
    private ReturnPartsDetail unserviceablePart;
    @Column(name = "unserviceable_part_id", insertable = false, updatable = false)
    private Long unserviceablePartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedById;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
}
