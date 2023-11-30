package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnserviceablePartPositionDto {
    private Long partId;
    private String positionName;
}
