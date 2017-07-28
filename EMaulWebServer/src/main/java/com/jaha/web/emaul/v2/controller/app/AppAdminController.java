/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.app;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.v2.model.app.AppVersionV2Vo;
import com.jaha.web.emaul.v2.service.app.AppAdminService;

/**
 * <pre>
 * Class Name : AppAdminController.java
 * Description : App관련 관리자 기능
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 22.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 22.
 * @version 1.0
 */
@Controller
public class AppAdminController {

    private static final Logger logger = LoggerFactory.getLogger(AppAdminController.class);


    @Autowired
    private UserService userService;

    @Autowired
    private AppAdminService appAdminService;

    /**
     * 자하권한 > 투표 > 목록 조회 site : admin / jaha
     *
     * @param request
     * @param model
     * @param pagingHelper
     * @param voteDto
     * @param site
     * @return
     */
    @RequestMapping(value = "/v2/jaha/app-version/list")
    public String adminVoteList(HttpServletRequest request, Model model, AppVersionV2Vo appVersionVo) throws Exception {
        try {

            User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

            if (!user.type.jaha) {
                logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }

            logger.debug(">>> list");

        } catch (Exception e) {
            logger.error("<<App 버전 리스트 조회 중 오류>>", e);
        }

        return "v2/admin/app/app-version-list";
    }

    /**
     * OS 별 App 버전 조회
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/jaha/app-version/list/{os}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getAppVersionList(HttpServletRequest request, Model model, AppVersionV2Vo appVersionVo, @PathVariable(value = "os") String os) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
        }

        logger.debug(">>> os : " + os);

        resultMap.put("appVersionList", appAdminService.selectAppVersionV2List(appVersionVo));
        return resultMap;
    }


    /**
     * OS 별 App 버전 조회
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/jaha/app-version/create", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> insertAppVersion(HttpServletRequest request, Model model, AppVersionV2Vo appVersionVo) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
        }

        appVersionVo.setOs(appVersionVo.getOs().toLowerCase());
        appVersionVo.setUserId(user.id);

        appAdminService.insertAppVersionV2(appVersionVo);

        resultMap.put("result", true);
        resultMap.put("os", appVersionVo.getOs());
        return resultMap;
    }


    /**
     * App 버전정보 삭제처리
     *
     * @param request
     * @param model
     * @param appVersionVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/jaha/app-version/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteAppVersion(HttpServletRequest request, Model model, AppVersionV2Vo appVersionVo) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
        }
        appVersionVo.setOs(appVersionVo.getOs().toLowerCase());
        appVersionVo.setModId(user.id);
        appVersionVo.setUseYn("N");

        appAdminService.updateAppVersionV2(appVersionVo);

        resultMap.put("result", true);
        resultMap.put("os", appVersionVo.getOs());
        return resultMap;
    }
}
