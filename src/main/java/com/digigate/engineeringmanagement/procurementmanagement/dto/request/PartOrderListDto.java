package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.procurementmanagement.constant.DiscountType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PartOrderListDto {
    @Size(max = 8000)
    private String tac;
    @Size(max = 8000)
    private String remark;
    private RfqType rfqType;
    private OrderType orderType = OrderType.PURCHASE;
    @Size(max = 8000)
    private String shipTo;
    @Size(max = 8000)
    private String invoiceTo;
    private DiscountType discountType = DiscountType.AMOUNT;
    private Double discount;
    @Valid
    List<PartOrderDto> partOrderDtoList;
    private String orderNo;
    private String companyName;
    private String pickUpAddress;
}
