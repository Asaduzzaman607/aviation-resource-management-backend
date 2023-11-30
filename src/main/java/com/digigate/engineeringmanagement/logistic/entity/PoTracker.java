package com.digigate.engineeringmanagement.logistic.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.logistic.constant.TrackerStatus;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tracker")
public class PoTracker extends AbstractDomainBasedEntity {

    @Column(name = "tracker_no", unique = true, nullable = false)
    private String trackerNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partOrderItem_id")
    private PartOrderItem partOrderItem;
    @Column(name = "partOrderItem_id", insertable = false, updatable = false)
    private Long partOrderItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trackerStatus", nullable = false)
    private TrackerStatus trackerStatus;
}
