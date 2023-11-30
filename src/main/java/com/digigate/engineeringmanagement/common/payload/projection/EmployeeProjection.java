package com.digigate.engineeringmanagement.common.payload.projection;

public interface EmployeeProjection {
    Long getId();

    String getName();

    Long getDesignationId();

    Long getDesignationSectionId();

    Long getDesignationSectionDepartmentId();

}
