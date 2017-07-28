package com.jaha.web.emaul.util;

/**
 * Created by doring on 15. 5. 10..
 */
public class VoteStatusUtil {

    public static String getPublicStatusText(String status) {
        if ("active".equals(status)) {
            return "진행 중";
        } else if ("ready".equals(status)) {
            return "준비";
        } else if ("done".equals(status)) {
            return "완료";
        }
        return "알 수 없음";
    }
}
