package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jaha.web.emaul.util.Util;

/**
 * @author
 * @description 택배 SMS내용
 */
@Entity
@Table(name = "parcel_sms_policy")
public class ParcelSmsPolicy {

    @Id
    @Column(nullable = false)
    private Long aptId;

    @OneToOne(targetEntity = Apt.class, cascade = {})
    @JoinColumn(name = "aptId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Apt apt;

    /** 비회원에게 SMS 발송 여부(Y/N) */
    @Column(length = 1, nullable = false)
    private String sendYn;

    /** 시범서비스시작날짜 */
    @Column(length = 8, nullable = false)
    private String testServiceStartDate;

    /** 시범서비스종료날짜 */
    @Column(length = 8, nullable = false)
    private String testServiceEndDate;

    /** 시범서비스기간 발송할 문자메시지 제목(30자) */
    @Column(length = 90, nullable = false)
    private String testServiceMsgTitle = "택배 알림 메시지";

    /** 시범서비스기간 발송할 문자메시지 본문(300자) */
    @Column(length = 900, nullable = false)
    private String testServiceMsg = "SMS 택배 알림은 시범서비스기간 동안 한시적으로 제공됩니다. 무인택배시스템 이용을 원하시는 분은 이마을앱을 설치하시기 바랍니다. https://goo.gl/iyyLTO";

    /** 장기보관택배수거일수 */
    @Column(nullable = false)
    private Integer longParcelCollectDay = 1;

    /** 등록자ID */
    @Column(nullable = false)
    private Long regId;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;

    /** 수정자ID */
    @Column(nullable = false)
    private Long modId;

    /** 수정일시 */
    @Column(nullable = false)
    private Date modDate;

    /** 시범서비스 유효여부 */
    @Transient
    private Boolean testServiceValid;

    /** 장기보관택배수거일 */
    @Transient
    private String longParcelCollectDate;

    @PrePersist
    public void prePersist() {
        this.regDate = new Date();
    }

    @PostLoad
    public void postPersist() {
        Date startDate = Util.convertString2Date(this.testServiceStartDate + "000000");
        Date endDate = Util.convertString2Date(this.testServiceEndDate + "235959");
        Date nowDate = new Date();

        if (nowDate.after(startDate) && nowDate.before(endDate)) {
            testServiceValid = true;
        } else {
            testServiceValid = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modDate = new Date();
    }

    // @PreRemove
    // public void preRemove() {
    //
    // }

    /**
     * @return the apt
     */
    public Apt getApt() {
        return apt;
    }

    /**
     * @param apt the apt to set
     */
    public void setApt(Apt apt) {
        this.apt = apt;
        this.aptId = apt.id;
    }

    /**
     * @return the sendYn
     */
    public String getSendYn() {
        return sendYn;
    }

    /**
     * @param sendYn the sendYn to set
     */
    public void setSendYn(String sendYn) {
        this.sendYn = sendYn;
    }

    /**
     * @return the testServiceStartDate
     */
    public String getTestServiceStartDate() {
        return testServiceStartDate;
    }

    /**
     * @param testServiceStartDate the testServiceStartDate to set
     */
    public void setTestServiceStartDate(String testServiceStartDate) {
        this.testServiceStartDate = testServiceStartDate;
    }

    /**
     * @return the testServiceEndDate
     */
    public String getTestServiceEndDate() {
        return testServiceEndDate;
    }

    /**
     * @param testServiceEndDate the testServiceEndDate to set
     */
    public void setTestServiceEndDate(String testServiceEndDate) {
        this.testServiceEndDate = testServiceEndDate;
    }

    /**
     * @return the testServiceMsgTitle
     */
    public String getTestServiceMsgTitle() {
        return testServiceMsgTitle;
    }

    /**
     * @param testServiceMsgTitle the testServiceMsgTitle to set
     */
    public void setTestServiceMsgTitle(String testServiceMsgTitle) {
        this.testServiceMsgTitle = testServiceMsgTitle;
    }

    /**
     * @return the testServiceMsg
     */
    public String getTestServiceMsg() {
        return testServiceMsg;
    }

    /**
     * @param testServiceMsg the testServiceMsg to set
     */
    public void setTestServiceMsg(String testServiceMsg) {
        this.testServiceMsg = testServiceMsg;
    }

    /**
     * @return the longParcelCollectDay
     */
    public Integer getLongParcelCollectDay() {
        return longParcelCollectDay;
    }

    /**
     * @param longParcelCollectDay the longParcelCollectDay to set
     */
    public void setLongParcelCollectDay(Integer longParcelCollectDay) {
        this.longParcelCollectDay = longParcelCollectDay;
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
     * @return the testServiceValid
     */
    public Boolean getTestServiceValid() {
        return testServiceValid;
    }

    /**
     * @param testServiceValid the testServiceValid to set
     */
    public void setTestServiceValid(Boolean testServiceValid) {
        this.testServiceValid = testServiceValid;
    }

    /**
     * @return the longParcelCollectDate
     */
    public String getLongParcelCollectDate() {
        return longParcelCollectDate;
    }

    /**
     * @param longParcelCollectDate the longParcelCollectDate to set
     */
    public void setLongParcelCollectDate(String longParcelCollectDate) {
        this.longParcelCollectDate = longParcelCollectDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParcelSmsPolicy [apt.id=" + apt.id + ", sendYn=" + sendYn + ", testServiceStartDate=" + testServiceStartDate + ", testServiceEndDate=" + testServiceEndDate + ", testServiceMsgTitle="
                + testServiceMsgTitle + ", testServiceMsg=" + testServiceMsg + ", longParcelCollectDay=" + longParcelCollectDay + ", regId=" + regId + ", regDate=" + regDate + ", modId=" + modId
                + ", modDate=" + modDate + ", testServiceValid=" + testServiceValid + ", longParcelCollectDate=" + longParcelCollectDate + "]";
    }
}
