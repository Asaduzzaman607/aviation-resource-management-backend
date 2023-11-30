package com.digigate.engineeringmanagement.common.entity;

import com.digigate.engineeringmanagement.common.constant.FeatureType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Setter
@Getter
@Entity
@Table(name="feature_role")
public class FeatureRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private Long featureId;

    @Column(nullable = false)
    private FeatureType featureType;
}
