package com.jaha.web.emaul.v2.model.common;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 10. 21.
 * @description 푸시로그
 */
@Alias(value = "PushLogVo")
public class PushLogVo {

    /** 일련번호 */
    private Long id;

    /** 아파트ID */
    private Long aptId;

    /** 사용자ID */
    private Long userId;

    /** 구분(parcel-ad, board) */
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

    /** 기타(무인택배함로그ID, 게시판ID 등) */
    private String etc;

    /** 푸시 엑션 */
    private String action;

    /** 등록일시 */
    private Date regDate;

    /** 수정일시 */
    private Date modDate;

    // public void prePersist() {
    // // this.gubun = "parcel-ad";
    // // this.deviceRecYn = "N";
    // this.pushSendCount = 1;
    // this.pushClickCount = 0;
    // this.smsYn = "N";
    // this.regDate = new Date();
    // this.modDate = new Date();
    // }

    public Long getId() {
        return id;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAptId() {
        return aptId;
    }

    public void setAptId(Long aptId) {
        this.aptId = aptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceRecYn() {
        return deviceRecYn;
    }

    public void setDeviceRecYn(String deviceRecYn) {
        this.deviceRecYn = deviceRecYn;
    }

    public Integer getPushSendCount() {
        return pushSendCount;
    }

    public void setPushSendCount(Integer pushSendCount) {
        this.pushSendCount = pushSendCount;
    }

    public Integer getPushClickCount() {
        return pushClickCount;
    }

    public void setPushClickCount(Integer pushClickCount) {
        this.pushClickCount = pushClickCount;
    }

    public String getSmsYn() {
        return smsYn;
    }

    public void setSmsYn(String smsYn) {
        this.smsYn = smsYn;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
