package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.MappingDto;
import com.digigate.engineeringmanagement.planning.payload.request.PropellerSearchDto;

public interface PropellerService {
    void apply(MappingDto mappingDto);

    byte[] getReport(PropellerSearchDto searchDto, String fileType);
}
