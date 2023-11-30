package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PositionModelView {
    private Position position;
    private Long modelId;
}
