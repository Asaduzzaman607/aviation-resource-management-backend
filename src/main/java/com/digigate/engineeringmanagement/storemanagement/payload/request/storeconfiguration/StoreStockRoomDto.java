package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class StoreStockRoomDto implements IDto {
    @NotBlank
    @JsonProperty("stockRoomCode")
    @Size(max = 100)
    private String storeStockRoomCode;
    @NotBlank
    @JsonProperty("stockRoomNo")
    @Size(max = 100)
    private String storeStockRoomNo;
    @JsonProperty("stockRoomDes")
    @Size(max = 8000)
    private String storeStockRoomDescription;
    @NotNull
    private Long officeId;
}
