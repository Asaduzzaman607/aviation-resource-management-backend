package com.digigate.engineeringmanagement.common.payload;


/**
 * Marker interface for search dto.
 *
 * @author Masud Rana
 */
public interface SDto {
    default Boolean getIsActive() {
        return true;
    }
     default Boolean getIsDesc(){
        return false;
    }
}
