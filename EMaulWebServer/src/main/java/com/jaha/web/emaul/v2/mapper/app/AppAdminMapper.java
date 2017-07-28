/**
 *
 */
package com.jaha.web.emaul.v2.mapper.app;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.app.AppVersionV2Vo;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Mapper class mapped db-table called app_version_v2
 */
/**
 * <pre>
 * Class Name : AppAdminMapper.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 22.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 19.
 * @version 1.0
 */
@Mapper
public interface AppAdminMapper {


    /**
     * App 버전정보를 조회한다.
     *
     * @param voteDto
     * @return
     */
    public List<AppVersionV2Vo> selectAppVersionV2List(AppVersionV2Vo appVersionV2Vo);

    /**
     * App 버전정보를 등록한다.
     *
     * @param appVersionV2Vo
     * @return
     */
    public int insertAppVersionV2(AppVersionV2Vo appVersionV2Vo);

    /**
     * App 버전정보를 수정한다. (삭제만 / use_yn = 'N')
     *
     * @param appVersionV2Vo
     * @return
     */
    public int updateAppVersionV2(AppVersionV2Vo appVersionV2Vo);


}
