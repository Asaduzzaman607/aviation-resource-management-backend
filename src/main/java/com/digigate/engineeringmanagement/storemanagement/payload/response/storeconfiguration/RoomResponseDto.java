package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomResponseDto {
    private String roomCode;
    private Long roomId;
    private String roomName;
    private Long officeId;
    private String officeCode;
}
