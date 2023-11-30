package com.digigate.engineeringmanagement.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String name;
    private Boolean isDeleted = false;
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> userSet;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "role_accesses",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "access_right_id")}
    )
    private Set<AccessRight> accessRightSet;

    public void addAccessRight(AccessRight accessRight) {
        if (accessRightSet == null) {
            accessRightSet = new HashSet<>();
        }
        if (!accessRightSet.contains(accessRight)) {
            accessRightSet.add(accessRight);
            accessRight.addRole(this);
        }
    }


    public void addUser(User user) {
        if (userSet == null) {
            userSet = new HashSet<>();
        }
        if (!userSet.contains(user)) {
            userSet.add(user);
            user.setRole(this);
        }
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Role)) return false;
        return this.getId() != 0 && this.getId().equals(((Role) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}