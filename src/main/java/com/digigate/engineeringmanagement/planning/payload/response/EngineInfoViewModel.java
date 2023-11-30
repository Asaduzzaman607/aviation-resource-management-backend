package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.Objects;

/**
 * Engine Info View Model
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineInfoViewModel {
    private Long aircraftBuildId;
    private String aircraftName;
    private String partDescription;
    private String positionName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineInfoViewModel that = (EngineInfoViewModel) o;
        return Objects.equals(getAircraftBuildId(), that.getAircraftBuildId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAircraftBuildId());
    }
}
