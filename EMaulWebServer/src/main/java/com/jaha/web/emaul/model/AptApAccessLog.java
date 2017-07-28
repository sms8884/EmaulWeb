package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by shavrani on 16-11-15
 */
public class AptApAccessLog extends BaseSecuModel {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Long id;

    public String apBeaconUuid;

    public String apId;

    public String aptApId;

    public String apName;

    public String expIp;

    public Long userId;

    public Long accessDeviceId;

    public Date accessDate;

    public String mobileDeviceModel;

    public String mobileDeviceOs;

    public Long delayTime;

    public String success;

    public String memo;

    public String waitingYn;

    public String inOut;

    public String fullName;

    public String nickname;

    public String aptName;

    public String dong;

    public String ho;

    public String secondUser;

    // 복호화한 이름
    private String fullNameDecrypted;


    public String getAccessDateText() {
        if (accessDate != null) {
            return sdf2.format(accessDate);
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
