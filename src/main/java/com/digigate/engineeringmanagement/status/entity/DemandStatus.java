package com.digigate.engineeringmanagement.status.entity;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "demand_status")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemandStatus extends AbstractDomainBasedEntity {

    @Column(name = "part_id", nullable = false)
    private Long partId;

    @Column(name = "child_id")
    private Long childId;

    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "workflow_action_id")
    private Long workFlowActionId;

    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = false;

    @Column(name = "active_status", columnDefinition = "bit default 1", nullable = false)
    private Boolean isActiveStatus = true;

    @Column(name = "voucher_type")
    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;

    @Column(name = "workflow_type")
    private String workFlowType;

    @Column(name = "input_type")
    private InputType inputType;

    @Column(name = "module")
    private String module;

    private Integer quantity;

    private Long vendorQuotationInvoiceDetailsId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DemandStatus)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((DemandStatus) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
