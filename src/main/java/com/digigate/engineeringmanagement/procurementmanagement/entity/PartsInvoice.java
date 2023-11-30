package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InvoiceType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parts_invoices")
public class PartsInvoice extends AbstractDomainBasedEntity {
    @Column(name = "invoice_no", nullable = false)
    private String invoiceNo;
    @Column(name = "invoice_type")
    private InvoiceType invoiceType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prats_order_id", nullable = false)
    private PartOrder partOrder;
    @Column(name = "prats_order_id", insertable = false, updatable = false)
    private Long partOrderId;
    @Column(name = "tac")
    private String tac;
    @Column(name = "vendor_address")
    private String vendorAddress;
    @Column(name = "vendor_email")
    private String vendorEmail;
    @Column(name = "vendor_telephone")
    private String vendorTelephone;
    @Column(name = "vendor_fax")
    private String vendorFax;
    @Column(name = "vendor_website")
    private String vendorWebsite;
    @Column(name = "vendor_from")
    private String vendorFrom;
    @Column(name = "follow_up_by")
    private String followUpBy;
    @Column(name = "to_fax")
    private String toFax;
    @Column(name = "to_tel")
    private String toTel;
    @Column(name = "remark")
    private String remark;
    @Column(name = "ship_to")
    private String shipTo;
    @Column(name = "bill_to")
    private String billTo;
    @Column(name = "payment_terms")
    private String paymentTerms;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected", columnDefinition = "bit default 0")
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date")
    private LocalDate updateDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;
    @Column(name = "submitted_by_id", insertable = false, updatable = false)
    private Long submittedById;
    @Enumerated(EnumType.STRING)
    @Column(name = "rfq_type", nullable = false)
    private RfqType rfqType;
    @Column(name ="workflow_type", columnDefinition = "varchar(32) default 'OWN_DEPARTMENT'")
    @Enumerated(value = EnumType.STRING)
    private PartsInVoiceWorkFlowType workFlowType;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<VendorQuotationInvoiceFee> vendorQuotationInvoiceFeeList;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartsInvoice)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((PartsInvoice) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
