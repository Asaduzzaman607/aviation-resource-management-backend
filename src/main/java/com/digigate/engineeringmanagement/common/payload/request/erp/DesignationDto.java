package com.digigate.engineeringmanagement.common.payload.request.erp;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DesignationDto implements IDto,Serializable {
    private Long id;
    private Long sectionId;
    private String name;
    private Long erpId;
}
