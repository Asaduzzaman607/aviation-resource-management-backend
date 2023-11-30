package com.digigate.engineeringmanagement.storemanagement.payload.response.scrap;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class StoreScrapPartViewModel {
    private Long id;
    private Long scrapId;
    private String scrapVoucherNo;
    private Long partId;
    private String partNo;
    private String partDescription;
    private PartClassification partClassification;
    private Set<ScrapPartSerialViewModel> partSerialViewModelList;
    private Boolean isActive;
    private Boolean isAlive;
    private Long uomId;
    private String uomCode;
}
