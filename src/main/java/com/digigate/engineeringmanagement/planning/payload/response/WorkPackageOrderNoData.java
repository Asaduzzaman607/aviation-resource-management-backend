package com.digigate.engineeringmanagement.planning.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkPackageOrderNoData {

    private Integer year;
    private Integer orderNo;
}
