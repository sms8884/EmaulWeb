/**
 * Copyright (c) 2017 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2017. 02. 08.
 */
package com.jaha.web.emaul.v2.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.user.UserHouseTransferLogVo;

/**
 * <pre>
 * Class Name : UserHouseTransferLogMapper.java
 * Description : 사용자별 세대 전입전출 로그 처리
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2017. 02. 08.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2017. 02. 08.
 * @version 1.0
 */
@Mapper
public interface UserHouseTransferLogMapper {

    /** user_house_transfer_log 테이블에 데이타를 입력한다. */
    int insertUserHouseTransferLog(UserHouseTransferLogVo param);

    /** user_house_transfer_log 테이블에서 데이타를 조회한다. */
    List<UserHouseTransferLogVo> selectUserHouseTransferLogList(Long userId);

    /** user_house_transfer_log 테이블에서 최근 데이타 한 건을 조회한다. */
    UserHouseTransferLogVo selectRecentUserHouseTransferLog(Long userId);

}
