package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "Vendors")
public class Vendor extends AbstractDomainBasedEntity {
    @Column(name = "vendor_type")
    @Enumerated(EnumType.STRING)
    private VendorType vendorType;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "country_origin_id")
    private Country countryOrigin;
    @Column(name = "country_origin_id", insertable = false, updatable = false)
    private Long countryOriginId;
    @Column(name = "address")
    private String address;
    @Column(name = "office_phone")
    private String officePhone;
    @Column(name = "emergency_contact")
    private String emergencyContact;
    @Column(name = "email")
    private String email;
    @Column(name = "website")
    private String website;
    @Column(name = "skype")
    private String skype;
    @Column(name = "items_build")
    private String itemsBuild;
    @Column(name = "loading_port")
    private String loadingPort;
    @Column(name = "valid_till")
    private LocalDate validTill;
    @Column(name = "status")
    private Boolean status = Boolean.FALSE;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    @Column(name = "city_id", insertable = false, updatable = false)
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedById;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id")
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected")
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date")
    private LocalDate updateDate;
    @Enumerated(EnumType.STRING)
    private VendorWorkFlowType workflowType;
    @Column(name = "contact_person", nullable = false, length = 8000)
    private String contactPerson;
    @Column(name = "contact_skype")
    private String contactSkype;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;

    public boolean isManufacturerOrSupplier() {
        return getVendorType() == VendorType.MANUFACTURER
                || getVendorType() == VendorType.SUPPLIER;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Vendor)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((Vendor) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
