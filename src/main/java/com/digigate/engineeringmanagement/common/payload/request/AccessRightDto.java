package com.digigate.engineeringmanagement.common.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessRightDto {
    @NotNull(message = ErrorId.ACCESS_RIGHT_REQUIRED)
    private Long accessRightId;
}
