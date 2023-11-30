package com.digigate.engineeringmanagement.storemanagement.payload.request.scrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreScrapPartAndSerialDto {

    private Long id;
    private Long storeScrapPartId;
    private Long storePartSerialId;
    private Integer quantity;
}
