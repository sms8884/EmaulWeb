package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.ParcelLockerForm.Search;
import com.jaha.web.emaul.model.Setting;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserHistory;
import com.jaha.web.emaul.model.UserNickname;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.model.UserType;
import com.jaha.web.emaul.model.UserViewLog;
import com.jaha.web.emaul.model.spec.UserSpecification;
import com.jaha.web.emaul.repo.UserRepository;
import com.jaha.web.emaul.repo.UserTypeRepository;
import com.jaha.web.emaul.service.BoardService;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.GcmService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.ParcelService;
import com.jaha.web.emaul.service.PhoneAuthService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.ExcelFileBuilder;
import com.jaha.web.emaul.util.PageWrapper;
import com.jaha.web.emaul.util.PasswordHash;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.Thumbnails;
import com.jaha.web.emaul.util.Util;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo;
import com.jaha.web.emaul.v2.service.user.UserHouseTransferLogService;
import com.jaha.web.emaul.v2.util.PagingHelper;
import com.jaha.web.emaul.v2.util.PushUtils;

/**
 * Created by doring on 15. 3. 9..
 */
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private GcmService gcmService;
    @Autowired
    private PhoneAuthService phoneAuthService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserHouseTransferLogService userHouseTransferLogService;

    private Gson gson = new Gson();

    // [START] 광고 푸시 추가 by realsnake 2016.09.19
    @Autowired
    private PushUtils pushUtils;

    // [END]

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 7.
     * @description 위치정보제공동의여부 추가
     *
     * @param req
     * @param uid
     * @param addressCode
     * @param dong
     * @param ho
     * @param email
     * @param name
     * @param birthYear
     * @param gender
     * @param password
     * @param phoneNumber
     * @param phoneAuthCode
     * @param recommNickName
     * @param phoneAuthKey
     * @param loiAgrmYn 위치정보제공동의여부
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/api/public/user/create-apt-user", method = RequestMethod.POST)
    public @ResponseBody String getOrRegisterUser(HttpServletRequest req, @RequestParam(value = "uid", required = false) String uid, @RequestParam(value = "addressCode") String addressCode,
            @RequestParam(value = "dong") String dong, @RequestParam(value = "ho") String ho, @RequestParam(value = "email") String email, @RequestParam(value = "name") String name,
            @RequestParam(value = "year") String birthYear, @RequestParam(value = "gender") String gender, @RequestParam(value = "password") String password,
            @RequestParam(value = "phoneNumber") String phoneNumber, @RequestParam(value = "phoneAuthCode") String phoneAuthCode,
            @RequestParam(value = "recommNickName", required = false) String recommNickName, @RequestParam(value = "phoneAuthKey") String phoneAuthKey,
            @RequestParam(value = "loiAgrmYn", required = false) String loiAgrmYn) throws JsonProcessingException {

        User user = userService.getUser(email);
        if (user != null && !user.type.deactivated) {
            return "EXIST_EMAIL";
        }
        if (!phoneAuthService.checkAuth(phoneAuthCode, phoneAuthKey)) {
            return "PHONE_AUTH_ERROR";
        }

        String phoneNumberDb = phoneAuthService.getPhoneNumber(phoneAuthCode, phoneAuthKey);
        if (phoneNumberDb == null) {
            return null;
        }

        User recommUser = null;
        Long recommId = 0l;

        // 추천인 값이 있는 경우 추천인 체크
        if (recommNickName != null && !recommNickName.isEmpty()) {
            recommUser = userService.getUserByNickName(recommNickName);

            if (recommUser == null) {
                return "NO_SUCH_NICKNAME";
            } else {
                recommId = recommUser.id;
            }
        }

        // String phoneNumberDb = phoneNumber;//지우기

        name = StringUtils.trimToEmpty(name);
        email = StringUtils.trimToEmpty(email);

        ObjectMapper mapper = new ObjectMapper();
        user = userService.createUser(req, uid, addressCode, dong, ho, email, name, birthYear, gender, password, phoneNumberDb, recommId);

        // -- 사용자 설정변경 HISTORY --
        try {
            userService.saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_SIGN_UP, null);

        } catch (Exception e) {
            LOGGER.error(">>> 사용자 가입 히스토리 오류", e);
        }
        // -- 사용자 설정변경 HISTORY --

        LOGGER.info("[회원가입정보] 이름:{}, 이메일:{}, 폰번호:{}, 생년:{}, 성별:{}, 동/호:{}/{}, 위치정보제공동의여부:{}", name, email, phoneNumber, birthYear, gender, dong, ho, loiAgrmYn);

        return user == null ? null : mapper.writeValueAsString(user);
    }

    @RequestMapping(value = "/api/public/user/login", method = RequestMethod.POST)
    public String login(HttpServletRequest req, @RequestParam(value = "email") String email, @RequestParam(value = "password") String password, Model model) {

        // LOGGER.debug("session : " + req.getSession().getId());
        User user = userService.login(req, email, password);

        if (user == null) {
            model.addAttribute("error", "true");
            return "index";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/api/user/logout", method = RequestMethod.POST)
    public @ResponseBody String logout(HttpServletRequest req) {
        userService.logout(req);

        return "";
    }

    @RequestMapping(value = "/session-check", method = RequestMethod.GET)
    public @ResponseBody String invalidUser() {
        return "OK";
    }

    @RequestMapping(value = "/api/gcm", method = RequestMethod.POST)
    public @ResponseBody String handleGcm(HttpServletRequest req, @RequestBody String json) throws IOException {

        String gcmId = new JSONObject(json).getString("gcmId");
        Long userId = SessionAttrs.getUserId(req.getSession());

        gcmService.setGcmId(userId, gcmId);

        return "";
    }

    @RequestMapping(value = "/api/setting", method = RequestMethod.POST)
    public @ResponseBody Setting handleSettingPost(HttpServletRequest req, @RequestBody String json) throws IOException {

        Setting setting = new Gson().fromJson(json, Setting.class);

        setting.userId = SessionAttrs.getUserId(req.getSession());

        return userService.saveAndFlush(setting);
    }

    @RequestMapping(value = "/api/setting", method = RequestMethod.GET)
    public @ResponseBody Setting handleSettingGet(HttpServletRequest req) throws IOException {

        Long userId = SessionAttrs.getUserId(req.getSession());

        return userService.getSetting(userId);
    }

    @RequestMapping(value = "/api/user/change-nickname", method = RequestMethod.POST)
    public @ResponseBody User changeNickname(HttpServletRequest req, @RequestBody String json) {

        String nickname = new JSONObject(json).getString("nickname");

        Long userId = SessionAttrs.getUserId(req.getSession());

        User user = userService.getUser(userId);

        if (user.getNickname() != null && user.getNickname().name.equals(nickname)) {
            return user;
        }

        user = userService.changeUserNickname(user, nickname);

        if (user != null) {

            // -- 사용자 설정변경 HISTORY --
            try {
                userService.saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_CHANGE_NICK, null);

            } catch (Exception e) {
                LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [닉네임] ", e);
            }
            // -- 사용자 설정변경 HISTORY --

            return user;
        }
        return null;
    }

    @RequestMapping(value = "/api/user/change-password", method = RequestMethod.POST)
    public @ResponseBody User changePassword(HttpServletRequest req, @RequestBody String json) {

        String pwOld = new JSONObject(json).getString("pwOld");
        String pwNew = new JSONObject(json).getString("pwNew");

        Long userId = SessionAttrs.getUserId(req.getSession());

        User user = userService.getUser(userId);

        try {
            if (PasswordHash.validatePassword(pwOld, user.passwordHash)) {
                user.passwordHash = PasswordHash.createHash(pwNew);

                // -- 사용자 설정변경 HISTORY --
                try {
                    String data = "userPwd : " + user.passwordHash;
                    userService.saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_CHANGE_PWD, data);
                } catch (Exception e) {
                    LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [비밀번호변경]", e);
                }
                // -- 사용자 설정변경 HISTORY --

                return userService.saveAndFlush(user);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return null;
    }

    @RequestMapping(value = "/api/user/deactivate", method = RequestMethod.POST)
    public @ResponseBody String deactivate(HttpServletRequest req) {

        userService.deactivate(req);

        return "";
    }

    @RequestMapping(value = "/api/user/profile-image/delete", method = RequestMethod.DELETE)
    public @ResponseBody User removeProfileImage(HttpServletRequest req) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        User user = userService.getUser(userId);

        long parentNum = user.id / 1000l;
        File dir = new File(String.format("/nas/EMaul/user/profile-image/%s/%s", parentNum, userId));
        File dest = new File(dir, String.format("%s.jpg", userId));
        File destThumb = new File(dir, String.format("%s-thumb.jpg", userId));

        if (dest.exists()) {
            dest.delete();
        }
        if (destThumb.exists()) {
            destThumb.delete();
        }

        user.hasProfileImage = false;

        user = userService.saveAndFlush(user);

        return user;
    }

    @RequestMapping(value = "/api/user/profile-image/upload", method = RequestMethod.POST)
    public @ResponseBody User saveProfileImage(HttpServletRequest req, @RequestParam(value = "image") MultipartFile image) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (image != null) {
            long parentNum = user.id / 1000l;
            long userId = user.id;

            try {
                File dir = new File(String.format("/nas/EMaul/user/profile-image/%s", parentNum));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dest = new File(dir, String.format("%s.jpg", userId));
                dest.createNewFile();
                image.transferTo(dest);
                Thumbnails.create(dest);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        user.hasProfileImage = true;
        user = userService.saveAndFlush(user);

        return user;
    }

    @RequestMapping(value = "/api/public/user/profile-image/{filename}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleImageRequest(@PathVariable("filename") String fileBaseName) {

        int idxThumb = fileBaseName.indexOf("-thumb");
        Long userId = Longs.tryParse(fileBaseName);
        if (idxThumb != -1) {
            userId = Longs.tryParse(fileBaseName.substring(0, idxThumb));
        }

        File toServeUp = new File("/nas/EMaul/user/profile-image", String.format("%s/%s.jpg", userId / 1000l, fileBaseName));

        if (!toServeUp.exists()) {
            toServeUp = new File("/nas/EMaul/user/anonymous.png");
            return Responses.getFileEntity(toServeUp, fileBaseName + ".png");
        }

        return Responses.getFileEntity(toServeUp, fileBaseName + ".jpg");
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @RequestMapping(value = "/admin/user/list")
    public String listUserAdmin(HttpServletRequest req, Model model, @PageableDefault(sort = {"regDate"}, direction = Direction.DESC, size = 10) Pageable pageable) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        LOGGER.debug("* pageable.getPageNumber(): {}", pageable.getPageNumber());
        LOGGER.debug("* pageable.getPageSize(): {}", pageable.getPageSize());

        String searchDong = req.getParameter("searchDong");
        String searchHo = req.getParameter("searchHo");
        String searchPart = req.getParameter("searchPart");
        String searchWord = req.getParameter("searchWord");
        String searchAuth = req.getParameter("searchAuth");

        Specifications<User> specs = Specifications.where(UserSpecification.aptIdEq(user.house.apt.id));
        specs = specs.and(UserSpecification.jahaAuthNotEq(true));

        if (StringUtils.isNotBlank(searchDong)) {
            specs = specs.and(UserSpecification.houseDongEq(searchDong));
        }
        if (StringUtils.isNotBlank(searchHo)) {
            specs = specs.and(UserSpecification.houseHoEq(searchHo));
        }
        if (StringUtils.isNotBlank(searchPart) && StringUtils.isNotBlank(searchWord)) {
            if ("fullName".equals(searchPart)) {
                specs = specs.and(UserSpecification.fullNameEq(searchWord));
            } else if ("phone".equals(searchPart)) {
                specs = specs.and(UserSpecification.phoneEq(searchWord));
            } else if ("email".equals(searchPart)) {
                specs = specs.and(UserSpecification.emailEq(searchWord));
            } else if ("nickname".equals(searchPart)) {
                specs = specs.and(UserSpecification.nicknameLike(searchWord));
            }
        }
        // 권한조회
        if (StringUtils.isNotBlank(searchAuth)) {
            specs = specs.and(UserSpecification.AuthEq(searchAuth));
        }


        Page<User> userList = userService.getUsersByAdmin(specs, pageable);
        LOGGER.debug("* userList.getNumber(): {}", userList.getNumber());
        LOGGER.debug("* userList.getNumberOfElements(): {}", userList.getNumberOfElements());
        LOGGER.debug("* userList..getSize(): {}", userList.getSize());
        LOGGER.debug("* userList.getTotalElements(): {}", userList.getTotalElements());
        LOGGER.debug("* userList.getTotalPages(): {}", userList.getTotalPages());

        PageWrapper<User> page = new PageWrapper<User>(userList, "/admin/user/list", req);
        model.addAttribute("page", page);

        return "admin/user-list";
    }

    @RequestMapping(value = "/jaha/user/list", method = RequestMethod.GET)
    public String listUserJaha(HttpServletRequest req, Model model, @PageableDefault(sort = {"regDate"}, direction = Direction.DESC, size = 10) Pageable pageable) {
        LOGGER.debug("* pageable.getPageNumber(): {}", pageable.getPageNumber());
        LOGGER.debug("* pageable.getPageSize(): {}", pageable.getPageSize());

        String searchAptName = req.getParameter("searchAptName");
        String searchDong = req.getParameter("searchDong");
        String searchHo = req.getParameter("searchHo");
        String searchPart = req.getParameter("searchPart");
        String searchWord = req.getParameter("searchWord");
        String searchAuth = req.getParameter("searchAuth");
        Specifications<User> specs = Specifications.where(UserSpecification.idGreaterThan(0L));

        if (StringUtils.isNotBlank(searchAptName)) {
            specs = specs.and(UserSpecification.aptNameLike(searchAptName));
        }
        if (StringUtils.isNotBlank(searchDong)) {
            specs = specs.and(UserSpecification.houseDongEq(searchDong));
        }
        if (StringUtils.isNotBlank(searchHo)) {
            specs = specs.and(UserSpecification.houseHoEq(searchHo));
        }
        if (StringUtils.isNotBlank(searchPart) && StringUtils.isNotBlank(searchWord)) {
            if ("fullName".equals(searchPart)) {
                specs = specs.and(UserSpecification.fullNameEq(searchWord));
            } else if ("phone".equals(searchPart)) {
                specs = specs.and(UserSpecification.phoneEq(searchWord));
            } else if ("email".equals(searchPart)) {
                specs = specs.and(UserSpecification.emailEq(searchWord));
            } else if ("nickname".equals(searchPart)) {
                specs = specs.and(UserSpecification.nicknameLike(searchWord));
            }
        }
        // 권한조회
        if (StringUtils.isNotBlank(searchAuth)) {
            specs = specs.and(UserSpecification.AuthEq(searchAuth));
        }


        Page<User> userList = userService.getAllUsers(specs, pageable);
        LOGGER.debug("* userList.getNumber(): {}", userList.getNumber());
        LOGGER.debug("* userList.getNumberOfElements(): {}", userList.getNumberOfElements());
        LOGGER.debug("* userList..getSize(): {}", userList.getSize());
        LOGGER.debug("* userList.getTotalElements(): {}", userList.getTotalElements());
        LOGGER.debug("* userList.getTotalPages(): {}", userList.getTotalPages());

        PageWrapper<User> page = new PageWrapper<User>(userList, "/jaha/user/list", req);
        model.addAttribute("page", page);

        return "jaha/user-all-list";
    }

    @RequestMapping(value = "/jaha/user/list/all", method = RequestMethod.GET)
    public @ResponseBody String listAllUser() {
        List<User> u = userService.getAllUsers();

        @SuppressWarnings("unused")
        final Gson gson = new Gson();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Joiner joiner = Joiner.on(", ");

        JSONArray arr = new JSONArray();
        for (User user : u) {
            try {
                JSONObject o = new JSONObject();
                o.put("아파트", user.house.apt.name);
                o.put("동", user.house.dong);
                o.put("호", user.house.ho);
                o.put("이름", user.getFullName());
                o.put("닉네임", user.getNickname() == null ? "없음" : user.getNickname().name);
                o.put("전화번호", user.getPhone());
                o.put("등록일", sdf.format(user.regDate));
                o.put("권한", joiner.join(user.type.listInKo()));
                o.put("수정", "<button onclick='editUser(" + user.id + ")' title=\"회원의 권한을 변경할 수 있습니다\">수정</button>");

                arr.put(o);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }

        JSONObject ret = new JSONObject();
        ret.put("aaData", arr);
        return ret.toString();
    }

    @RequestMapping(value = "/jaha/user/info/{userId}", method = RequestMethod.GET)
    public @ResponseBody String infoUserJaha(@PathVariable(value = "userId") Long userId) {
        User user = userService.getUser(userId);
        final Joiner joiner = Joiner.on(", ");

        JSONObject o = new JSONObject();

        try {
            o.put("아파트", user.house.apt.name);
            o.put("동", user.house.dong);
            o.put("호", user.house.ho);
            o.put("이름", user.getFullName());
            o.put("나이", user.birthYear);
            o.put("성별", user.gender);
            o.put("이메일", user.getEmail());
            o.put("사진", "/api/public/user/profile-image/" + user.id);
            o.put("권한", joiner.join(user.type.getTrueTypes()));
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return o.toString();
    }

    /**
     * @author shavrani 2016-11-18
     * @desc 영문변수명으로 변경된 method
     */
    @RequestMapping(value = "/jaha/user/info")
    public @ResponseBody String defailUserJaha(@RequestParam(value = "userId") Long userId) {
        User user = userService.getUser(userId);
        final Joiner joiner = Joiner.on(", ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        JSONObject o = new JSONObject();

        try {
            o.put("aptName", user.house.apt.name);
            o.put("dong", user.house.dong);
            o.put("ho", user.house.ho);
            o.put("fullName", user.getFullName());
            o.put("nickname", (user.getNickname() == null ? "" : user.getNickname().name));
            o.put("email", user.getEmail());
            o.put("phone", user.getPhone());
            o.put("birthYear", user.birthYear);
            o.put("gender", user.gender);
            o.put("regDate", sdf.format(user.regDate));
            if (user.deactiveDate != null) {
                o.put("deactiveDate", sdf.format(user.deactiveDate));
            }
            o.put("auth", joiner.join(user.type.getTrueTypes()));
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return o.toString();
    }

    @RequestMapping(value = "/admin/user/info/{userId}", method = RequestMethod.GET)
    public String infoUserAdmin(@PathVariable(value = "userId") Long userId, HttpServletRequest req, Model model) {

        Long userIdSession = SessionAttrs.getUserId(req.getSession());
        User userSession = userService.getUser(userIdSession);

        User user = userService.getUser(userId);
        // hello, world
        if (userSession.house.apt.id.equals(user.house.apt.id) || userSession.type.jaha) {
            if (user.birthYear != null && !user.birthYear.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                int birthYear = Integer.valueOf(user.birthYear);
                int currentYear = Integer.valueOf(sdf.format(new Date()));
                model.addAttribute("age", String.valueOf(currentYear - birthYear + 1));
            }

            model.addAttribute("ui", user);

            // 유저 상세보기 열람이력저장.
            UserViewLog userViewLog = new UserViewLog();
            userViewLog.viewUser = user;
            userViewLog.regUser = userSession;
            userService.saveUserViewLog(userViewLog);

            return "admin/user-info";
        } else {
            return "redirect:/error";
        }
    }

    /**
     * update 공통 사용 ( page redirect와 json에서 사용하기위해 따로 method 생성 )
     */
    @SuppressWarnings("unused")
    private String updateUserData(User loginUser, String id, String[] types, String modifyUserName, String dong, String ho, String email, String phone, String birthYear, String gender,
            String nickname) throws Exception {

        String result = "Y";

        User user = userService.getUser(Long.parseLong(id));
        // 전입 처리를 위한 변수
        Boolean wasAnonymous = user.type.anonymous;

        StringBuilder historyData = new StringBuilder("");
        if (StringUtils.isNotBlank(modifyUserName)) {
            if (!modifyUserName.equals(user.getFullName())) {
                user.setFullName(modifyUserName);
            }
        }
        if (StringUtils.isNoneBlank(dong, ho)) {
            if (!dong.equals(user.house.dong) || !ho.equals(user.house.ho)) {
                House house = houseService.getHouse(user.house.apt.id, dong, ho);
                if (house == null) {
                    house = new House();
                    house.apt = user.house.apt;
                    house.dong = dong;
                    house.ho = ho;

                    houseService.insertHouse(house);
                }
                user.house = house;
            }
        }
        if (StringUtils.isNoneBlank(email)) {
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
            }
        }
        if (StringUtils.isNoneBlank(phone)) {
            if (!phone.equals(user.getPhone())) {
                user.setPhone(phone);
            }
        }
        if (StringUtils.isNoneBlank(birthYear)) {
            if (!birthYear.equals(user.birthYear)) {
                user.birthYear = birthYear;
            }
        }
        if (StringUtils.isNoneBlank(gender)) {
            if (!gender.equals(user.gender)) {
                user.gender = gender; // male & female
            }
        }
        if (StringUtils.isNoneBlank(nickname)) {
            UserNickname nick = user.getNickname();
            // 닉네임이 없으면 생성. 있을경우 닉네임이 다르면 수정.
            if (nick == null || !nick.name.equals(nickname)) {
                user = userService.changeUserNickname(user, nickname);
                if (user == null) {
                    return "99";// 닉네임오류 코드 ( 화면에서 구분하여 메시지줌. )
                }
            }
        }

        List<String> trueTypes = Lists.newArrayList(types);

        // 주민 승인이 된 경우에 알림 메시지 전송
        if (user != null && user.type.anonymous && !user.type.user && !user.type.deactivated && trueTypes != null && trueTypes.contains("user") && !trueTypes.contains("anonymous")
                && !trueTypes.contains("blocked") && !trueTypes.contains("deactivated")) {
            sendApporvedGcmMsg(user.id);
        }

        // 차단된 사용자의 경우 방문자로 세팅
        if (trueTypes != null && trueTypes.contains("blocked")) {
            trueTypes.remove("houseHost");
            trueTypes.remove("user");
            trueTypes.add("anonymous");
        }

        boolean isTypeChange = false;
        List<String> originTrueTypes = user.type.getTrueTypes();

        // size가 같으면 내용이 같은지 한번더 체크, size가 다르면 변동이 있는것
        if (originTrueTypes.size() == trueTypes.size()) {
            int matchCnt = 0;
            for (String origin : originTrueTypes) {
                if (trueTypes.contains(origin)) {
                    matchCnt++;
                }
            }
            // 같은 갯수의 list를 비교하여 matchCnt로 변동여부 확인
            if (originTrueTypes.size() != matchCnt) {
                isTypeChange = true;
            }

        } else {
            isTypeChange = true;
        }

        if (isTypeChange == true) {
            String androidValue = "사용자 권한이 변경되었습니다.\n권한 갱신을 위해\n재로그인 또는 앱을 종료후 다시 시작해주세요.";
            String iosValue = "사용자 권한이 변경되었습니다.\n여기를 터치하여 권한을 갱신해주세요.";
            gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("authUpdate"), Lists.newArrayList(user), false);
        }

        user.type.updateTrueTypes(trueTypes);

        userService.save(user);

        // 전입 처리를 위한 변수
        Boolean isUser = user.type.user;

        // 사용자 전입 로그 처리
        this.userHouseTransferLogService.saveTransferInByAdmin(wasAnonymous, isUser, user.id, user.house.id, loginUser.id);

        // -- 사용자 설정변경 HISTORY --
        try {
            String tempAuth = "";
            Field[] fields = UserType.class.getDeclaredFields();
            for (Field field : fields) {
                if ("userId".equals(field.getName()) || "typeMap".equals(field.getName())) {
                    continue;
                }
                Boolean value = trueTypes.contains(field.getName());
                tempAuth += "[" + field.getName() + value + "] ";
            }

            userService.saveUserUpdateHistory(loginUser, user, UserUpdateHistoryVo.TYPE_JAHA_AUTH, null);

        } catch (Exception e) {
            LOGGER.error(">>> 사용자 정보수정 히스토리 오류", e);
        }
        // -- 사용자 설정변경 HISTORY --

        return result;

    }

    @RequestMapping(value = "/jaha/user/updateUserType", method = RequestMethod.POST)
    public String jahaUpdateType(HttpServletRequest req, @RequestParam(value = "userId") String id, @RequestParam(value = "page") String page, @RequestParam(value = "type") String[] types,
            @RequestParam(value = "userName") String modifyUserName, @RequestParam(value = "dong", required = false) String dong, @RequestParam(value = "ho", required = false) String ho,
            @RequestParam(value = "email", required = false) String email, @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "birthYear", required = false) String birthYear, @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "nickname", required = false) String nickname) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        updateUserData(user, id, types, modifyUserName, dong, ho, email, phone, birthYear, gender, nickname);

        if (page.equalsIgnoreCase("apt")) {
            return "redirect:/admin/user/list";
        } else {
            return "redirect:/jaha/user/list";
        }
    }

    @RequestMapping(value = "/jaha/user/updateUserData", method = RequestMethod.POST)
    @ResponseBody
    public String jahaUpdateUserData(HttpServletRequest req, @RequestParam(value = "userId") String id, @RequestParam(value = "type") String[] types,
            @RequestParam(value = "userName") String modifyUserName, @RequestParam(value = "dong") String dong, @RequestParam(value = "ho") String ho, @RequestParam(value = "email") String email,
            @RequestParam(value = "phone") String phone, @RequestParam(value = "birthYear") String birthYear, @RequestParam(value = "gender") String gender,
            @RequestParam(value = "nickname") String nickname) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = updateUserData(user, id, types, modifyUserName, dong, ho, email, phone, birthYear, gender, nickname);

        return result;
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "/admin/user/updateUserType", method = RequestMethod.POST)
    public String adminUpdateType(HttpServletRequest req, @RequestParam(value = "userId") String id, @RequestParam(value = "type") String[] types) throws IllegalAccessException {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        User oldUser = userService.getUser(Long.parseLong(id));
        // 전입 처리를 위한 변수
        Boolean wasAnonymous = oldUser.type.anonymous;

        List<String> trueTypes = Lists.newArrayList(types);
        // 차단된 사용자의 경우 방문자로 세팅
        if (trueTypes != null && trueTypes.contains("blocked")) {
            trueTypes.remove("houseHost");
            trueTypes.remove("user");
            trueTypes.add("anonymous");
        }
        oldUser.type.updateTrueTypes(trueTypes);

        User newUser = userService.save(oldUser);
        // 전입 처리를 위한 변수
        Boolean isUser = newUser.type.user;

        // 사용자 전입 로그 처리
        this.userHouseTransferLogService.saveTransferInByAdmin(wasAnonymous, isUser, newUser.id, newUser.house.id, SessionAttrs.getUserId(req.getSession()));

        // -- 사용자 설정변경 HISTORY --
        try {
            String tempAuth = "";
            Field[] fields = UserType.class.getDeclaredFields();
            for (Field field : fields) {
                if ("userId".equals(field.getName()) || "typeMap".equals(field.getName())) {
                    continue;
                }
                Boolean value = trueTypes.contains(field.getName());
                tempAuth += "[" + field.getName() + value + "] ";
            }

            userService.saveUserUpdateHistory(user, oldUser, UserUpdateHistoryVo.TYPE_ADMIN_AUTH, null);
        } catch (Exception e) {
            LOGGER.error(">>> 사용자 권한변경 히스토리 오류", e);
        }
        // -- 사용자 설정변경 HISTORY --

        return "redirect:/admin/user/list";
    }

    // 아래는 안쓰는 거
    @Deprecated
    @RequestMapping(value = "/admin/user/updateUserType/{id}", method = RequestMethod.PUT)
    public @ResponseBody User updateUserType(HttpServletRequest req, @RequestBody String json) {
        UserType newType = gson.fromJson(json, UserType.class);

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userRepository.findOne(userId);
        newType.userId = userId;
        userTypeRepository.save(newType);

        return user;
    }

    @RequestMapping(value = "/admin/logout", method = RequestMethod.GET)
    public String adminLogout(HttpServletRequest req) {
        logout(req);

        return "redirect:/";
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    public String userLogout(HttpServletRequest req) {
        logout(req);

        return "redirect:/";
    }

    // @RequestMapping(value = "/jaha/user/switch-apt/{aptId}", method = RequestMethod.PUT)
    @RequestMapping(value = "/jaha/user/switch-apt/{aptId}")
    public String switchApt(HttpSession session, @PathVariable(value = "aptId") Long aptId) {
        // Long userId = SessionAttrs.getUserId(session);
        // User user = userService.getUser(userId);
        // Apt apt = houseService.getApt(aptId);
        // House house = user.house;
        // if (house != null) {
        // house.apt = apt;
        // house.dong = "0";
        // house.ho = "0";
        // houseService.saveAndFlush(house);
        // } else { // 사용할일 없을 것 같지만. 혹시 모를 방어로직 (회원의 아파트 ID가 정보가 없을 경우..)
        // House houseObj = new House();
        // houseObj.apt = apt;
        // houseObj.dong = "0";
        // houseObj.ho = "0";
        // house = houseService.saveAndFlush(houseObj);
        // user.house = house;
        // userService.saveAndFlush(user);
        // }

        Long userId = SessionAttrs.getUserId(session);
        User user = userService.getUser(userId);

        LOGGER.debug(">>> before house : " + user.house.id + "/ dong : " + user.house.dong + "/ ho : " + user.house.dong);
        Apt apt = houseService.getApt(aptId);
        House house = userService.selectOrCreateHouse(apt.address.건물관리번호, "0", "0");

        user.house = house;
        LOGGER.debug(">>> after house : " + user.house.id + "/ dong : " + user.house.dong + "/ ho : " + user.house.dong);
        userService.saveAndFlush(user);

        // 아파트 변경에따른 앱 초기데이터 갱신 ( 안드로이드는 리스타트하고 IOS는 초기필수데이터만 갱신한다. ) // 2017.01.19 function이 init이면 안드로이드는 메시지를 팝업으로 띄운다.
        String androidValue = "아파트가 변경되었습니다.\n앱을 재시작후 서비스를 이용하실 수 있습니다.";
        String iosValue = "아파트가 변경되었습니다.\n여기를 터치하여 앱의 초기필요정보를 갱신해주세요.";
        gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("init"), Lists.newArrayList(user), false);

        if (user.type.groupAdmin && "지자체".equals(apt.address.비고1)) { // TODO: 기준정보인 address 테이블보다는 apt 테이블에 지자체 아파트 여부 추가
            // 단체 관리자인 경우, 단체 게시판 카테고리 조회
            List<BoardCategory> categoryList = boardService.getCategories(userId, "group", "N");

            if (categoryList == null || categoryList.isEmpty()) {
                LOGGER.info("<<단체 게시판을 생성해주세요!>> 사용자아이디: {}, 아파트아이디: {}", user.id, apt.id);
            } else {
                return "redirect:/v2/group-admin/board/group/create-form/" + categoryList.get(0).id;
            }
        }

        return "redirect:/jaha/apt";
    }

    @RequestMapping(value = "/jaha/sms/send", method = RequestMethod.POST)
    public @ResponseBody String sendSms(@RequestBody String body) throws InterruptedException {

        JSONObject obj = new JSONObject(body);
        JSONArray list = obj.getJSONArray("list");
        String sender = obj.getString("sender");
        String msg = obj.getString("msg");

        @SuppressWarnings("serial")
        List<String> tempList = new Gson().fromJson(list.toString(), new TypeToken<List<String>>() {}.getType());
        List<String> targetList = Lists.transform(tempList, new Function<String, String>() {
            @Override
            public String apply(String input) {
                int startIndex = input.lastIndexOf("(") + 1;
                int endIndex = input.lastIndexOf(")");
                return input.substring(startIndex, endIndex);
            }
        });

        if (targetList != null && !targetList.isEmpty()) {
            for (String number : targetList) {
                phoneAuthService.sendMsgNow(number, sender, msg, "", "");
            }
        } else {
            return "{\"result\":\"empty-target\"}";
        }


        return "{\"result\":\"success\"}";
    }


    @RequestMapping(value = "/jaha/mms/send", method = RequestMethod.POST)
    public @ResponseBody String sendMms(@RequestBody String body) throws InterruptedException {
        JSONObject obj = new JSONObject(body);
        JSONArray list = obj.getJSONArray("list");
        String sender = obj.getString("sender");
        String msg = obj.getString("msg");
        String title = StringUtil.nvl(obj.getString("title"));

        @SuppressWarnings("serial")
        List<String> tempList = new Gson().fromJson(list.toString(), new TypeToken<List<String>>() {}.getType());
        List<String> targetList = Lists.transform(tempList, new Function<String, String>() {
            @Override
            public String apply(String input) {
                int startIndex = input.lastIndexOf("(") + 1;
                int endIndex = input.lastIndexOf(")");
                return input.substring(startIndex, endIndex);
            }
        });

        if (targetList != null && !targetList.isEmpty()) {
            for (String number : targetList) {
                phoneAuthService.sendMmsMsgNow(number, sender, title, msg, "", "");
            }
        } else {
            return "{\"result\":\"empty-target\"}";
        }


        return "{\"result\":\"success\"}";
    }


    @RequestMapping(value = "/jaha/sms/prepass/send", method = RequestMethod.POST)
    public @ResponseBody String sendSmsToPrepassUser(@RequestBody String body) throws InterruptedException {

        JSONObject obj = new JSONObject(body);
        JSONArray list = obj.getJSONArray("list");
        String sender = obj.getString("sender");
        String msg = obj.getString("msg");

        @SuppressWarnings("serial")
        List<String> tempList = new Gson().fromJson(list.toString(), new TypeToken<List<String>>() {}.getType());
        List<String> targetList = Lists.transform(tempList, new Function<String, String>() {
            @Override
            public String apply(String input) {

                return input;
            }
        });

        if (targetList != null && !targetList.isEmpty()) {
            for (String number : targetList) {
                phoneAuthService.sendMsgNow(number, sender, msg, "", "");
            }
        } else {
            return "{\"result\":\"empty-target\"}";
        }


        return "{\"result\":\"success\"}";
    }


    @RequestMapping(value = "/jaha/mms/prepass/send", method = RequestMethod.POST)
    public @ResponseBody String sendMmsToPrepassUser(@RequestBody String body) throws InterruptedException {

        JSONObject obj = new JSONObject(body);
        JSONArray list = obj.getJSONArray("list");
        String sender = obj.getString("sender");
        String msg = obj.getString("msg");
        String title = StringUtil.nvl(obj.getString("title"));

        @SuppressWarnings("serial")
        List<String> tempList = new Gson().fromJson(list.toString(), new TypeToken<List<String>>() {}.getType());
        List<String> targetList = Lists.transform(tempList, new Function<String, String>() {
            @Override
            public String apply(String input) {

                return input;
            }
        });

        if (targetList != null && !targetList.isEmpty()) {
            for (String number : targetList) {
                phoneAuthService.sendMmsMsgNow(number, sender, title, msg, "", "");
            }
        } else {
            return "{\"result\":\"empty-target\"}";
        }

        return "{\"result\":\"success\"}";
    }


    @RequestMapping(value = "/jaha/apt/user/upload", method = RequestMethod.POST)
    public String uploadAptUserPrepass(HttpServletRequest req, @RequestParam(value = "files[]", required = false) MultipartFile[] files) throws IOException {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.jaha) {
            return "관리자만 이용 가능한 메뉴입니다.";
        }

        List<UserPrepass> userPrepasses = new ArrayList<UserPrepass>();

        for (MultipartFile file : files) {

            byte[] buf = file.getBytes();
            String s = new String(buf, "EUC-KR");
            List<String> prepassUserList = Splitter.on("\n").splitToList(s);

            for (String prepassUser : prepassUserList) {
                try {
                    List<String> data = Splitter.on(",").trimResults().splitToList(prepassUser);

                    if (data.size() == 1) {
                        continue;
                    }

                    UserPrepass userPrepass = new UserPrepass();
                    userPrepass.aptId = Long.parseLong(data.get(0));
                    userPrepass.dong = data.get(1);
                    userPrepass.ho = data.get(2);
                    userPrepass.fullName = data.get(3);
                    userPrepass.phone = data.get(4).trim().replaceAll("-", "");

                    userPrepasses.add(userPrepass);

                } catch (Exception e) {
                    LOGGER.error("", e);
                    return "입주자 명단 입력 중 오류가 발생했습니다.";
                }

            }
        }

        userService.saveUserPrepass(userPrepasses);

        return "redirect:/jaha/apt/user";
    }

    // 주민 승인 완료 메시지 보내기
    private void sendApporvedGcmMsg(Long id) {
        // ////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
        // GcmSendForm form = new GcmSendForm();
        // Map<String, String> msg = Maps.newHashMap();
        // msg.put("type", "action");
        // msg.put("value", "주민 승인이 완료되었습니다.");
        // form.setUserIds(Lists.newArrayList(id));
        // form.setMessage(msg);
        //
        // gcmService.sendGcm(form);

        List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.ALARM, Lists.newArrayList(id));
        String value = PushMessage.USER_AGREE_COMPLETE.getValue();

        this.pushUtils.sendPush(PushGubun.USER_AGREE, "주민승인완료", value, null, null, false, targetUserList);
        // ////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
    }

    @RequestMapping(value = "/admin/user/transfer", method = RequestMethod.POST)
    public @ResponseBody String userHouseTransfer(HttpServletRequest req, @RequestParam(value = "userId") Long userId) {
        // User user = userRepository.findOne(userId);
        // // House house = houseService.getHouse(1L, "101", "101");
        // House house = user.house;
        // house.apt = houseService.getApt(1L); // 자하아파트로 변경
        // house.dong = "0"; // 0동으로 변경
        // house.ho = "0"; // 0호로 변경
        // house = houseService.saveAndFlush(house);
        // user.house = house;
        //
        // // 유저 권한 변경
        // user.type.anonymous = true;
        // user.type.blocked = false;
        // user.type.gasChecker = false;
        // user.type.houseHost = false;
        // user.type.user = false;
        // user.type.admin = false;
        // user = userService.userHouseTransfer(user);
        //
        // return String.valueOf(1);

        User loginUser = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        /**
         * 전출처리는 해당 user의 apt id를 1로 변경하고 방문자권한으로 변경.
         */
        User user = userRepository.findOne(userId);
        // 전출 처리를 위한 변수
        Long oldHouseId = user.house.id;

        House house = houseService.getHouse(1L, "101", "101");

        user.house = house;
        user.type.anonymous = true;
        user.type.blocked = false;
        user.type.gasChecker = false;
        user.type.houseHost = false;
        user.type.user = false;
        user.type.admin = false;
        user = userService.userHouseTransfer(user);

        // 전출 로그 저장
        this.userHouseTransferLogService.saveTransferOutByAdmin(userId, oldHouseId, SessionAttrs.getUserId(req.getSession()));

        // -- 사용자 설정변경 HISTORY --
        try {
            userService.saveUserUpdateHistory(loginUser, user, UserUpdateHistoryVo.TYPE_TRANSFER, null);
        } catch (Exception e) {
            LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [전출]", e);
        }
        // -- 사용자 설정변경 HISTORY --

        return String.valueOf(1);
    }

    @RequestMapping(value = "/admin/user/list-data")
    @ResponseBody
    public List<SimpleUser> getUserListData(HttpServletRequest req, @RequestParam(value = "aptId", required = false) Long aptId, @RequestParam(value = "dong", required = false) String dong,
            @RequestParam(value = "ho", required = false) String ho, @RequestParam(value = "userName", required = false) String userName) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("aptId", StringUtil.nvlLong(aptId, user.house.apt.id));
        params.put("dong", dong);
        params.put("ho", ho);

        if (!StringUtil.isEmpty(userName)) {
            BaseSecuModel bsm = new BaseSecuModel();
            params.put("userName", bsm.encString(userName));
        }

        List<SimpleUser> simpleUserList = userService.selectUser(params);

        return simpleUserList;
    }

    // 회원 전체 인쇄
    @RequestMapping(value = "/admin/user/print_list")
    public String printlistUserAdmin(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        List<User> userList = userService.getUsersByAdmin(user.house.apt.id);

        List<User> userPrintList = new ArrayList<User>();

        if (userList != null && userList.size() > 0) {
            // 자하권한 사용자 제외
            for (User u : userList) {
                // if ((u.type.user || u.type.houseHost || u.type.admin || u.type.gasChecker) && !u.type.jaha) {
                // userPrintList.add(u);
                // }
                if (u.type.jaha) {
                    continue;
                }

                userPrintList.add(u);
            }
        }

        model.addAttribute("userList", userPrintList);
        return "admin/user-list-print";
    }

    // 닉네임없는 사용자 닉네임 변경
    @RequestMapping(value = "/jaha/userNickname/change")
    public String changeUserNickname(HttpServletRequest req, Model model) {
        try {
            // this.userService.changeNicknameOnce();
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return "OK";
    }

    /**
     * 기본적인 유저 선택하는 팝업
     */
    @RequestMapping(value = "/admin/user/search/default-popup")
    public String defaultUserPopup(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        model.addAttribute("callback", req.getParameter("callback"));

        List<String> dongs = houseService.getDongs(user.house.apt.id);
        model.addAttribute("dongs", dongs);

        return "admin/default-user-search-popup";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/admin/user/last-login-pop", method = RequestMethod.GET)
    @ResponseBody
    public String lastLoginCheckPop(HttpServletRequest req) throws ParseException {

        Boolean result = false;
        JSONObject obj = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Date sysDate = new Date();
        // sysDate.setHours(0);
        Date userDate = (Date) req.getSession().getAttribute("lastLogin");
        // LOGGER.info("user email" + user.getEmail() + " user date" + userDate);
        if (userDate != null) {
            String tmp1 = dateFormat.format(sysDate);
            String tmp2 = dateFormat.format(userDate);
            sysDate = dateFormat.parse(tmp1);
            userDate = dateFormat.parse(tmp2);
            int compare = sysDate.compareTo(userDate);

            if (compare > 0) {
                result = true;
                req.getSession().removeAttribute("lastLogin");
            }
            sysDate.setHours(0);

            Long totalcount = parcelService.parcelLogTotalCount(user.house.apt.id, 1, sysDate);
            if (totalcount == 0) {
                result = false;
            }

            obj.append("totalcount", totalcount);
            obj.append("result", result);
            obj.toString();
        }

        return obj.toString();
    }


    @RequestMapping(value = "/api/public/user/phone-search")
    @ResponseBody
    public Map<String, Object> phoneAccountSearch(@RequestParam(value = "phone", required = false) String phone) {

        if (StringUtil.isBlank(phone)) {
            LOGGER.info("<< /api/public/user/phone-search , required parameter is empty !! >>");
            return null;
        }

        return userService.phoneAccountSearch(phone);
    }

    /**
     * 개편된 계정등록
     *
     * @author shavrani 2016-10-18
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "/api/public/user/create-user")
    @ResponseBody
    public Map<String, Object> selectOrRegisterUser(HttpServletRequest req, @RequestParam Map<String, Object> params) throws Exception {

        Map<String, Object> result = Maps.newHashMap();

        String uid = StringUtil.nvl(params.get("uid"));
        String addressCode = StringUtil.nvl(params.get("addressCode"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));
        String birthYear = StringUtil.nvl(params.get("birthYear"));
        String gender = StringUtil.nvl(params.get("gender"));
        String email = StringUtils.trimToEmpty(StringUtil.nvl(params.get("email")));
        String name = StringUtils.trimToEmpty(StringUtil.nvl(params.get("name")));
        String password = StringUtil.nvl(params.get("password"));
        String phoneAuthCode = StringUtil.nvl(params.get("phoneAuthCode"), null);
        String phoneAuthKey = StringUtil.nvl(params.get("phoneAuthKey"), null);
        String recommNickName = StringUtil.nvl(params.get("recommNickName"));

        // addressCode가 없을시 받는 parameter
        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String emdNm = StringUtil.nvl(params.get("emdNm"));
        String addressDetail = StringUtil.nvl(params.get("addressDetail"));

        /** parameter validation */
        if (StringUtil.isBlank(email) || StringUtil.isBlank(name) || StringUtil.isBlank(password)) {
            LOGGER.info("<< /api/public/user/create-user , required parameter is empty !! >>", email);
            result.put("resultCode", "94");
            result.put("resultMessage", "required parameter is empty");
            return result;
        }

        if (StringUtil.isBlank(addressCode)) {
            if (StringUtil.isBlank(sidoNm) || StringUtil.isBlank(sggNm) || StringUtil.isBlank(emdNm) || StringUtil.isBlank(addressDetail)) {
                LOGGER.info("<< /api/public/user/create-user , required parameter is empty !! >>", email);
                result.put("resultCode", "94");
                result.put("resultMessage", "required parameter is empty");
                return result;
            }
        } else {
            if (StringUtil.isBlank(dong) || StringUtil.isBlank(ho)) {
                LOGGER.info("<< /api/public/user/create-user , required parameter is empty !! >>", email);
                result.put("resultCode", "94");
                result.put("resultMessage", "required parameter is empty");
                return result;
            }
        }

        User user = userService.getUser(email);
        if (user != null) {
            // user.type.deactivated 상관없이 이메일 중복차단
            LOGGER.info("<< /api/public/user/create-user , already emaul [{}] !! >>", email);
            result.put("resultCode", "99");
            result.put("resultMessage", "exist email");
            return result;
        }

        if (!phoneAuthService.checkAuth(phoneAuthCode, phoneAuthKey)) {
            LOGGER.info("<< /api/public/user/create-user , phone auth check fail !! >>");
            result.put("resultCode", "98");
            result.put("resultMessage", "phone auth error");
            return result;
        }
        String phoneNumberDb = phoneAuthService.getPhoneNumber(phoneAuthCode, phoneAuthKey);
        if (phoneNumberDb == null) {
            LOGGER.info("<< /api/public/user/create-user , phone auth check fail !! >>");
            result.put("resultCode", "98");
            result.put("resultMessage", "phone auth error");
            return result;
        }
        params.put("phoneNumber", phoneNumberDb);

        Map<String, Object> phoneUserMap = userService.phoneAccountSearch(phoneNumberDb);
        if (phoneUserMap != null) {
            int currCnt = StringUtil.nvlInt(phoneUserMap.get("currCnt"));
            int maxCnt = StringUtil.nvlInt(phoneUserMap.get("maxCnt"));
            if (currCnt >= maxCnt) {
                LOGGER.info("<< /api/public/user/create-user , phone create account fail,  currCnt :{}, maxCnt : {} >>", currCnt, maxCnt);
                result.put("resultCode", "97");
                result.put("resultMessage", "phone create account max fail");
                return result;
            }
        }

        User recommUser = null;
        Long recommId = 0l;

        // 추천인 값이 있는 경우 추천인 체크
        if (!StringUtil.isBlank(recommNickName)) {
            recommUser = userService.getUserByNickName(recommNickName);
            if (recommUser == null) {
                LOGGER.info("<< /api/public/user/create-user , phone auth check fail !! >>");
                result.put("resultCode", "96");
                result.put("resultMessage", "no search recommender nickname");
                return result;
            } else {
                recommId = recommUser.id;
            }
        }
        params.put("recommId", recommId);

        try {
            user = userService.createUser(req, params);

            // -- 사용자 설정변경 HISTORY --
            try {
                userService.saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_SIGN_UP, null);

            } catch (Exception e) {
                LOGGER.error(">>> 사용자 가입 히스토리 오류", e);
            }
            // -- 사용자 설정변경 HISTORY --

            LOGGER.info("[회원가입정보] 이름:{}, 이메일:{}, 폰번호:{}, 생년:{}, 성별:{}, 동/호:{}/{}", name, email, phoneNumberDb, birthYear, gender, dong, ho);
        } catch (Exception e) {
            LOGGER.error("<< /api/public/user/create-user , 회원가입 중 오류 >>", e);
            result.put("resultCode", "95");
            result.put("resultMessage", "user save exception, createUser fail !!");
            return result;
        }

        if (user == null) {
            LOGGER.info("<< /api/public/user/create-user , user save fail !! >>");
            result.put("resultCode", "95");
            result.put("resultMessage", "user save exception, user is null !!");
        } else {
            result.put("resultCode", "00");
            result.put("resultMessage", "SUCCESS");
        }

        return result;
    }

    /**
     * 사용자 검색 팝업
     */
    @RequestMapping(value = "/jaha/user/search/popup")
    public String userSearchPopup(Model model, HttpServletRequest req, @RequestParam(value = "_type", required = false) String _type, @RequestParam(value = "_aptId", required = false) String _aptId) {
        model.addAttribute("_type", _type);

        if (!StringUtil.isBlank(_aptId)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("_aptId", _aptId);
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        return "jaha/user-search-popup";
    }

    /**
     * 사용자 검색 팝업 데이터
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "/jaha/user/search/list-data")
    @ResponseBody
    public List<SimpleUser> userSearchPopupData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String aptId = StringUtil.nvl(params.get("aptId"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));
        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// 전화번호, 이메일, 이름

        String _type = StringUtil.nvl(params.get("_type"));// 없으면 모두, 1 : 활동유저, 2 : 탈퇴유저
        if ("1".equals(_type)) {
            params.put("_active", true); // _active false는 탈퇴자, 없으면 전체
        } else if ("2".equals(_type)) {
            params.put("_active", false); // _active false는 탈퇴자, 없으면 전체
        }

        return userService.selectUserList(params);

    }


    /**
     * 외부기기 로그인 허용여부 설정
     *
     * @param req
     * @param multiLoginYn
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/jaha/v2/user/update-multilogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateUserMultiLogin(HttpServletRequest req, @RequestParam Map<String, Object> params) throws IOException {

        Map<String, Object> ret = new HashMap<String, Object>();

        try {


            Long userId = Long.parseLong((String) params.get("userId"));
            String multiLoginYn = (String) params.get("multiLoginYn");

            LOGGER.debug(">>> userId : " + userId);
            LOGGER.debug(">>> multiLoginYn : " + multiLoginYn);

            User targetUser = userService.getUser(userId);

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

            if (!user.type.jaha) {
                ret.put("result", false);
                ret.put("message", "권한이 없습니다.");
                return ret;
            }


            this.userService.updateUserMultiLogin(userId, multiLoginYn);

            // -- 사용자 설정변경 HISTORY --
            try {
                String data = "multiLoginYn : " + multiLoginYn;
                userService.saveUserUpdateHistory(user, targetUser, UserUpdateHistoryVo.TYPE_MULTILOGIN, data);
            } catch (Exception e) {
                LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [외부기기 설정]", e);
            }
            // -- 사용자 설정변경 HISTORY --

            ret.put("result", true);
            ret.put("message", "SUCCESS");
            return ret;


        } catch (Exception e) {
            ret.put("result", false);
            ret.put("message", "외부기기 로그인 허용여부를 변경 오류.");
            LOGGER.debug(">>> 외부기기 로그인 허용여부를 변경 오류 : " + e.getMessage());
            return ret;
        }
    }

    @RequestMapping(value = "/v2/group-admin/logout", method = RequestMethod.GET)
    public String groupAdminLogout(HttpServletRequest req) {
        this.logout(req);

        return "redirect:/";
    }

    /**
     * 사용자 비밀번호 초기화
     */
    @RequestMapping(value = "/jaha/user/init_pwd/{userId}")
    @ResponseBody
    public Boolean userInitPwd(HttpServletRequest req, @PathVariable(value = "userId") Long userId) {
        boolean result;
        User loginUser = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        User user = userService.getUser(userId);
        try {
            int update = userService.updateInitUserPwd(user);
            result = (update > 0) ? true : false;

            if (result == true) {
                // -- 사용자 설정변경 HISTORY --
                try {
                    String data = "userPwd : " + user.getPasswordHash();
                    userService.saveUserUpdateHistory(loginUser, user, UserUpdateHistoryVo.TYPE_INIT_PWD, data);
                } catch (Exception e) {
                    LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [비밀번호 초기화]", e);
                }
                // -- 사용자 설정변경 HISTORY --
            }

        } catch (Exception e) {
            LOGGER.error("<<사용자 비밀번호 초기화 오류>>", e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * 사용자 이메일 초기화
     */
    @RequestMapping(value = "/jaha/user/init_email/{userId}")
    @ResponseBody
    public Boolean userInitEmail(HttpServletRequest req, @PathVariable(value = "userId") Long userId) {
        boolean result;
        User loginUser = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        User user = userService.getUser(userId);
        try {
            int update = userService.updateInitUserEmail(user);
            result = (update > 0) ? true : false;
            if (result == true) {
                // -- 사용자 설정변경 HISTORY --
                try {
                    userService.saveUserUpdateHistory(loginUser, user, UserUpdateHistoryVo.TYPE_INIT_EMAIL, null);
                } catch (Exception e) {
                    LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [이메일 초기화]", e);
                }
                // -- 사용자 설정변경 HISTORY --
            }

        } catch (Exception e) {
            LOGGER.error("<<사용자 이메일 초기화 오류>>", e.getMessage());
            result = false;
        }
        return result;
    }

    @RequestMapping(value = "/jaha/user/list/excel-download", method = RequestMethod.GET)
    public void downloadUserList(HttpServletRequest req, HttpServletResponse res, Model model, @PageableDefault(sort = {"regDate"}, direction = Direction.DESC, size = 10) Pageable pageable)
            throws Exception {
        String searchAptName = req.getParameter("searchAptName");
        String searchDong = req.getParameter("searchDong");
        String searchHo = req.getParameter("searchHo");
        String searchPart = req.getParameter("searchPart");
        String searchWord = req.getParameter("searchWord");
        String searchAuth = req.getParameter("searchAuth");
        Specifications<User> specs = Specifications.where(UserSpecification.idGreaterThan(0L));

        if (StringUtils.isNotBlank(searchAptName)) {
            specs = specs.and(UserSpecification.aptNameLike(searchAptName));
        }
        if (StringUtils.isNotBlank(searchDong)) {
            specs = specs.and(UserSpecification.houseDongEq(searchDong));
        }
        if (StringUtils.isNotBlank(searchHo)) {
            specs = specs.and(UserSpecification.houseHoEq(searchHo));
        }
        if (StringUtils.isNotBlank(searchPart) && StringUtils.isNotBlank(searchWord)) {
            if ("fullName".equals(searchPart)) {
                specs = specs.and(UserSpecification.fullNameEq(searchWord));
            } else if ("phone".equals(searchPart)) {
                specs = specs.and(UserSpecification.phoneEq(searchWord));
            } else if ("email".equals(searchPart)) {
                specs = specs.and(UserSpecification.emailEq(searchWord));
            } else if ("nickname".equals(searchPart)) {
                specs = specs.and(UserSpecification.nicknameLike(searchWord));
            }
        }
        // 권한조회
        if (StringUtils.isNotBlank(searchAuth)) {
            specs = specs.and(UserSpecification.AuthEq(searchAuth));
        }

        // Sort sort = new Sort(Sort.Direction.DESC, "house.apt.id").and(new Sort(Sort.Direction.DESC, "id"));
        Sort sort = new Sort(Direction.DESC, "id");

        try {
            List<User> userList = this.userService.findUserList(specs, sort);

            final String[] headers = {"아파트", "동", "호", "이름", "닉네임", "전화번호", "등록일", "권한", "이메일", "회원ID"};
            final ExcelFileBuilder excelFileBuilder = new ExcelFileBuilder("Sheet1");
            excelFileBuilder.setHeaders(headers);

            if (userList != null && userList.size() > 0) {
                final Joiner joiner = Joiner.on(", ");
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (User user : userList) {
                    int col = 0;
                    excelFileBuilder.setDataValue(col++, user.house.apt.name);
                    excelFileBuilder.setDataValue(col++, user.house.dong);
                    excelFileBuilder.setDataValue(col++, user.house.ho);
                    excelFileBuilder.setDataValue(col++, user.getFullName());
                    excelFileBuilder.setDataValue(col++, (user.getNickname() == null) ? StringUtils.EMPTY : user.getNickname().name);
                    excelFileBuilder.setDataValue(col++, user.getPhone());
                    excelFileBuilder.setDataValue(col++, sdf.format(user.regDate));
                    excelFileBuilder.setDataValue(col++, joiner.join(user.type.listInKo()));
                    excelFileBuilder.setDataValue(col++, user.getEmail());
                    excelFileBuilder.setDataValue(col++, String.valueOf(user.id));
                    excelFileBuilder.nextRow();
                }
            }

            excelFileBuilder.autoSizeColumns();
            Responses.downloadEexcel("UserListExcel-" + Util.getNowDate() + ".xls", excelFileBuilder, req, res);
        } catch (Exception e) {
            LOGGER.error("<<사용자 목록 엑셀 다운로드 중 오류>>", e);
            throw e;
        }
    }

    @RequestMapping(value = "/jaha/apt/user/list/popup")
    public String getAptUserListPopup(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        if (aptId != null) {
            Apt apt = houseService.getApt(aptId);
            model.addAttribute("aptId", aptId);
            model.addAttribute("aptName", apt.name);
        }
        model.addAttribute("monitoringType", req.getParameter("monitoringType"));
        model.addAttribute("user_type", req.getParameter("user_type"));
        model.addAttribute("reg_date", req.getParameter("reg_date"));

        return "jaha/apt-user-list-popup";
    }

    @RequestMapping(value = "/jaha/apt/deactive/user/list/popup")
    public String getAptDeactiveUserListPopup(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        if (aptId != null) {
            Apt apt = houseService.getApt(aptId);
            model.addAttribute("aptId", aptId);
            model.addAttribute("aptName", apt.name);
        }
        model.addAttribute("monitoringType", req.getParameter("monitoringType"));
        model.addAttribute("user_type", req.getParameter("user_type"));
        model.addAttribute("reg_date", req.getParameter("reg_date"));

        return "jaha/apt-deactive-user-list-popup";
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "/jaha/apt/user/list/popup-data")
    @ResponseBody
    public Map<String, Object> getAptUserListPopupData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String aptId = StringUtil.nvl(params.get("aptId"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));
        String monitoringType = StringUtil.nvl(params.get("monitoringType")); // Edoor / Emaul 구분
        String userType = StringUtil.nvl(params.get("user_type")); // 신규가입자 / 탈퇴자 구분
        String regDate = StringUtil.nvl(params.get("reg_date")); // 해당날짜를 통한검색
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        if (aptList != null) {
            params.put("aptList", aptList);
        }

        String _type = StringUtil.nvl(params.get("_type"));// 없으면 모두, 1 : 활동유저, 2 : 탈퇴유저
        if ("1".equals(_type)) {
            params.put("_active", true); // _active false는 탈퇴자, 없으면 전체
        } else if ("2".equals(_type)) {
            params.put("_active", false); // _active false는 탈퇴자, 없으면 전체
        }

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pageSize);
        params.put("pagingHelper", pagingHelper);
        Search search;

        List<SimpleUser> userList = userService.selectUserList(params);
        int totalRecordCount = userService.selectUserListCount(params);

        pagingHelper.setTotalRecordCount(totalRecordCount);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", userList);
        result.put("pagingHelper", pagingHelper);
        result.put("param", params);

        return result;
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "/jaha/user/data")
    @ResponseBody
    public SimpleUser getAptUserData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String userId = StringUtil.nvl(params.get("id"));

        SimpleUser simpleUser = null;
        List<SimpleUser> simpleUserList = userService.selectUser(params);
        if (simpleUserList != null && simpleUserList.size() > 0) {
            simpleUser = simpleUserList.get(0);
        }

        return simpleUser;

    }

    @RequestMapping(value = "/admin/user-request/list")
    public String listUserRequestAdmin(HttpServletRequest req, Model model, @PageableDefault(sort = {"id"}, direction = Direction.ASC, size = 100) Pageable pageable) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        String searchDong = req.getParameter("searchDong");
        String searchHo = req.getParameter("searchHo");
        String searchPart = req.getParameter("searchPart");
        String searchWord = req.getParameter("searchWord");
        String searchAuth = req.getParameter("searchAuth");

        Specifications<User> specs = Specifications.where(UserSpecification.aptIdEq(user.house.apt.id));
        // specs = specs.and(UserSpecification.jahaAuthNotEq(true));
        specs = specs.and(UserSpecification.AuthEq("anonymous"));
        specs = specs.and(UserSpecification.AuthNotEq("user"));
        specs = specs.and(UserSpecification.AuthNotEq("admin"));
        specs = specs.and(UserSpecification.AuthNotEq("blocked"));
        specs = specs.and(UserSpecification.AuthNotEq("deactivated"));

        if (StringUtils.isNotBlank(searchDong)) {
            specs = specs.and(UserSpecification.houseDongEq(searchDong));
        }
        if (StringUtils.isNotBlank(searchHo)) {
            specs = specs.and(UserSpecification.houseHoEq(searchHo));
        }
        if (StringUtils.isNotBlank(searchPart) && StringUtils.isNotBlank(searchWord)) {
            if ("fullName".equals(searchPart)) {
                specs = specs.and(UserSpecification.fullNameEq(searchWord));
            } else if ("phone".equals(searchPart)) {
                specs = specs.and(UserSpecification.phoneEq(searchWord));
            } else if ("email".equals(searchPart)) {
                specs = specs.and(UserSpecification.emailEq(searchWord));
            } else if ("nickname".equals(searchPart)) {
                specs = specs.and(UserSpecification.nicknameLike(searchWord));
            }
        }
        // 권한조회
        if (StringUtils.isNotBlank(searchAuth)) {
            specs = specs.and(UserSpecification.AuthEq(searchAuth));
        }

        Page<User> userList = userService.getUsersByAdmin(specs, pageable);
        PageWrapper<User> page = new PageWrapper<User>(userList, "/admin/user-request/list", req);

        model.addAttribute("page", page);

        return "admin/user-request-list";
    }

    @RequestMapping(value = "/jaha/user/history/popup")
    public String userHistoryPopup(HttpServletRequest req, @RequestParam Map<String, Object> params, Model model) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String userId = StringUtil.nvl(params.get("userId"));
        model.addAttribute("userId", userId);

        return "jaha/user-auth-history-popup";
    }

    @RequestMapping(value = "/jaha/user/history/data")
    @ResponseBody
    public Map<String, Object> userHistoryData(HttpServletRequest req, @RequestParam Map<String, Object> params, PagingHelper pagingHelper) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pageSize);
        params.put("pagingHelper", pagingHelper);

        List<UserHistory> userHistoryList = userService.selectUserHistory(params);

        for (UserHistory userHistory : userHistoryList) {

            // 권한 영문을 한글로 치환.
            Iterator<String> iter = UserType.typeMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String auth = userHistory.getAuth();
                auth = auth.replace(key, UserType.typeMap.get(key));
                userHistory.setAuth(auth);
            }

            // 히스토리 변경 사유를 한글로 치환
            String type = userHistory.getType();
            String typeKo = UserUpdateHistoryVo.typeMap.get(type);
            userHistory.setType(typeKo);
        }



        int totalRecordCount = userService.selectUserHistoryCount(params);

        pagingHelper.setTotalRecordCount(totalRecordCount);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", userHistoryList);
        result.put("pagingHelper", pagingHelper);

        return result;

    }

}
