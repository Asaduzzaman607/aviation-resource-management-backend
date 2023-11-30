package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import com.digigate.engineeringmanagement.planning.constant.MelType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AMLDefectRectificationDto implements IDto {

    private Long id;
    private Long amlId;
    private Long nrcId;

    private String seqNo;

    private Long defectSignId;

    private Long defectStaId;

    private String defectDmiNo;

    private String defectDescription;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime defectSignTime;

    private Long rectSignId;

    private Long rectStaId;

    private String rectDmiNo;

    private String rectMelRef;

    private MelCategory melCategory;

    private String rectAta;

    private String rectPos;

    private String rectPnOff;

    private String rectSnOff;

    private String rectPnOn;

    private String rectSnOn;

    private String rectGrn;

    private String rectDescription;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rectSignTime;

    private MelType melType = MelType.NONE;

    private LocalDate dueDate;

    private Long melId;

    private String reasonForRemoval;

    private String remark;
    private String woNo;
}
