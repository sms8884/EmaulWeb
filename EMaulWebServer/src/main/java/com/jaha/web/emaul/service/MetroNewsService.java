/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 8. 27.
 */
package com.jaha.web.emaul.service;

import java.util.List;

import com.jaha.web.emaul.model.MetroNews;

/**
 * <pre>
 * Class Name : MetroNewsService.java
 * Description : 메트로신문사 뉴스 연동
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 27.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 27.
 * @version 1.0
 */
public interface MetroNewsService {

    /**
     * 메트로 뉴스 DB 등록
     *
     * @param paramList
     */
    void mergeMetroNews(List<MetroNews> paramList);

}
