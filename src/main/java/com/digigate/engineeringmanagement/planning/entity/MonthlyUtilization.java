package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import io.hypersistence.utils.hibernate.type.basic.YearMonthDateType;
import io.hypersistence.utils.hibernate.type.basic.YearMonthIntegerType;
import lombok.*;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.YearMonth;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "aml_monthly_utilization")
@Builder
@TypeDef(
        typeClass = YearMonthDateType.class,
        defaultForType = YearMonth.class
)
public class MonthlyUtilization extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    private Double acHours;

    private Integer acCycle;

    private Double apuHrs;

    private Integer apuCycle;

    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;

    @Column(columnDefinition = "decimal(10,2) default '0.00'")
    private Double ratio;

}
