package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 15. 3. 9..
 */
@Entity
@Table(name = "user", indexes = {@Index(name = "idx_keyword_search_user", columnList = "email, typeId"), @Index(name = "idx_fullname_user", columnList = "fullName"),
        @Index(name = "idx_nickname_user", columnList = "nickname"), @Index(name = "idx_reg_date_user", columnList = "regDate")})
public class User extends BaseSecuModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 200)
    @JsonIgnore
    public String passwordHash;

    @Column(length = 80)
    private String fullName;

    @Column(length = 10)
    public String birthYear;

    @Column(length = 10)
    public String gender;

    @OneToOne(targetEntity = UserNickname.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "nickname", referencedColumnName = "name")
    private UserNickname nickname;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId", referencedColumnName = "userId")
    @JsonIgnore
    public UserType type;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "settingId", referencedColumnName = "userId")
    public Setting setting;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "houseId", referencedColumnName = "id")
    public House house;

    @Column(length = 40)
    private String phone;

    @Column(nullable = false)
    @JsonIgnore
    public Date regDate;

    @JsonIgnore
    public Date deactiveDate;

    @Column(length = 40)
    @JsonIgnore
    public String uniqueDeviceId;

    public Boolean hasProfileImage;

    // 추천인 ID
    public Long recommId;

    // 로그인한 단말기 종류
    public String kind;

    /** 사용자GCM ID */
    @Column(nullable = true)
    @JsonIgnore
    public String gcmId;

    /** 사용자앱버전 */
    @Column(nullable = true)
    @JsonIgnore
    public String appVersion;

    /** 마지막 로그인 시간(연월일시) */
    @Column(nullable = true)
    @JsonIgnore
    public Date lastLoginDate;

    /** 위치정보제공동의 여부 */
    @Column(length = 1, nullable = true)
    public String loiAgrmYn = "N";

    @Column(length = 500)
    private String addressDetail;

    /** 접속지 IP 2016.11.17 cyt */
    @Column(length = 20)
    public String remoteIp;

    /** 외부기기 로그인 허용여부 */
    @Column(columnDefinition = "VARCHAR(1) DEFAULT 'Y' ")
    public String multiLoginYn;

    @Transient
    // 복호화한 이메일
    private String emailDecrypted = null;

    @Transient
    // 복호화한 이름
    private String fullNameDecrypted = null;

    @Transient
    // 복호화한 휴대폰 번호
    private String phoneDecrypted = null;

    @Transient
    // 가상아파트일경우 복호화한 상세주소
    private String addressDetailDecrypted = null;

    // 이메일 가져오기
    public String getEmail() {
        if (this.emailDecrypted == null) {
            this.emailDecrypted = descString(email);
            return this.emailDecrypted;
        }

        return this.emailDecrypted;
    }

    // 이메일 원본 가져오기
    public String getEmailRawData() {
        return email;
    }

    // 이메일 저장하기
    public void setEmail(String email) {
        if (email == null) {
            this.email = null;
        } else {
            this.email = encString(email);
            this.emailDecrypted = email;
        }
    }

    // 이메일 원본에 저장하기
    public void setEmailRawData(String email) {
        this.email = email;
    }

    // 이름 가져오기
    public String getFullName() {
        if (this.fullNameDecrypted == null) {
            this.fullNameDecrypted = descString(fullName);
            return this.fullNameDecrypted;
        }

        return this.fullNameDecrypted;
    }

    // 이름 원본 가져오기
    public String getFullNameRawData() {
        return fullName;
    }

    // 이름 저장하기
    public void setFullName(String fullName) {
        if (fullName == null) {
            this.fullName = null;
        } else {
            this.fullName = encString(fullName);
            this.fullNameDecrypted = fullName;
        }
    }

    // 이름 원본에 저장하기
    public void setFullNameRawData(String fullName) {
        this.fullName = fullName;
    }

    // 휴대폰 번호 가져오기
    public String getPhone() {
        if (this.phoneDecrypted == null) {
            this.phoneDecrypted = descString(phone);
            return this.phoneDecrypted;
        }

        return this.phoneDecrypted;
    }

    // 휴대폰 번호 원본 가져오기
    public String getPhoneRawData() {
        return phone;
    }

    // 휴대폰 번호 저장하기
    public void setPhone(String phone) {
        if (phone == null) {
            this.phone = null;
        } else {
            this.phone = encString(phone);
            this.phoneDecrypted = phone;
        }
    }

    // 휴대폰 번호 원본에 저장하기
    public void setPhoneRawData(String phone) {
        this.phone = phone;
    }

    // 상세주소 가져오기
    public String getAddressDetail() {
        if (this.addressDetailDecrypted == null) {
            this.addressDetailDecrypted = descString(addressDetail);
            return this.addressDetailDecrypted;
        }
        return this.addressDetailDecrypted;
    }

    // 상세주소 원본 가져오기
    public String getAddressDetailRawData() {
        return phone;
    }

    // 상세주소 저장하기
    public void setAddressDetail(String addressDetail) {
        if (addressDetail == null) {
            this.addressDetail = null;
        } else {
            this.addressDetail = encString(addressDetail);
            this.addressDetailDecrypted = addressDetail;
        }
    }

    // 상세주소 원본에 저장하기
    public void setAddressDetailRawData(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    // 닉네임 가져오기
    public UserNickname getNickname() {
        return nickname;
    }

    // 닉네임 저장하기
    public void setNickname(UserNickname nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @param passwordHash the passwordHash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
