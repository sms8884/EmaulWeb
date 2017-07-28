package com.jaha.web.emaul.constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
public interface Constants {

    /** 기본 페이지 사이징: 10 */
    static final int DEFAULT_PAGE_SIZE = 10;
    /** 기본 문자 인코딩: UTF-8 */
    static final String DEFAULT_ENCODING = "UTF-8";
    /** 기본 날짜형식: yyyyMMddHHmmss */
    static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
    /** 기본 버퍼 사이즈: 4096 */
    static final int DEFAULT_BUFFER_SIZE = 4096;
    /** 기본 SimpleDateFormat */
    static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    public static final String SHORT_DATE_FORMAT = "yyyyMMdd";
    public static final SimpleDateFormat SHORT_DATE_SDF = new SimpleDateFormat(SHORT_DATE_FORMAT, Locale.KOREA);


    /** HTTP Method: GET 또는 POST */
    enum HTTP_METHOD {
        GET, POST;
    }
    /** 배치스케줄러 그룹 키 */
    enum BATCH_GROUP_PREFIX {
        APT_FEE_PUSH_;
    }

    static final String ANDROID_HTML_WRAP =
            "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/>\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/> <link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head><body>%s</body></html>";

    /** 파일 최상위 그룹 */
    static final String FILE_CATEGORY_NOTICE = "system-notice";// 시스템공지사항
    static final String FILE_CATEGORY_APP_MAIN = "main";// 시스템공지사항

    /** 파일저장 중간 경로 */
    static final String ATTACH_FILE_MIDDLEPATH_NOTICE = "system-notice";
    static final String FILE_MIDDLEPATH_MANUAL = "manual";
    static final String FILE_APP_MAIN = "main";
    static final String EDITOR_IMAGE_MIDDLEPATH_SYSTEMNOTICE = "systemNotice";
    static final String EDITOR_IMAGE_MIDDLEPATH_SYSTEMFAQ = "systemFaq";
    static final String EDITOR_IMAGE_MIDDLEPATH_PROVISION = "provision";

    /** 메트로뉴스 FTP 접속 정보 */
    static final String METRO_FTP_SERVER_IP = "14.63.167.27";
    static final int METRO_FTP_SERVER_PORT = 22100;
    static final String METRO_FTP_ID = "jahasmart";
    static final String METRO_FTP_PW = "jaha0816!$";
    static final String METRO_FTP_ROOT_DIR = "/pub";

    /** 회원가입 */
    static final int PHONE_USER_ACCOUNT_MAX = 4;// 휴대폰번호로 등록가능한 최대 계정개수

    /** 마을뉴스 앱 URI */
    static final String APP_URI_MAUL_NEWS = "emaul://today-detail?id=%s&newsCategory=%s";
    /** 게시판 앱 URI */
    static final String APP_URI_BOARD_POST = "emaul://post-detail?id=%s";

    /** AP 테스트 아파트 ID */
    static final Long AP_TEST_APT_ID = 191L;

    /** AP 작동에서 제외할 아파트 ID */
    static final List<Long> AP_EXCLUDE_APT_ID = Lists.newArrayList(1L, 576L);

    /** 푸쉬광고의 접두사 */
    static final String ADVERT_PUSH_PREFIX = "(후원)";// (광고) or (후원)

    /** ap gpiodelay의 기본값 */
    static final Integer AP_DEFAULT_GPIODELAY = 500;

}
