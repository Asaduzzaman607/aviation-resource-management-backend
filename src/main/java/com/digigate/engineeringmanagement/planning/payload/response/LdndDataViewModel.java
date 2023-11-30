package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LdndDataViewModel {
    private Long ldndId;
    private Long taskId;
    private String taskNo;
    private Long partId;
    private String partNo;
    private String serialNo;
    private Long aircraftId;
    private String aircraftName;
    private LocalDate dueDate;
}
