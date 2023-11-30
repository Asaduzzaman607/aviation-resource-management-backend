package com.digigate.engineeringmanagement.common.payload.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERPSearchRequestDto implements SDto {
    private String name;
    private String code;
    private String email;
    private Boolean isActive = true;
    private Long id;
}
