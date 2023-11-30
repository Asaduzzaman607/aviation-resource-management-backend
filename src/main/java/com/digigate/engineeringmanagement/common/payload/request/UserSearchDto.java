package com.digigate.engineeringmanagement.common.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchDto {
    private String login;
    private String name;
    private Boolean isActive;
    private Integer roleId;
}
