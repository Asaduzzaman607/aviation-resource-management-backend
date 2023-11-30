package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto implements IDto {
    private Long id;
    @NotBlank
    @JsonProperty("roomCode")
    @Size(min = 1, max = 100)
    private String code;
    @JsonProperty("roomName")
    @Size(min = 1, max = 100)
    private String name;
    @NotNull(message = ErrorId.ID_IS_REQUIRED)
    private Long officeId;
}
