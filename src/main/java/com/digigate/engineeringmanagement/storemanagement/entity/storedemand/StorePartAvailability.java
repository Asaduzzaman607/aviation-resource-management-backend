package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.constant.LocationTag;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.*;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_parts_availabilities")
public class StorePartAvailability extends AbstractDomainBasedEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_row_id")
    private RackRow rackRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_row_bin_id")
    private RackRowBin rackRowBin;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_tag")
    private LocationTag locationTag;

    @Column(name = "other_location")
    private String otherLocation;

    @Column(name = "min_stock")
    private Integer minStock = 0;

    @Column(name = "max_stock")
    private Integer maxStock = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_room_id")
    private StoreStockRoom stockRoom;

    @Column(columnDefinition = "integer default 0")
    private Integer quantity = 0;

    @Column(name = "office_id", insertable = false, updatable = false)
    private Long officeId;

    @Column(name = "stock_room_id", insertable = false, updatable = false)
    private Long stockRoomId;

    @Column(name = "room_id", insertable = false, updatable = false)
    private Long roomId;

    @Column(name = "rack_id", insertable = false, updatable = false)
    private Long rackId;

    @Column(name = "rack_row_id", insertable = false, updatable = false)
    private Long rackRowId;

    @Column(name = "rack_row_bin_id", insertable = false, updatable = false)
    private Long rackRowBinId;

    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @Column(columnDefinition = "integer default 0")
    private Integer demandQuantity = 0;

    @Column(columnDefinition = "integer default 0")
    private Integer issuedQuantity = 0;

    @Column(columnDefinition = "integer default 0")
    private Integer requisitionQuantity = 0;

    @OneToMany(mappedBy = "storePartAvailability", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<StorePartSerial> storePartSerialList = new ArrayList<>();

    public static StorePartAvailability from(Part part) {
        StorePartAvailability availability = new StorePartAvailability();
        availability.setPart(part);
        return availability;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StorePartAvailability)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StorePartAvailability) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
