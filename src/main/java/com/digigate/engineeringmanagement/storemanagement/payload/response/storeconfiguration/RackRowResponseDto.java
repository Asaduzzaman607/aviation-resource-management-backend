package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RackRowResponseDto {
    private Long rackRowId;
    private String rackRowCode;
    private Long rackId;
    private String rackCode;
    private Long roomId;
    private String roomName;
    private String roomCode;
    private Long officeId;
    private String officeCode;
}
