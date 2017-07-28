package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 15. 3. 30..
 */
@Entity
@Table(name = "apt", indexes = {@Index(name = "idx_apt_name", columnList = "name"), @Index(name = "idx_apt_registered", columnList = "registeredApt")})
public class Apt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 80)
    public String name;

    @ManyToOne(targetEntity = Address.class, cascade = {})
    @JoinColumn(name = "addressCode", referencedColumnName = "건물관리번호", nullable = false)
    @JsonIgnore
    public Address address;

    public Double latitude;
    public Double longitude;

    @Column(length = 50)
    public String aptOfficePhoneNumberInner;



    @Transient
    public String strAddress;
    @Transient
    public String strAddressOld;

    @Column(columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean registeredApt;

    @Column(columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean virtual;

    @Column
    @JsonIgnore
    public Date regDate;

    @ManyToOne(targetEntity = AptInfo.class, cascade = {})
    @JoinColumn(name = "aptInfoId", referencedColumnName = "id")
    @JsonIgnore
    public AptInfo aptInfo;
}
