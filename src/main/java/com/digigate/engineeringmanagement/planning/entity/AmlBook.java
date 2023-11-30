package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.*;

/**
 * AML book entity class
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "aml_books", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aircraft_id", "start_page_no"})
})
public class AmlBook extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    private String bookNo;
    @Column(name = "start_page_no", nullable = false)
    private Integer startPageNo;
    @Column(nullable = false)
    private Integer endPageNo;
}
