package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "comparative_statements")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ComparativeStatement extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private QuoteRequest quoteRequest;
    @Column(name = "rfq_id", insertable = false, updatable = false)
    private Long quoteRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "existing_cs_id")
    private ComparativeStatement existingCs;
    @Column(name = "existing_cs_id", insertable = false, updatable = false)
    private Long existingCsId;

    @Column(name = "cs_no", nullable = false, unique = true)
    private String comparativeStatementNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @Column(name = "submitted_by", insertable = false, updatable = false)
    private Long submittedId;

    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
    @Column(nullable = false)
    private CsWorkflowType workflowType;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;
    @Enumerated(EnumType.STRING)
    @Column(name = "rfq_type", nullable = false)
    private RfqType rfqType;
    @Builder.Default
    private OrderType orderType = OrderType.PURCHASE;
    @OneToMany(
            mappedBy = "comparativeStatement",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<CsDetail> csDetailSet;
    @OneToMany(
            mappedBy = "comparativeStatement",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<CsPartDetail> csPartDetailSet;

    public void addCsDetail(CsDetail csDetail) {
        if (Objects.isNull(csDetailSet)) {
            csDetailSet = new HashSet<>();
        }

        csDetailSet.add(csDetail);
        csDetail.setComparativeStatement(this);
    }

    public void addCsPartDetail(CsPartDetail csPartDetail) {
        if (Objects.isNull(csPartDetailSet)) {
            csPartDetailSet = new HashSet<>();
        }

        csPartDetailSet.add(csPartDetail);
        csPartDetail.setComparativeStatement(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ComparativeStatement)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((ComparativeStatement) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
