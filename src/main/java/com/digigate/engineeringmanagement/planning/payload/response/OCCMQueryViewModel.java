package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.Position;
import lombok.*;

import java.time.LocalDate;

/**
 * OCCM Query result view model
 *
 * @author ashinisingha
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OCCMQueryViewModel {
    private Long id;
    private String ata;
    private String description;
    private String partNumber;
    private String serialNumber;
    private Position position;
    private LocalDate installationDate;
    private Double installationFH;
    private Integer installationFC;
    private Boolean isTsnAvailable;
    private Double currentTSN;
    private Integer currentCSN;
    private Boolean isOverhauled;
    private Double currentTSO;
    private Integer currentCSO;
    private Boolean IsShopVisited;
    private Double currentTSR;
    private Integer currentCSR;
    private Double countFactor;
}
