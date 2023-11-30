package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.DefermentCode;
import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import com.digigate.engineeringmanagement.planning.constant.MelStatus;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MelModelView {
    private Long id;
    private LocalDate dueDate;
    private LocalDate clearedDate;
    private MelStatus status;
    private MelCategory melCategory;
    private String intermediateAction;
    private DefermentCode defermentCode;
    private String dmiNo;
    private AmlPageNoData amlPage;
    private Boolean isActive;
}
