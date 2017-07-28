/**
 * Copyright (c) 2017 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2017. 2. 8.
 */
package com.jaha.web.emaul.v2.service.user;

import java.util.List;

import com.jaha.web.emaul.v2.model.user.UserHouseTransferLogVo;

/**
 * <pre>
 * Class Name : UserHouseTransferLogService.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2017. 2. 8.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2017. 2. 8.
 * @version 1.0
 */
public interface UserHouseTransferLogService {

    /**
     * 관리자가 사용자의 권한을 방문자에서 주민으로 승인 시 전입 로그 저장
     *
     * @param wasAnonymous
     * @param isUser
     * @param userId
     * @param newHouseId
     * @param regId
     */
    void saveTransferInByAdmin(Boolean wasAnonymous, Boolean isUser, Long userId, Long newHouseId, Long regId);

    /**
     * 관리자가 사용자를 전출시 전출 로그 저장
     *
     * @param userId
     * @param oldHouseId
     * @param regId
     */
    void saveTransferOutByAdmin(Long userId, Long oldHouseId, Long regId);

    /**
     * 사용자가 주소 변경 시 전출 로그 저장
     *
     * @param wasUser
     * @param isAnonymous
     * @param userId
     * @param oldHouseId
     */
    void saveTransferOutByUser(Boolean wasUser, Boolean isAnonymous, Long userId, Long oldHouseId);

    /**
     * 사용자 아이디로 세대 전입전출 로그 목록 조회
     *
     * @param userId
     * @return
     */
    List<UserHouseTransferLogVo> findTransferInOrOutLogList(Long userId);

    /**
     * 사용자 아이디로 최근 세대 전입전출 로그 조회
     *
     * @param userId
     * @return
     */
    UserHouseTransferLogVo findRecentTransferInOrOutLog(Long userId);

}
