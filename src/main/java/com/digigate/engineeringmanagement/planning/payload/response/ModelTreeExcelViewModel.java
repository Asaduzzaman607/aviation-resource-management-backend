package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * ModelTreeExcelViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelTreeExcelViewModel {
    private String modelName;
    private String higherModelName;
    private String locationName;
    private String positionName;
}
