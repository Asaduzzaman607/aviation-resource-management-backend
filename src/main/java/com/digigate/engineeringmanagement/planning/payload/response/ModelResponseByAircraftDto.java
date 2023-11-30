package com.digigate.engineeringmanagement.planning.payload.response;


import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelResponseByAircraftDto {

    private Long modelId;

    private String modelName;

    private ModelType modelType;
}
