package com.jaha.web.emaul.util;

import javax.servlet.http.HttpSession;

import com.jaha.web.emaul.v2.model.common.LoginSession;

/**
 * Created by doring on 15. 4. 14. 수정(20161017) : 로그인 세션 추가
 */
public class SessionAttrs {

    public static Long getUserId(HttpSession session) {
        LoginSession loginSession = getLoginSession(session);
        // return (Long) session.getAttribute("userId");
        if (loginSession == null) {
            session.invalidate();
            return null;
        } else {
            return loginSession.getUserId();
        }
    }

    public static void setUserId(HttpSession session, Long userId) {
        session.setAttribute("userId", userId);
    }

    public static LoginSession getLoginSession(HttpSession session) {
        return (LoginSession) session.getAttribute("loginSession");
    }

    public static void setLoginSession(HttpSession session, LoginSession loginSession) {
        session.setAttribute("loginSession", loginSession);
    }

}
