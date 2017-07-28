package com.jaha.web.emaul.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.BoardPost;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.service.BoardService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.PhoneAuthService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.PageWrapper;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;

/**
 * Created by Administrator on 2015-04-17. it is a temporary file.
 */
@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private PhoneAuthService phoneAuthService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest req) {
        Long userId = SessionAttrs.getUserId(req.getSession());

        if (userId == null || userId == 0l) {
            return "index";
        } else {
            User user = userService.getUser(userId);

            LOGGER.debug("<<단체관리자 권한>> {}", user.type.groupAdmin);

            if (user.type.admin || user.type.jaha || user.type.groupAdmin) {
                if (user.type.groupAdmin) { // TODO: 자하 권한인 경우 제외
                    // 단체 관리자인 경우, 단체 게시판 카테고리 조회
                    List<BoardCategory> categoryList = boardService.getCategories(userId, "group", "N");

                    if (categoryList == null || categoryList.isEmpty()) {
                        return (user.type.jaha) ? "redirect:/admin/user/list" : "redirect:/user";
                    } else {
                        return "redirect:/v2/group-admin/board/group/list/" + categoryList.get(0).id;
                    }
                }

                return "redirect:/admin/user/list";
            } else {
                // AP 테스터 업체전용페이지 ( 임시로 특정계정의 페이지를 따로 view )
                if ("apmanager@jahasmart.com".equals(user.getEmail())) {
                    return "redirect:/partner/apt/ap/access/log/inspection/list";
                } else {
                    return "redirect:/user";
                }
            }
        }
    }

    @RequestMapping(value = "/api/public/web/login")
    public String login(Model model, String userEmail) {
        model.addAttribute("userEmail", userEmail);
        return "index";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(HttpServletRequest req) {
        @SuppressWarnings("unused")
        Long userId = SessionAttrs.getUserId(req.getSession());

        return "admin/admin";
    }

    @RequestMapping(value = "/join/submit", method = RequestMethod.GET)
    public String joinPage(Model model) {
        model.addAttribute("sidoList", houseService.getSidoNames());
        return "user/join";
    }

    @RequestMapping(value = "/join/agreement", method = RequestMethod.GET)
    public String agreement() {
        return "user/agreement";
    }



    @RequestMapping(value = "/jaha/fee", method = RequestMethod.GET)
    public String jahaAdminFeePage() {
        LOGGER.debug("feepage");
        return "jaha/fee-list";
    }

    @RequestMapping(value = "/jaha/sms", method = RequestMethod.GET)
    public String jahaSendSmsPage(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<UserPrepass> u = userService.getUserPrepass(user.house.apt.id);
        model.addAttribute("u", u);

        model.addAttribute("user", user);

        return "jaha/send-sms";
    }

    @RequestMapping(value = "/jaha/apt/user", method = RequestMethod.GET)
    public String jahaPutUserList() {
        return "jaha/user-put";
    }


    @RequestMapping(value = "/user/apt/info", method = RequestMethod.GET)
    public String getUserAptInfo(Model model) {
        model.addAttribute("leftSideMenu", "apt");
        return "user/apt-info";
    }

    @RequestMapping(value = "/user/apt/map", method = RequestMethod.GET)
    public String getUserAptMap(Model model) {
        model.addAttribute("leftSideMenu", "apt");
        return "user/apt-map";
    }

    @RequestMapping(value = "/user/apt/contact", method = RequestMethod.GET)
    public String getUserAptContact(Model model) {
        model.addAttribute("leftSideMenu", "apt");
        return "user/apt-contact";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String user(HttpServletRequest req, Pageable pageable, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<BoardCategory> categories = Lists.newArrayList();

        LOGGER.debug("<<공지사항 카테고리 조회>>");
        List<BoardCategory> noticeBoardCategoryList = this.boardService.getCategories(userId, "notice", "N");
        LOGGER.debug("<<커뮤니티 카테고리 조회>>");
        List<BoardCategory> communityBoardCategoryList = this.boardService.getCategories(userId, "community", "N");
        // LOGGER.debug("<<방송게시판 카테고리 조회>>");
        // List<BoardCategory> ttsBoardCategoryList = this.boardService.getCategories(userId, "tts", "N");
        // LOGGER.debug("<<민원게시판 카테고리 조회>>");
        // List<BoardCategory> complaintBoardCategoryList = this.boardService.getCategories(userId, "complaint", "N");

        categories.addAll(noticeBoardCategoryList);
        categories.addAll(communityBoardCategoryList);
        // categories.addAll(ttsBoardCategoryList);
        // categories.addAll(complaintBoardCategoryList);

        Page<BoardPost> notice = null;
        if (noticeBoardCategoryList.size() > 0) {
            notice = boardService.getPosts(user, noticeBoardCategoryList.get(0).id, new PageRequest(0, 5, Sort.Direction.DESC, "topFix", "regDate"));
        }

        Page<BoardPost> community = null;
        if (communityBoardCategoryList.size() > 0) {
            community = boardService.getPosts(user, communityBoardCategoryList.get(0).id, new PageRequest(0, 5, Sort.Direction.DESC, "regDate"));
        }

        model.addAttribute("categories", categories);
        model.addAttribute("notice", notice == null ? null : notice.getContent());
        model.addAttribute("community", community == null ? null : community.getContent());

        /* 마을뉴스 */
        BoardCategory category = boardService.getTodayCategory(user.house.apt.id, "N");

        if (category != null) {
            Long categoryId = category.id;

            // 마을뉴스
            List<BoardPost> headerPosts = boardService.getFirst4Todays(user, categoryId);
            Page<BoardPost> todayList = null;
            if (headerPosts.size() >= 4) {
                PageRequest request = new PageRequest(pageable.getPageNumber(), 12, new Sort(Sort.Direction.DESC, "regDate"));
                todayList = boardService.getPostsLessThan(user, categoryId, headerPosts.get(headerPosts.size() - 1).regDate, request, 4);
            }

            if (todayList != null) {
                // 전체
                PageWrapper<BoardPost> page = new PageWrapper<BoardPost>(todayList, "/user/board/today/list");
                model.addAttribute("page", page);
            }

            Long postSize = boardService.getPostSize(categoryId);
            model.addAttribute("postSize", postSize);

            model.addAttribute("headerPosts", headerPosts);
        }

        return "user/user-home";
    }

    @RequestMapping(value = "/user/mypage", method = RequestMethod.GET)
    public String getUserMypage(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        model.addAttribute("leftSideMenu", "mypage");
        model.addAttribute("sidoList", houseService.getSidoNames());
        return "user/mypage";
    }

    @RequestMapping(value = "/user/mypage/deactivate", method = RequestMethod.GET)
    public String getDeactivatePageForUser(Model model) {
        model.addAttribute("leftSideMenu", "mypage");
        return "user/mypage-deactivate";
    }

    @RequestMapping(value = "/admin/manual", method = RequestMethod.GET)
    public String getManualForAdmin(Model model) {
        return "admin/manual";
    }

    // ///////////////////////////////////////////////// 아이디 / 비밀번호 찾기 추가 ///////////////////////////////////////////////////
    @RequestMapping(value = "/join/id-search-form", method = RequestMethod.GET)
    public String moveIdSearchPage(Model model) {
        model.addAttribute("accountMaxCnt", Constants.PHONE_USER_ACCOUNT_MAX);
        return "user/id-search-form";
    }

    @RequestMapping(value = "/join/pw-search-form")
    public String movePwSearchPage(Model model, @RequestParam(value = "userEmail", required = false, defaultValue = "") String userEmail,
            @RequestParam(value = "userRegDate", required = false, defaultValue = "") String userRegDate) {
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("userRegDate", userRegDate);

        return "user/pw-search-form";
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 9.
     * @modifier shavrani 2016-10-20
     * @description
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/join/auth-code/req")
    @ResponseBody
    public String reqIdCheckAuthCode(@RequestBody String json) {
        LOGGER.debug("* 요청 JSON: {}", json);

        JSONObject obj = new JSONObject(json);

        String reqType = obj.getString("reqType");
        String userEmail = obj.getString("userEmail");
        String userName = obj.getString("userName");// 인증번호발송시 이름은 전달받되 활용은하지않는다.( 화면상에서 인증되는듯한 view용도 )
        String phoneNumber = obj.getString("phoneNumber");

        JSONObject ret = new JSONObject();

        List<SimpleUser> userList = null;
        try {
            userList = userService.checkUserInfo(userEmail, phoneNumber);
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        if (userList == null || userList.isEmpty()) {
            ret.put("resultCode", "01");
            ret.put("resultMsg", "USER NOT EXISTS!");
            ret.put("key", StringUtils.EMPTY);
            return ret.toString();
        }

        String code = String.format("%06d", (int) (Math.random() * 1000000));
        String key = RandomKeys.make(32);
        // 발신자번호는 비즈뿌리오에 사전 등록된 번호만 문자 전송이 가능함
        boolean tf = phoneAuthService.sendMsgNow(phoneNumber, "028670816", String.format("e마을 아이디찾기 인증번호 [%s]를 입력해주세요.", code), code, key);

        String temp = null;
        if ("id-search".equals(reqType)) {
            temp = "아이디찾기";
        } else if ("pw-search".equals(reqType)) {
            temp = "비밀번호찾기";
        }

        if (tf) {
            LOGGER.info("* {}, e마을 {} 인증번호[{}] 발송 성공", phoneNumber, temp, code);
        } else {
            LOGGER.info("* {}, e마을 {} 인증번호[{}] 발송 실패", phoneNumber, temp, code);
        }

        ret.put("resultCode", "00");
        ret.put("resultMsg", "OK");
        ret.put("key", key);

        return ret.toString();
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 9.
     * @modifier shavrani 2016-10-20
     * @description
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/join/auth-code/check", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkAuthCode(@RequestBody String json) {
        JSONObject obj = new JSONObject(json);

        String userEmail = obj.getString("userEmail");
        String phoneNumber = obj.getString("phoneNumber");
        String code = obj.getString("code");
        String key = obj.getString("key");

        Map<String, Object> result = Maps.newHashMap();

        if (phoneAuthService.checkAuth(code, key, phoneNumber)) {
            List<SimpleUser> userList = null;
            try {
                userList = userService.checkUserInfo(userEmail, phoneNumber);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            result.put("resultCode", "00");
            result.put("resultMsg", "OK");
            if (StringUtil.isBlank(userEmail)) {
                result.put("data", userList);
            } else {
                if (userList != null && !userList.isEmpty()) {
                    SimpleUser simpleUser = userList.get(0);
                    result.put("data", simpleUser);
                }
            }
        } else {
            result.put("resultCode", "99");
            result.put("resultMsg", "CODE IS WRONG!");
        }

        return result;
    }

    @RequestMapping(value = "/join/pw-reset", method = RequestMethod.POST)
    @ResponseBody
    public String resetPassword(@RequestBody String json) {
        JSONObject obj = new JSONObject(json);

        String password = obj.getString("password");
        String email = obj.getString("email");

        boolean tf = this.userService.resetPassword(password, email);

        JSONObject ret = new JSONObject();

        if (tf) {
            ret.put("resultCode", "00");
            ret.put("resultMsg", "OK");

            LOGGER.info("[{}]님의 비밀번호가 재설정되었습니다.", email);
        } else {
            ret.put("resultCode", "03");
            ret.put("resultMsg", "PASSWORD FAILS TO RESET!");
        }

        return ret.toString();
    }

    @RequestMapping(value = "/join/id-search", method = RequestMethod.POST)
    @ResponseBody
    public String searchId(@RequestBody String json) {
        JSONObject obj = new JSONObject(json);

        String email = obj.getString("email");

        User user = this.userService.getUser(email);

        JSONObject ret = new JSONObject();

        if (user == null) {
            ret.put("resultCode", "01");
            ret.put("resultMsg", "USER NOT EXISTS!");
        } else {

            if (user.type.deactivated) {
                ret.put("resultCode", "02");
                ret.put("resultMsg", "deactivated");
            } else {
                ret.put("resultCode", "00");
                ret.put("resultMsg", "OK");
            }
        }

        return ret.toString();
    }
    // ///////////////////////////////////////////////// 아이디 / 비밀번호 찾기 추가 ///////////////////////////////////////////////////

}
