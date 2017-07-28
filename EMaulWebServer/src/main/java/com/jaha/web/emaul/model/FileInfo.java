package com.jaha.web.emaul.model;

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by shavrani on 16. 5. 30..
 */
@Entity
@Table(name = "file_info")
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long fileKey;

    public String category;

    public String fileGroupKey;

    public String type;

    public String filePath;

    public String fileName;

    public String fileOriginName;

    public Long size;

    public String ext;

    public Long regId;

    public Date regDate;

    @Transient
    public File getFile() {
        File file = null;
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(fileName)) {
            try {
                file = new File(filePath + fileName);
            } catch (Exception e) {
            }

        }
        return file;
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
