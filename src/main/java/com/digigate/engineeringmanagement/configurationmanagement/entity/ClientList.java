package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client_list")
public class ClientList extends AbstractDomainBasedEntity {
    @Column(name = "name", length = 8000, nullable = false, unique = true)
    private String clientName;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ClientList)) return false;
        return this.getId() != 0 && this.getId().equals(((ClientList) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
