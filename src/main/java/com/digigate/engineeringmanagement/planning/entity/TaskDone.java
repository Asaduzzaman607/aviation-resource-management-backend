package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Task Done Entity
 *
 * @author Asifur Rahman
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "ldnd_done_list")
public class TaskDone extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ldnd_id", nullable = false)
    private Ldnd ldnd;

    @Column(name = "ldnd_id", insertable = false, updatable = false)
    private Long ldndId;

    private Double doneHour;
    private Integer doneCycle;
    private LocalDate doneDate;
    private Double initialHour;
    private Integer initialCycle;
    @Enumerated(value = EnumType.ORDINAL)
    private IntervalType intervalType;
    private String remark;
}
