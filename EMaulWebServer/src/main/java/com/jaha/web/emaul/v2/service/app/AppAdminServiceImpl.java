/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.v2.mapper.app.AppAdminMapper;
import com.jaha.web.emaul.v2.model.app.AppVersionV2Vo;

/**
 * <pre>
 * Class Name : AppAdminServiceImpl.java
 * Description : App 관련 어드민 서비스 구현
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
@Service
public class AppAdminServiceImpl implements AppAdminService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AppAdminMapper appAdminMapper;

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.app.AppAdminService#selectAppVersionV2List(com.jaha.web.emaul.v2.model.app.AppVersionV2Vo)
     */
    @Override
    @Transactional(readOnly = true)
    public List<AppVersionV2Vo> selectAppVersionV2List(AppVersionV2Vo appVersionV2Vo) throws Exception {

        return this.appAdminMapper.selectAppVersionV2List(appVersionV2Vo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.app.AppAdminService#insertAppVersionV2(com.jaha.web.emaul.v2.model.app.AppVersionV2Vo)
     */
    @Override
    @Transactional
    public int insertAppVersionV2(AppVersionV2Vo appVersionV2Vo) throws Exception {
        return this.appAdminMapper.insertAppVersionV2(appVersionV2Vo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.v2.service.app.AppAdminService#updateAppVersionV2(com.jaha.web.emaul.v2.model.app.AppVersionV2Vo)
     */
    @Override
    @Transactional
    public int updateAppVersionV2(AppVersionV2Vo appVersionV2Vo) throws Exception {
        return this.appAdminMapper.updateAppVersionV2(appVersionV2Vo);
    }


}
