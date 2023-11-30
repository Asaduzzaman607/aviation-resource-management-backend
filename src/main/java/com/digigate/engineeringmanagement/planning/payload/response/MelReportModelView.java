package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.MelCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MelReportModelView {
    private Long melId;
    private LocalDate date;
    private String station;
    private Integer refAml;
    private Character refAmlAlphabet;
    private String ata;
    private String defectDescription;
    private String intermediateAction;
    private MelCategory melCategory;
    private LocalDate melDueDate;
    private LocalDate melCleared;
    private Integer correctiveRefAml;
    private Character correctiveRefAmlAlphabet;
    private String correctiveAction;
    private String position;
    private String removalPN;
    private String removalSN;
    private String installedPN;
    private String installedSN;
    private String GRN;
}
