package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import com.digigate.engineeringmanagement.planning.constant.MelType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmlDefectRectificationModelView {

    private Long id;

    private Long amlId;
    private Long nrcId;
    private Integer amlPageNo;

    private String seqNo;

    private String defectDmiNo;
    private String defectDescription;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime defectSignTime;

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

    private Long defectStaId;
    private String defectStaName;

    private Long rectStaId;
    private String rectStaName;

    private Long defectSignId;
    private String defectSignAuthNo;
    private String defectSignedEmployeeName;

    private Long rectSignId;
    private String rectSignAuthNo;
    private String rectSignedEmployeeName;
    private MelType melType;
    private LocalDate dueDate;

    private String reasonForRemoval;
    private String remark;
    private String woNo;

    private Boolean isActive;
}
