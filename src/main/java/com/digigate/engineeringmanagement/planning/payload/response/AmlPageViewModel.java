package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;


/**
 * Aml Page ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmlPageViewModel {
    private Long amlId;
    private Integer pageNo;
    private Character alphabet;
    private LocalDate date;
}
