/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 21.
 */
package com.jaha.web.emaul.v2.mapper.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.SimpleUser;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.model.common.PushLogVo;

/**
 * <pre>
 * Class Name : PushMapper.java
 * Description : 푸시 타켓 사용자 조회 및 로그 입력
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 21.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 21.
 * @version 1.0
 */
@Mapper
public interface PushMapper {

    /** push_log에 데이타를 입력한다. */
    int insertPushLog(PushLogVo param);

    /** 푸시발송 대상을 조회한다. */
    List<SimpleUser> selectTargetUserListForPush(Map<String, Object> params);

}
