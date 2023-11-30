package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vendor_capabilities")
public class VendorCapability extends AbstractDomainBasedEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VendorCapability)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        VendorCapability vendorCapability = (VendorCapability) o;

        return getId() != null ? getId().equals(vendorCapability.getId()) : vendorCapability.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }

}
