package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.DefermentCode;
import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mels")
public class Mel extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long amlAircraftId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intermediate_df_id")
    private AMLDefectRectification intDefRect;

    @Column(name = "intermediate_df_id", insertable = false, updatable = false)
    private Long intDefRectId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corrective_rf_id")
    private AMLDefectRectification correctDefRect;

    @Column(name = "corrective_rf_id", insertable = false, updatable = false)
    private Long correctDefRectId;
}
