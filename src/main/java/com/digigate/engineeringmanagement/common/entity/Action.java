package com.digigate.engineeringmanagement.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * action entity
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "actions")
public class Action {
    @Id
    private Integer id;

    private String actionName;

    public static Action withId(Integer id) {
        Action action = new Action();
        action.setId(id);
        return action;
    }
}
