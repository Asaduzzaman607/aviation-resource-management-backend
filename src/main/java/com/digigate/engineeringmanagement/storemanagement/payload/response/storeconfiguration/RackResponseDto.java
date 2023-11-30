package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RackResponseDto {
    private Long rackId;
    private String rackCode;
    private Double rackHeight;
    private Double rackWidth;
    private Long roomId;
    private String roomName;
    private String roomCode;
    private Long officeId;
    private String officeCode;
}
