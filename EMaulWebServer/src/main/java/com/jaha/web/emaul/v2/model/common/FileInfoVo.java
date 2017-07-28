package com.jaha.web.emaul.v2.model.common;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * Created by shavrani on 16. 5. 30..
 */
@Alias(value = "FileInfoVo")
public class FileInfoVo implements Serializable {

    private static final long serialVersionUID = 7562738785226399190L;

    private Long fileKey;

    private String category;

    private String fileGroupKey;

    private String type;

    private String filePath;

    private String fileName;

    private String fileOriginName;

    private Long size;

    private String ext;

    private Long regId;

    private Date regDate;

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

    public void prePersist() {
        regDate = new Date();
    }


    /**
     * @return the fileKey
     */
    public Long getFileKey() {
        return fileKey;
    }

    /**
     * @param fileKey the fileKey to set
     */
    public void setFileKey(Long fileKey) {
        this.fileKey = fileKey;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the fileGroupKey
     */
    public String getFileGroupKey() {
        return fileGroupKey;
    }

    /**
     * @param fileGroupKey the fileGroupKey to set
     */
    public void setFileGroupKey(String fileGroupKey) {
        this.fileGroupKey = fileGroupKey;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the fileOriginName
     */
    public String getFileOriginName() {
        return fileOriginName;
    }

    /**
     * @param fileOriginName the fileOriginName to set
     */
    public void setFileOriginName(String fileOriginName) {
        this.fileOriginName = fileOriginName;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the ext
     */
    public String getExt() {
        return ext;
    }

    /**
     * @param ext the ext to set
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * @return the regId
     */
    public Long getRegId() {
        return regId;
    }

    /**
     * @param regId the regId to set
     */
    public void setRegId(Long regId) {
        this.regId = regId;
    }

    /**
     * @return the regDate
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
