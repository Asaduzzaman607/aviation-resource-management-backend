package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rooms")
public class Room extends AbstractDomainBasedEntity {
    @Column(name = "code", nullable = false, length = 100)
    private String code;
    @Column(name = "name", length = 100)
    private String name;
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "office_id", insertable = false, updatable = false)
    private Long officeId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Room)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((Room) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
