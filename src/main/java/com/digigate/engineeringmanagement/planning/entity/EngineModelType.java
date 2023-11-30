package com.digigate.engineeringmanagement.planning.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "engine_model_types")
public class EngineModelType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public String getCreatedAt() {
        return createdAt.toString();
    }
}
