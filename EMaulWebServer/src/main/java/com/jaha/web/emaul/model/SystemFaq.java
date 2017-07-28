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

@Entity
@Table(name = "system_faq")
public class SystemFaq extends BaseSecuModel {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String type;

    public String title;

    public String content;

    public String status;

    public int view_cnt;

    public String viewService;

    public Long regId;

    public Date regDate;

    public Long modId;

    public Date modDate;

    @Transient
    public String statusName;

    @Transient
    public String regDateStr;

    @Transient
    public String modDateStr;

    @Transient
    public String typeName;

    @Transient
    public String regName;

    @Transient
    // 복호화한 이름
    private String regNameDecrypted;

    @Transient
    public String viewServiceText;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getView_cnt() {
        return view_cnt;
    }

    public void setView_cnt(int view_cnt) {
        this.view_cnt = view_cnt;
    }

    public Long getRegId() {
        return regId;
    }

    public void setRegId(Long regId) {
        this.regId = regId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Long getModId() {
        return modId;
    }

    public void setModId(Long modId) {
        this.modId = modId;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    /** name decryption */
    public String getRegName() {
        if (this.regNameDecrypted == null) {
            this.regNameDecrypted = descString(this.regName);
            return this.regNameDecrypted;
        }

        return this.regNameDecrypted;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public String getRegDateStr() {
        if (regDate != null) {
            return sdf.format(regDate);
        } else {
            return "";
        }
    }

    public void setRegDateStr(String regDateStr) {
        this.regDateStr = regDateStr;
    }

    public String getModDateStr() {
        if (modDate != null) {
            return sdf.format(modDate);
        } else {
            return "";
        }
    }

    public void setModDateStr(String modDateStr) {
        this.modDateStr = modDateStr;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getViewService() {
        return viewService;
    }

    public void setViewService(String viewService) {
        this.viewService = viewService;
    }

    public String getViewServiceText() {
        return viewServiceText;
    }

    public void setViewServiceText(String viewServiceText) {
        this.viewServiceText = viewServiceText;
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
