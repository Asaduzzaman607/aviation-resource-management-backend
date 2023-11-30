package com.digigate.engineeringmanagement.storemanagement.entity.scrap;

import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "scrap_part_serials")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreScrapPartSerial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_scrap_part_id")
    private StoreScrapPart storeScrapPart;

    @Column(name = "store_scrap_part_id", insertable = false, updatable = false)
    private Long storeScrapPartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_part_serial_id")
    private StorePartSerial storePartSerial;

    @Column(name = "store_part_serial_id", insertable = false, updatable = false)
    private Long storePartSerialId;

    private Integer quantity;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreScrapPartSerial)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreScrapPartSerial) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
