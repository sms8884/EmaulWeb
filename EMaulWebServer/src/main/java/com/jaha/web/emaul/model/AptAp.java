package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by shavrani on 16-06-17
 */
@Entity
@Table(name = "apt_ap")
public class AptAp extends BaseSecuModel implements Cloneable {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Transient
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long aptId;

    public String apUuid;

    public String apBeaconUuid;

    public String apBeaconMajor;

    public String apBeaconMinor;

    public String apId;

    public String apPassword;

    public String sshPassword;

    public String apName;

    public Long regId;

    public Date regDate;

    public Long modId;

    public Date modDate;

    public Date deactiveDate;

    public String skipAuth;

    public String expIp;

    public String modem;

    public String firmwareVersion;

    public String natWay;

    public Integer rssi;

    public Integer keepon;

    public Integer rssiApp;

    public String operationMode;

    public String status;

    public String memo;

    public Integer gpiodelay;

    public String wifiMac;


    @Transient
    public String buildingAddress;

    @Transient
    public String aptName;

    @Transient
    public String regName;

    @Transient
    // 복호화한 이름
    private String fullNameDecrypted;


    public String getRegDate() {
        if (regDate != null) {
            return sdf.format(regDate);
        } else {
            return "";
        }
    }

    public String getRegDateText() {
        if (regDate != null) {
            return sdf2.format(regDate);
        } else {
            return "";
        }
    }

    public String getModDate() {
        if (modDate != null) {
            return sdf.format(modDate);
        } else {
            return "";
        }
    }

    public String getRegName() {
        if (this.fullNameDecrypted == null) {
            this.fullNameDecrypted = descString(regName);
            return this.fullNameDecrypted;
        }

        return this.fullNameDecrypted;
    }

    public String getDeactiveDateText() {
        if (deactiveDate != null) {
            return sdf2.format(deactiveDate);
        } else {
            return "";
        }
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
        modDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }

    @PreRemove
    public void preRemove() {

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public AptAp clone() throws CloneNotSupportedException {
        return (AptAp) super.clone();
    }

}
