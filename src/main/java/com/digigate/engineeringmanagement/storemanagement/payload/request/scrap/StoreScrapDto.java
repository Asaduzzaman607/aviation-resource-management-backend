package com.digigate.engineeringmanagement.storemanagement.payload.request.scrap;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class StoreScrapDto implements IDto {
    private Long id;
    private String remarks;
    private Long uomId;
    @Valid
    private List<StoreScrapPartDto> scrapParts;
    private Set<String> attachmentList = new HashSet<>();
}
