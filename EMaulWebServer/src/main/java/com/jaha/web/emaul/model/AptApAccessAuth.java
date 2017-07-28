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
 * Created by shavrani on 16-06-21
 */
@Entity
@Table(name = "apt_ap_access_auth")
public class AptApAccessAuth {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long aptApId;

    public String type;

    public String dong;

    public String ho;

    public String hoType;

    public String hoLineDigit;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;

    public Long regId;

    public Date regDate;

    @Transient
    public String dongText;

    @Transient
    public String hoText;

    @Transient
    public String accessUserAptName;

    @Transient
    public String accessUserDong;

    @Transient
    public String accessUserHo;

    @Transient
    public String accessUserName;

    @Transient
    public String accessUserNickname;

    public String getRegDate() {
        if (regDate != null) {
            return sdf.format(regDate);
        } else {
            return "";
        }
    }

    public String getDongText() {
        return "All".equals(dong) ? "전체" : dong;
    }

    public String getHoText() {
        if ("All".equals(ho)) {
            return "전체";
        } else if ("1".equals(hoType)) {
            return ho + " 라인";
        } else {
            return ho;
        }
    }

    public String getAccessUserAptName() {
        if (user != null) {
            accessUserAptName = user.house.apt.name;
        }
        return accessUserAptName;
    }

    public String getAccessUserDong() {
        if (user != null) {
            accessUserDong = user.house.dong;
        }
        return accessUserDong;
    }

    public String getAccessUserHo() {
        if (user != null) {
            accessUserHo = user.house.ho;
        }
        return accessUserHo;
    }

    public String getAccessUserName() {
        if (user != null) {
            accessUserName = user.getFullName();
        }
        return accessUserName;
    }

    public String getAccessUserNickname() {
        if (user != null && user.getNickname() != null) {
            accessUserNickname = user.getNickname().name;
        }
        return accessUserNickname;
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {}

    @PreRemove
    public void preRemove() {}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
