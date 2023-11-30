package com.digigate.engineeringmanagement.common.payload.request.erp;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SectionDto implements IDto {
    private Long id;
    private Long departmentId;
    private String name;
    private Long erpId;
}
