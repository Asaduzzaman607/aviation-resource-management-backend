package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder

public class PartWiseUomResponseDto {
    private Long id;
    private Long partId;
    private Long uomId;
    private String uomCode;
}
