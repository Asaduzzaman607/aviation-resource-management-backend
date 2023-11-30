package com.digigate.engineeringmanagement.common.config.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelData {
    List<Map<String, ?>> dataList;
    List<String> errorMessages;
}
