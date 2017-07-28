/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 24.
 */
package com.jaha.web.emaul.v2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * <pre>
 * Class Name : PushQueue.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 24.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 24.
 * @version 1.0
 */
@Component
public class PushScheduler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Gson gson = new Gson();

    @Value("${gcm.server.url}")
    private String gcmServerUrl;

    @Autowired
    private PushQueue pushQueue;

    // @Scheduled(fixedRate = 10000)
    public void runPush() {
        logger.debug("<<푸시 스케줄 실행>>");
        // try {
        // GcmSendForm gcmSendForm = this.pushQueue.pollPushFrom();
        //
        // if (gcmSendForm == null) {
        // logger.info("<<GcmSendForm이 NULL이기에 푸시를 발송하지 않습니다!>>");
        // }
        //
        // RestTemplate restTemplate = new RestTemplate();
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        //
        // String gcmSendFromJson = gson.toJson(gcmSendForm);
        //
        // HttpEntity<byte[]> request = new HttpEntity<>(gcmSendFromJson.getBytes("utf-8"), headers);
        // logger.debug("<<푸시데이타>> {}", gcmSendFromJson);
        //
        // String gcmServerUrl = this.gcmServerUrl + "/send";
        // // logger.debug(gcmServerUrl);
        // String pushResult = restTemplate.postForObject(gcmServerUrl, request, String.class);
        // logger.debug("<<푸시결과>> {}", pushResult);
        // } catch (UnsupportedEncodingException e) {
        // logger.error("<<푸시발송 중 오류 발생>>", e);
        // }
    }

}
