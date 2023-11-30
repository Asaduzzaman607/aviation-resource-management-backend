package com.digigate.engineeringmanagement.storemanagement.payload.request.scrap;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreScrapPartDto implements IDto {
    private Long id;
    @NotEmpty
    private List<ScrapPartSerialDto> scrapPartSerialDtos;
    @NotNull
    private Long partId;
    private Long uomId;
}
