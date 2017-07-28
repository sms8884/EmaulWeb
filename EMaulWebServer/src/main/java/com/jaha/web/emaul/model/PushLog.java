package com.jaha.web.emaul.model;

import java.util.Date;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 푸시로그
 */
public class PushLog {

    private Long id;

    /** 아파트ID */
    private Long aptId;

    /** 사용자ID */
    private Long userId;

    /** 구분 */
    private String gubun;

    /** 푸시제목 */
    private String title;

    /** 푸시메시지 */
    private String message;

    /** 단말수신여부 */
    private String deviceRecYn = "N";

    /** 푸시발송카운트 */
    private Integer pushSendCount = 1;

    /** 푸시클릭카운트 */
    private Integer pushClickCount = 0;

    /** SMS발송여부 */
    private String smsYn = "N";

    /** 단말유형(안드로이드 또는 iOS) */
    private String deviceType;

    /** 기타(무인택배함로그ID) */
    private String etc;

    /** 등록일시 */
    private Date regDate;

    /** 수정일시 */
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
     * @return the aptId
     */
    public Long getAptId() {
        return aptId;
    }

    /**
     * @param aptId the aptId to set
     */
    public void setAptId(Long aptId) {
        this.aptId = aptId;
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
     * @return the gubun
     */
    public String getGubun() {
        return gubun;
    }

    /**
     * @param gubun the gubun to set
     */
    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the deviceRecYn
     */
    public String getDeviceRecYn() {
        return deviceRecYn;
    }

    /**
     * @param deviceRecYn the deviceRecYn to set
     */
    public void setDeviceRecYn(String deviceRecYn) {
        this.deviceRecYn = deviceRecYn;
    }

    /**
     * @return the pushSendCount
     */
    public Integer getPushSendCount() {
        return pushSendCount;
    }

    /**
     * @param pushSendCount the pushSendCount to set
     */
    public void setPushSendCount(Integer pushSendCount) {
        this.pushSendCount = pushSendCount;
    }

    /**
     * @return the pushClickCount
     */
    public Integer getPushClickCount() {
        return pushClickCount;
    }

    /**
     * @param pushClickCount the pushClickCount to set
     */
    public void setPushClickCount(Integer pushClickCount) {
        this.pushClickCount = pushClickCount;
    }

    /**
     * @return the smsYn
     */
    public String getSmsYn() {
        return smsYn;
    }

    /**
     * @param smsYn the smsYn to set
     */
    public void setSmsYn(String smsYn) {
        this.smsYn = smsYn;
    }

    /**
     * @return the deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType the deviceType to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return the etc
     */
    public String getEtc() {
        return etc;
    }

    /**
     * @param etc the etc to set
     */
    public void setEtc(String etc) {
        this.etc = etc;
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
