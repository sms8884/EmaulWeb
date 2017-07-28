package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <pre>
 * Class Name : AptApMonitoringNoti.java
 * Description : Description
 *  
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 12. 7.     shavrani      Generation
 * </pre>
 *
 * @author shavrani
 * @since 2016. 12. 7.
 * @version 1.0
 */
public class AptApMonitoringNoti extends BaseSecuModel {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Long id;

    private Long apId;

    private String apBeaconUuid;

    private Integer lteRssi;

    private Integer lteRsrp;

    private Integer lteRsrq;

    private String lteDial;

    private String lteAddr4;

    private Date regDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApId() {
        return apId;
    }

    public void setApId(Long apId) {
        this.apId = apId;
    }

    public String getApBeaconUuid() {
        return apBeaconUuid;
    }

    public void setApBeaconUuid(String apBeaconUuid) {
        this.apBeaconUuid = apBeaconUuid;
    }

    public Integer getLteRssi() {
        return lteRssi;
    }

    public void setLteRssi(Integer lteRssi) {
        this.lteRssi = lteRssi;
    }

    public Integer getLteRsrp() {
        return lteRsrp;
    }

    public void setLteRsrp(Integer lteRsrp) {
        this.lteRsrp = lteRsrp;
    }

    public Integer getLteRsrq() {
        return lteRsrq;
    }

    public void setLteRsrq(Integer lteRsrq) {
        this.lteRsrq = lteRsrq;
    }

    public String getLteDial() {
        return lteDial;
    }

    public void setLteDial(String lteDial) {
        this.lteDial = lteDial;
    }

    public String getLteAddr4() {
        return lteAddr4;
    }

    public void setLteAddr4(String lteAddr4) {
        this.lteAddr4 = lteAddr4;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getRegDateText() {
        if (regDate != null) {
            return sdf.format(regDate);
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
