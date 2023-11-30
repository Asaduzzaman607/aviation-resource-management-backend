package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface RackRowProjection {
    Long getId();

    String getCode();

    Long getRoomId();

    String getRoomName();

    String getRoomCode();

    Long getRoomOfficeId();

    String getRoomOfficeCode();
}
