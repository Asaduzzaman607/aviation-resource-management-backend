package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "propellers")
public class Propeller extends AbstractDomainBasedEntity {
    private String nomenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Long installationTsn;
    private Long installationTso;
    private Long currentTsn;
    private Long currentTso;
    private Integer limitMonth;
    private Integer limitFh;
    private LocalDate estimatedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Aircraft aircraft;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Propeller)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((Propeller) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
