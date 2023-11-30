package com.digigate.engineeringmanagement.planning.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Wo Task Entity
 * @author ashinisingha
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wo_tasks")
public class WoTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "wo_id", insertable = false, updatable = false)
    private Long workOrderId;

    @Column(nullable = false)
    private Integer slNo;

    private String description;
    private String workCardNo;
    private LocalDate complianceDate;
    private LocalDate accomplishDate;
    private String authNo;
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
