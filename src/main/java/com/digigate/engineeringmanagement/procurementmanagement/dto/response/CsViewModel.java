package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class CsViewModel {
     Long id;
     String csNo;
     Long parentId;
}
