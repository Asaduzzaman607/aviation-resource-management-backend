package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AMLDefectRectificationSearchDto implements SDto {

    private String pageNo;

    private Boolean isActive;
}
