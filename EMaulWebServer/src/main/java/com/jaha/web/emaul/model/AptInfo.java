package com.jaha.web.emaul.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Created by doring on 15. 6. 7..
 */
@Entity
public class AptInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 2000)
    public String introduce;

    @Column(length = 200)
    public String aptOfficePhoneNumber;

    @Column(length = 1000)
    public String trafficInfo;

    @Column(length = 1000)
    public String trafficBusInfo;

    @Column(length = 250)
    public String urlLogo;

    @Column(length = 250)
    public String urlAptPhoto;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean canAnonymousViewFee = false;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "aptInfoId", referencedColumnName = "id")
    public List<AptContact> contacts;

    @Column(length = 100)
    public String naverClientId;

    @Column(length = 100)
    public String naverClientSecret;

    /** 입주자 대표회의 현황 / 인사말 */
    @Column(length = 2000)
    public String greetingOccupant;

    /** 관리소 현황 / 인사말 */
    @Column(length = 2000)
    public String greetingOffice;

    /** 노인회 현황 / 인사말 */
    @Column(length = 2000)
    public String greetingSenior;

    /** 아파트 표시 주소 */
    @Column(length = 1000)
    public String displayAddress;

    @Column(length = 200)
    public String aptOfficeFaxNumber;

}
