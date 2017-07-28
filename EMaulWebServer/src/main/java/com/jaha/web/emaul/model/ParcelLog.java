package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 택배사용기록
 */
@Entity
@Table(name = "parcel_log")
public class ParcelLog {

    /** 일련번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = ParcelLocker.class, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "lockerId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private ParcelLocker parcelLocker;

    @ManyToOne
    private ParcelCompany parcelCompany;

    public ParcelCompany getParcelCompany() {
        return parcelCompany;
    }

    /** 택배함UUID(무인택배함 단말기에서 서버로 통신할 때 사용) */
    // @Transient
    @Column(length = 40, nullable = false)
    private String uuid;

    /** 보관함번호 */
    @Column(length = 40, nullable = false)
    private String lockerNum;

    /** 구분(new:신규발송, return:반송, long:장기미수거) */
    @Column(length = 10, nullable = false)
    private String gubun = "new";

    /** 타입(keep:물품보관, find:택배찾기) */
    @Column(length = 10, nullable = false)
    private String type;


    /** 동 */
    @Column(nullable = false)
    private Integer dong;

    /** 호 */
    @Column(nullable = false)
    private Integer ho;

    /** 핸드폰번호 */
    @Column(length = 20, nullable = true)
    private String phone;

    /** 택배기사핸드폰번호 */
    @Column(length = 20, nullable = true)
    private String parcelPhone;

    /** 보관함비밀번호 */
    @Column(length = 300, nullable = false)
    private String password;

    /** 상태(push/sms) */
    @Column(length = 10, nullable = false)
    private String status;

    /** 맡긴날짜 */
    @Column(length = 10, nullable = false)
    private String inputDate;

    /** 찾아간날짜 */
    @Column(length = 10, nullable = true)
    private String outputDate;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;

    /** 찾아간 일시 */
    @Column(nullable = true)
    private Date findDate;

    /* 관리사무소에서 찾아간일시 */
    @Column(nullable = true)
    private Date officeKeepDate;

    /** 사무실 저장여부 */
    @Column(nullable = false)
    private String officeStoreYn;

    /** 수정자 id */
    @Column(nullable = false)
    private Long modId;

    /** 삭제여부 */
    @Column(nullable = false)
    private String delYn;
    /** 찾아간 일시 */
    @Column(nullable = true)
    private Date delDate;



    @Transient
    public Date sendTime;

    @Transient
    public int warningDay;


    /** ApiNumber */
    @Column(nullable = false)
    private Integer apiNumber;



    @PrePersist
    public void prePersist() {
        this.uuid = this.parcelLocker.getUuid();
        // this.type = "find";
        // this.status = "sms";
        this.regDate = new Date();
    }

    @PostLoad
    public void postPersist() {

    }

    @PreUpdate
    public void preUpdate() {

    }

    @PreRemove
    public void preRemove() {

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
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the lockerNum
     */
    public String getLockerNum() {
        return lockerNum;
    }

    /**
     * @param lockerNum the lockerNum to set
     */
    public void setLockerNum(String lockerNum) {
        this.lockerNum = lockerNum;
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
     * @return the dong
     */
    public Integer getDong() {
        return dong;
    }

    /**
     * @param dong the dong to set
     */
    public void setDong(Integer dong) {
        this.dong = dong;
    }

    /**
     * @return the ho
     */
    public Integer getHo() {
        return ho;
    }

    /**
     * @param ho the ho to set
     */
    public void setHo(Integer ho) {
        this.ho = ho;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the parcelPhone
     */
    public String getParcelPhone() {
        return parcelPhone;
    }

    /**
     * @param parcelPhone the parcelPhone to set
     */
    public void setParcelPhone(String parcelPhone) {
        this.parcelPhone = parcelPhone;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the inputDate
     */
    public String getInputDate() {
        return inputDate;
    }

    /**
     * @param inputDate the inputDate to set
     */
    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }

    /**
     * @return the outputDate
     */
    public String getOutputDate() {
        return outputDate;
    }

    /**
     * @param outputDate the outputDate to set
     */
    public void setOutputDate(String outputDate) {
        this.outputDate = outputDate;
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
     * @return the parcelLocker
     */
    public ParcelLocker getParcelLocker() {
        return parcelLocker;
    }

    /**
     * @param parcelLocker the parcelLocker to set
     */
    public void setParcelLocker(ParcelLocker parcelLocker) {
        this.parcelLocker = parcelLocker;
    }

    /**
     * @return the apiNumber
     */
    public Integer getApiNumber() {
        return apiNumber;
    }

    /**
     * @param apiNumber the apiNumber to set
     */
    public void setApiNumber(Integer apiNumber) {
        this.apiNumber = apiNumber;
    }



    /**
     * @return the findDate
     */
    public Date getFindDate() {
        return findDate;
    }

    /**
     * @param findDate the findDate to set
     */
    public void setFindDate(Date findDate) {
        this.findDate = findDate;
    }

    /**
     * @return the officeKeepDate
     */
    public Date getOfficeKeepDate() {
        return officeKeepDate;
    }

    /**
     * @param officeKeepDate the officeKeepDate to set
     */
    public void setOfficeKeepDate(Date officeKeepDate) {
        this.officeKeepDate = officeKeepDate;
    }

    /**
     * @return the officeStoreYn
     */
    public String getOfficeStoreYn() {
        return officeStoreYn;
    }

    /**
     * @param officeStoreYn the officeStoreYn to set
     */
    public void setOfficeStoreYn(String officeStoreYn) {
        this.officeStoreYn = officeStoreYn;
    }

    /**
     * @return the warningDay
     */
    public int getWarningDay() {
        return warningDay;
    }

    /**
     * @param warningDay the warningDay to set
     */
    public void setWarningDay(int warningDay) {
        this.warningDay = warningDay;
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
     * @return the delYn
     */
    public String getDelYn() {
        return delYn;
    }

    /**
     * @param delYn the delYn to set
     */
    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    /**
     * @return the delDate
     */
    public Date getDelDate() {
        return delDate;
    }

    /**
     * @param delDate the delDate to set
     */
    public void setDelDate(Date delDate) {
        this.delDate = delDate;
    }

    @Override
    public String toString() {
        return "ParcelLog{" + "id=" + id + ", parcelLocker=" + parcelLocker + ", parcelCompany=" + parcelCompany + ", uuid='" + uuid + '\'' + ", lockerNum='" + lockerNum + '\'' + ", gubun='" + gubun
                + '\'' + ", type='" + type + '\'' + ", dong=" + dong + ", ho=" + ho + ", phone='" + phone + '\'' + ", parcelPhone='" + parcelPhone + '\'' + ", password='" + password + '\''
                + ", status='" + status + '\'' + ", inputDate='" + inputDate + '\'' + ", outputDate='" + outputDate + '\'' + ", regDate=" + regDate + '\'' + "apiNumber=" + apiNumber + '}';
    }
}
