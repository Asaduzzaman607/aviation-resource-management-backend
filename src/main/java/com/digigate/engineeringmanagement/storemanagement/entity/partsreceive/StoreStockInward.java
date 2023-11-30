package com.digigate.engineeringmanagement.storemanagement.entity.partsreceive;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "store_stock_inwards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreStockInward extends AbstractDomainBasedEntity {
    @Column(name = "voucher_no", unique = true, nullable = false)
    private String voucherNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private PartsInvoice partsInvoice;
    @Column(name = "invoice_id", updatable = false, insertable = false)
    private Long invoiceId;
    @Column(name = "receive_date")
    private LocalDateTime receiveDate;
    @Column(name = "tpt_mode")
    private Integer tptMode;
    @Column(name = "flight_no")
    private String flightNo;
    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;
    @Column(name = "airways_bill")
    private String airwaysBill;
    @Column(name = "packing_mode")
    private String packingMode;
    @Column(name = "packing_no")
    private String packingNo;
    @Column(name = "weight")
    private Integer weight;
    @Column(name = "no_of_items")
    private Integer noOfItems;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "import_no")
    private String importNo;
    @Column(name = "import_date")
    private LocalDateTime importDate;
    @Column(name = "discrepancy_report_no")
    private String discrepancyReportNo;
    @Column(name = "remarks")
    private String remarks;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_id", nullable = false)
    private User receivedBy;
    @Column(name = "received_by_id", updatable = false, insertable = false)
    private Long receivedById;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreStockInward)) return false;
        return this.getId() != 0 && this.getId().equals(((StoreStockInward) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
