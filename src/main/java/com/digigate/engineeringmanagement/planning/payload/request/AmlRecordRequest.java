package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Aml OilRecord Request Model
 *
 * @author Sayem Hasnat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmlRecordRequest {
    @NotNull
    private AmlOilRecordDto onArrival;
    @NotNull
    private AmlOilRecordDto upLift;
}
