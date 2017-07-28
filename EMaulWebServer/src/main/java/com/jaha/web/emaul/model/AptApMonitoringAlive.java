package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by shavrani on 17-01-16
 */
public class AptApMonitoringAlive extends BaseSecuModel {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Long id;

    public String type;

    public String apBeaconUuid;

    public Long apId;

    public String aptApId;

    public String apName;

    public String expIp;

    public String success;

    public Long regId;

    public Date regDate;

    public String memo;

    public String fullName;

    // 복호화한 이름
    private String fullNameDecrypted;


    public String getRegDateText() {
        if (regDate != null) {
            return sdf2.format(regDate);
        } else {
            return "";
        }
    }

    public String getFullName() {
        if (this.fullNameDecrypted == null) {
            this.fullNameDecrypted = descString(fullName);
            return this.fullNameDecrypted;
        }

        return this.fullNameDecrypted;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
