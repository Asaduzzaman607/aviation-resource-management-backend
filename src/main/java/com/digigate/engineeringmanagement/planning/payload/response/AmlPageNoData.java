package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmlPageNoData {
    private Integer itDefectAmlPage;
    private Integer ctRectificationAmlPage;
    private Long itDefectAmlPageId;
    private Long ctRectificationAmlPageId;
    private Long itDefectId;
    private Long ctRectificationId;
    private String itDefectSeqNo;
    private String ctRectificationSeqNo;
}
