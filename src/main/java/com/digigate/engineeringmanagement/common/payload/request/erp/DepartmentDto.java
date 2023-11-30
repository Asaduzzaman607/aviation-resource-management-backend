package com.digigate.engineeringmanagement.common.payload.request.erp;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DepartmentDto implements IDto, Serializable {
    private Long id;
    private String companyId;
    private String name;
    private String code;
    private String info;
    private Long erpId;
}
