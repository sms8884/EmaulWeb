/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 14.
 */
package com.jaha.web.emaul.v2.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.constants.Constants;
// /////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.GcmSendForm;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.util.StringUtil;
// /////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.constants.CommonConstants;
import com.jaha.web.emaul.v2.mapper.common.PushMapper;
import com.jaha.web.emaul.v2.model.common.PushLogVo;

/**
 * <pre>
 * Class Name : PushUtils.java
 * Description : 푸시 발송
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 14.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 14.
 * @version 1.0
 */
@Component
public class PushUtils implements CommonConstants {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final String AD = "ad";
    static final String TEXT = "text";

    /** 광고푸시가 가능한 앱 버전 */
    static final int CAN_AD_PUSH_APP_VERSION = 220;

    @Value("${gcm.server.send.url}")
    private String gcmServerSendUrl;

    @Value("${adapi.data.service.url}")
    private String adServerUrl;

    @Value("${adapi.data.service.param}")
    private String adServerParam;

    @Autowired
    private PushMapper pushMapper;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper mapper;

    private HttpHeaders headers;

    static final String PUSH_DETAIL_APP_URI = "emaul://push-detail?id=%s";

    @PostConstruct
    private void init() {
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);

        this.mapper = new ObjectMapper();
    }

    /**
     * 전체 푸시를 발송한다.<br />
     * 광고문구, 푸시목록제목이 없으면 value 값이 기본으로 선택된다.
     *
     * @param title
     * @param value
     * @param action
     * @param targetUserList
     */
    @Async
    public void sendPushToAll(PushGubun pushGubun, String title, String value, String action) {
        this.sendPush(PushGubun.ACTION, title, value, action, null, null, 0, null, false, this.findPushTargetUserList());
    }

    /**
     * PushGubun별로 푸시를 발송한다.<br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다. <br />
     * 광고문구, 푸시목록제목이 없으면 value 값이 기본으로 선택된다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, List<SimpleUser> targetUserList) {
        this.sendPush(pushGubun, title, value, action, null, null, 0, null, false, targetUserList);
    }

    /**
     * 푸시(titleResId 여부 선택)를 발송한다.<br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다. <br />
     * 광고문구, 푸시목록제목이 없으면 value 값이 기본으로 선택된다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param titleResIdYn
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, boolean titleResIdYn, List<SimpleUser> targetUserList) {
        this.sendPush(pushGubun, title, value, action, null, null, 0, null, titleResIdYn, targetUserList);
    }

    /**
     * 푸시(특정 서비스 아이디-etcId-의 발송로그 포함)를 발송한다. <br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다. <br />
     * 광고문구, 푸시목록제목이 없으면 value 값이 기본으로 선택된다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param etcId
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, String etcId, List<SimpleUser> targetUserList) {
        this.sendPush(pushGubun, title, value, action, null, null, 0, etcId, false, targetUserList);
    }

    /**
     * 푸시(titleResId 여부 선택, 특정 서비스 아이디-etcId-의 발송로그 포함)를 발송한다.<br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다. <br />
     * 광고문구, 푸시목록제목이 없으면 value 값이 기본으로 선택된다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param etcId
     * @param titleResIdYn
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, String etcId, boolean titleResIdYn, List<SimpleUser> targetUserList) {
        this.sendPush(pushGubun, title, value, action, null, null, 0, etcId, titleResIdYn, targetUserList);
    }

    /**
     * 푸시(광고문구 및 푸시목록제목 직접 입력)를 발송한다. <br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param adText
     * @param pushListTitle
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, String adText, String pushListTitle, List<SimpleUser> targetUserList) {
        this.sendPush(pushGubun, title, value, action, adText, pushListTitle, 0, null, false, targetUserList);
    }

    /**
     * 푸시를 발송한다.<br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다.
     *
     * @param pushGubun
     * @param title
     * @param value
     * @param action
     * @param adText
     * @param pushListTitle
     * @param apiNumber
     * @param etcId
     * @param titleResIdYn
     * @param targetUserList
     */
    @Async
    public void sendPush(PushGubun pushGubun, String title, String value, String action, String adText, String pushListTitle, int apiNumber, String etcId, boolean titleResIdYn,
            List<SimpleUser> targetUserList) {
        if (value == null) {
            logger.info("<<value값이 없어 푸시 발송을 중단합니다.>>");
            return;
        }
        if (targetUserList == null || targetUserList.isEmpty()) {
            logger.info("<<발송대상자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        long startTime = System.currentTimeMillis();

        int targetUserListSize = targetUserList.size();

        for (int i = 0; i < targetUserListSize; i++) {
            try {
                SimpleUser targetUser = targetUserList.get(i);

                GcmSendForm gcmSendForm =
                        this.getPushForm(pushGubun, targetUser.getId(), targetUser.getKind(), StringUtil.nvl(title, ""), value, action, adText, StringUtil.nvl(pushListTitle, ""), titleResIdYn,
                                apiNumber, targetUser.getAppVersion());

                if (gcmSendForm == null) {
                    logger.info("<<GcmSendForm이 null이기 때문에 푸시 발송을 하지 않습니다!>>");
                    continue;
                }

                Map<String, String> msgMap = gcmSendForm.getMessage();
                String adOrText = (StringUtils.isEmpty(gcmSendForm.getAdOrText())) ? "" : "-" + gcmSendForm.getAdOrText();

                PushLogVo pushLog = new PushLogVo();
                pushLog.setAptId(targetUser.getAptId());
                pushLog.setUserId(targetUser.getId());
                pushLog.setTitle(StringUtil.nvl(msgMap.get("title"), ""));
                pushLog.setMessage(msgMap.get("value"));
                pushLog.setGubun(pushGubun.getValue() + adOrText);
                pushLog.setDeviceRecYn("N");
                pushLog.setPushSendCount(1);
                pushLog.setPushClickCount(0);
                pushLog.setSmsYn("N");
                pushLog.setEtc(etcId);
                pushLog.setAction(action);

                this.savePushLog(pushLog);

                if (msgMap.get("push_check_ids") != null) {
                    msgMap.put("push_check_ids", String.valueOf(pushLog.getId()));
                }

                if (PUSH_DETAIL_APP_URI.equals(msgMap.get("action"))) {
                    msgMap.put("action", String.format(PUSH_DETAIL_APP_URI, pushLog.getId()));
                }

                gcmSendForm.setMessage(msgMap);

                String gcmSendFormJson = this.mapper.writeValueAsString(gcmSendForm);

                HttpEntity<byte[]> request = new HttpEntity<>(gcmSendFormJson.getBytes("utf-8"), this.headers);
                logger.debug("<<푸시데이타>> {}", gcmSendFormJson);

                String pushResult = this.restTemplate.postForObject(this.gcmServerSendUrl, request, String.class);
                logger.debug("<<푸시결과>> {}", pushResult);
            } catch (Exception e) {
                logger.error("<<푸시발송 중 오류 발생>>", e);
            }
        }

        long endTime = System.currentTimeMillis();

        logger.info("<<푸시발송시간: {}초>>", (endTime - startTime) / 1000.0);
    }

    /**
     * 안전 귀가 푸시를 발송한다.<br />
     * 먼저 targetUser<SimpleUser> 조회 후 파라미터로 입력하여 발송한다.
     *
     * @param paramMap
     * @param targetUser
     */
    @Async
    public void sendPushForSafeCombackHome(Map<String, String> paramMap, SimpleUser targetUser) {
        if (paramMap == null) {
            logger.info("<<paramMap값이 없어 푸시 발송을 중단합니다.>>");
            return;
        }
        if (targetUser == null) {
            logger.info("<<발송대상자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            String title = StringUtil.nvl(paramMap.get("title"));
            String value = paramMap.get("value");
            String action = paramMap.get("action");

            GcmSendForm gcmSendForm = this.getPushForm(PushGubun.SAFE_COMEBACK_HOME, targetUser.getId(), targetUser.getKind(), title, value, action, null, null, false, 0, targetUser.getAppVersion());

            if (gcmSendForm == null) {
                logger.info("<<GcmSendForm이 null이기 때문에 푸시 발송을 하지 않습니다!>>");
                return;
            }

            Map<String, String> msgMap = gcmSendForm.getMessage();
            String adOrText = (StringUtils.isEmpty(gcmSendForm.getAdOrText())) ? "" : "-" + gcmSendForm.getAdOrText();

            String type = msgMap.get("type");
            if (PushGubun.ACTION.getValue().equalsIgnoreCase(type)) { // 일반 푸시일 경우
                msgMap.put("type", "notification");
            }

            PushLogVo pushLog = new PushLogVo();
            pushLog.setAptId(targetUser.getAptId());
            pushLog.setUserId(targetUser.getId());
            pushLog.setTitle(StringUtil.nvl(msgMap.get("title")));
            pushLog.setMessage(msgMap.get("value"));
            pushLog.setGubun(PushGubun.SAFE_COMEBACK_HOME.getValue() + adOrText);
            pushLog.setDeviceRecYn("N");
            pushLog.setPushSendCount(1);
            pushLog.setPushClickCount(0);
            pushLog.setSmsYn("N");
            pushLog.setAction(action);

            this.savePushLog(pushLog);

            if (msgMap.get("push_check_ids") != null) {
                msgMap.put("push_check_ids", String.valueOf(pushLog.getId()));
            }

            if (PUSH_DETAIL_APP_URI.equals(msgMap.get("action"))) {
                msgMap.put("action", String.format(PUSH_DETAIL_APP_URI, pushLog.getId()));
            }

            gcmSendForm.setMessage(msgMap);

            String gcmSendFormJson = this.mapper.writeValueAsString(gcmSendForm);

            HttpEntity<byte[]> request = new HttpEntity<>(gcmSendFormJson.getBytes("utf-8"), this.headers);
            logger.debug("<<안전 귀가 푸시데이타>> {}", gcmSendFormJson);

            String pushResult = this.restTemplate.postForObject(this.gcmServerSendUrl, request, String.class);
            logger.debug("<<안전 귀가 푸시결과>> {}", pushResult);
        } catch (Exception e) {
            logger.error("<<안전 귀가 푸시발송 중 오류 발생>>", e);
            return;
        }

        long endTime = System.currentTimeMillis();

        logger.info("<<푸시발송시간: {}초>>", (endTime - startTime) / 1000.0);
    }

    /**
     * 방문 주차 푸시를 발송한다.<br />
     * 먼저 List<SimpleUser> 조회 후 파라미터로 입력하여 발송한다.
     *
     * @param paramMap
     * @param targetUserList
     */
    @Async
    public void sendPushForPmsVisit(Map<String, String> paramMap, List<SimpleUser> targetUserList) {
        if (paramMap == null) {
            logger.info("<<paramMap값이 없어 푸시 발송을 중단합니다.>>");
            return;
        }
        if (targetUserList == null || targetUserList.isEmpty()) {
            logger.info("<<발송대상자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        long startTime = System.currentTimeMillis();

        int targetUserListSize = targetUserList.size();

        for (int i = 0; i < targetUserListSize; i++) {
            try {
                SimpleUser targetUser = targetUserList.get(i);

                String title = StringUtil.nvl(paramMap.get("title"));
                String value = paramMap.get("value");
                String action = paramMap.get("action");

                GcmSendForm gcmSendForm = this.getPushForm(PushGubun.PMS, targetUser.getId(), targetUser.getKind(), title, value, action, null, null, false, 0, targetUser.getAppVersion());

                if (gcmSendForm == null) {
                    logger.info("<<GcmSendForm이 null이기 때문에 푸시 발송을 하지 않습니다!>>");
                    return;
                }

                Map<String, String> msgMap = gcmSendForm.getMessage();
                String adOrText = (StringUtils.isEmpty(gcmSendForm.getAdOrText())) ? "" : "-" + gcmSendForm.getAdOrText();

                PushLogVo pushLog = new PushLogVo();
                pushLog.setAptId(targetUser.getAptId());
                pushLog.setUserId(targetUser.getId());
                pushLog.setTitle(StringUtil.nvl(msgMap.get("title")));
                pushLog.setMessage(msgMap.get("value"));
                pushLog.setGubun(PushGubun.PMS.getValue() + adOrText);
                pushLog.setDeviceRecYn("N");
                pushLog.setPushSendCount(1);
                pushLog.setPushClickCount(0);
                pushLog.setSmsYn("N");
                pushLog.setAction(action);

                this.savePushLog(pushLog);

                if (msgMap.get("push_check_ids") != null) {
                    msgMap.put("push_check_ids", String.valueOf(pushLog.getId()));
                }

                if (PUSH_DETAIL_APP_URI.equals(msgMap.get("action"))) {
                    msgMap.put("action", String.format(PUSH_DETAIL_APP_URI, pushLog.getId()));
                }

                gcmSendForm.setMessage(msgMap);

                String gcmSendFormJson = this.mapper.writeValueAsString(gcmSendForm);

                HttpEntity<byte[]> request = new HttpEntity<>(gcmSendFormJson.getBytes("utf-8"), this.headers);
                logger.debug("<<방문 주차 푸시데이타>> {}", gcmSendFormJson);

                String pushResult = this.restTemplate.postForObject(this.gcmServerSendUrl, request, String.class);
                logger.debug("<<방문 주차 푸시결과>> {}", pushResult);
            } catch (Exception e) {
                logger.error("<<방문 주차 푸시발송 중 오류 발생>>", e);
            }
        }

        long endTime = System.currentTimeMillis();

        logger.info("<<푸시발송시간: {}초>>", (endTime - startTime) / 1000.0);
    }

    /**
     * 푸시 로그 저장
     *
     * @param pushLog
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int savePushLog(PushLogVo pushLog) {
        return this.pushMapper.insertPushLog(pushLog);
    }

    /**
     * 전체 푸시 발송 대상자(PushAlarmSetting.ALARM은 true)를 조회한다.
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList() {
        return this.findTargetUserList(PushAlarmSetting.ALARM, 1, null, null, null, null, null, null, null, null, false, null);
    }

    /**
     * 디바이스 타입(안드로이드 / IOS)별로 푸시 발송 대상자(PushAlarmSetting.ALARM은 true)를 조회한다.
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(UserDeviceType userDeviceType) {
        return this.findTargetUserList(PushAlarmSetting.ALARM, 1, null, null, null, null, null, null, null, null, false, userDeviceType);
    }

    /**
     * 사용자아이디 목록으로 푸시 발송 대상자(PushAlarmSetting.ALARM은 true)를 조회한다.
     *
     * @param userIdList
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(List<Long> userIdList) {
        return this.findTargetUserList(PushAlarmSetting.ALARM, 1, null, null, null, null, null, userIdList, null, null, false, null);
    }

    /**
     * 사용자 푸시 발송 대상자(PushAlarmSetting, 사용자 전화번호)를 조회한다.
     *
     * @param pushAlarmSetting
     * @param userPhoneNumber
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(PushAlarmSetting pushAlarmSetting, String userPhoneNumber) {
        return this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, userPhoneNumber, null, null, null, false, null);
    }

    /**
     * PushAlarmSetting과 사용자아이디 목록으로 푸시 발송 대상자를 조회한다.
     *
     * @param pushAlarmSetting
     * @param userIdList
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(PushAlarmSetting pushAlarmSetting, List<Long> userIdList) {
        return this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, null, userIdList, null, null, false, null);
    }

    /**
     * 지역별(시도, 시군구)/성별/연령별(PushAlarmSetting.ALARM은 true) 푸시 발송 대상자를 조회한다.
     *
     * @param sido
     * @param sigungu
     * @param gender
     * @param age
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(String sido, String sigungu, Gender gender, String age) {
        return this.findTargetUserList(PushAlarmSetting.ALARM, 0, sido, sigungu, gender, age, null, null, null, null, false, null);
    }

    /**
     * 지역별(시도, 시군구, 동)로 푸시 발송 대상자를 조회한다.
     *
     * @param sido
     * @param sigungu
     * @param dong
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(String sido, String sigungu, String dong) {
        return this.findTargetUserListPerArea(PushAlarmSetting.ALARM, 0, sido, sigungu, dong);
    }

    /**
     * PushAlarmSetting과 지역(시도, 시군구)/성별/연령별 푸시 발송 대상자를 조회한다.
     *
     * @param pushAlarmSetting
     * @param sido
     * @param sigungu
     * @param gender
     * @param age
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(PushAlarmSetting pushAlarmSetting, String sido, String sigungu, Gender gender, String age) {
        return this.findTargetUserList(pushAlarmSetting, 0, sido, sigungu, gender, age, null, null, null, null, false, null);
    }

    /**
     * PushAlarmSetting과 사용자목록/세대목록/아파트목록 푸시 발송 대상자를 조회한다.
     *
     * @param pushTargetType
     * @param pushAlarmSetting
     * @param idList
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetUserList(PushTargetType pushTargetType, PushAlarmSetting pushAlarmSetting, List<Long> idList) {
        List<SimpleUser> targetUserList = null;

        if (idList == null || idList.isEmpty()) {
            return targetUserList;
        }

        switch (pushTargetType) {
            case USER:
                targetUserList = this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, null, idList, null, null, false, null);
                break;
            case HOUSE:
                targetUserList = this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, null, null, idList, null, false, null);
                break;
            case APT:
                targetUserList = this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, null, null, null, idList, false, null);
                break;
            default:
                return null;
        }

        return targetUserList;
    }

    /**
     * PushAlarmSetting과 아파트 아이디 목록으로 관리자 푸시 발송 대상자를 조회한다.
     *
     * @param pushAlarmSetting
     * @param aptIdList
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findPushTargetAdminList(PushAlarmSetting pushAlarmSetting, List<Long> aptIdList) {
        return this.findTargetUserList(pushAlarmSetting, 1, null, null, null, null, null, null, null, aptIdList, true, null);
    }

    /**
     * 푸시 발송 유형 반환
     *
     * @param jsonArrayLength
     * @param deviceType
     * @param userAppVersion
     * @return
     */
    private PushSendType getPushSendType(int jsonArrayLength, String deviceType, String userAppVersion) throws Exception {
        logger.debug("<<jsonArrayLength>> {}", jsonArrayLength);

        if (StringUtils.isBlank(deviceType) || UserDeviceType.ANDROID.getValue().equals(deviceType)) { // user.kind=android 또는 null
            if (jsonArrayLength == 0) {
                logger.info("<<(안드로이드) 광고가 없단다 -> 광고푸시아닌 일반푸시>>");
                return PushSendType.NOAD;
            } else {
                userAppVersion = StringUtils.defaultString(userAppVersion, "0.0.0");
                userAppVersion = StringUtils.left(StringUtils.remove(userAppVersion, '.'), 3);

                int uav = (userAppVersion.length() == 3) ? Integer.valueOf(userAppVersion) : 0;

                if (uav < CAN_AD_PUSH_APP_VERSION) { // 사용자 앱버전과 광고 최신 앱버전 비교, 버전 3자리
                    logger.info("<<(안드로이드) 버전({})이 낮단다 -> 광고푸시아닌 일반푸시>>", uav);
                    return PushSendType.NOAD;
                }

                logger.info("<<(안드로이드) 광고푸시>>");
                return PushSendType.ANDROID_AD;
            }
        } else if (UserDeviceType.IOS.getValue().equals(deviceType)) { // user.kind=ios
            if (jsonArrayLength == 0) {
                logger.info("<<(아이폰) 광고가 없단다 -> 광고푸시아닌 일반푸시>>");
                return PushSendType.IOS_NOAD;
            } else {
                logger.info("<<(아이폰) 광고푸시>>");
                return PushSendType.IOS_AD;
            }
        } else {
            logger.info("<<(아이폰/안드로이드 모두 아님(?)) 일반푸시>>");
            return PushSendType.NOAD;
        }
    }

    /**
     * 푸시 폼 에 데이터담기
     *
     * @param pushGubun
     * @param userId
     * @param deviceType
     * @param pushFormTitle
     * @param pushFormValue
     * @param pushFormAction
     * @param adText
     * @param pushListTitle
     * @param titleResIdYn
     * @param apiNumber
     * @param userAppVersion
     * @return
     */
    private GcmSendForm getPushForm(PushGubun pushGubun, Long userId, String deviceType, String pushFormTitle, String pushFormValue, String pushFormAction, String adText, String pushListTitle,
            boolean titleResIdYn, int apiNumber, String userAppVersion) {
        JSONArray jsonArray = null;

        try {
            String adUrl = this.adServerUrl + "?" + String.format(this.adServerParam, userId); // 광고 플랫폼 API

            String jsonTmp = this.restTemplate.getForObject(adUrl, String.class);
            logger.debug("<<광고 내용>> {}", jsonTmp);

            jsonArray = new JSONArray(jsonTmp);
        } catch (Exception e) {
            jsonArray = new JSONArray(ArrayUtils.EMPTY_STRING_ARRAY);
            logger.info("<<푸시광고 API 처리 중 오류>>", e.getMessage());
        }

        GcmSendForm pushForm = null;
        Map<String, String> msg = Maps.newConcurrentMap();
        boolean result = false;

        try {
            pushForm = new GcmSendForm();
            pushFormValue = (StringUtils.isNotEmpty(pushFormValue)) ? StringUtils.abbreviate(pushFormValue, 100) : StringUtils.EMPTY;

            // List<Long> pushLogIdList = new ArrayList<Long>(); // 게시글 번호 등
            // pushLogIdList.add(pushLogId);

            PushSendType pushType = this.getPushSendType(jsonArray.length(), deviceType, userAppVersion);
            logger.debug("<<pushType>> {}", pushType.getValue());

            switch (pushType) {
                case ANDROID_AD:
                    pushListTitle = (StringUtils.isEmpty(pushListTitle)) ? pushFormTitle : pushListTitle;

                    JSONObject jsonObj = jsonArray.getJSONObject(0);
                    adText = (jsonObj.get("description") == null) ? StringUtils.EMPTY : jsonObj.get("description").toString();

                    pushFormValue = StringEscapeUtils.escapeJson(pushFormValue);

                    if (StringUtils.isNotBlank(adText) && !"null".equals(adText)) {
                        adText = Constants.ADVERT_PUSH_PREFIX + " " + adText;
                    } else {
                        adText = (StringUtils.isEmpty(pushFormValue)) ? StringUtils.defaultString(pushFormTitle) : pushFormValue;
                    }

                    StringBuilder json = new StringBuilder("{" + '"' + "content" + '"' + ":[");
                    json.append(jsonArray.get(0).toString());
                    json.append("]," + '"' + "push_message" + '"' + ":" + '"' + pushFormValue + '"');
                    json.append("," + '"' + "push_title" + '"' + ":" + '"' + StringUtil.nvl(pushListTitle) + '"');
                    json.append("," + '"' + "ad_text" + '"' + ":" + '"' + adText + '"');
                    json.append("," + '"' + "prefix_text" + '"' + ":" + '"' + Constants.ADVERT_PUSH_PREFIX + '"');// 2017-01-26 광고 푸쉬의 (후원) prefix글자.
                    json.append("," + '"' + "api_number" + '"' + ":" + '"' + apiNumber + '"' + "}");
                    pushFormValue = json.toString();

                    // 광고 푸시
                    msg.put("push_type", pushGubun.getValue() + "-" + AD);
                    msg.put("type", pushGubun.getValue());
                    msg.put("push_check_ids", StringUtils.EMPTY); // 1,2,3,4 의 형태

                    pushForm.setAdOrText(AD);

                    break;
                case IOS_AD:
                    // IOS
                    // JSONObject jsonObj = jsonArray.getJSONObject(0);
                    // String adDesc = (jsonObj.get("description") == null) ? StringUtils.EMPTY : jsonObj.get("description").toString();
                    // String adUrl = jsonObj.getString("landing_url");

                    // if (StringUtils.isNotBlank(adDesc) && !"null".equals(adDesc)) {
                    // pushFormValue = StringUtils.defaultString(pushFormTitle) + StringUtils.LF + "(광고)" + adDesc;
                    // } else {
                    // pushFormValue = (StringUtils.isEmpty(pushFormValue)) ? StringUtils.defaultString(pushFormTitle) : pushFormValue;
                    // }

                    // 아이폰 광고 푸시
                    msg.put("type", pushGubun.getValue() + "-" + AD);
                    // if (StringUtils.isNotBlank(adUrl)) {
                    // pushFormAction = adUrl;
                    // }

                    pushFormValue = (StringUtils.isEmpty(pushFormValue)) ? StringUtils.defaultString(pushFormTitle) : pushFormValue;
                    pushFormAction = PUSH_DETAIL_APP_URI;
                    pushForm.setAdOrText(AD);

                    break;
                case IOS_NOAD:
                    // 아이폰 일반 푸시
                    msg.put("type", pushGubun.getValue() + "-" + TEXT);
                    // msg.put("action", "emaul://push-list");

                    pushForm.setAdOrText(TEXT);

                    break;
                default:
                    // 일반 푸시
                    // msg.put("type", pushGubun.getValue() + "-" + TEXT);
                    msg.put("type", PushGubun.ACTION.getValue());
                    msg.put("push_check_ids", StringUtils.EMPTY); // 1,2,3,4 의 형태
            }

            if (titleResIdYn) {
                msg.put("titleResId", pushFormTitle);
                msg.put("title", StringUtils.EMPTY);
            } else {
                msg.put("title", StringUtils.defaultString(pushFormTitle, StringUtils.EMPTY));
            }

            msg.put("value", StringUtils.defaultString(pushFormValue, StringUtils.EMPTY));

            if (StringUtils.isNotBlank(pushFormAction)) {
                msg.put("action", pushFormAction);
            }

            pushForm.setUserIds(Lists.newArrayList(userId));

            result = true;
        } catch (Exception e) {
            logger.error("<<푸시 데이타 세팅 처리 중 오류>>", e);
        }

        if (result) {
            pushForm.setMessage(msg);
            return pushForm;
        }

        return null;
    }

    /**
     * 푸시 발송 대상자를 조회한다.
     *
     * @param pushAlarmSetting
     * @param rangeAll
     * @param sido
     * @param sigungu
     * @param gender
     * @param age
     * @param userPhoneNumber
     * @param userIdList
     * @param houseIdList
     * @param aptIdList
     * @param adminYn userIdList = null, houseIdList = null, aptIdList != null일 경우에만 유효
     * @param userDeviceType
     * @return
     */
    private List<SimpleUser> findTargetUserList(PushAlarmSetting pushAlarmSetting, int rangeAll, String sido, String sigungu, Gender gender, String age, String userPhoneNumber, List<Long> userIdList,
            List<Long> houseIdList, List<Long> aptIdList, boolean adminYn, UserDeviceType userDeviceType) {
        List<SimpleUser> targetUserList = null;

        try {
            if (rangeAll == 0) { // 지역별 대상자 검색 후 발송
                if (StringUtils.isBlank(sido) && StringUtils.isBlank(sigungu) || StringUtils.isBlank(sido) && StringUtils.isNotBlank(sigungu)) {
                    logger.info("<<전체 발송이 아니나 지역구분이 없어 마을뉴스 푸시 발송을 중단합니다!>>");
                    return null;
                }
            }

            Map<String, Object> searchMap = new HashMap<String, Object>();

            searchMap.put("searchAlarm", pushAlarmSetting.getValue());

            if (gender != null) {
                logger.info("<<푸시 발송 대상 성별>> {}", gender.getValue());
                searchMap.put("searchGender", gender.getValue());
            }

            if (age != null && !"ALL".equalsIgnoreCase(age)) {
                logger.info("<<푸시 발송 대상 연령>> {}", age);
                searchMap.put("searchAges", age.split("[|]", -1)); // 00|10|20 등의 형태
            }

            if (rangeAll == 0) {
                searchMap.put("searchSido", sido);
                searchMap.put("searchSigungu", sigungu);
                logger.info("<<푸시 발송 대상 지역>> {} {}", sido, sigungu);
            } else {
                logger.info("<<푸시 발송 대상 지역>> 전체");
            }

            if (userPhoneNumber != null) {
                searchMap.put("searchUserPhoneNumber", userPhoneNumber);
            }

            if (userIdList != null && !userIdList.isEmpty()) {
                logger.info("<<푸시 발송 대상 사용자(들)>> {}", userIdList);
                searchMap.put("searchUserIdList", userIdList);
            }

            if (houseIdList != null && !houseIdList.isEmpty()) {
                logger.info("<<푸시 발송 대상 세대(들)>>{}", houseIdList);
                searchMap.put("searchHouseIdList", houseIdList);
            }

            if (aptIdList != null && !aptIdList.isEmpty()) {
                if (adminYn) {
                    logger.info("<<푸시 발송 대상 어드민 여부)>> {}", adminYn);
                    searchMap.put("searchAdminYn", "Y");
                }

                logger.info("<<푸시 발송 대상 아파트(들)>> {}", aptIdList);
                searchMap.put("searchAptIdList", aptIdList);
            }

            if (userDeviceType != null) {
                logger.info("<<푸시 발송 대상 디바이스>> {}", userDeviceType.getValue());
                searchMap.put("searchDeviceType", userDeviceType.getValue());
            }

            targetUserList = this.pushMapper.selectTargetUserListForPush(searchMap);

            if (targetUserList != null && !targetUserList.isEmpty()) {
                logger.info("<<푸시 발송 대상자수>> {}", targetUserList.size());
            }
        } catch (Exception e) {
            logger.error("<<푸시 발송 대상자 조회 중 오류>>", e);
        }

        return targetUserList;
    }

    /**
     * 지역별 푸시 발송 대상자를 조회한다.
     *
     * @param pushAlarmSetting
     * @param rangeAll
     * @param sido
     * @param sigungu
     * @param dong
     * @return
     */
    private List<SimpleUser> findTargetUserListPerArea(PushAlarmSetting pushAlarmSetting, int rangeAll, String sido, String sigungu, String dong) {
        List<SimpleUser> targetUserList = null;

        try {
            if (rangeAll == 0) { // 지역별 대상자 검색 후 발송
                if (StringUtils.isBlank(sido) && StringUtils.isBlank(sigungu) || StringUtils.isBlank(sido) && StringUtils.isNotBlank(sigungu)) {
                    logger.info("<<전체 발송이 아니나 지역구분이 없어 마을뉴스 푸시 발송을 중단합니다!>>");
                    return null;
                }
            }

            Map<String, Object> searchMap = new HashMap<String, Object>();

            searchMap.put("searchAlarm", pushAlarmSetting.getValue());

            if (rangeAll == 0) {
                searchMap.put("searchSido", sido);
                searchMap.put("searchSigungu", sigungu);
                searchMap.put("searchDong", dong);
                logger.info("<<푸시 발송 대상 지역>> 시도: {},  시군구: {}, 동: {}", sido, sigungu, dong);
            } else {
                logger.info("<<푸시 발송 대상 지역>> 전체");
            }

            targetUserList = this.pushMapper.selectTargetUserListForPush(searchMap);

            if (targetUserList != null && !targetUserList.isEmpty()) {
                logger.info("<<푸시 발송 대상자수>> {}", targetUserList.size());
            }
        } catch (Exception e) {
            logger.error("<<푸시 발송 대상자 조회 중 오류>>", e);
        }

        return targetUserList;
    }

    // public static void main(String[] args) {
    // RestTemplate restTemplate = new RestTemplate();
    //
    // String adUrl = String.format("http://192.168.0.120:8080/advert/api/advert/advertImageData?userId=%s&category=9&pushLog=Y", 5334);
    // String jsonTmp = restTemplate.getForObject(adUrl, String.class);
    // System.out.format("<<광고 내용>> %s", jsonTmp);
    //
    // JSONArray jsonArray = new JSONArray(ArrayUtils.EMPTY_STRING_ARRAY);
    // System.out.println();
    // System.out.format("<<jsonArray length>> %s", jsonArray.length());
    //
    // PushGubun pushGubun = PushGubun.BOARD_COMMONT;
    // System.out.println();
    // System.out.println(pushGubun.getValue());
    // }

}
