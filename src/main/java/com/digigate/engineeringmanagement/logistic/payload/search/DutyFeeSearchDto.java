package com.digigate.engineeringmanagement.logistic.payload.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DutyFeeSearchDto  implements SDto {
    private String query;
}
