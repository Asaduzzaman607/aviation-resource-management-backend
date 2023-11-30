package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.OfficeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoreStockRoomResponseDto {
    @JsonProperty("stockRoomId")
    private Long storeStockRoomId;
    @JsonProperty("stockRoomCode")
    private String storeStockRoomCode;
    @JsonProperty("stockRoomNo")
    private String storeStockRoomNo;
    @JsonProperty("stockRoomDes")
    private String storeStockRoomDescription;
    private Long officeId;
    private String officeCode;
}
