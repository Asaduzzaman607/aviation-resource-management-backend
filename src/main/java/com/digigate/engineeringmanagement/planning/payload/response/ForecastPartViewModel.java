package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.Part;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastPartViewModel {
    private Long modelId;
    private Part part;
}
