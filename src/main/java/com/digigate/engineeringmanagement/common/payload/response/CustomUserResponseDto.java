package com.digigate.engineeringmanagement.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserResponseDto {
    private Long designationId;
    private Long sectionId;
    private Long departmentId;
    private Long userId;
    private String logIn;
    private Long employeeId;

    public List<String> populateAsList() {
        return List.of(String.valueOf(getDesignationId()), String.valueOf(getDepartmentId())
                , String.valueOf(getUserId()), String.valueOf(getEmployeeId()), logIn, String.valueOf(getSectionId()));
    }
}
