package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_issue_serials")
public class StoreIssueSerial extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "issue_item_id")
    private StoreIssueItem storeIssueItem;

    @Column(name = "issue_item_id", insertable = false, updatable = false)
    private Long storeIssueItemId;


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "part_serial_id")
    private StorePartSerial storePartSerial;

    @Column(name = "part_serial_id", insertable = false, updatable = false)
    private Long storePartSerialId;

    @Column(name = "grn_no")
    private String grnNo;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreIssueSerial)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreIssueSerial) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
