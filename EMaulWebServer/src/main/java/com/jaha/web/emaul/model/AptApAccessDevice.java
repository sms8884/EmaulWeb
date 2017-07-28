package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by shavrani on 16-08-09
 */
@Entity
@Table(name = "apt_ap_access_device")
public class AptApAccessDevice {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String type;

    @Transient
    public String typeNm;

    public String accessKey;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;

    public String aptApIds;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "regId", referencedColumnName = "id")
    public User regUser;

    public Date regDate;

    public Long modId;

    public Date modDate;

    public Date deactiveDate;

    public String secondUser;

    public String memo;

    public String getUserName() {
        String userName = "";
        if (user != null) {
            userName = user.getFullName();
        }
        return userName;
    }

    public String getRegDate() {
        return regDate == null ? "" : sdf.format(regDate);
    }

    public String getRegName() {
        return regUser == null ? "" : regUser.getFullName();
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }

    @PreRemove
    public void preRemove() {}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
