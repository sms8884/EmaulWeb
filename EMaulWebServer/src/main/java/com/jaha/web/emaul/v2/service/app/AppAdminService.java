/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.app;

import java.util.List;

import com.jaha.web.emaul.v2.model.app.AppVersionV2Vo;

/**
 * <pre>
 * Class Name : AppAdminService.java
 * Description : App관련 서비스
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
public interface AppAdminService {

    /**
     * App 버전 목록
     *
     * @param appVersionV2Vo
     * @return
     * @throws Exception
     */
    List<AppVersionV2Vo> selectAppVersionV2List(AppVersionV2Vo appVersionV2Vo) throws Exception;

    /**
     * App 버전 등록
     *
     * @param appVersionV2Vo
     * @return
     * @throws Exception
     */
    int insertAppVersionV2(AppVersionV2Vo appVersionV2Vo) throws Exception;

    /**
     * App 버전 수정 (삭제처리 : use_yn = 'N')
     * 
     * @param appVersionV2Vo
     * @return
     * @throws Exception
     */
    int updateAppVersionV2(AppVersionV2Vo appVersionV2Vo) throws Exception;

}
