package com.digigate.engineeringmanagement.planning.payload.response;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PropellerReportHeaderData {
    private Double tat;
    private Integer tac;
    private Double propTsn;
    private Integer propCsn;
    private Double propTso;
    private Integer propCso;
    private String modelName;
    private String propPartNo;
    private String propSerialNo;
    private String positionName;
    private LocalDate updatedDate;
}
