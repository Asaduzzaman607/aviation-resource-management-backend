package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDemandsDto implements IDto {
    private Long id;
    private Long departmentId;
    private DepartmentType departmentType;
    private Long aircraftId;
    private Long airportId;
    private Long vendorId;
    private Set<String> attachment;
    private LocalDate demandDate;
    private LocalDate validTill;
    @Size(max = 8000)
    private String remarks;
    @Size(max = 100)
    private String workOrderNo;
    @Valid
    @NotEmpty
    private List<StoreDemandDetailsDto> storeDemandDetailsDtoList;
}
