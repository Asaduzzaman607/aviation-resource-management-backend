package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface RpdProjection {
    Long getId();
    String getTsn();
    String getCsn();
    String getTso();
    String getCso();
    String getTsr();
    String getCsr();
}
