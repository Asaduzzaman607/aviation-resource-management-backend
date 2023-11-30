package com.digigate.engineeringmanagement.logistic.payload.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DutyFeeItemResponseDto {

    private Long id;
    private String fees;
    private Double totalAmount;
    private Long dutyFeeId;
    private Long currencyId;
    private String currencyCode;
    private Boolean isActive;
}
