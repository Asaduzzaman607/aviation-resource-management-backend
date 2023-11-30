package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskTypeModelView {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;

}
