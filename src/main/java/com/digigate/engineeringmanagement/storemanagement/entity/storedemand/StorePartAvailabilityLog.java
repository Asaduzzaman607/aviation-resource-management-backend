package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_parts_availability_logs")
public class StorePartAvailabilityLog extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_serial_id", nullable = false)
    private StorePartSerial storePartSerial;
    @Column(name = "part_serial_id", insertable = false, updatable = false)
    private Long storePartSerialId;

    @Column(name = "quantity", nullable = false, columnDefinition = "integer default 0")
    private Integer quantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_type")
    private StorePartAvailabilityLogParentType parentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_status")
    private PartStatus partStatus;

    @Column(name = "parent_id")
    private Long parentId;
    private LocalDate receiveDate;
    private LocalDate shelfLife;
    @Column(name = "self_life_type")
    private String selfLifeType;
    private LocalDate expiryDate;
    private Double unitPrice;
    private Long currencyId;
    private String issuedAc;
    private String location;
    private TransactionType transactionType;
    private String grnNo;

    @Column(name = "issued_qty")
    private Integer issuedQty;
    @Column(name = "in_stock")
    private Integer inStock;
    @Column(name = "received_qty")
    private Integer receivedQty;
    @Column(name = "voucher_no")
    private String voucherNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @Column(name = "submitted_by", insertable = false, updatable = false)
    private Long submittedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
}
