/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 6.
 */
package com.jaha.web.emaul.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.model.AppMainTemplate;
import com.jaha.web.emaul.model.AppMainTemplateDetail;
import com.jaha.web.emaul.model.User;

/**
 * <pre>
 * Class Name : AppMainTemplateService.java
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
public interface AppMainTemplateService {


    AppMainTemplate getAppMainTemplate(Integer id);

    List<AppMainTemplate> getAppMainTemplateList(String tabId);

    List<AppMainTemplateDetail> getAppMainTemplateDetailList(Map<String, Object> params);

    List<AppMainTemplateDetail> getAppMainTemplateDetailListDisplay(String tabId);

    boolean saveAndFlushTemplateAndDetail(List<AppMainTemplate> appMainTemplate, List<AppMainTemplateDetail> appMainTemplateDetail, Map<String, MultipartFile> fileMap, User user);

    List<String> getAppMenuIdList();



}
