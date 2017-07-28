package com.jaha.web.emaul.model;

import java.io.Serializable;

public class UserHistory extends BaseSecuModel implements Serializable {

    /** SID */
    private static final long serialVersionUID = -8158933102650450866L;

    public Long userId;

    public String type;

    public String email;

    public String fullName;

    public String auth;

    public String birthYear;

    public String gender;

    public Long aptId;

    public String aptName;

    public String dong;

    public String ho;

    public String phone;

    public Long modId;

    public String modName;

    public String modDate;

    public String data;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getEmail() {
        return descString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return descString(fullName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getAptId() {
        return aptId;
    }

    public void setAptId(Long aptId) {
        this.aptId = aptId;
    }

    public String getAptName() {
        return aptName;
    }

    public void setAptName(String aptName) {
        this.aptName = aptName;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getPhone() {
        return descString(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getModId() {
        return modId;
    }

    public void setModId(Long modId) {
        this.modId = modId;
    }

    public String getModName() {
        return descString(modName);
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
