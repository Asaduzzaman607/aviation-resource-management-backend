package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Folder type enum
 *
 * @author Junaid Khan Pathan
 */

public enum FileUploadItemType {
    ATL(0),
    WORK_ORDER(1),
    AD(2),
    SB(3),
    OUT_OF_PHASE_TASK_CARD(4),
    ARC(5),
    DMI_LOG(6),
    CDL_LOG(7),
    ON_BOARD_DOCUMENTS(8),
    LETTERS(9),
    OTHERS(10),
    TASK_DONE(11);

    private Integer uploadItemType;

    FileUploadItemType(Integer uploadItemType) {
        this.uploadItemType = uploadItemType;
    }

    public Integer getUploadItemType() {
        return uploadItemType;
    }

    private static final Map<Integer, FileUploadItemType> uploadItemTypeMap = new HashMap<>();

    static {
        for (FileUploadItemType t: FileUploadItemType.values()) {
            uploadItemTypeMap.put(t.getUploadItemType(), t);
        }
    }

    public static FileUploadItemType get(Integer id) {
        if (!uploadItemTypeMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_FOLDER_TYPE);
        }
        return uploadItemTypeMap.get(id);
    }

    public static FileUploadItemType getByName(String name) {
        for(FileUploadItemType type: FileUploadItemType.values()){
            if(StringUtils.equals(type.name(), name)){
                return type;
            }
        }
        return null;
    }
}
