/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 21.
 */
package com.jaha.web.emaul.v2.constants;

/**
 * <pre>
 * Class Name : CommonConstants.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 21.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 21.
 * @version 1.0
 */
public interface CommonConstants {

    /** 기본 페이지 사이즈 */
    static final int DEFAULT_PAGE_SIZE = 20;
    /** 기본 페이지 블록 사이즈 */
    static final int DEFAULT_PAGE_BLOCK_SIZE = 10;

    /** 정렬 유형: ASC / DESC */
    enum SortType {
        ASC("ASC"), DESC("DESC");

        private String value;

        SortType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 성별 */
    enum Gender {
        MALE("male"), FEMALE("female"), MALE_ABBR("M"), FEMALE_ABBR("F");

        private String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 사용자 폰 유형 */
    enum UserDeviceType {
        ANDROID("android"), IOS("ios"), NONE("none");

        private String value;

        UserDeviceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 발송 유형 */
    enum PushSendType {
        ANDROID_AD("android-ad"), IOS_AD("ios-ad"), IOS_NOAD("ios-noad"), NOAD("noad");

        private String value;

        PushSendType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시로그구분: parcel / eDoor / board 등 */
    enum PushGubun {
        PARCEL("parcel"), EDOOR("eDoor"), BOARD_POST("board-post"), BOARD_COMMENT("board-comment"), BOARD_REPLY("board-comment-reply"), BOARD_NOTICE("board-notice"), BOARD_COMPLAINT(
                "board-complaint"), BOARD_AIR("board-air"), BOARD_EVENT("board-event"), BOARD_GROUP("board-group"), FEE("fee"), SCHEDULE("schedule"), USER_AGREE(
                        "user-agree"), VOTE("vote"), USER_AGREE_REQ("user-agree-req"), SAFE_COMEBACK_HOME("safe-comback-home"), ACTION("action"), AIR_POLLUTION("air-pollution"), PMS("pms");

        private String value;

        PushGubun(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 알람 세팅 */
    enum PushAlarmSetting {
        ALARM("noti_alarm"), BOARD("noti_board"), PARCEL("noti_parcel"), EDOOR("noti_edoor"), FEE("noti_fee"), AIR_POLLUTION("noti_air_pollution"), VISIT("noti_visit");

        private String value;

        PushAlarmSetting(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 메시지 */
    // 2016.12.21 cyt : 임시 메세지 처리 3건 (App에서 매핑못하는 데이터)
    // vote_request : 전자투표
    // scheduler_title : 일정
    // new_vote_registed : 전자투표
    enum PushMessage {
        USER_AGREE_REQ("%s동 %s호 %s님이 가입하셨습니다.\n관리자 웹페이지에서 주민 확인 후에 승인해주세요."), USER_AGREE_COMPLETE("주민 승인이 완료되었습니다."), BOARD_COMPLAINT_TITLE("새로운 민원이 접수되었습니다."), BOARD_COMPLAINT_COMMENT_TITLE(
                "접수하신 민원의 답변이 등록되었습니다."), BOARD_POST_REG("%s게시판에 %s님의 새로운 글이 등록되었습니다."), BOARD_COMMENT_REG("new_comment_replied"), BOARD_REPLY_REG("new_comment_reply_added"), BOARD_NOTICE_REG(
                        "notice"), FEE("%s의 %s아파트 관리비 %,d원이 부과되었습니다.\n세부내용은 우리아파트앱 \"이마을\"에서 확인하세요."), SCHEDULE_TITLE("일정"), SCHEDULE_BODY("%s의 일정을 확인해주세요."), VOTE_STARTED_TITLE(
                                "전자투표"), VOTE_STARTED_BODY("'%s' 전자투표가 시작되었습니다."), VOTE_TOBE_TITLE("전자투표"), VOTE_TOBE_BODY("'%s'부터 전자투표가 진행될 예정입니다. 후보자 공약을 확인하세요."), VOTE_REQ_TITLE(
                                        "전자투표"), VOTE_REQ_BODY("'%s'에 에 참여하세요."), SAFE_COMEBACKHOME_TITLE("안전귀가 위치 알림 서비스입니다."), SAFE_COMEBACKHOME_BODY(
                                                "%s님이 \"이마을\"앱의 안전귀가 서비스를 요청하셨습니다. https://goo.gl/iyyLTO https://itunes.apple.com/kr/app/ima-eul-mobail-tupyo-mich/id1058747039?mt=8"), WEATHER_ALERT_TITLE(
                                                        "push_title_res_id_fine_dust"), WEATHER_ALERT_BODY(
                                                                "e마을\n%s\n\n%s\n\n%s"), PMS_VISIT_BODY("방문 신청이 도착했습니다. 방문 차량(%s)을 확인해주세요."), PMS_VISIT_TITLE("e마을 방문차량");



        private String value;

        PushMessage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 Action Uri */
    enum PushAction {
        FEE("emaul://aptfee"), MAULNEWS("emaul://today-detail?id=%s&newsCategory=%s"), BOARD("emaul://post-detail?id=%s"), BOARD_GROUP("emaul://post-group-detail?id=%s"), BOARD_EVENT(
                "emaul://post-event-detail?id=%s"), BOARD_COMMENT("emaul://comment-detail?id=%s&postId=%s"), SCHEDULE("emaul://aptschedule-detail?id=%s"), VOTE("emaul://vote"), PUSH_DETAIL(
                        "emaul://push-detail?id=%s"), PUSH_LIST(
                                "emaul://push-list"), WEATHER_ALERT("emaul://weather-alert"), TRACKER("emaul://tracker-map?phone=%s"), PMS_VISIT_DETAIL("emaul://visit-detail?id=%s");

        private String value;

        PushAction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 발송 대상 유형 */
    enum PushTargetType {
        USER("user"), HOUSE("house"), APT("apt");

        private String value;

        PushTargetType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 푸시 상태 : 예약 reserv, 즉시 instant */
    enum PushStatus {
        RESERV("reserv"), INSTANT("instant");

        private String value;

        PushStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
