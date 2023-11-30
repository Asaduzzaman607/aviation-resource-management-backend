package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Systems ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemsViewModel {

    private Long id;
    private Long locationId;
    private String locationName;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
