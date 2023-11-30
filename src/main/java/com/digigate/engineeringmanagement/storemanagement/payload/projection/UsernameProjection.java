package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface UsernameProjection {
    Long getId();
    String getLogin();
    Long getEmployeeId();
    String getEmployeeName();
    Long getEmployeeDesignationId();
    String getEmployeeDesignationName();
}