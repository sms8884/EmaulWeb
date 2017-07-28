package com.jaha.web.emaul.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 2014. 4. 9..
 */
public class GcmSendForm {

    private Map<String, String> message;

    private Boolean sendToAll = false;
    // or
    private List<Long> userIds;
    // or
    private List<String> gcmIds;

    public Map<String, String> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }

    public Boolean isSendToAll() {
        return sendToAll;
    }

    public void setSendToAll(Boolean sendToAll) {
        this.sendToAll = sendToAll;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<String> getGcmIds() {
        return gcmIds;
    }

    public void setGcmIds(List<String> gcmIds) {
        this.gcmIds = gcmIds;
    }



    /** 푸시 본문내용이 광고냐 텍스트냐, 20161201, 전강욱 */
    @JsonIgnore
    private String adOrText;

    public String getAdOrText() {
        return adOrText;
    }

    public void setAdOrText(String adOrText) {
        this.adOrText = adOrText;
    }

}
