package com.jaha.web.emaul.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.mapper.AddressMapper;
import com.jaha.web.emaul.mapper.BoardMapper;
import com.jaha.web.emaul.mapper.HouseMapper;
import com.jaha.web.emaul.mapper.UserMapper;
import com.jaha.web.emaul.model.Address;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.Setting;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserHistory;
import com.jaha.web.emaul.model.UserNickname;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.model.UserType;
import com.jaha.web.emaul.model.UserViewLog;
import com.jaha.web.emaul.repo.AddressRepository;
import com.jaha.web.emaul.repo.AptRepository;
import com.jaha.web.emaul.repo.BoardCategoryRepository;
import com.jaha.web.emaul.repo.HouseRepository;
import com.jaha.web.emaul.repo.SettingRepository;
import com.jaha.web.emaul.repo.UserNicknameRepository;
import com.jaha.web.emaul.repo.UserPrepassRepository;
import com.jaha.web.emaul.repo.UserRepository;
import com.jaha.web.emaul.repo.UserViewLogRepository;
import com.jaha.web.emaul.util.AddressConverter;
import com.jaha.web.emaul.util.Locations;
import com.jaha.web.emaul.util.PasswordHash;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.v2.model.common.LoginSession;
import com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo;

/**
 * Created by doring on 15. 3. 9..
 */
@Service
public class UserServiceImpl extends BaseSecuModel implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserNicknameRepository userNicknameRepository;
    @Autowired
    private UserPrepassRepository userPrepassRepository;
    @Autowired
    private AptRepository aptRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private BoardCategoryRepository boardCategoryRepository;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private UserViewLogRepository userViewLogRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private GcmService gcmService;

    @Value("${multilogin.push.key}")
    private String pushKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Gson gson = new Gson();

    @Override
    public User createUser(HttpServletRequest req, String uid, String addressCode, String dong, String ho, String email, String name, String birthYear, String gender, String password,
            String phoneNumber, Long recommId) {

        User user = userRepository.findOneByEmail(encString(email));

        if (user == null) {
            user = new User();
        } else {
            if (!user.type.deactivated) {
                return null;
            }
        }

        user.setEmail(email);

        try {
            user.passwordHash = PasswordHash.createHash(password);
        } catch (Exception e) {
            logger.error("<<비밀번호 해싱 중 오류>>", e);
            return null;
        }
        user.deactiveDate = null;
        user.setFullName(name);
        user.hasProfileImage = false;
        user.setPhone(phoneNumber);
        user.regDate = new Date();
        user.uniqueDeviceId = uid;
        user.birthYear = birthYear;
        user.gender = gender;

        user.house = selectOrCreateHouse(addressCode, dong, ho);

        // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////
        // 같은 이름을 가진 사용자 목록
        List<User> userList = userRepository.findByFullName(encString(name));
        String nickname = null;
        int coun = 0;
        if (userList != null && userList.size() > 0) { // 이름이 존재한다.
            coun = userList.size() + 1;
            nickname = name + coun;
        } else { // 이름이 존재하지 않는다.
            nickname = name;
        }

        // 닉네임이 존재하는지 확인한다.
        UserNickname checkNick = this.userNicknameRepository.findOne(nickname);
        if (checkNick != null) {
            coun++;
            nickname = name + coun;
        }

        UserNickname nick = new UserNickname();
        nick.name = nickname;
        user.setNickname(nick);
        // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////

        user = userRepository.saveAndFlush(user);

        user.setting = new Setting(user.id);
        user.type = new UserType(user.id);

        // 추천인 ID
        if (recommId > 0) {
            user.recommId = recommId;
        }

        user = userRepository.saveAndFlush(user);

        // SessionAttrs.setUserId(req.getSession(), user.id);
        // 20161017, 전강욱, 로그인세션 추가
        LoginSession loginSession = new LoginSession();
        loginSession.setUserId(user.id);
        loginSession.setHouseId(user.house.id);
        loginSession.setAptId(user.house.apt.id);
        SessionAttrs.setLoginSession(req.getSession(), loginSession);

        return convertToPrivateUser(user);
    }

    /**
     * @author shavrani 2016-10-18
     */
    @Override
    public User createUser(HttpServletRequest req, Map<String, Object> params) throws Exception {

        String uid = StringUtil.nvl(params.get("uid"));
        String addressCode = StringUtil.nvl(params.get("addressCode"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));
        String birthYear = StringUtil.nvl(params.get("birthYear"));
        String gender = StringUtil.nvl(params.get("gender"));
        String email = StringUtils.trimToEmpty(StringUtil.nvl(params.get("email")));
        String name = StringUtils.trimToEmpty(StringUtil.nvl(params.get("name")));
        String password = StringUtil.nvl(params.get("password"));
        String phoneAuthCode = StringUtil.nvl(params.get("phoneAuthCode"));
        String phoneAuthKey = StringUtil.nvl(params.get("phoneAuthKey"));
        String recommNickName = StringUtil.nvl(params.get("recommNickName"));
        String phoneNumber = StringUtil.nvl(params.get("phoneNumber"));
        Long recommId = StringUtil.nvlLong(params.get("recommId"));

        // addressCode가 없을시 받는 parameter
        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String emdNm = StringUtil.nvl(params.get("emdNm"));
        String addressDetail = StringUtil.nvl(params.get("addressDetail"));

        User user = userRepository.findOneByEmail(encString(email));

        if (user == null) {
            user = new User();
        } else {
            return null;
        }

        user.setEmail(email);

        try {
            user.passwordHash = PasswordHash.createHash(password);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
        user.deactiveDate = null;
        user.setFullName(name);
        user.hasProfileImage = false;
        user.setPhone(phoneNumber);
        user.regDate = new Date();
        user.uniqueDeviceId = uid;
        user.birthYear = birthYear;
        user.gender = gender;

        if (StringUtil.isBlank(addressCode)) {
            // addressCode가 없으면 동단위의 address 생성후 아파트 생성
            user.house = selectOrCreateAddressAndHouse(sidoNm, sggNm, emdNm);
            user.setAddressDetail(addressDetail);
        } else {
            // addressCode가 있으면 조회하여 house 생성
            user.house = selectOrCreateHouse(addressCode, dong, ho);
        }

        // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////
        // 같은 이름을 가진 사용자 목록
        List<User> userList = userRepository.findByFullName(encString(name));
        String nickname = null;
        int coun = 0;
        if (userList != null && userList.size() > 0) { // 이름이 존재한다.
            coun = userList.size() + 1;
            nickname = name + coun;
        } else { // 이름이 존재하지 않는다.
            nickname = name;
        }

        // 닉네임이 존재하는지 확인한다.
        List<UserNickname> checkNickList = this.userNicknameRepository.findByNameStartingWith(name);
        if (checkNickList != null && checkNickList.size() > 0) {
            coun = checkNickList.size() + 1;
            nickname = name + coun;
        } else { // 닉네임이 존재하지 않는다.
            nickname = name;
        }

        // 닉네임이 존재하는지 확인한다.
        UserNickname checkNick = this.userNicknameRepository.findOne(nickname);
        if (checkNick != null) {
            coun++;
            nickname = name + " " + coun; // 방어코드
        }

        UserNickname nick = new UserNickname();
        nick.name = nickname;
        user.setNickname(nick);
        // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////

        user = userRepository.saveAndFlush(user);

        user.setting = new Setting(user.id);

        UserType userType = new UserType(user.id);
        if (StringUtil.isBlank(addressCode)) {
            // 가상아파트로 생성된 유저는 기본 type이 익명이 아니고 주민 ( 가상아파트는 주민승인해줄 관리자가 없음 )
            userType.anonymous = false;
            userType.user = true;
        } else {
            // 정상적인 아파트로 등록했지만 계약된 아파트가 아니면 관리자를 할 관리소가 없기때문에 주민으로 처리 ( 차후 계약아파트 지정시 주민권한박탈및 게시판 재정립해야함. )
            if (user.house.apt.registeredApt == false) {
                userType.anonymous = false;
                userType.user = true;
            }
        }
        user.type = userType;

        // 추천인 ID
        if (recommId > 0) {
            user.recommId = recommId;
        }

        // 유저 자동 인증
        try {
            Boolean isPrepass = isPrepassUser(user);
            if (isPrepass != null && isPrepass) {
                user.type.user = true;
                user.type.houseHost = true;
            }
        } catch (Exception e) {
            // do nothing
        }

        user = userRepository.saveAndFlush(user);

        SessionAttrs.setUserId(req.getSession(), user.id);

        return convertToPrivateUser(user);
    }

    @Override
    public House selectOrCreateHouse(String addressCode, String dong, String ho) {
        House house = null;
        Long aptId = jdbcTemplate.query("SELECT id FROM apt WHERE address_code=?", new Object[] {addressCode}, rs -> rs.first() ? rs.getLong("id") : null);

        if (aptId == null) {
            Address address = addressRepository.findOne(addressCode);
            if (address == null) {
                logger.info("adress NULL");
            }

            Apt apt = new Apt();
            apt.name = address.시군구용건물명;
            apt.address = address;
            Locations.LatLng latLng = Locations.getLocationFromAddress(AddressConverter.toStringAddressOld(address));
            if (latLng != null) {
                apt.latitude = latLng.lat;
                apt.longitude = latLng.lng;
            }
            apt.registeredApt = false;
            apt.virtual = false;// 가상아파트 flag 설정
            apt = aptRepository.saveAndFlush(apt);
            logger.info("[아파트정보생성] 아이디: {}, 아파트명: {}", apt.id, apt.name);

            house = new House();
            house.apt = apt;
            house.dong = dong;
            house.ho = ho;

            house = houseRepository.saveAndFlush(house);
            logger.info("[하우스정보생성-1] 아이디: {}, 동: {}, 호: {}", house.id, house.dong, house.ho);

            saveBoardCategory(apt, "오늘", 1, "today", Lists.newArrayList("jaha", "admin", "user", "gasChecker", "anonymous"), Lists.newArrayList("jaha", "admin"));
            saveBoardCategory(apt, "우리이야기", 2, "community", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin", "user", "gasChecker"));
            saveBoardCategory(apt, "생활정보", 3, "community", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin", "user", "gasChecker"));

            // saveBoardCategory(apt, "공지사항", 2, "notice", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin"));
            // saveBoardCategory(apt, "방송 게시판", 3, "tts", Lists.newArrayList("jaha", "admin", "user"), Lists.newArrayList("jaha", "admin"));
            // saveBoardCategory(apt, "민원", 4, "complaint", Lists.newArrayList("jaha", "admin", "user"), Lists.newArrayList("jaha", "admin", "user"));
        } else {
            try {
                house = houseRepository.findOneByAptIdAndDongAndHo(aptId, dong, ho);
            } catch (Exception e) {
                logger.info("<<하우스가 복수, 하우스 목록 조회, 아파트아이디: {}, 동: {}, 호: {}>>", aptId, dong, ho);

                List<House> houseList = this.houseRepository.findByAptIdAndDongAndHo(aptId, dong, ho);
                if (houseList != null && houseList.size() > 0) {
                    house = houseList.get(0);
                }
            }

            if (house == null) {
                house = new House();
                house.apt = aptRepository.findOne(aptId);
                house.dong = dong;
                house.ho = ho;

                house = houseRepository.saveAndFlush(house);
                logger.info("[하우스정보생성-2] 아이디: {}, 동: {}, 호: {}", house.id, house.dong, house.ho);
            }
        }

        return house;
    }

    @Override
    public House selectOrCreateAddressAndHouse(String sidoNm, String sggNm, String emdNm) {
        House house = null;

        Map<String, Object> params = Maps.newHashMap();
        params.put("sidoNm", sidoNm);
        params.put("sggNm", sggNm);
        params.put("emdNm", emdNm);
        Address virtualAddress = addressMapper.selectVirtualAddress(params);

        if (virtualAddress == null) {
            String addressCode = addressMapper.createVirtualAddressBuildingNo();
            params.put("addressCode", addressCode);
            params.put("buildingNm", emdNm + " 커뮤니티");
            addressMapper.insertVirtualAddress(params);
            virtualAddress = addressMapper.selectVirtualAddress(params);
        }

        params.put("addressCode", virtualAddress.건물관리번호);
        Apt apt = houseMapper.selectApt(params);

        if (apt == null) {

            apt = new Apt();
            apt.name = virtualAddress.시군구용건물명;
            apt.address = virtualAddress;
            apt.registeredApt = false;
            apt.virtual = true;// 가상아파트 flag 설정
            Locations.LatLng latLng = Locations.getLocationFromAddress(AddressConverter.toStringAddressOld(virtualAddress));
            if (latLng != null) {
                apt.latitude = latLng.lat;
                apt.longitude = latLng.lng;
            }
            houseMapper.insertApt(apt);
            logger.info("[아파트정보생성] 아이디: {}, 아파트명: {}", apt.id, apt.name);

            house = new House();
            house.apt = apt;
            house.dong = "0";
            house.ho = "0";

            houseMapper.insertHouse(house);

            createBoardCategory(apt, "오늘", 1, "today", Lists.newArrayList("jaha", "admin", "user", "gasChecker", "anonymous"), Lists.newArrayList("jaha", "admin"));
            createBoardCategory(apt, "공지사항", 2, "notice", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin"));
            createBoardCategory(apt, "우리이야기", 3, "community", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin", "user", "gasChecker"));
            createBoardCategory(apt, "생활정보", 4, "community", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin", "user", "gasChecker"));

            logger.info("[하우스정보생성-1] 아이디: {}, 동: {}, 호: {}", house.id, house.dong, house.ho);

        } else {

            params.clear();
            params.put("aptId", apt.id);
            params.put("dong", "0");
            params.put("ho", "0");
            house = houseMapper.selectHouse(params);

            if (house == null) {
                house = new House();
                house.apt = apt;
                house.dong = "0";
                house.ho = "0";

                houseMapper.insertHouse(house);

                logger.info("[하우스정보생성-2] 아이디: {}, 동: {}, 호: {}", house.id, house.dong, house.ho);
            }

            apt.address = virtualAddress;
            house.apt = apt;


        }

        return house;
    }

    private void saveBoardCategory(Apt apt, String name, int ord, String type, List<String> readable, List<String> writable) {
        BoardCategory boardCategory = new BoardCategory();
        boardCategory.apt = apt;
        boardCategory.name = name;
        boardCategory.ord = ord;
        boardCategory.type = type;
        boardCategory.contentMode = ("today".equals(type)) ? "html" : "text";
        boardCategory.pushAfterWrite = "N";
        boardCategory.jsonArrayReadableUserType = gson.toJson(readable);
        boardCategory.jsonArrayWritableUserType = gson.toJson(writable);
        boardCategory.setUserPrivacy(UserPrivacy.ALIAS);
        boardCategoryRepository.save(boardCategory);
    }

    /**
     * @author shavrani 2016-10-25
     * @desc board category 생성 ( saveBoardCategory의 mybatis 버전 )
     */
    private void createBoardCategory(Apt apt, String name, int ord, String type, List<String> readable, List<String> writable) {
        BoardCategory boardCategory = new BoardCategory();
        boardCategory.apt = apt;
        boardCategory.name = name;
        boardCategory.ord = ord;
        boardCategory.type = type;
        boardCategory.contentMode = ("today".equals(type)) ? "html" : "text";
        boardCategory.pushAfterWrite = "N";
        boardCategory.jsonArrayReadableUserType = gson.toJson(readable);
        boardCategory.jsonArrayWritableUserType = gson.toJson(writable);
        boardCategory.setUserPrivacy(UserPrivacy.ALIAS);
        boardMapper.insertBoardCategory(boardCategory);
    }

    @Override
    @Transactional
    public User login(HttpServletRequest req, String email, String password) {
        HttpSession session = req.getSession();

        User user = userRepository.findOneByEmail(encString(email));
        if (user == null || user.type.deactivated) {
            session.invalidate();
            return null;
        }

        try {
            if (PasswordHash.validatePassword(password, user.passwordHash)) {
                // logger.info("* 사용자아이디[{}], 최종 로그인 시간 수정", user.id);
                if (user.type.jaha || user.type.admin) {
                    session.setAttribute("lastLogin", user.lastLoginDate);
                }
                this.modifyLastLoginDate(user.id);

                // SessionAttrs.setUserId(session, user.id);
                // 20161017, 전강욱, 로그인세션 추가
                LoginSession loginSession = new LoginSession();
                loginSession.setUserId(user.id);
                loginSession.setHouseId(user.house.id);
                loginSession.setAptId(user.house.apt.id);
                SessionAttrs.setLoginSession(req.getSession(), loginSession);

                return convertToPrivateUser(user);
            }
        } catch (Exception e) {
            logger.error("<<로그인 오류>>", e);
        }

        session.invalidate();
        return null;
    }

    @Override
    public void logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
    }

    @Override
    public void deactivate(HttpServletRequest req) {
        Long userId = SessionAttrs.getUserId(req.getSession());

        User user = userRepository.findOne(userId);
        user.type.deactivated = true;
        user.deactiveDate = new Date();

        // -- 사용자 설정변경 HISTORY --
        try {
            saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_DEACTIVE, null);
        } catch (Exception e) {
            logger.error(">>> 사용자 설정변경 히스토리 오류 [ 탈퇴 ]", e);
        }
        // -- 사용자 설정변경 HISTORY --

        userRepository.saveAndFlush(user);

        req.getSession().invalidate();
    }

    @Override
    public User saveAndFlush(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findOneByEmail(encString(email));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getAllUsers(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Override
    public List<User> getAllAptUsers(Long aptId) {
        List<House> houseList = houseRepository.findByAptId(aptId);
        List<Long> houseIds = Lists.transform(houseList, new Function<House, Long>() {
            @Override
            public Long apply(House input) {
                return input.id;
            }
        });

        return userRepository.findByHouseIdIn(houseIds);
    }

    @Override
    @Transactional
    public User changeUserNickname(User user, String nickname) {
        // 닉네임이 null이거나 공백인 경우
        if (nickname == null || "".equals(nickname.trim())) {
            // user.setNickname(null);
            // return userRepository.saveAndFlush(user);
            return null;
        }

        // 닉네임이 존재할 경우
        UserNickname nick = userNicknameRepository.findOne(nickname);
        if (nick != null) {
            return null;
        }

        // user 테이블에 닉네임이 있다면 삭제
        if (user.getNickname() != null) {
            userNicknameRepository.delete(user.getNickname());
            user.setNickname(null);
        }
        // 닉네임 변경
        nick = new UserNickname();
        nick.name = nickname;
        user.setNickname(nick);

        try {
            return convertToPrivateUser(userRepository.saveAndFlush(user));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User convertToPublicUser(User user) {
        // logger.info("* 게시판 사용자정보 초기화, 사용자아이디: {}, 이메일: {}, 휴대전화번호: {}, 생년: {}, 성별: {}", user.id, user.getEmail(), user.getPhone(), user.birthYear, user.gender);

        user.house = null;
        user.setPhone(null);
        user.type = null;
        user.setting = null;
        user.setEmail(null);
        user.birthYear = null;
        user.gender = null;
        return user;
    }

    @Override
    public User convertToPrivateUser(User user) {
        user.house.apt.strAddress = AddressConverter.toStringAddress(user.house.apt.address);
        user.house.apt.strAddressOld = AddressConverter.toStringAddressOld(user.house.apt.address);

        return user;
    }

    @Override
    public Setting getSetting(Long userId) {
        Setting ret = settingRepository.findOne(userId);
        if (ret == null) {
            ret = settingRepository.saveAndFlush(new Setting(userId));
        }

        return ret;
    }

    @Override
    public Setting saveAndFlush(Setting setting) {
        return settingRepository.saveAndFlush(setting);
    }

    @Override
    public User save(User user) {
        User saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public List<User> getUsersByAdmin(Long aptId) {
        List<House> houseList = houseRepository.findByAptId(aptId);
        List<Long> houseIds = Lists.transform(houseList, input -> input.id);

        List<User> userList = userRepository.findByHouseIdIn(houseIds);
        List<User> newUserList = Lists.newArrayList(userList);

        // jaha 권한인 경우 관리자페이지에서 표시 안되게 수정
        if (userList != null) {
            for (User user : userList) {
                if (user != null && user.type != null && user.type.jaha) {
                    newUserList.remove(user);
                }
            }
        }

        return userList;
    }

    @Override
    public Page<User> getUsersByAdmin(Long aptId, Pageable pageable) {
        List<House> houseList = houseRepository.findByAptId(aptId);
        List<Long> houseIds = Lists.transform(houseList, input -> input.id);

        Page<User> userList = userRepository.findByHouseIdIn(houseIds, pageable);

        return userList;
    }

    @Override
    public Page<User> getUsersByAdmin(Specification<User> spec, Pageable pageable) {
        // List<House> houseList = houseRepository.findByAptId(aptId);
        // List<Long> houseIds = Lists.transform(houseList, input -> input.id);

        Page<User> userList = userRepository.findAll(spec, pageable);

        return userList;
    }

    @Override
    public List<UserPrepass> getUserPrepass(Long aptId) {
        return userPrepassRepository.findByAptId(aptId);
    }

    @Override
    public List<User> getUsersByHouseIn(List<Long> houseIds) {
        return userRepository.findByHouseIdIn(houseIds);
    }

    // 닉네임으로 사용자 검색
    @Override
    public User getUserByNickName(String nickName) {
        UserNickname userNickName = userNicknameRepository.findOne(nickName);

        if (userNickName == null) {
            return null;
        }

        return userRepository.findOneByNickname(userNickName);
    }

    @Override
    public void saveUserPrepass(List<UserPrepass> userPrepasses) {
        userPrepassRepository.save(userPrepasses);
    }

    @Override
    public User userHouseTransfer(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsersByDongAndHoIn(Long aptId, String dong, List<String> ho) {
        List<House> houseList = Lists.newArrayList();
        if (ho != null && ho.size() > 0) {
            houseList = houseRepository.findByAptIdAndDongAndHoIn(aptId, dong, ho);
        } else {
            houseList = houseRepository.findByAptIdAndDong(aptId, dong);
        }
        List<Long> houseIds = Lists.newArrayList();
        for (House house : houseList) {
            houseIds.add(house.id);
        }
        return userRepository.findByHouseIdIn(houseIds);
    }

    @Override
    @Transactional
    public void modifyLastLoginDate(long userId) {
        this.userRepository.updateLastLoginDate(userId);
    }

    @Override
    public User checkUserInfo(String email, String fullName, String phoneNumber) throws Exception {
        BaseSecuModel bsm = new BaseSecuModel();

        List<User> userList = null;

        if (StringUtils.isBlank(email)) {
            userList = this.userRepository.findByFullNameAndPhone(bsm.encString(fullName), bsm.encString(phoneNumber));
        } else {
            userList = this.userRepository.findByEmailAndFullNameAndPhone(bsm.encString(email), bsm.encString(fullName), bsm.encString(phoneNumber));
        }

        if (userList == null || userList.isEmpty()) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    @Override
    public List<SimpleUser> checkUserInfo(String email, String phone) throws Exception {
        BaseSecuModel bsm = new BaseSecuModel();

        Map<String, Object> params = Maps.newHashMap();

        params.put("phone", bsm.encString(phone));
        if (!StringUtils.isBlank(email)) {
            params.put("email", bsm.encString(email));
        }

        List<SimpleUser> userList = userMapper.selectUserList(params);

        return userList;
    }

    @Override
    @Transactional
    public boolean resetPassword(String password, String email) {
        try {
            BaseSecuModel bsm = new BaseSecuModel();

            this.userRepository.updatePassword(PasswordHash.createHash(password), bsm.encString(email));

            return true;
        } catch (Exception e) {
            logger.error("", e);
        }

        return false;
    }

    @Override
    public UserViewLog saveUserViewLog(UserViewLog userViewLog) {
        return userViewLogRepository.save(userViewLog);
    }

    @Override
    public List<SimpleUser> selectUser(Map<String, Object> params) {
        return userMapper.selectUser(params);
    }

    @Override
    public void changeNicknameOnce() throws Exception {
        List<User> userList = this.userRepository.findByNicknameIsNull();

        if (userList != null && userList.size() > 0) {
            for (User user : userList) {
                // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////
                // 같은 이름을 가진 사용자 목록
                List<User> checkUserList = userRepository.findByFullName(user.getFullNameRawData());
                String nickname = null;
                if (checkUserList != null && checkUserList.size() == 1) { // 이름이 하나인 경우
                    nickname = user.getFullName();
                } else { // 복수인 경우
                    nickname = user.getFullName() + (checkUserList.size() + 1);
                }

                logger.debug("* id: {}, nickname: {}", user.id, nickname);

                UserNickname nick = new UserNickname();
                nick.name = nickname;
                user.setNickname(nick);

                user = userRepository.saveAndFlush(user);
                // ////////////////////////////////////////////////////////////////////// 닉네임 등록 ////////////////////////////////////////////////////////////////////////
            }
        }
    }

    @Override
    public Map<String, Object> phoneAccountSearch(String phone) {
        Map<String, Object> result = Maps.newHashMap();

        BaseSecuModel bsm = new BaseSecuModel();
        String encPhone = bsm.encString(phone);
        List<User> userList = userRepository.findByPhone(encPhone);
        // userList.sort((User x, User y) -> y.regDate.compareTo(x.regDate)); // 2016-10-24 카운트만 필요해서 list는 같이 전달안하여 정렬필요없음.

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        int size = userList.size();

        result.put("currCnt", size);
        result.put("maxCnt", Constants.PHONE_USER_ACCOUNT_MAX);

        // 2016-10-24 카운트만 필요해서 list는 같이 전달안하여 정렬필요없음.
        /*
         * List<Map<String, Object>> resultList = Lists.newArrayList(); for (int i = 0; i < size; i++) { User _user = userList.get(i); Map<String, Object> _item = Maps.newHashMap();
         * 
         * _item.put("id", _user.id); _item.put("email", _user.getEmail()); _item.put("fullName", _user.getFullName()); _item.put("nickname", _user.getNickname());
         * 
         * String lastLoginDate = _user.lastLoginDate == null ? "" : sdf.format(_user.lastLoginDate); String deactiveDate = _user.deactiveDate == null ? "" : sdf.format(_user.deactiveDate); String
         * regDate = _user.regDate == null ? "" : sdf.format(_user.regDate);
         * 
         * _item.put("lastLoginDate", lastLoginDate); _item.put("deactiveDate", deactiveDate); _item.put("regDate", regDate); resultList.add(_item); }
         * 
         * result.put("accountList", resultList);
         */
        return result;
    }

    @Override
    public Boolean isPrepassUser(User user) {
        return userPrepassRepository.findOneByFullNameAndPhoneAndAptIdAndDongAndHo(user.getFullName(), user.getPhone(), user.house.apt.id, user.house.dong, user.house.ho) != null;
    }

    @Override
    public List<SimpleUser> selectUserList(Map<String, Object> params) {
        BaseSecuModel bsm = new BaseSecuModel();

        String aptId = StringUtil.nvl(params.get("aptId"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));
        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// 전화번호, 이메일, 이름
        String searchWord = StringUtil.nvl(params.get("searchWord"));// 전화번호, 이메일, 이름

        if (!StringUtil.isBlank(searchKeyword)) {
            params.put("searchKeyword", bsm.encString(searchKeyword));
        }

        if (!StringUtil.isBlank(searchWord)) {
            params.put("searchWord", bsm.encString(searchWord));
        }

        List<SimpleUser> userList = userMapper.selectUserList(params);

        return userList;
    }

    @Override
    public int selectUserListCount(Map<String, Object> params) {
        return userMapper.selectUserListCount(params);
    }

    @Override
    @Transactional
    public int updateUserMultiLogin(Long userId, String multiLoginYn) throws Exception {

        User user = userRepository.findOneById(userId);
        if (user == null) {
            return 0;
        }
        user.multiLoginYn = multiLoginYn.toUpperCase();
        user = userRepository.saveAndFlush(user);

        // 외부기기 접속 비허용 시 기존 로그아웃을 위하여 푸시 발송
        // if (!"Y".equalsIgnoreCase(multiLoginYn)) {
        //
        // try {
        // // 기존 접속의 GCM_ID에 로그아웃 푸시를 발송한다.
        // logger.debug(">>> pushKey : " + pushKey);
        // Sender sender = new Sender(pushKey);
        // Message.Builder Builders = new Message.Builder();
        // Builders.addData("push_check_ids", "1");
        // Builders.addData("push_type", "function-execute");
        // Builders.addData("type", "function-execute");
        // Builders.addData("function", "[\"logout\"]");
        // Builders.addData("userId", user.id.toString());
        // Builders.addData("title", "외부기기 로그아웃");
        // Builders.addData("value", "외부기기 사용으로 로그아웃 합니다.");
        //
        // Message message = Builders.build();
        // Map<String, Object> map = new HashMap<String, Object>();
        // map.put("userId", userId);
        // List<String> list = userMapper.selectUserGcmHistory(map); // new ArrayList<String>();
        //
        // if (!(list == null || list.isEmpty())) {
        // MulticastResult multiResult;
        //
        // multiResult = sender.send(message, list, 5);
        // if (multiResult != null) {
        // List<Result> resultList = multiResult.getResults();
        // for (Result result : resultList) {
        // logger.debug(">>> push result : " + result.getMessageId());
        // }
        // }
        // }
        //
        // } catch (Exception e) {
        // logger.debug(">>> 외부기기 로그아웃 푸시발송중 오류", e);
        // }
        // }

        return 1;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.service.UserService#updateUserUpdateHistory(com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo)
     */
    @Override
    @Transactional
    public int insertUserUpdateHistory(UserUpdateHistoryVo history) throws Exception {
        return userMapper.insertUserUpdateHistory(history);
    }

    @Override
    public int saveUserUpdateHistory(User user, User targetUser, String type, String data) {

        UserUpdateHistoryVo history = new UserUpdateHistoryVo();

        history.setUserId(targetUser.id);
        history.setType(type);
        history.setModId(user.id);
        history.setData(data);

        history.setAuth(targetUser.type.getTrueTypes().toString());
        history.setUserName(targetUser.getFullNameRawData());
        history.setEmail(targetUser.getEmailRawData());
        history.setPhone(targetUser.getPhoneRawData());
        history.setBirthYear(targetUser.birthYear);
        history.setGender(targetUser.gender);
        if (targetUser.getNickname() != null) {
            history.setNickname(targetUser.getNickname().name);
        }
        history.setHouseId(targetUser.house.id);

        return userMapper.insertUserUpdateHistory(history);
    }

    /*
     * 
     * 사용자 비밀번호 초기화
     * 
     * @see com.jaha.web.emaul.service.UserService#updateUserPwd(com.jaha.web.emaul.model.User)
     */
    @Override
    @Transactional
    public int updateInitUserPwd(User user) throws Exception {
        String newPasswd = "";
        try {
            newPasswd = PasswordHash.createHash("11111111");
            user.setPasswordHash(newPasswd);
            user.setEmail("");
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (InvalidKeySpecException e) {
            throw e;
        }
        return userMapper.updateInitUserInfo(user);
    }

    /*
     * 
     * 사용자 이메일 초기화
     * 
     * @see com.jaha.web.emaul.service.UserService#updateUserPwd(com.jaha.web.emaul.model.User)
     */
    @Override
    @Transactional
    public int updateInitUserEmail(User user) throws Exception {
        String randomKey = RandomKeys.make(5);
        int result = 0;

        try {
            BaseSecuModel bsm = new BaseSecuModel();
            String sString = randomKey + "@test.com";
            String sEncrypt = bsm.encString(sString);
            user.setEmail(sEncrypt);
            user.setPasswordHash("");
            result = userMapper.updateInitUserInfo(user);
            userMapper.updateUserDeactivated(user);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    @Override
    public List<User> findUserList(Specification<User> spec, Sort sort) throws Exception {
        return this.userRepository.findAll(spec, sort);
    }

    @Override
    public List<UserHistory> selectUserHistory(Map<String, Object> params) {

        List<UserHistory> userHistoryList = userMapper.selectUserHistory(params);

        for (UserHistory userHistory : userHistoryList) {
            String data = StringUtil.nvl(userHistory.getData());
        }

        return userHistoryList;
    }

    @Override
    public int selectUserHistoryCount(Map<String, Object> params) {
        return userMapper.selectUserHistoryCount(params);
    }


}
