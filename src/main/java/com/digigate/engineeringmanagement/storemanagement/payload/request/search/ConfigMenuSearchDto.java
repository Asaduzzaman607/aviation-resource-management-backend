package com.digigate.engineeringmanagement.storemanagement.payload.request.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigMenuSearchDto extends IdQuerySearchDto {
    private Boolean isWorkflow;
}
