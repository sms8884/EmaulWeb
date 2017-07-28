/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.constants;

/**
 * <pre>
 * Class Name : BoardConstants.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 22.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 9. 22.
 * @version 1.0
 */
public interface BoardConstants extends CommonConstants {

    /** 게시판 마이바티스 매퍼 접두사 */
    @Deprecated
    static final String MAPPER_NAMESPACE_BOARD_PREFIX = "com.jaha.web.emaul.v2.mapper.board";

    /** 스마트폰에서 보여줄 게시글의 HTML 스타일 */
    static final String APP_HTML_FORMAT =
            "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/><link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head><body>%s</body></html>";

    // community
    // complaint
    // notice
    // tts

    // today (마을뉴스)
    // jaha-notice (시스템공지)
    // provision (약관)
    // faq (시스템FAQ)
    // event (이벤트)
    // group-admin (그룹어드민, 지자체)

    /** 게시판 (카테고리) 유형 */
    enum BoardType {
        COMMUNITY("community", "커뮤니티"), COMPLAINT("complaint", "민원"), NOTICE("notice", "공지사항"), TTS("tts", "방송"), TODAY("today", "마을뉴스"), SYSTEM_NOTICE("system-notice",
                "e마을 공지사항"), PROVISION("provision", "약관"), FAQ("faq", "FAQ"), EVENT("event", "이벤트"), GROUP("group", "단체 게시판");

        private String code;
        private String value;

        BoardType(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    /** 게시판 관리 유형 */
    enum BoardMgrType {
        BLIND("blind", "숨김글"), SPAM("spam", "스팸글"), POST("post", "게시글"), COMMENT("comment", "댓글");

        private String code;
        private String value;

        BoardMgrType(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    /** 게시글 컨텐츠 유형: html / text */
    enum ContentMode {
        HTML("html"), TEXT("text");

        private String value;

        ContentMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** 게시글 작성자 유형: 실명 / 닉네임 */
    enum UserPrivacy {
        ALIAS("ALIAS", "닉네임"), NAME("NAME", "실명");

        private String code;
        private String value;

        UserPrivacy(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

}
