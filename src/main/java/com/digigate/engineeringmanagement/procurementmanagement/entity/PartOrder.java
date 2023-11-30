package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.procurementmanagement.constant.DiscountType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "part_orders")
public class PartOrder extends AbstractDomainBasedEntity {
    @Column(nullable = false)
    private String voucherNo;
    @Column(name = "order_no", nullable = false)
    private String orderNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;
    @Column(name = "submitted_by_id", insertable = false, updatable = false)
    private Long submittedById;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)//nullable false
    private Boolean isRejected = Boolean.FALSE;
    private String rejectedDesc;
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)//nullable false
    private LocalDate updateDate;
    @Column(length = 8000)
    private String tac;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "discount_type")
    private DiscountType discountType = DiscountType.AMOUNT;
    @Column(length = 8000)
    private String remark;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cs_detail_id", nullable = false)
    private CsDetail csDetail;
    @Column(name = "cs_detail_id", insertable = false, updatable = false)
    private Long csDetailId;
    @Enumerated(EnumType.STRING)
    @Column(name = "rfq_type", nullable = false)
    private RfqType rfqType;
    @Builder.Default
    private InputType inputType = InputType.CS;
    @Builder.Default
    private OrderType orderType = OrderType.PURCHASE;
    @Column(length = 8000)
    private String shipTo;
    @Column(length = 8000)
    private String invoiceTo;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;
    @Column(length = 8000)
    private String companyName;
    @Column(length = 8000)
    private String pickUpAddress;
    @OneToMany(
            mappedBy = "partOrder",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<PartOrderItem> partOrderItemList;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartOrder)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((PartOrder) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
