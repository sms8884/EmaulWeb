package com.jaha.web.emaul.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.Setting;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserHistory;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.model.UserViewLog;
import com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo;

/**
 * Created by doring on 15. 3. 9..
 */
public interface UserService {
    User createUser(HttpServletRequest req, String uid, String addressCode, String dong, String ho, String email, String name, String birthYear, String gender, String password, String phoneNumber,
            Long recommId);

    /**
     * 개편된 계정생성
     *
     * @author shavrani 2016-10-18
     */
    User createUser(HttpServletRequest req, Map<String, Object> params) throws Exception;

    Boolean isPrepassUser(User user);

    House selectOrCreateHouse(String addressCode, String dong, String ho);

    /**
     * address, apt, house 생성
     *
     * @author shavrani 2016-10-18
     */
    House selectOrCreateAddressAndHouse(String sidoNm, String sggNm, String emdNm);

    User login(HttpServletRequest req, String email, String password);

    void logout(HttpServletRequest req);

    void deactivate(HttpServletRequest req);

    User saveAndFlush(User user);

    User getUser(Long userId);

    User getUser(String email);

    List<User> getAllUsers();

    Page<User> getAllUsers(Pageable pageable);

    Page<User> getAllUsers(Specification<User> spec, Pageable pageable);

    List<User> getAllAptUsers(Long aptId);

    User changeUserNickname(User user, String nickname);

    User convertToPublicUser(User user);

    User convertToPrivateUser(User user);

    Setting getSetting(Long userId);

    Setting saveAndFlush(Setting setting);

    User save(User user);

    List<User> getUsersByAdmin(Long aptId);

    Page<User> getUsersByAdmin(Long aptId, Pageable pageable);

    Page<User> getUsersByAdmin(Specification<User> spec, Pageable pageable);

    List<UserPrepass> getUserPrepass(Long aptId);

    List<User> getUsersByHouseIn(List<Long> houseIds);

    // 닉네임으로 사용자 검색
    User getUserByNickName(String nickName);

    void saveUserPrepass(List<UserPrepass> userPrepasses);

    User userHouseTransfer(User user);

    List<User> getUsersByDongAndHoIn(Long aptId, String dong, List<String> ho);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 2.
     * @description 사용자의 마지막 로그인 시간을 수정한다.
     *
     * @param userId
     */
    void modifyLastLoginDate(long userId);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 8.
     * @description 아이디/비밀번호 찾기 시 사용자 정보 조회
     *
     * @param email
     * @param fullName
     * @param phoneNumber
     * @return
     */
    User checkUserInfo(String email, String fullName, String phoneNumber) throws Exception;

    /**
     * @author shavrani 2016-10-20
     */
    List<SimpleUser> checkUserInfo(String email, String phoneNumber) throws Exception;

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 9.
     * @description 비밀번호 재설정
     *
     * @param password
     * @param email
     * @return
     */
    boolean resetPassword(String password, String email);

    /**
     * @author shavrani 2016-06-16
     */
    UserViewLog saveUserViewLog(UserViewLog userViewLog);

    List<SimpleUser> selectUser(Map<String, Object> params);

    /**
     * 닉네임이 null인 사용자 닉네임 변경(한번만 수행)
     */
    void changeNicknameOnce() throws Exception;

    Map<String, Object> phoneAccountSearch(String phone);

    /**
     * user search mybatis version
     */
    List<SimpleUser> selectUserList(Map<String, Object> params);

    int selectUserListCount(Map<String, Object> params);

    /**
     * 외부 기기 로그인 허용 여부 변경
     *
     * @param userId
     * @param multiLoginYn
     * @return
     * @throws Exception
     */
    int updateUserMultiLogin(Long userId, String multiLoginYn) throws Exception;


    /**
     * 사용자 설정변경 히스토리 저장
     *
     * @param history
     * @return
     * @throws Exception
     */
    int insertUserUpdateHistory(UserUpdateHistoryVo history) throws Exception;

    /**
     * 사용자 설정변경 히스토리 저장 ( 저장당시의 유저의 정보를 입력 )
     */
    int saveUserUpdateHistory(User user, User targetUser, String type, String data);

    /**
     * 사용자 비밀번호 초기화
     *
     * @param user
     * @return
     * @throws Eception
     */
    int updateInitUserPwd(User user) throws Exception;

    /**
     * 사용자 이메일초기화
     *
     * @param user
     * @return
     * @throws Eception
     */
    int updateInitUserEmail(User user) throws Exception;

    /**
     * 페이징없이 사용자 정보를 검색한다.
     *
     * @param spec
     * @return
     * @throws Exception
     */
    List<User> findUserList(Specification<User> spec, Sort sort) throws Exception;

    /**
     * 사용자 설정변경 이력과 아파트 변경내역을 검색
     */
    List<UserHistory> selectUserHistory(Map<String, Object> params);

    int selectUserHistoryCount(Map<String, Object> params);



}
