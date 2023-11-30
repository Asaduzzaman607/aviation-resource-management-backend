package com.digigate.engineeringmanagement.storeinspector.payload.projection;

public interface PlanningSiProjection {
    Double getReturnPartsDetailTsn();
    Integer getReturnPartsDetailCsn();
    Double getReturnPartsDetailTsr();
    Integer getReturnPartsDetailCsr();
    Double getReturnPartsDetailTso();
    Integer getReturnPartsDetailCso();
    String getPartStateName();
}
