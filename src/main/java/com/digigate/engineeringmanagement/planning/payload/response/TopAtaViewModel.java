package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopAtaViewModel {
    private String ata;
    private String system;
    private Long total;
    private Long totalMarep;
    private Long totalPirep;
}
