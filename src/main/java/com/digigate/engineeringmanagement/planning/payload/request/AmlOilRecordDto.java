package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * AmlOilRecord dto
 *
 * @author Sayem Hasnat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmlOilRecordDto implements IDto {
    private Long id;
    private OilRecordTypeEnum type;
    private Double hydOil1;
    private Double hydOil2;
    private Double hydOil3;
    private Double engineOil1;
    private Double engineOil2;
    private Double apuOil;
    private Double csdOil1;
    private Double csdOil2;
    private Double oilRecord;
    private Long amlId;
    private Boolean isActive;
}
