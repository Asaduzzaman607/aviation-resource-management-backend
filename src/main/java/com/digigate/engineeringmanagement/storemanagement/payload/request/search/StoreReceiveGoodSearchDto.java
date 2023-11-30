package com.digigate.engineeringmanagement.storemanagement.payload.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreReceiveGoodSearchDto implements SDto {
    private LocalDate goodReceiveDate;
    private Long id;
    private Boolean isActive = true;
}
