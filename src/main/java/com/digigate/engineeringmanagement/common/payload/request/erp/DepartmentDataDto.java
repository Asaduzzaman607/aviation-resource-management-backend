package com.digigate.engineeringmanagement.common.payload.request.erp;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentDataDto implements Serializable {
    private Long id;
    private String name;
    private String code;
    private String info;
}
