package com.jaha.web.emaul.service;

import java.util.List;

import com.jaha.web.emaul.model.GcmSendForm;

/**
 * Created by doring on 15. 4. 28..
 */
public interface GcmService {

    void setGcmId(Long userId, String gcmId);

    String sendGcm(GcmSendForm form);

    /**
     * 푸시를 발송한다.
     *
     * @param value
     * @param action 앱URI
     * @param targetUserIdList
     * @return
     */
    void sendPush(String value, String action, List<Long> targetUserIdList);

    /**
     * @author shavrani 2016-11-23
     * @descandroid and ios 통합 기능 호출 gcm send
     * @desc 광고는 전송할수 없음.
     * @param title alarmView가 true면 title은 사용하고 false이면 사용안함.
     * @param androidValue
     * @param iosValue
     * @param functions 앱과 약속된 기능 id
     * @param targetList 유저목록, List로 제네릭 User,SimpleUser 둘다 사용가능
     * @param alarmView true : 알람표시함. false : 알람없이 기능만 수행함. )
     */
    void sendGcmFunction(String title, String androidValue, String iosValue, List<String> functions, List<?> targetList, boolean alarmView);

}
