package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.SettingsHeaderEnum;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settings")
public class Settings extends AbstractDomainBasedEntity {

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private SettingsHeaderEnum headerKey;
    private String headerValue;

}
