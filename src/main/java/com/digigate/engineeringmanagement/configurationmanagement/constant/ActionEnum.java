package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.entity.Action;

public enum ActionEnum {
    SAVE(10000, "Save"),
    EDIT(10001, "Edit"),
    DELETE(10002, "Delete"),
    SEARCH(10003, "Search"),
    REPLENISHMENT(10004, "Replenishment");

    private Integer id;
    private String name;

    ActionEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Action toEntity(ActionEnum actionEnum) {
        Action action = new Action();
        action.setId(actionEnum.getId());
        action.setActionName(actionEnum.getName());
        return action;
    }
}
