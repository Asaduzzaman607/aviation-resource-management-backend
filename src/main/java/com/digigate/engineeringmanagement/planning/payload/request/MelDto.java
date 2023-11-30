package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.DefermentCode;
import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import com.digigate.engineeringmanagement.planning.constant.MelStatus;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MelDto implements IDto {
    private Long intDefRectId;
    private Long correctDefRectId;

    private LocalDate dueDate;
    private LocalDate clearedDate;
    private String intermediateAction;
    private String dmiNo;
    private DefermentCode defermentCode;
    private MelCategory melCategory;
    private MelStatus status;
}
