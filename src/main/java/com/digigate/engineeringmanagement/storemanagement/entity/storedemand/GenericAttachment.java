package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "generic_attachments")
public class GenericAttachment extends AbstractDomainBasedEntity {
    @Column(name = "feature_name")
    @Enumerated(EnumType.STRING)
    private FeatureName featureName;
    @Column(name = "record_id")
    private Long recordId;
    private String link;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof GenericAttachment)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((GenericAttachment) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
