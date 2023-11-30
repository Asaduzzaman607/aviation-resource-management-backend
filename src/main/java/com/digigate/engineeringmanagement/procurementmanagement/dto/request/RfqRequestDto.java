
package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RfqRequestDto implements IDto {
    private Long id;
    private Long requisitionId;
    private Long partOrderId;
    private RfqType rfqType = RfqType.PROCUREMENT;
    private Boolean isOrder = false;
    @Valid
    private List<QuoteRequestVendorDto> quoteRequestVendorModelList;

    @JsonIgnore
    private ProcurementRequisition requisition;
    @JsonIgnore
    private PartOrder partOrder;
    @JsonIgnore
    private InputType inputType;
}

