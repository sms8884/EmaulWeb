package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 무인택배함 회사
 */
@Entity
@Table(name = "parcel_company")
public class ParcelCompany {

    /** 일련번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** 회사명 */
    @Column(length = 40, nullable = false)
    private String name;

    /** 상태(active/unactive) */
    @Column(length = 10, nullable = false)
    private String status;

    /** 등록자ID */
    @Column(nullable = false)
    private Long regId;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;

    /** 수정자ID */
    @Column(nullable = true)
    private Long modId;

    /** 수정일시 */
    @Column(nullable = true)
    private Date modDate;

    @PrePersist
    public void prePersist() {
        this.status = "active";
        this.regDate = new Date();
    }

    @PostLoad
    public void postPersist() {

    }

    @PreUpdate
    public void preUpdate() {
        this.modDate = new Date();
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParcelCompany [id=" + id + ", name=" + name + ", status=" + status + ", regId=" + regId + ", regDate=" + regDate + ", modId=" + modId + ", modDate=" + modDate + "]";
    }

}
