package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vendor_capabilities_logs")
public class VendorCapabilityLog extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_capabilities_id", nullable = false)
    private VendorCapability vendorCapability;

    @Column(name = "vendor_capabilities_id", insertable = false, updatable = false)
    private Long vendorCapabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "vendor_id", updatable = false, insertable = false)
    private Long vendorId;

    @Column(name = "status")
    private boolean status = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VendorCapabilityLog)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        VendorCapabilityLog vendorCapabilityLog = (VendorCapabilityLog) o;

        return getId() != null ? getId().equals(vendorCapabilityLog.getId()) : vendorCapabilityLog.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
