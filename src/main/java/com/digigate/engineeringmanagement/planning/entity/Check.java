package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Check Entity
 *
 * @author Ashraful
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "checks")
public class Check extends AbstractDomainBasedEntity {
    @Column(nullable = false,unique = true)
    private String title;
    private String description;
}
