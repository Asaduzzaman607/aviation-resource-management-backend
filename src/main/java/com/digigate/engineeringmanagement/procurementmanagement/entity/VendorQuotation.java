package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "vendor_quotations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VendorQuotation extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private QuoteRequest quoteRequest;
    @Column(name = "rfq_id", insertable = false, updatable = false)
    private Long quoteRequestId;

    @Column(nullable = false, unique = true)
    private String quotationNo;
    @Column(nullable = false)
    private LocalDate date;
    private LocalDate validUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_req_vendor_id", nullable = false)
    private QuoteRequestVendor quoteRequestVendor;
    @Column(name = "quote_req_vendor_id", insertable = false, updatable = false)
    private Long quoteRequestVendorId;

    private String vendorAddress;
    private String vendorEmail;
    private String vendorTel;
    private String vendorFax;
    private String vendorWebsite;
    private String vendorFrom;
    private String vendorQuotationNo;
    private String toAttention;
    private String toFax;
    private String toTel;
    private String remark;
    private Boolean quoteStatus;
    private String termsCondition;
    @Enumerated(EnumType.STRING)
    @Column(name = "rfq_type", nullable = false)
    private RfqType rfqType = RfqType.PROCUREMENT;
    private InputType inputType = InputType.CS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_order_id")
    private PartOrder partOrder;
    @Column(name = "part_order_id", insertable = false, updatable = false)
    private Long partOrderId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VendorQuotation)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((VendorQuotation) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}