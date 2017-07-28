/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 6.
 */
package com.jaha.web.emaul.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaha.web.emaul.model.AppMainTemplate;
import com.jaha.web.emaul.model.AppMainTemplateDetail;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.AppMainTemplateService;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;

/**
 * <pre>
 * Class Name : AppMainTemplateController.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 6.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 6.
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/admin")
public class AppMainTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppMainTemplateController.class);

    @Autowired
    AppMainTemplateService appmainTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/appMain", method = RequestMethod.GET)
    public String appMainTemplate(HttpServletRequest req, Model model, @RequestParam(value = "tapId") String tapId) {

        // AppMainTemplate appTemplate = appmainTemplateService.getAppMainTemplate(1);
        List<AppMainTemplate> appMaintemplateList = appmainTemplateService.getAppMainTemplateList(tapId);
        List<AppMainTemplateDetail> appMainTemplateDetiailList = appmainTemplateService.getAppMainTemplateDetailListDisplay(tapId);
        List<String> appMenuIdList = appmainTemplateService.getAppMenuIdList();
        Map<String, String> appMainTemplateFileInfo = new HashMap<>();
        System.out.println("appMainTemplateDetiailList.size() - - - - " + appMainTemplateDetiailList.size());
        for (int i = 0; i < appMainTemplateDetiailList.size(); i++) {
            List<FileInfo> fileInfoTmp = commonService.getFileGroup("main", appMainTemplateDetiailList.get(i).getId());

            if (!fileInfoTmp.isEmpty() || fileInfoTmp.size() != 0) {
                appMainTemplateFileInfo.put(fileInfoTmp.get(0).fileGroupKey, fileInfoTmp.get(0).fileOriginName);
            }
        }

        System.out.println("fileInfo" + appMainTemplateFileInfo);


        Map<String, Object> result = new HashMap<String, Object>();
        result.put("appMainTemplateDetailList", appMainTemplateDetiailList);
        result.put("appMainTemplate", appMaintemplateList);
        result.put("fileInfo", appMainTemplateFileInfo);
        model.addAttribute("appMenuIdList", appMenuIdList);
        model.addAttribute("mainTemplateList", appMaintemplateList);
        model.addAttribute("data", result);
        return "admin/app-main-template_" + tapId;
    }



    @RequestMapping(value = "/appMain/read", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> templateDataRead(HttpServletRequest req, Model model, @RequestParam(value = "templateId", required = false) Integer templateId) {

        // AppMainTemplate appTemplate = appmainTemplateService.getAppMainTemplate(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mainTemplateId", templateId);
        List<AppMainTemplateDetail> appMainTemplateDetiailList = appmainTemplateService.getAppMainTemplateDetailList(params);
        AppMainTemplate appMainTemplate = appmainTemplateService.getAppMainTemplate(templateId);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("appMainTemplateDetailList", appMainTemplateDetiailList);
        result.put("appMainTemplate", appMainTemplate);

        return result;
    }



    @RequestMapping(value = "/appMain/saveAndflush", method = RequestMethod.POST)
    @ResponseBody
    public boolean templateSaveAndflush(HttpServletRequest req, HttpSession session, Model model, @RequestParam(value = "jsonData") String params) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        boolean result = true;

        User user = userService.getUser(SessionAttrs.getUserId(session));
        JSONObject obj = new JSONObject(params);
        JSONArray appMainTemplate = obj.getJSONArray("appMainTemplate");
        JSONArray appMainTemplateDetail = obj.getJSONArray("appMainTemplateDetailList");
        Gson gson = new Gson();
        List<AppMainTemplate> appMainTemplateList = gson.fromJson(appMainTemplate.toString(), new TypeToken<List<AppMainTemplate>>() {}.getType());
        List<AppMainTemplateDetail> appMainTemplateDetailList = gson.fromJson(appMainTemplateDetail.toString(), new TypeToken<List<AppMainTemplateDetail>>() {}.getType());
        appmainTemplateService.saveAndFlushTemplateAndDetail(appMainTemplateList, appMainTemplateDetailList, fileMap, user);
        return result;
    }



}
