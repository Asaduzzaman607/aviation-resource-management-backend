package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.*;

/**
 * Model Excel ResponseDto
 *
 * @author Nafiul Islam
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelExcelResponseDto {
    private String modelName;
    private ModelType modelType;
    private String description;
    private String aircraftModelName;

}
