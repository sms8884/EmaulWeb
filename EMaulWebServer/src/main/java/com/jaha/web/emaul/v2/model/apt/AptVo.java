package com.jaha.web.emaul.v2.model.apt;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * <pre>
 * Class Name : AptVo.java
 * Description : AptVo
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 12.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 12.
 * @version 1.0
 */
@Alias(value = "AptVo")
public class AptVo implements Serializable {

    private static final long serialVersionUID = -7042884511170674133L;

    /** 아파트 일련번호 */
    private Long id;
    /** 아파트 명 */
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean registeredApt;
    private String addressCode;
    private Long aptInfoId;
    private String aptOfficePhoneNumberInner;
    private String roadAddress;

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
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the registeredApt
     */
    public Boolean getRegisteredApt() {
        return registeredApt;
    }

    /**
     * @param registeredApt the registeredApt to set
     */
    public void setRegisteredApt(Boolean registeredApt) {
        this.registeredApt = registeredApt;
    }

    /**
     * @return the addressCode
     */
    public String getAddressCode() {
        return addressCode;
    }

    /**
     * @param addressCode the addressCode to set
     */
    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    /**
     * @return the aptInfoId
     */
    public Long getAptInfoId() {
        return aptInfoId;
    }

    /**
     * @param aptInfoId the aptInfoId to set
     */
    public void setAptInfoId(Long aptInfoId) {
        this.aptInfoId = aptInfoId;
    }

    /**
     * @return the aptOfficePhoneNumberInner
     */
    public String getAptOfficePhoneNumberInner() {
        return aptOfficePhoneNumberInner;
    }

    /**
     * @param aptOfficePhoneNumberInner the aptOfficePhoneNumberInner to set
     */
    public void setAptOfficePhoneNumberInner(String aptOfficePhoneNumberInner) {
        this.aptOfficePhoneNumberInner = aptOfficePhoneNumberInner;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

}
