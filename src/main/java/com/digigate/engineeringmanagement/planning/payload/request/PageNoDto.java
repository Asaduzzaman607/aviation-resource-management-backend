package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageNoDto {
    private Long aircraftId;
    private Integer pageNo;
    private Character alphabet;
}
