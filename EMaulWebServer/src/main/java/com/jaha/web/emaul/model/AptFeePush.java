/**
 * 
 */
package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
// import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
// import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
@Entity
@Table(name = "apt_fee_push", indexes = {
        // @Index(name = "idx_keyword_search_user", columnList = "email, typeId")
        // , @Index(name = "idx_fullname_user", columnList = "fullName")
        // , @Index(name = "idx_nickname_user", columnList = "nickname")
        // , @Index(name = "idx_reg_date_user", columnList = "regDate")
})
public class AptFeePush {

    /** 일련번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** 구분(1:부과,2:마감) */
    @Column(length = 1, nullable = false)
    private String gubun;
    /** 예약여부(Y:예약,N:즉시) */
    @Column(length = 1, nullable = false)
    private String bookYn;
    /** 발송일시(예약:yyyyMMddhh,즉시:yyyyMMddhhmmss) */
    @Column(length = 14, nullable = false)
    private String sendDate;
    /** 발송여부(Y:발송완료,N:미발송) */
    @Column(length = 1, nullable = false)
    private String sendYn;
    /** 내용(20자이상,100자미만) */
    @Column(length = 300, nullable = false)
    private String contents;

    @ManyToOne(targetEntity = Apt.class, cascade = {})
    @JoinColumn(name = "aptId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Apt apt;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "regId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;
    /** 수정자ID */
    @Column(nullable = true)
    @JsonIgnore
    private Long modId;
    /** 수정일시 */
    @Column(nullable = true)
    @JsonIgnore
    private Date modDate;

    /** 홍길동(101동) 형식의 등록자 정보 */
    @Transient
    private String regUserInfo;

    @PrePersist
    public void prePersist() {
        this.regDate = new Date();
    }

    @PostLoad
    public void postPersist() {
        this.regUserInfo = this.user.getFullName() + "(" + this.user.house.dong + "동)";
    }



    /**
     * @return the id
     */
    @Transient
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Transient
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the gubun
     */
    @Transient
    public String getGubun() {
        return gubun;
    }

    /**
     * @param gubun the gubun to set
     */
    @Transient
    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    /**
     * @return the sendDate
     */
    @Transient
    public String getSendDate() {
        return sendDate;
    }

    /**
     * @param sendDate the sendDate to set
     */
    @Transient
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    /**
     * @return the contents
     */
    @Transient
    public String getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    @Transient
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * @return the regUserInfo
     */
    @Transient
    public String getRegUserInfo() {
        return regUserInfo;
    }

    /**
     * @param regUserInfo the regUserInfo to set
     */
    @Transient
    public void setRegUserInfo(String regUserInfo) {
        this.regUserInfo = regUserInfo;
    }

    /**
     * @return the sendYn
     */
    @Transient
    public String getSendYn() {
        return sendYn;
    }

    /**
     * @param sendYn the sendYn to set
     */
    @Transient
    public void setSendYn(String sendYn) {
        this.sendYn = sendYn;
    }

    /**
     * @return the bookYn
     */
    @Transient
    public String getBookYn() {
        return bookYn;
    }

    /**
     * @param bookYn the bookYn to set
     */
    @Transient
    public void setBookYn(String bookYn) {
        this.bookYn = bookYn;
    }

    /**
     * @return the modId
     */
    @Transient
    public Long getModId() {
        return modId;
    }

    /**
     * @param modId the modId to set
     */
    @Transient
    public void setModId(Long modId) {
        this.modId = modId;
    }

    /**
     * @return the regDate
     */
    @Transient
    public Date getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    @Transient
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the apt
     */
    @Transient
    public Apt getApt() {
        return apt;
    }

    /**
     * @param apt the apt to set
     */
    @Transient
    public void setApt(Apt apt) {
        this.apt = apt;
    }

    /**
     * @return the user
     */
    @Transient
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    @Transient
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the modDate
     */
    @Transient
    public Date getModDate() {
        return modDate;
    }

    /**
     * @param modDate the modDate to set
     */
    @Transient
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
        return "AptFeePush [id=" + id + ", gubun=" + gubun + ", bookYn=" + bookYn + ", sendDate=" + sendDate + ", sendYn=" + sendYn + ", contents=" + contents + ", regDate=" + regDate
                + ", regUserInfo=" + regUserInfo + "]";
    }

}
