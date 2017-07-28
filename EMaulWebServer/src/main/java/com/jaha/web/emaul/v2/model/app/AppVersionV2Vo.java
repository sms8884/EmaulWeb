package com.jaha.web.emaul.v2.model.app;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called app_version_v2
 */
@Alias(value = "AppVersionV2Vo")
public class AppVersionV2Vo implements Serializable {

    private static final long serialVersionUID = -643578752825219702L;

    /** 투표 일련번호 */
    private Long id;
    /** 기기구분 : A or android, I or IOS */
    private String os;
    /** 버전코드 */
    private String versionCode;
    /** 버전 */
    private String versionName;
    /** 강제 업데이트 여부 (Y/N) */
    private String forceYn;
    /** 안내 메세지 */
    private String message;
    /** 업데이트 경로 */
    private String url;
    /** 등록자 */
    private Long userId;
    /** 등록일 */
    private Date regDate;

    /** 사용여부 (Y/N) */
    private String useYn;

    private Long modId;
    private Date modDate;

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
     * @return the versionCode
     */
    public String getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set
     */
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the forceYn
     */
    public String getForceYn() {
        return forceYn;
    }

    /**
     * @param forceYn the forceYn to set
     */
    public void setForceYn(String forceYn) {
        this.forceYn = forceYn;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
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
