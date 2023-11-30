package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * This class is use for oit uplift report query
 * @author ashraful
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmlFlightDataForOilUpliftReportViewModel {
    Long amlId;
    Double airTime;
}

