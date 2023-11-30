package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashboardPartDemandViewModel {
    private Long id;
    private String partNo;
    private Long demandId;
    private String voucherNo;
    private String name;
}
