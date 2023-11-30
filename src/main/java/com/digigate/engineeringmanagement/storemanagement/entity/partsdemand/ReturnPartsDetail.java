package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.AircraftBuild;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "return_parts_details")
public class ReturnPartsDetail extends AbstractDomainBasedEntity{
    @Column(name = "removal_date")
    private LocalDate removalDate;
    @Column(name = "auth_code_no",length = 100)
    private String authCodeNo;
    @Column(name = "reason_removed", length = 8000)
    private String reasonRemoved;
    @Column(name = "tsn")
    private Double tsn;
    @Column(name = "csn")
    private Integer csn;
    @Column(name = "tso")
    private Double tso;
    @Column(name = "cso")
    private Integer cso;
    @Column(name = "tsr")
    private Double tsr;
    @Column(name = "csr")
    private Integer csr;
    @Column(name = "is_used")
    private Boolean isUsed = Boolean.FALSE;

    @Column(name = "caab_enabled")
    private Boolean caabEnabled = Boolean.FALSE;
    @Column(name = "caab_status")
    private String caabStatus;
    @Column(name = "caab_remarks")
    private String caabRemarks;
    @Column(name = "caab_checkbox")
    private String caabCheckbox;
    @Column(name = "approval_auth_no")
    private String approvalAuthNo;
    @Column(name = "authorized_date")
    private LocalDate authorizedDate;
    @Column(name = "authorizes_date")
    private LocalDate authorizesDate;
    @Column(name = "cert_approval_ref")
    private String certApprovalRef;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_user")
    private Employee authorizedUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizes_user")
    private Employee authorizesUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "store_return_part_id")
    private StoreReturnPart storeReturnPart;
    @Column(name = "store_return_part_id", insertable = false, updatable = false)
    private Long storeReturnPartId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "removed_part_serial_id", nullable = false)
    private StorePartSerial removedPartSerial;
    @Column(name = "removed_part_serial_id", updatable = false, insertable = false)
    private Long removedPartSerialId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "installed_part_serial_id", nullable = false)
    private StorePartSerial installedPartSerial;
    @Column(name = "installed_part_serial_id", insertable = false, updatable = false)
    private Long installedPartSerialId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "part_position_id", nullable = false)
    private Position position;
    @Column(name = "part_position_id", insertable = false, updatable = false)
    private Long positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "removed_from_aircraft_id")
    private Aircraft removedFromAircraft;
    @Column(name = "removed_from_aircraft_id", insertable = false, updatable = false)
    private Long removedFromAircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "airport_id")
    private Airport airport;
    @Column(name = "airport_id", insertable = false, updatable = false)
    private Long airportId;
    @Column(name = "update_date", nullable = false)
    @UpdateTimestamp
    private LocalDate updateDate;

    private String authNo;
    private String sign;
    private LocalDate createdDate;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ReturnPartsDetail)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(),
                (((ReturnPartsDetail) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
