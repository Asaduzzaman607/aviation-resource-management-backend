package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MelViewMode {
    private Long correctiveRectificationId;
    private Integer pageNo;
    private Character alphabet;
    private Long melId;
    private String seqNo;
}
