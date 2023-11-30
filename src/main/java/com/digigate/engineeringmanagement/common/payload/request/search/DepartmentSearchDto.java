package com.digigate.engineeringmanagement.common.payload.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentSearchDto implements SDto {
    private String name;
    private String code;
    private Boolean isActive = true;
}
