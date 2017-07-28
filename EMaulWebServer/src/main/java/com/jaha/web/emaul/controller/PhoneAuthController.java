package com.jaha.web.emaul.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jaha.web.emaul.exception.ErrorJson;
import com.jaha.web.emaul.exception.PhoneNumberDuplicatedException;
import com.jaha.web.emaul.service.PhoneAuthService;
import com.jaha.web.emaul.util.RandomKeys;

/**
 * Created by doring on 15. 4. 29..
 */
@Controller
public class PhoneAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneAuthController.class);

    @Autowired
    private PhoneAuthService phoneAuthService;

    @RequestMapping(value = "/api/public/phone-auth/req", method = RequestMethod.POST)
    public @ResponseBody String phoneAuthReq(@RequestBody String json) {

        JSONObject obj = new JSONObject(json);

        String phoneNumber = obj.getString("phoneNumber");

        String code = String.format("%06d", (int) (Math.random() * 1000000));
        String key = RandomKeys.make(32);
        // 발신자번호는 비즈뿌리오에 사전 등록된 번호만 문자 전송이 가능함
        boolean tf = phoneAuthService.sendMessageWithValidation(phoneNumber, "028670816", String.format("e마을 인증번호 [%s]를 입력해주세요.", code), code, key);
        if (tf) {
            LOGGER.info("* {}, e마을 인증번호[{}] 발송 성공", phoneNumber, code);
        } else {
            LOGGER.info("* {}, e마을 인증번호[{}] 발송 실패", phoneNumber, code);
        }

        JSONObject ret = new JSONObject();
        ret.put("key", key);

        return ret.toString();
    }

    @RequestMapping(value = "/api/public/phone-auth/check", method = RequestMethod.POST)
    public @ResponseBody String phoneAuthCode(@RequestBody String json) {

        JSONObject obj = new JSONObject(json);

        String code = obj.getString("code");
        String key = obj.getString("key");

        return phoneAuthService.checkAuth(code, key) ? "OK" : "";
    }

    @ExceptionHandler(PhoneNumberDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorJson handlePhoneNumberDuplicateException(PhoneNumberDuplicatedException e) {
        return new ErrorJson(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
