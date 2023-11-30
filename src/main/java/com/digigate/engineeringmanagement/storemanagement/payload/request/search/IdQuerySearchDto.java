package com.digigate.engineeringmanagement.storemanagement.payload.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IdQuerySearchDto implements SDto {
    private String query;
    private Long id;
    private Boolean isActive = true;
    private Boolean isUsed = false;
}
