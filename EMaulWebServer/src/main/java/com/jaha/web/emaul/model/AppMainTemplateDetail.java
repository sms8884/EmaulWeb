/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 6.
 */
package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * Class Name : AppMainTemplateDetail.java
 * Description : Description
 *  
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 6.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 6.
 * @version 1.0
 */
public class AppMainTemplateDetail {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private Integer id; // Primary Key

    private Integer mainTemplateId;

    private String code;

    private String contentsTitle;

    private Long postId;

    private Long fileGroupKey;

    private String contentsText;

    private String category;

    private String color;

    private Long regId;

    private Date regDate;

    private Long modId;

    private Date modDate;

    private String webUrl;

    private String displayYn;

    private String direction;



    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the contentsTitle
     */
    public String getContentsTitle() {
        return contentsTitle;
    }

    /**
     * @param contentsTitle the contentsTitle to set
     */
    public void setContentsTitle(String contentsTitle) {
        this.contentsTitle = contentsTitle;
    }

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /**
     * @return the fileGroupKey
     */
    public Long getFileGroupKey() {
        return fileGroupKey;
    }

    /**
     * @param fileGroupKey the fileGroupKey to set
     */
    public void setFileGroupKey(Long fileGroupKey) {
        this.fileGroupKey = fileGroupKey;
    }

    /**
     * @return the contentsText
     */
    public String getContentsText() {
        return contentsText;
    }

    /**
     * @param contentsText the contentsText to set
     */
    public void setContentsText(String contentsText) {
        this.contentsText = contentsText;
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
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
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
    // public String getRegDate() {
    // return regDate == null ? "" : sdf.format(regDate);
    // }
    public Date getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the modId
     */
    public Long getModId() {
        return modId;
    }

    /**
     * @param modId the modId to set
     */
    public void setModId(Long modId) {
        this.modId = modId;
    }

    /**
     * @return the modDate
     */
    public Date getModDate() {
        return modDate;
    }

    /**
     * @param modDate the modDate to set
     */
    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }


    /**
     * @return the webUrl
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * @param webUrl the webUrl to set
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * @return the mainTemplateId
     */
    public Integer getMainTemplateId() {
        return mainTemplateId;
    }

    /**
     * @param mainTemplateId the mainTemplateId to set
     */
    public void setMainTemplateId(Integer mainTemplateId) {
        this.mainTemplateId = mainTemplateId;
    }

    /**
     * @return the displayYn
     */
    public String getDisplayYn() {
        return displayYn;
    }

    /**
     * @param displayYn the displayYn to set
     */
    public void setDisplayYn(String displayYn) {
        this.displayYn = displayYn;
    }

    /**
     * @return the direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    // ----------------------------------------------------------------------
    // toString METHOD
    // ----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(code);
        sb.append("|");
        sb.append(contentsTitle);
        sb.append("|");
        sb.append(postId);
        sb.append("|");
        sb.append(fileGroupKey);
        sb.append("|");
        sb.append(contentsText);
        sb.append("|");
        sb.append(category);
        sb.append("|");
        sb.append(color);
        sb.append("|");
        sb.append(regId);
        sb.append("|");
        sb.append(regDate);
        sb.append("|");
        sb.append(modId);
        sb.append("|");
        sb.append(modDate);
        sb.append("|");
        sb.append(mainTemplateId);
        return sb.toString();
    }

}
