package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashboardPartIssueViewModel {
    private Long id;
    private String partNo;
    private Long issueId;
    private String voucherNo;
    private String name;
}
