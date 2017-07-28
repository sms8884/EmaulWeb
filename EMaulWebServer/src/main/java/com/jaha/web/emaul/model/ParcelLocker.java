package com.jaha.web.emaul.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.RsaKey;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 무인택배함
 */
@Entity
@Table(name = "parcel_locker")
public class ParcelLocker {

    /** 일련번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Apt.class, cascade = {})
    @JoinColumn(name = "aptId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Apt apt;

    /** 택배함UUID(무인택배함 단말기에서 서버로 통신할 때 사용) */
    @Column(length = 40, nullable = false)
    private String uuid;

    /** 인증키 */
    @Column(length = 300, nullable = false)
    private String authKey;

    /** 택배함명칭 */
    @Column(length = 100, nullable = false)
    private String name;

    /** 설치위치 */
    @Column(length = 100, nullable = true)
    private String location;

    /** 택배함수량 */
    @Column(nullable = true)
    private Integer count;

    /** 상태(active/unactive) */
    @Column(length = 10, nullable = false)
    private String status;

    @Column(length = 3000, nullable = false)
    private String publicKey;

    @Column(length = 3000, nullable = false)
    private String privateKey;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;

    @Enumerated(EnumType.STRING)
    private Deleted deleted;


    public Deleted getDeleted() {
        return deleted;
    }

    public void setDeleted(Deleted deleted) {
        this.deleted = deleted;
    }

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.status = "active";
        this.regDate = new Date();

        RsaKey rsaKey = new RsaKey();
        this.publicKey = rsaKey.getPublicKey();
        this.privateKey = rsaKey.getPrivateKey();
        this.deleted = Deleted.N;
    }

    @PostLoad
    public void postPersist() {

    }

    @PreUpdate
    public void preUpdate() {

    }

    @PreRemove
    public void preRemove() {

    }

    public String updateAuthKey() {
        this.authKey = RandomKeys.make(12);
        return this.authKey;
    }

    public boolean vertifyApt(long aptId) {
        return this.apt.id == aptId;
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
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the authKey
     */
    public String getAuthKey() {
        return authKey;
    }

    /**
     * @param authKey the authKey to set
     */
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
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
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Integer count) {
        this.count = count;
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
     * @return the apt
     */
    public Apt getApt() {
        return apt;
    }

    /**
     * @param apt the apt to set
     */
    public void setApt(Apt apt) {
        this.apt = apt;
    }

    @Override public String toString() {
        return "ParcelLocker{" + "id=" + id + ", apt=" + apt + ", uuid='" + uuid + '\'' + ", authKey='" + authKey + '\'' + ", name='" + name + '\'' + ", location='" + location + '\'' + ", count="
                + count + ", status='" + status + '\'' + ", publicKey='" + publicKey + '\'' + ", privateKey='" + privateKey + '\'' + ", regDate=" + regDate + ", deleted='" + deleted + '\'' + '}';
    }


    public enum Deleted {
        Y, N
    }

}
