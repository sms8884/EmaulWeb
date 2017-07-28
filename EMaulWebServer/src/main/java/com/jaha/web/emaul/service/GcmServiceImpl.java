package com.jaha.web.emaul.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.mapper.PushLogMapper;
import com.jaha.web.emaul.model.GcmSendForm;
import com.jaha.web.emaul.model.PushLog;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.prop.UrlProperties;
import com.jaha.web.emaul.util.StringUtil;

/**
 * Created by doring on 15. 4. 28..
 */
@Service
public class GcmServiceImpl implements GcmService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UrlProperties urlProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private PushLogMapper pushLogMapper;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    @Async
    public void setGcmId(Long userId, String gcmId) {
        RestTemplate restTemplate = new RestTemplate();
        logger.debug("gcm url : " + urlProperties.getGcmServer());
        if (gcmId.isEmpty()) {
            restTemplate.delete(urlProperties.getGcmServer() + "/emaul/" + userId);
        } else {
            restTemplate.put(urlProperties.getGcmServer() + "/emaul/" + userId + "/" + gcmId, null);
        }
    }

    @Override
    @Async
    public String sendGcm(GcmSendForm form) {
        long startTime = System.currentTimeMillis();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Long> checkUserIds = form.getUserIds();
        List<Long> finalUserIds = new ArrayList<Long>();

        for (Long userId : checkUserIds) {
            User user = this.userService.getUser(userId);
            // 탈퇴, 차단, 방문자 제외
            if (user.type.deactivated || user.type.blocked || user.type.anonymous) {
                continue;
            }

            finalUserIds.add(userId);
        }

        form.setUserIds(finalUserIds);

        HttpEntity<byte[]> request = null;

        try {
            String formJson = this.mapper.writeValueAsString(form);
            request = new HttpEntity<>(formJson.getBytes("utf-8"), headers);
            logger.debug("<<푸시데이타>> {}", formJson);
        } catch (Exception e) {
            logger.error("<<푸시발송 중 오류 발생>>", e);
            return "Failed";
        }

        String gcmServerUrl = urlProperties.getGcmServer() + "/send";
        // logger.debug(gcmServerUrl);
        String pushResult = restTemplate.postForObject(gcmServerUrl, request, String.class);

        long endTime = System.currentTimeMillis();

        logger.info("<<푸시발송시간: {}초>>", (endTime - startTime) / 1000.0);

        return pushResult;
    }

    @Override
    @Async
    public void sendPush(String value, String action, List<Long> targetUserIdList) {
        if (value == null) {
            logger.debug("<<value값이 없어 푸시 발송을 중단합니다.>>");
            return;
        }
        if (targetUserIdList == null || targetUserIdList.isEmpty()) {
            logger.debug("<<발송대상자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 푸시 발송
            GcmSendForm gcmSendForm = new GcmSendForm();
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("type", "action");
            // paramMap.put("title", title);
            paramMap.put("value", value);
            paramMap.put("action", action);
            gcmSendForm.setUserIds(targetUserIdList);
            gcmSendForm.setMessage(paramMap);

            String formJson = this.mapper.writeValueAsString(gcmSendForm);
            HttpEntity<byte[]> request = new HttpEntity<>(formJson.getBytes("utf-8"), headers);
            logger.debug("<<푸시데이타>> {}", formJson);

            String gcmServerUrl = urlProperties.getGcmServer() + "/send";
            // logger.debug(gcmServerUrl);
            String pushResult = restTemplate.postForObject(gcmServerUrl, request, String.class);
            logger.debug("<<푸시결과>> {}", pushResult);
        } catch (Exception e) {
            logger.error("<<푸시발송 중 오류 발생>>", e);
            return;
        }

        long endTime = System.currentTimeMillis();

        logger.info("<<푸시발송시간: {}초>>", (endTime - startTime) / 1000.0);
    }

    @Override
    public void sendGcmFunction(String title, String androidValue, String iosValue, List<String> functions, List<?> targetList, boolean alarmView) {

        if (StringUtil.isBlank(androidValue) || StringUtil.isBlank(iosValue)) {
            logger.debug("<<androidValue 또는 iosValue 값이 없어 푸시 발송을 중단합니다.>>");
            return;
        }
        if (targetList == null || targetList.isEmpty()) {
            logger.debug("<<발송대장자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        if (functions == null || functions.isEmpty()) {
            logger.debug("<<functions가 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        List<Long> androidUserList = Lists.newArrayList();
        List<Long> androidUserAptList = Lists.newArrayList();

        List<Long> iosUserList = Lists.newArrayList();
        List<Long> iosUserAptList = Lists.newArrayList();

        for (Object object : targetList) {
            Long userId = null;
            String kind = "";
            Long aptId = null;
            if (object instanceof User) {
                User user = (User) object;
                userId = user.id;
                kind = user.kind;
                aptId = user.house.apt.id;
            } else if (object instanceof SimpleUser) {
                SimpleUser user = (SimpleUser) object;
                userId = user.id;
                kind = user.kind;
                aptId = user.aptId;
            }

            if ("android".equals(kind)) {
                // 중복되지않은 유저만 add
                if (!androidUserList.contains(userId)) {
                    androidUserList.add(userId);
                    androidUserAptList.add(aptId);
                }
            } else if ("ios".equals(kind)) {
                // 중복되지않은 유저만 add
                if (!iosUserList.contains(userId)) {
                    iosUserList.add(userId);
                    iosUserAptList.add(aptId);
                }
            }
        }

        if (androidUserList.isEmpty() && iosUserList.isEmpty()) {
            logger.debug("<< kind가 android, ios가 아님. 발송대장자(들)이 없어 푸시 발송을 중단합니다.>>");
            return;
        }

        try {

            String pushResult = "";

            if (!androidUserList.isEmpty()) {
                GcmSendForm form = new GcmSendForm();
                Map<String, String> msg = Maps.newHashMap();
                msg.put("type", "action");
                if (alarmView == true) {
                    msg.put("title", StringUtil.nvl(title));
                    msg.put("push_type", "function-execute-alarm");// function-execute-alarm : function도 실행하고 push 알림도 띄움 ( title과 value가 필수 )
                } else {
                    msg.put("push_type", "function-execute");// function-execute : function만 실행
                }
                msg.put("value", androidValue); // 구버전에서는 알림이 보이기때문에 대비 메시지는 입력한다.
                JSONArray ja = new JSONArray(functions);
                msg.put("function", ja.toString());
                form.setUserIds(androidUserList);
                form.setMessage(msg);
                pushResult = sendGcm(form);

                for (int i = 0; i < androidUserList.size(); i++) {
                    Long userId = androidUserList.get(i);
                    Long aptId = androidUserAptList.get(i);

                    PushLog pushLog = new PushLog();
                    pushLog.setAptId(aptId);
                    pushLog.setUserId(userId);
                    pushLog.setTitle(title);
                    pushLog.setMessage(androidValue);
                    pushLog.setGubun("function-execute");
                    pushLog.setDeviceType("android");
                    pushLog.setDeviceRecYn("Y");
                    pushLog.setPushSendCount(1);
                    pushLog.setPushClickCount(0);
                    pushLog.setSmsYn("N");

                    pushLogMapper.insertPushLog(pushLog);
                }

            }

            if (!iosUserList.isEmpty()) {
                GcmSendForm form = new GcmSendForm();
                Map<String, String> msg = Maps.newHashMap();
                msg.put("type", "action");
                msg.put("value", iosValue);

                String actionParam = "";
                for (int i = 0; i < functions.size(); i++) {
                    String func = functions.get(i);
                    actionParam += (i == 0 ? "?" : "&") + func + "=Y";
                }

                msg.put("action", "emaul://function-execute" + actionParam);// 아이폰 전용 기능 실행 포맷 action protocol ==> emaul://function-execute, parameter는 아이폰개발자와 정의해서 사용 ( parameter 여러개 사용가능 )
                form.setUserIds(iosUserList);
                form.setMessage(msg);
                pushResult = sendGcm(form);

                for (int i = 0; i < iosUserList.size(); i++) {
                    Long userId = iosUserList.get(i);
                    Long aptId = iosUserAptList.get(i);

                    PushLog pushLog = new PushLog();
                    pushLog.setAptId(aptId);
                    pushLog.setUserId(userId);
                    pushLog.setTitle(title);
                    pushLog.setMessage(androidValue);
                    pushLog.setGubun("function-execute");
                    pushLog.setDeviceType("ios");
                    pushLog.setDeviceRecYn("Y");
                    pushLog.setPushSendCount(1);
                    pushLog.setPushClickCount(0);
                    pushLog.setSmsYn("N");

                    pushLogMapper.insertPushLog(pushLog);
                }
            }

            logger.debug("<<푸시결과>> {}", pushResult);
        } catch (Exception e) {
            logger.error("<<푸시발송 중 오류 발생>>", e);
        }

    }
}
