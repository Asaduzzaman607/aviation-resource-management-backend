package com.digigate.engineeringmanagement.status.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandStatusRequestDto {
    private Long demandId;
    private Long partId;

}
