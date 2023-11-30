package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreWorkOrderDto implements IDto {
    private Long id;
    private String chkSvcOhrnNo;
    @NotNull
    private Long unserviceablePartId;
    private String reasonRemark;
}
