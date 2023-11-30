package com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInspectionGrnRequestDto implements IDto {
    private Long id;
    @NotBlank(message = ErrorId.GRN_NO_MUST_NOT_BE_NULL)
    private String grnNo;
    @NotNull
    private LocalDate createdDate;
}
