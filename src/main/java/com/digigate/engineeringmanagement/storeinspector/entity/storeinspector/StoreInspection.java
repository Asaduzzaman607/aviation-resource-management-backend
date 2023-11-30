package com.digigate.engineeringmanagement.storeinspector.entity.storeinspector;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_inspections")
public class StoreInspection extends AbstractDomainBasedEntity {

    @Column(name = "inspection_no", unique = true, nullable = false, length = 100)
    private String inspectionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_inward_id")
    private StoreStockInward stockInward;
    @Column(name = "stock_inward_id", updatable = false, insertable = false)
    private Long stockInwardId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_part_details_id")
    private ReturnPartsDetail returnPartsDetail;
    @Column(name = "return_part_details_id", insertable = false, updatable = false)
    private Long returnPartsDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;
    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", nullable = false)
    private Serial serial;
    @Column(name = "serial_id", insertable = false, updatable = false)
    private Long serialId;

    @Column(name = "serial_no", length = 100)
    private String serialNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_serial_id")
    private StorePartSerial partSerial;

    @Column(name = "part_serial_id", insertable = false, updatable = false)
    private Long partSerialId;

    @Column(name = "status")
    private InspectionApprovalStatus status;

    @OneToOne
    @JoinColumn(name = "store_inspection_grn_id")
    private StoreInspectionGrn storeInspectionGrn;

    @Column(name = "store_inspection_grn_id", insertable = false, updatable = false)
    private Long storeInspectionGrnId;

    @Column(name = "remarks", length = 8000)
    private String remarks;
    @Column(name = "is_alive")
    private Boolean isAlive;
    @Builder.Default()
    @Column(name = "quantity")
    private Integer quantity = INT_ONE;

    @Column(name = "valid_until")
    private String validUntil;

    @Column(name = "lot_num")
    private String lotNum;

    @Column(name = "certi_No")
    private String certiNo;

    @Column(name = "inspection_auth_no")
    private String inspectionAuthNo;

    @Column(name = "part_state_name")
    private String partStateName;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreInspection)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreInspection) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}

