package com.jaha.web.emaul.v2.model.app;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called app_category
 */
@Alias(value = "AppCategoryVo")
public class AppCategoryVo implements Serializable {

    private static final long serialVersionUID = -972635177965465924L;

    /** 카테고리 일련번호 */
    private Long id;
    /** 부모 카테고리 번호 */
    private Long pId;
    /** 기기구분 : A or android, I or IOS */
    private String os;
    /** 카테고리 구분 */
    // app_push : App > 알람메세지
    private String category;
    /** 카테고리 명 */
    private String categoryName;
    /** 카테고리 아이디 */
    private String categoryId;
    /** 정렬순서 */
    private int sortNo;
    /** 사용여부 */
    private String useYn;
    /** 등록자 */
    private Long userId;
    /** 등록일시 */
    private Date regDate;
    /** 수정자 */
    private Long modId;
    /** 수정일시 */
    private Date modDate;
    /** 최종 등록일 DB컬럼 아님 */
    private Date lastDate;
    /** 등록 갯수 */
    private int lastCnt;


    /**
     * @return the lastCnt
     */
    public int getLastCnt() {
        return lastCnt;
    }

    /**
     * @param lastCnt the lastCnt to set
     */
    public void setLastCnt(int lastCnt) {
        this.lastCnt = lastCnt;
    }

    /**
     * @return the lastDate
     */
    public Date getLastDate() {
        return lastDate;
    }

    /**
     * @param lastDate the lastDate to set
     */
    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
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
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the pId
     */
    public Long getpId() {
        return pId;
    }

    /**
     * @param pId the pId to set
     */
    public void setpId(Long pId) {
        this.pId = pId;
    }

    /**
     * @return the os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the sortNo
     */
    public int getSortNo() {
        return sortNo;
    }

    /**
     * @param sortNo the sortNo to set
     */
    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    /**
     * @return the useYn
     */
    public String getUseYn() {
        return useYn;
    }

    /**
     * @param useYn the useYn to set
     */
    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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


}
