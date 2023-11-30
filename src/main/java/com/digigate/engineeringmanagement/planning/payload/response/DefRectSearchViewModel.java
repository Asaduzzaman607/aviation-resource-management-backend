package com.digigate.engineeringmanagement.planning.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefRectSearchViewModel {

    private Long defectId;
    private LocalDate amlDate;

    @JsonIgnore
    private Integer pageNo;
    @JsonIgnore
    private Character alphabet;

    private String defectDesc;
    private String actionDesc;
    private String partNo;
    private String ata;

    public DefRectSearchViewModel(Long defectId, LocalDate amlDate, Integer pageNo, Character alphabet,
                                  String defectDesc, String actionDesc, String partNo, String ata) {
        this.defectId = defectId;
        this.amlDate = amlDate;
        this.pageNo = pageNo;
        this.alphabet = alphabet;
        this.defectDesc = defectDesc;
        this.actionDesc = actionDesc;
        this.partNo = partNo;
        this.ata = ata;
    }


    private String reference;
    private Long partId;
    private Long locationId;
    private Long aircraftId;
}
