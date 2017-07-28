/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 6.
 */
package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.AppMainTemplate;
import com.jaha.web.emaul.model.AppMainTemplateDetail;

/**
 * <pre>
 * Class Name : AppMainTemplateMapper.java
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
@Mapper
public interface AppMainTemplateMapper {

    List<AppMainTemplate> selectAppMainTemplateList(Map<String, Object> params);

    AppMainTemplate selectAppMainTemplate(Integer id);

    int deleteAppMainTemplate(Integer id);

    int insertAppMainTemplate(AppMainTemplate appMainTemplate);

    int updateAppMainTemplate(AppMainTemplate appMainTemplate);

    long countAppMainTemplate(AppMainTemplate appMainTemplate);

    List<AppMainTemplate> selectappMainTemplateDetailList(Map<String, Object> params);



    AppMainTemplate selectAppMainTemplateDetail(Integer id);

    List<AppMainTemplateDetail> selectAppMainTemplateDetailDisplay(Map<String, Object> params);

    List<AppMainTemplateDetail> selectAppMainTemplateDetailList(Map<String, Object> params);

    int deleteAppMainTemplateDetail(Integer id);

    int insertAppMainTemplateDetail(AppMainTemplateDetail appMainTemplateDetail);

    int updateAppMainTemplateDetail(AppMainTemplateDetail appMainTemplateDetail);

    long countAppMainTemplateDetail(AppMainTemplateDetail appMainTemplateDetail);

    int insertAppMainTemplateAndDetail(Map<String, Object> params);

    List<String> selectAppMenuIdList();



}
