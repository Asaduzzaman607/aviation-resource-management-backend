package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import com.digigate.engineeringmanagement.planning.constant.MelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aml_defect_rectifications")
public class AMLDefectRectification extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aml_id")
    private AircraftMaintenanceLog aircraftMaintenanceLog;

    @Column(name = "aml_id", insertable = false, updatable = false)
    private Long aircraftMaintenanceLogId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nrc_id")
    private NonRoutineCard nonRoutineCard;

    @Column(name = "nrc_id", insertable = false, updatable = false)
    private Long nonRoutineCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defect_sta_id")
    private Airport defectAirport;

    @Column(name = "defect_sta_id", insertable = false, updatable = false)
    private Long defectAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rect_sta_id")
    private Airport rectAirport;

    @Column(name = "rect_sta_id", insertable = false, updatable = false)
    private Long rectAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rect_sign_id")
    private Signature rectSign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defect_sign_id")
    private Signature defectSign;

    private String seqNo;
    private String defectDmiNo;
    @Column(length = 500)
    private String defectDescription;
    private LocalDateTime defectSignTime;

    private String rectDmiNo;
    private String rectMelRef;
    private MelCategory melCategory;
    private String rectAta;
    private String rectPos;
    private String rectPnOff;
    private String rectSnOff;
    private String rectPnOn;
    private String rectSnOn;
    private String rectGrn;
    @Column(length = 500)
    private String rectDescription;
    private LocalDateTime rectSignTime;
    private MelType melType;
    private LocalDate dueDate;
    private String reasonForRemoval;
    private String remark;
    @Column(name = "wo_no")
    private String woNo;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AMLDefectRectification)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AMLDefectRectification) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
