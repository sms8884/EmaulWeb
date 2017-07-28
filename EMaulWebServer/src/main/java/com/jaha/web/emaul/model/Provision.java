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

@Entity
@Table(name = "provision")
public class Provision {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String title;

    public String content;

    public String status;

    @Transient
    public String statusName;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "regId", referencedColumnName = "id")
    public User regUser;

    @Transient
    public String regName;

    public Date regDate;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "modId", referencedColumnName = "id")
    public User modUser;

    @Transient
    public String modName;

    public Date modDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegDate() {
        if (regDate != null) {
            return sdf.format(regDate);
        } else {
            return "";
        }
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getModDate() {
        if (modDate != null) {
            return sdf.format(modDate);
        } else {
            return "";
        }
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public String getRegName() {
        if (regUser != null) {
            regName = regUser.getFullName();
        }
        return regName;
    }

    public String getModName() {
        if (modUser != null) {
            modName = modUser.getFullName();
        }
        return modName;
    }

    public String getStatusName() {
        return "1".equals(status) ? "게시" : "미게시";
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

}
