package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.Part;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;

/**
 * Aircraft Build Part and serial search viewmodel
 *
 * @author ashinisingha
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftBuildPartSerialSearchViewModel {
    private Boolean isTsnAvailable;
    private Double tsnHour;
    private Integer tsnCycle;
    private Double tsoHour;
    private Integer tsoCycle;
    private Boolean isOverhauled;
    private Double tslsvHour;
    private Integer tslsvCycle;
    private Boolean isShopVisited;
    @JsonIgnore
    private Double aircraftInHour;
    @JsonIgnore
    private Integer aircraftInCycle;
    @JsonIgnore
    private Double aircraftOutHour;
    @JsonIgnore
    private Integer aircraftOutCycle;
}
