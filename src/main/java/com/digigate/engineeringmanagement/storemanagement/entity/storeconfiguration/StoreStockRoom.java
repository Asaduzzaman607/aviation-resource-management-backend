package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_stock_rooms")
public class StoreStockRoom extends AbstractDomainBasedEntity {
    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code;
    @Column(name = "stock_room_no", nullable = false, length = 100)
    private String stockRoomNo;
    @Column(name = "description", length = 8000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;
    @Column(name = "office_id", insertable = false, updatable = false)
    private Long officeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoreStockRoom)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        StoreStockRoom storeStockRoom = (StoreStockRoom) o;
        return getId() != null ? getId().equals(storeStockRoom.getId()) : storeStockRoom.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
