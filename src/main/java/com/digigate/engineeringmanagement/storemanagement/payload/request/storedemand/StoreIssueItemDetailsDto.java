package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueItemDetailsDto implements IDto {

    private Long id;
    @NotNull
    private Long demandItemId;
    @NotNull
    private Long issueId;
    private Integer quantity;
    private String cardLineNo;
    private String remark;
}
