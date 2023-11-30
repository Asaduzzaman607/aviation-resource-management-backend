package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Apu Shop Visit Info
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApuShopVisitInfo {
    private String model;
    private LocalDate date;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private String status;
}
