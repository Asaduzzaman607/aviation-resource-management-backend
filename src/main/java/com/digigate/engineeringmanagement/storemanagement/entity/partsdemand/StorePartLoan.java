package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_parts_loans")
public class StorePartLoan extends AbstractDomainBasedEntity {
    @Column(name = "loan_no")
    private String loanNo;
    @Column(name = "loan_expires")
    private LocalDate loanExpires;
    @Column(name = "attachment")
    private String attachment;
    @Column(name = "update_date")
    private LocalDate updateDate;
    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @OneToMany(mappedBy = "storePartLoan", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<StorePartLoanDetails> storePartLoanDetails = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    @Column(name = "vendor_id", insertable = false, updatable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    private String remarks;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StorePartLoan)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StorePartLoan) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
