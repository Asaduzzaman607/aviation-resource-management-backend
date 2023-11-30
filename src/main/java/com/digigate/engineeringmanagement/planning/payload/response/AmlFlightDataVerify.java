package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.AmlType;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class AmlFlightDataVerify {
    private Long amlId;
    private Integer pageNo;
    private Long flightDataId;

    public AmlFlightDataVerify(Long amlId, Integer pageNo, Long flightDataId) {
        this.amlId = amlId;
        this.pageNo = pageNo;
        this.flightDataId = flightDataId;
    }

    private AmlType amlType;

    public AmlFlightDataVerify(Long flightDataId, AmlType amlType) {
        this.flightDataId = flightDataId;
        this.amlType = amlType;
    }
}
