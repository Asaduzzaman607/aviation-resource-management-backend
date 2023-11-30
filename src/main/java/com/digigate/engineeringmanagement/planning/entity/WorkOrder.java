package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Work Order entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "work_orders")
public class WorkOrder extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    private String workShopMaint;
    @Column(nullable = false, unique = true)
    private String woNo;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Double totalAcHours;
    @Column(nullable = false)
    private Integer totalAcLanding;
    private String tsnComp;
    private String tsoComp;
    @Column(nullable = false)
    private LocalDate asOfDate;

    @JsonIgnore
    @OneToMany(mappedBy = "workOrder",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<WoTask>woTaskSet;

    public void addWoTask(WoTask woTask){
        if(Objects.isNull(woTaskSet)){
            woTaskSet = new HashSet<>();
        }
        woTaskSet.add(woTask);
        woTask.setWorkOrder(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WorkOrder)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((WorkOrder) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
