/**
 * Copyright (c) 2017 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2017. 2. 8.
 */
package com.jaha.web.emaul.v2.service.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.v2.mapper.user.UserHouseTransferLogMapper;
import com.jaha.web.emaul.v2.model.user.UserHouseTransferLogVo;

/**
 * <pre>
 * Class Name : UserHouseTransferLogServiceImpl.java
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
@Service
public class UserHouseTransferLogServiceImpl implements UserHouseTransferLogService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserHouseTransferLogMapper userHouseTransferLogMapper;

    @Override
    @Transactional
    public void saveTransferInByAdmin(Boolean wasAnonymous, Boolean isUser, Long userId, Long newHouseId, Long regId) {
        try {
            if (wasAnonymous && isUser) { // 관리자가 주민승인한 경우
                UserHouseTransferLogVo param = new UserHouseTransferLogVo();
                param.setUserId(userId);
                param.setHouseId(newHouseId);
                param.setGubun(UserHouseTransferLogVo.TRANSFER_IN);
                param.setRegId(regId);
                this.userHouseTransferLogMapper.insertUserHouseTransferLog(param);
                logger.info("<<세대 전입 로그 저장 완료>> {}", param.toString());
            }
        } catch (Exception e) {
            logger.error("<<세대 전입 로그 저장 중 오류>>", e);
        }
    }

    @Override
    @Transactional
    public void saveTransferOutByAdmin(Long userId, Long oldHouseId, Long regId) {
        this.saveTransferOut(userId, oldHouseId, regId);
    }

    @Override
    @Transactional
    public void saveTransferOutByUser(Boolean wasUser, Boolean isAnonymous, Long userId, Long oldHouseId) {
        if (wasUser && isAnonymous) {
            this.saveTransferOut(userId, oldHouseId, userId);
        }
    }

    @Transactional
    private void saveTransferOut(Long userId, Long oldHouseId, Long regId) {
        try {
            UserHouseTransferLogVo param = new UserHouseTransferLogVo();
            param.setUserId(userId);
            param.setHouseId(oldHouseId);
            param.setGubun(UserHouseTransferLogVo.TRANSFER_OUT);
            param.setRegId(regId);
            this.userHouseTransferLogMapper.insertUserHouseTransferLog(param);
            logger.info("<<세대 전출 로그 저장 완료>> {}", param.toString());
        } catch (Exception e) {
            logger.error("<<세대 전출 로그 저장 중 오류>>", e);
        }
    }

    @Override
    public List<UserHouseTransferLogVo> findTransferInOrOutLogList(Long userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserHouseTransferLogVo findRecentTransferInOrOutLog(Long userId) {
        // TODO Auto-generated method stub
        return null;
    }

}
