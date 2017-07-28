package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "CommonCode")
@Table(name = "common_code")
// @IdClass(CommonCodeID.class)
public class CommonCode {
    @Id
    @Column(name = "code", insertable = false, updatable = false)
    public String code;

    @Column(name = "code_group", insertable = false, updatable = false)
    public String codeGroup;

    @Column(length = 300)
    public String name;

    @Column(length = 300)
    public int depth;

    @Column(length = 300)
    public int sort_order;

    @Column(length = 300)
    public String use_yn;

    @Column(length = 100, name = "data_1")
    public String data1;

    @Column(length = 100, name = "data_2")
    public String data2;

    @Column(length = 100, name = "data_3")
    public String data3;

    @Column(length = 100, name = "data_4")
    public String data4;

    @Column(length = 100, name = "data_5")
    public String data5;

    @Column(length = 20, name = "reg_id")
    public String regId;

    @Column
    public Date regDate;

    public String nickname;

    /** 코드 리스트용 */
    @Transient
    private String[] cdIds;



    /* getter and setter */

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the cdIds
     */
    public String[] getCdIds() {
        return cdIds;
    }

    /**
     * @param cdIds the cdIds to set
     */
    public void setCdIds(String[] cdIds) {
        this.cdIds = cdIds;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getCode() {
        return code;
    }

    /**
     * @return the regId
     */
    public String getRegId() {
        return regId;
    }

    /**
     * @param regId the regId to set
     */
    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeGroup() {
        return codeGroup;
    }

    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    public String getUse_yn() {
        return use_yn;
    }

    public void setUse_yn(String use_yn) {
        this.use_yn = use_yn;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

}
