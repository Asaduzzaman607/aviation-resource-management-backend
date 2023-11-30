package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Folder Entity
 *
 * @author Junaid Khan Pathan
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "folders")
public class Folder extends AbstractDomainBasedEntity {
    @Column(name = "item_type", nullable = false)
    private FileUploadItemType uploadItemType;

    @Column(name = "folder_name", nullable = false, length = ApplicationConstant.MAX_FILE_FOLDER_SIZE)
    private String folderName;

    @OneToMany(mappedBy = "folder",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<PlanningFile> planningFiles = new HashSet<>();

    @Column(name = "match_string")
    private String matchString;

    @Column(name = "folder_path")
    private String folderPath;
}
