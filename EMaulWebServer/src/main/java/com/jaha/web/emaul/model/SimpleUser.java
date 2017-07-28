package com.jaha.web.emaul.model;

import java.io.Serializable;

public class SimpleUser extends BaseSecuModel implements Serializable {

    /** SID */
    private static final long serialVersionUID = -5854073093633892336L;

    public Long id;

    public String email;

    public String fullName;

    public String birthYear;

    public String gender;

    public String nickname;

    public Long aptId;

    public String aptName;

    public String dong;

    public String ho;

    public String phone;

    public String regDate;

    /** 사용자 폰 유형: android / ios */
    public String kind;

    /** 사용자 GCM ID */
    public String gcmId;

    /** 사용자앱버전 */
    public String appVersion;

    public String deactiveDate;

    public String addressDetail;

    public String sido;

    public String sigungu;

    public String addrDong;

    public String typeEngText;// ',' 구분자로 나열한 영문 String

    public String typeKoText;// ',' 구분자로 나열한 한글 String

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeactiveDate() {
        return deactiveDate;
    }

    public void setDeactiveDate(String deactiveDate) {
        this.deactiveDate = deactiveDate;
    }

    public String getAddressDetail() {
        return descString(addressDetail);
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getSigungu() {
        return sigungu;
    }

    public void setSigungu(String sigungu) {
        this.sigungu = sigungu;
    }

    public String getAddrDong() {
        return addrDong;
    }

    public void setAddrDong(String addrDong) {
        this.addrDong = addrDong;
    }

    public String getTypeEngText() {
        return typeEngText;
    }

    public void setTypeEngText(String typeEngText) {
        this.typeEngText = typeEngText;
    }

    public String getTypeKoText() {
        return typeKoText;
    }

    public void setTypeKoText(String typeKoText) {
        this.typeKoText = typeKoText;
    }

}
