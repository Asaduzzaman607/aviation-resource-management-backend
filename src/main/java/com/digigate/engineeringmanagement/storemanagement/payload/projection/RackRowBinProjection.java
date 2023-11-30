package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface RackRowBinProjection {
    //Rack Row
    Long getId();

    String getCode();

    //Rack
    Long getRackId();

    String getRackCode();

    //Room
    Long getRackRoomId();

    String getRackRoomName();

    String getRackRoomCode();

    //store
    Long getRackRoomOfficeId();

    String getRackRoomOfficeCode();
}
