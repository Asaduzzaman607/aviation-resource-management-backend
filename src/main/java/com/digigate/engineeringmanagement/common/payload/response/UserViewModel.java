package com.digigate.engineeringmanagement.common.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserViewModel {
    private Long id;
    private Long employeeId;
    private String login;
    private Integer roleId;
    private String roleName;
    private String name;
    private String email;
    private String phoneNumber;
    private String mobile;
    private String position;
    private String department;
    private String section;
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern = "dd-MM-YYYY hh:mm:ss" , timezone="UTC")
    private LocalDateTime createdAt;
    private Boolean isActive;
}
