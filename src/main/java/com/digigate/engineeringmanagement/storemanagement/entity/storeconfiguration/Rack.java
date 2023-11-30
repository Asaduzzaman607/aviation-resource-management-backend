package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "racks")
public class Rack extends AbstractDomainBasedEntity {
    @Column(name = "code", nullable = false, length = 100)
    private String code;
    @Column(name = "height")
    private Double height;
    @Column(name = "width")
    private Double width;
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    @Column(name = "room_id", insertable = false, updatable = false)
    private Long roomId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Rack)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((Rack) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
