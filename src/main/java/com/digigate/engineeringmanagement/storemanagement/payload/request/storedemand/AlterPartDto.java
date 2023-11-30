package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlterPartDto {
    private Long id;
    private Long partId;
    private String partNo;
    private String partDescription;
    private Long uomId;
    private String uomCode;
}
