package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vendor_wise_client_list")
public class VendorWiseClientList extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId", nullable = false)
    private Vendor vendor;
    @Column(name = "vendorId", insertable = false, updatable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientListId", nullable = false)
    private ClientList clientList;
    @Column(name = "clientListId", insertable = false, updatable = false)
    private Long clientListId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VendorWiseClientList)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((VendorWiseClientList) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
