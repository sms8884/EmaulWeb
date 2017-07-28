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
 * Class Name : AppMainTemplate.java
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
public class AppMainTemplate {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private Integer id;

    private String code; // Primary Key

    private String templateTitle;

    private String moreShowUrl;

    private Short keywordCount;

    private Short displayOrder;

    private String displayYn;

    private String displayTemplateName;

    private Long regId;

    private Date regDate;

    private Long modId;

    private Date modDate;

    private String highlightText;

    private String moreShowUrlUseYn;


    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

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
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the templateTitle
     */
    public String getTemplateTitle() {
        return templateTitle;
    }

    /**
     * @param templateTitle the templateTitle to set
     */
    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    /**
     * @return the moreShowUrl
     */
    public String getMoreShowUrl() {
        return moreShowUrl;
    }

    /**
     * @param moreShowUrl the moreShowUrl to set
     */
    public void setMoreShowUrl(String moreShowUrl) {
        this.moreShowUrl = moreShowUrl;
    }

    /**
     * @return the keywordCount
     */
    public Short getKeywordCount() {
        return keywordCount;
    }

    /**
     * @param keywordCount the keywordCount to set
     */
    public void setKeywordCount(Short keywordCount) {
        this.keywordCount = keywordCount;
    }

    /**
     * @return the displayOrder
     */
    public Short getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder the displayOrder to set
     */
    public void setDisplayOrder(Short displayOrder) {
        this.displayOrder = displayOrder;
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
     * @return the displayTemplateName
     */
    public String getDisplayTemplateName() {
        return displayTemplateName;
    }

    /**
     * @param displayTemplateName the displayTemplateName to set
     */
    public void setDisplayTemplateName(String displayTemplateName) {
        this.displayTemplateName = displayTemplateName;
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
    public String getRegDate() {
        return regDate == null ? "" : sdf.format(regDate);
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
     * @return the highlightText
     */
    public String getHighlightText() {
        return highlightText;
    }

    /**
     * @param highlightText the highlightText to set
     */
    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }

    /**
     * @return the moreShowUrlUseYn
     */
    public String getMoreShowUrlUseYn() {
        return moreShowUrlUseYn;
    }

    /**
     * @param moreShowUrlUseYn the moreShowUrlUseYn to set
     */
    public void setMoreShowUrlUseYn(String moreShowUrlUseYn) {
        this.moreShowUrlUseYn = moreShowUrlUseYn;
    }

    // ----------------------------------------------------------------------
    // toString METHOD
    // ----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(id);
        sb.append("|");
        sb.append(code);
        sb.append("|");
        sb.append(templateTitle);
        sb.append("|");
        sb.append(moreShowUrl);
        sb.append("|");
        sb.append(keywordCount);
        sb.append("|");
        sb.append(displayOrder);
        sb.append("|");
        sb.append(displayYn);
        sb.append("|");
        sb.append(displayTemplateName);
        sb.append("|");
        sb.append(regId);
        sb.append("|");
        sb.append(regDate);
        sb.append("|");
        sb.append(modId);
        sb.append("|");
        sb.append(modDate);
        sb.append("|");
        sb.append(highlightText);
        sb.append("|");
        sb.append(moreShowUrlUseYn);
        return sb.toString();
    }


}
