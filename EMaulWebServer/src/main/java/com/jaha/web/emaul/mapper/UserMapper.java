package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserHistory;
import com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo;

/**
 * Created by shavrani on 16-06-23
 */
@Mapper
public interface UserMapper {

    List<SimpleUser> selectUser(Map<String, Object> params);

    List<SimpleUser> selectUserList(Map<String, Object> params);

    int selectUserListCount(Map<String, Object> params);

    /** 푸시발송 대상을 조회한다. */
    List<SimpleUser> selectTargetUserListForPush(Map<String, Object> params);

    /**
     * 로그인 히스토레이서 해당 사용자의 접속 기기 이력을 조회한다. <br/>
     * 로그아웃 푸시발송 용 GCM_ID
     *
     * @param map
     * @return
     */
    List<String> selectUserGcmHistory(Map<String, Object> map);


    /**
     * 사용자 설정변경 이력
     *
     * @param history
     * @return
     */
    int insertUserUpdateHistory(UserUpdateHistoryVo history);


    /**
     * 사용자 특정정보를 초기화한다 (이메일,비밀번호 )
     * 
     * @param user
     * @return
     */
    int updateInitUserInfo(User user);

    /**
     * 사용자 탈퇴처리
     * 
     * @param user
     * @return
     */
    int updateUserDeactivated(User user);

    /**
     * 사용자 설정변경 이력과 아파트 변경내역을 검색
     */
    List<UserHistory> selectUserHistory(Map<String, Object> params);

    int selectUserHistoryCount(Map<String, Object> params);



}
