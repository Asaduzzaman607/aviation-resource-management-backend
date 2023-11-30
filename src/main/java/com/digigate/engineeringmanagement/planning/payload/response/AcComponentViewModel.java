package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcComponentViewModel {
    private String ataChapter;
    private String partName;
    private String partNo;
    private String serialNo;
    private Set<String> higherSerialNo;
    private Set<String> alternatePartNo;
    private String higherPartName;
    private String higherPartNo;
    private Set<String> taskCardRef;
    private Set<String> dueFor;
    private Set<String> ampRef;
    private LocalDate comManufactureDate;
    private Long lifeLimit;
    private String lifeLimitUnit;
    private LocalDate discardDueDate;
    private Double discardDueHour;
    private Long discardDueCycle;
    List<AcComponentHistory> acComponentHistories;
    List<TaskComponentResponse> tboData;
}
