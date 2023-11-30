package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Non Technical ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NonTechnicalViewModel {
    private Integer month;
    private Integer year;
    private Integer turbulence;
    private Integer lightningStrike;
    private Integer birdStrike;
    private Integer foreignObjectDamage;
    private Integer acDamagedByGroundEqpt;
    private Integer other;
    private Integer nonTechnicalTotal;
    private Double nonTechnicalRate;
}
