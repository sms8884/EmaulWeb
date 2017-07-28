package com.jaha.web.emaul.service;

/**
 * Created by doring on 15. 4. 30..
 */
public interface PhoneAuthService {

    boolean sendMsgNow(String destNumber, String sendNumber, String msg, String code, String key);

    boolean sendMessageWithValidation(String destNumber, String sendNumber, String msg, String code, String key);

    boolean sendMmsMsgNow(String destNumber, String sendNumber, String title, String msg, String code, String key);

    boolean checkAuth(String code, String key);

    boolean checkAuth(String code, String key, String phone);

    String getPhoneNumber(String code, String key);
}
