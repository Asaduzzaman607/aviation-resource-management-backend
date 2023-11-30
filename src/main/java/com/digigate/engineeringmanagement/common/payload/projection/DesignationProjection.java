package com.digigate.engineeringmanagement.common.payload.projection;

public interface DesignationProjection {
    //Designation
    Long getId();

    String getName();

    //selection

    Long getSectionId();

    String getSectionName();

    //department

    Long getSectionDepartmentId();

    String getSectionDepartmentCompanyId();

    String getSectionDepartmentName();

    String getSectionDepartmentCode();

    String getSectionDepartmentInfo();


}
