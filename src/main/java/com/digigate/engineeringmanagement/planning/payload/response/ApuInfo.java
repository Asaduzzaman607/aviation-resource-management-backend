package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Apu Info
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApuInfo {
    private String apuPartNo;
    private String apuSerialNo;
    private Double apuTsn;
    private Integer apuCsn;
    private Double apuTSR;
    private Integer apuCSR;

}
