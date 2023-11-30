package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * PlanningFile Entity
 *
 * @author Junaid Khan Pathan
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "planning_files")
public class PlanningFile extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @Column(name = "folder_id", insertable = false, updatable = false)
    private Long folderId;

    @Column(name = "file_name", nullable = false, length = ApplicationConstant.MAX_FILE_FOLDER_SIZE)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_key")
    private String fileKey;

//    @Column(name = "match_string")
//    private String matchString;
}
