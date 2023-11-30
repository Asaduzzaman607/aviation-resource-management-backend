package com.digigate.engineeringmanagement.storemanagement.entity.scrap;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "store_scraps")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StoreScrap extends AbstractDomainBasedEntity {
    @Column(name = "voucher_no", nullable = false)
    private String voucherNo;

    @OneToMany(mappedBy = "storeScrap", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<StoreScrapPart> storeScraps = new ArrayList<>();

    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = Boolean.FALSE;

    @Column(name = "rejected_desc")
    private String rejectedDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @Column(name = "submitted_by", insertable = false, updatable = false)
    private Long submittedById;

    private String remarks;
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreScrap)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreScrap) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
