package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.AptContact;
import com.jaha.web.emaul.model.AptFee;
import com.jaha.web.emaul.model.AptFeeAvr;
import com.jaha.web.emaul.model.AptFeePush;
import com.jaha.web.emaul.model.AptInfo;
import com.jaha.web.emaul.model.AptMgtCorp;
import com.jaha.web.emaul.model.AptScheduler;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserType;
import com.jaha.web.emaul.repo.AptApFirmwareLogRepository;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.GcmService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.MonitoringService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.AddressConverter;
import com.jaha.web.emaul.util.HttpUtils;
import com.jaha.web.emaul.util.Locations;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAction;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.model.user.UserUpdateHistoryVo;
import com.jaha.web.emaul.v2.service.user.UserHouseTransferLogService;
import com.jaha.web.emaul.v2.util.PushUtils;

/**
 * Created by doring on 15. 3. 30..
 */
@Controller
public class AptController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AptController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private CommonService commonService;

    @SuppressWarnings("unused")
    @Autowired
    private AptApFirmwareLogRepository aptApFirmwareLogRepository;

    @Autowired
    private GcmService gcmService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private UserHouseTransferLogService userHouseTransferLogService;
    @Autowired
    private MonitoringService monitoringService;

    // [START] 광고 푸시 추가 by realsnake 2016.09.19
    @Autowired
    private PushUtils pushUtils;

    // [END]

    @RequestMapping(value = "/api/public/apt/search", method = RequestMethod.GET)
    public @ResponseBody String searchApt(@RequestParam(value = "keyword") String keyword) {
        List<Apt> list = houseService.searchApt(keyword);

        JSONArray jsonArray = new JSONArray();
        for (Apt apt : list) {
            JSONObject obj = new JSONObject();
            obj.put("address", apt.strAddress);
            obj.put("addressOld", apt.strAddressOld);
            obj.put("name", apt.name);
            obj.put("code", apt.address.건물관리번호);
            jsonArray.put(obj);
        }

        return jsonArray.toString();
    }

    @RequestMapping(value = "/api/apt/reset", method = RequestMethod.POST)
    public @ResponseBody User resetApt(HttpServletRequest req, @RequestParam Map<String, Object> params) {
        // Long userId = SessionAttrs.getUserId(req.getSession());
        // User user = userService.getUser(userId);
        //
        // // user.house = userService.selectOrCreateHouse(addressCode, dong, ho);
        // House house = user.house;
        // Apt apt = houseService.getAptByAddressCode(addressCode);
        // house.apt = apt;
        // house.dong = dong;
        // house.ho = ho;
        // user.house = houseService.saveAndFlush(house);
        //
        // // 자하권한 이외에는 기존 권한을 모두 초기화한다.
        // if (!user.type.jaha) {
        // user.type = new UserType(user.id);
        // }
        //
        // return userService.saveAndFlush(user);

        String addressCode = StringUtil.nvl(params.get("addressCode"));
        String dong = StringUtil.nvl(params.get("dong"));
        String ho = StringUtil.nvl(params.get("ho"));

        // addressCode가 없을시 받는 parameter
        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String emdNm = StringUtil.nvl(params.get("emdNm"));
        String addressDetail = StringUtil.nvl(params.get("addressDetail"));

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // 전출 처리를 위한 변수
        Boolean wasUser = user.type.user;
        Long oldHouseId = user.house.id;
        // 전출 처리를 위한 변수

        if (StringUtil.isBlank(addressCode)) {
            // addressCode가 없으면 동단위의 address 생성후 아파트 생성
            user.house = userService.selectOrCreateAddressAndHouse(sidoNm, sggNm, emdNm);
            user.setAddressDetail(addressDetail);
        } else {
            // addressCode가 있으면 조회하여 house 생성
            user.house = userService.selectOrCreateHouse(addressCode, dong, ho);
        }

        // 자하권한 이외에는 기존 권한을 모두 초기화한다.
        if (!user.type.jaha) {

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

        }

        // 전출 처리를 위한 변수
        Boolean isAnonymous = user.type.anonymous;

        // 사용자 주소 변경 시 전출 처리
        this.userHouseTransferLogService.saveTransferOutByUser(wasUser, isAnonymous, userId, oldHouseId);

        // -- 사용자 설정변경 HISTORY --
        try {
            userService.saveUserUpdateHistory(user, user, UserUpdateHistoryVo.TYPE_CHANGE_APT, null);
        } catch (Exception e) {
            LOGGER.error(">>> 사용자 설정변경 히스토리 오류 [ 아파트변경 ]", e);
        }

        // 아파트 변경에따른 앱 초기데이터 갱신 ( 안드로이드는 리스타트하고 IOS는 초기필수데이터만 갱신한다. ) // 2017.01.19 function이 init이면 안드로이드는 메시지를 팝업으로 띄운다.
        String androidValue = "아파트가 변경되었습니다.\n앱을 재시작후 서비스를 이용하실 수 있습니다.";
        String iosValue = "아파트가 변경되었습니다.\n여기를 터치하여 앱의 초기필요정보를 갱신해주세요.";
        gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("init"), Lists.newArrayList(user), false);

        return userService.saveAndFlush(user);
    }

    public static String removeSpecialChars(String str) {
        return str.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
    }

    @RequestMapping(value = "/api/apt/fee/upload", method = RequestMethod.POST)
    public String uploadAptFeeTest(HttpServletRequest req, @RequestParam(value = "meter", required = false) Boolean isMeter, @RequestParam(value = "files[]", required = false) MultipartFile[] files)
            throws IOException {

        if (files == null) {
            LOGGER.debug("* 관리비 파일이 없음!");
            return "먼저 관리비 파일을 등록하시기 바랍니다.";
        }

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.admin && !user.type.jaha) {
            LOGGER.debug("* 관리자만 이용 가능한 메뉴!");
            return "관리자만 이용 가능한 메뉴입니다.";
        }
        if (isMeter == null) {
            isMeter = true;
        }

        Long aptId = user.house.apt.id;

        Apt apt = houseService.getApt(aptId);

        if (apt == null) {
            LOGGER.debug("* 아파트 조회 불가!");
            return "관리비 입력 중 오류가 발생했습니다.";
        }

        // List<User> aptUserList = userService.getAllAptUsers(aptId);

        Gson gson = new Gson();
        boolean isPushSended = false;
        String value = null;

        for (MultipartFile file : files) {
            byte[] buf = file.getBytes();
            String s = new String(buf, "EUC-KR");
            List<String> feeList = Splitter.on("\n").splitToList(s);
            List<String> infoColumns = Splitter.on(",").trimResults().splitToList(feeList.get(0));
            int idxAppliedDate = infoColumns.indexOf("부과년월");

            String date = Splitter.on(",").trimResults().splitToList(feeList.get(1)).get(idxAppliedDate);

            houseService.deleteAptFee(apt.id, date);

            int index = 0;
            int idxDong = -1;
            int idxHo = -1;
            Map<String, Map<String, Long>> avrMap = Maps.newHashMap();
            Map<String, Long> avrCountMap = Maps.newHashMap();

            for (String fee : feeList) {
                if (index++ == 0) {
                    idxDong = infoColumns.indexOf("동");
                    idxHo = infoColumns.indexOf("호");
                    continue;
                }

                try {
                    List<String> data = Splitter.on(",").trimResults().splitToList(fee);
                    if (data.size() == 1) {
                        continue;
                    }
                    String dong = data.get(idxDong);
                    String ho = data.get(idxHo);

                    Map<String, String> colMap = Maps.newHashMap();
                    for (int i = 0; i < infoColumns.size(); i++) {
                        String col = infoColumns.get(i);
                        String val = data.get(i);
                        col = removeSpecialChars(col);
                        if ("입주일자".equals(col)) {
                            val = val.replace("-", "");
                        }
                        colMap.put(col, val);
                    }

                    String houseSize = colMap.get("평형");
                    if (houseSize == null || houseSize.isEmpty() || "0".equals(houseSize)) {
                        continue;
                    }
                    String sizeMeter;
                    String sizePyung;
                    if (isMeter) {
                        sizeMeter = houseSize;
                        sizePyung = String.format("%d", (int) (Double.valueOf(sizeMeter) * 0.3025));
                    } else {
                        sizePyung = houseSize;
                        sizeMeter = String.format("%.3f", (float) (Double.valueOf(sizePyung) * 3.3058));
                    }

                    // 평균을 위한 sum, count
                    Set<String> colKeys = colMap.keySet();
                    for (String col : colKeys) {
                        String val = colMap.get(col);
                        Long valLong = Longs.tryParse(val);
                        if (valLong != null) {
                            Map<String, Long> exist = avrMap.get(houseSize);
                            if (exist == null) {
                                exist = Maps.newHashMap();
                            }
                            Long valSum = exist.get(col);
                            if (valSum == null) {
                                valSum = 0l;
                            }
                            exist.put(col, valSum + valLong);
                            avrMap.put(houseSize, exist);
                        }
                    }

                    Long avrCount = avrCountMap.get(houseSize);
                    avrCountMap.put(houseSize, avrCount == null ? 1 : avrCount + 1);

                    String ym = colMap.get("부과년월");
                    String mfee = colMap.get("당월부과금액");
                    removeNoNeedData(colMap);

                    // /////////////////////////////// 아파트아이디, 동, 호로 조회 시 하우스 데이터가 중복일 경우도 있으므로 수정 /////////////////////////////////
                    House house = null;

                    List<House> houseList = this.houseService.getHouseList(apt.id, dong, ho);
                    if (houseList != null && !houseList.isEmpty()) {
                        house = houseList.get(0);
                    }
                    // house = houseService.getHouse(apt.id, dong, ho);
                    // /////////////////////////////// 아파트아이디, 동, 호로 조회 시 하우스 데이터가 중복일 경우도 있으므로 수정 /////////////////////////////////

                    if (house == null) {
                        house = new House();
                        house.apt = apt;
                        house.dong = dong;
                        house.ho = ho;
                        house.sizeMeter = sizeMeter;
                        house.sizePyung = sizePyung;
                        house = houseService.saveAndFlush(house);

                        LOGGER.info("[아파트하우스정보추가] 아파트아이디: {}, 아파트명: {}, 동: {}, 호: {}", house.apt.id, house.apt.name, dong, ho);
                    } else {
                        if (house.sizeMeter == null || house.sizeMeter.isEmpty()) {
                            house.sizeMeter = sizeMeter;
                            house.sizePyung = sizePyung;

                            house = houseService.saveAndFlush(house);

                            LOGGER.info("[아파트하우스정보수정] 아파트아이디: {}, 아파트명: {}, 동: {}, 호: {}", house.apt.id, house.apt.name, dong, ho);
                        }
                    }

                    AptFee aptFee = new AptFee();
                    aptFee.date = date;
                    aptFee.house = house;
                    aptFee.json = gson.toJson(colMap);

                    houseService.save(aptFee);

                    // /////////////////////////////////////////////////////// 관리비 푸시알림 추가(전강욱, 20160601) ///////////////////////////////////////////////////////////
                    String pushSendYn = req.getParameter("pushSendYn");

                    if ("Y".equals(pushSendYn)) {
                        // final House compHouse = house;
                        //
                        // List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(aptUserList, new Predicate<User>() {
                        // @Override
                        // public boolean apply(User input) {
                        // return (compHouse.id.equals(input.house.id) && input.setting.notiFeePush);
                        // }
                        // }));
                        // List<Long> pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);
                        //
                        // if (pushApplyUserIds != null && pushApplyUserIds.size() > 0) {
                        // String pushMessage = "%s의 %s 아파트 관리비 %,d원이 부과되었습니다. 세부내용은 우리아파트앱 \"이마을\"에서 확인하세요.";
                        // value = String.format(pushMessage, house.dong + "동 " + house.ho + "호", ym.substring(2, 4) + "년 " + ym.substring(4, 6) + "월", Integer.valueOf(mfee));
                        //
                        // GcmSendForm form = new GcmSendForm();
                        // Map<String, String> msg = Maps.newHashMap();
                        // msg.put("type", "action");
                        // // msg.put("title", "관리비 푸시알림");
                        // msg.put("value", value);
                        // msg.put("action", "emaul://aptfee");
                        // form.setUserIds(pushApplyUserIds);
                        // form.setMessage(msg);
                        //
                        // this.gcmService.sendGcm(form);
                        //
                        // LOGGER.info("[관리비 부과 푸시 발송] " + value);
                        // isPushSended = true;
                        // }

                        // ////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
                        List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.HOUSE, PushAlarmSetting.FEE, Lists.newArrayList(house.id));
                        value = String.format(PushMessage.FEE.getValue(), house.dong + "동 " + house.ho + "호", ym.substring(2, 4) + "년 " + ym.substring(4, 6) + "월", Integer.valueOf(mfee));

                        this.pushUtils.sendPush(PushGubun.FEE, house.apt.name + " 관리비 알림", value, PushAction.FEE.getValue(), null, false, targetUserList);
                        // ////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////

                        isPushSended = true;
                    }
                    // /////////////////////////////////////////////////////// 관리비 푸시알림 추가(전강욱, 20160601) ///////////////////////////////////////////////////////////
                } catch (Exception e) {
                    LOGGER.error("<<아파트 관리비 등록 중 오류>>", e);
                }
            }

            // avrMap, avrCountMap을 이용해서 평균 구함
            Set<String> roomSizeKeys = avrMap.keySet();
            for (String roomSizeKey : roomSizeKeys) {
                if (roomSizeKey == null || roomSizeKey.isEmpty() || "0".equals(roomSizeKey)) {
                    continue;
                }
                Map<String, String> avrColMap = Maps.newHashMap();
                Map<String, Long> map = avrMap.get(roomSizeKey);

                Set<String> colKeys = map.keySet();
                for (String colKey : colKeys) {
                    Long sumVal = map.get(colKey);
                    Long divVal = avrCountMap.get(roomSizeKey);
                    avrColMap.put(colKey, divVal == 0 ? "0" : String.valueOf(sumVal / divVal));
                }

                removeNoNeedDataForAvr(avrColMap);
                AptFeeAvr aptFeeAvr = houseService.getAptFeeAvr(aptId, date, roomSizeKey);
                if (aptFeeAvr == null) {
                    aptFeeAvr = new AptFeeAvr();
                }
                aptFeeAvr.aptId = aptId;
                aptFeeAvr.date = date;

                String sizeMeter;
                if (isMeter) {
                    sizeMeter = roomSizeKey;
                } else {
                    sizeMeter = String.format("%.3f", (float) (Double.valueOf(roomSizeKey) * 3.3058));
                }
                aptFeeAvr.houseSize = sizeMeter;
                aptFeeAvr.json = gson.toJson(avrColMap);

                houseService.save(aptFeeAvr);
            }
        }

        if (isPushSended) {
            AptFeePush aptFeePush = new AptFeePush();
            // long userId = SessionAttrs.getUserId(req.getSession());
            // User user = userService.getUser(userId);

            aptFeePush.setUser(user);
            aptFeePush.setApt(user.house.apt);
            aptFeePush.setBookYn("N");
            aptFeePush.setContents(value);
            aptFeePush.setGubun("1");
            aptFeePush.setSendDate(Constants.DEFAULT_SDF.format(new Date()));
            aptFeePush.setSendYn("Y");

            this.houseService.addAptFeePush(aptFeePush);
        }

        return "redirect:/admin/fee/list";
    }

    private void removeNoNeedData(Map<String, String> colMap) {
        colMap.remove("");
        colMap.remove("부과년월");
        colMap.remove("동");
        colMap.remove("호");
        colMap.remove("평형");
        colMap.remove("아파트명");
        colMap.remove("이지스단지코드");
        colMap.remove("성명");
        colMap.remove("주거");
        colMap.remove("구분");
        colMap.remove("가스전월지침");
        colMap.remove("난방전월지침");
        colMap.remove("수도전월지침");
        colMap.remove("온수전월지침");
        colMap.remove("전기전월지침");
    }

    private void removeNoNeedDataForAvr(Map<String, String> colMap) {
        removeNoNeedData(colMap);

        colMap.remove("납기내");
        colMap.remove("납기후");
        colMap.remove("납기후연체료");
        colMap.remove("당월후연체료");
        colMap.remove("미납금액");
        colMap.remove("미납연체");
        colMap.remove("자동이체");
        colMap.remove("입주일자");
    }


    @RequestMapping(value = "/api/apt/fee/list", method = RequestMethod.GET)
    public @ResponseBody List<AptFee> getAptFeeList(HttpServletRequest req) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (user.house == null || user.house.apt == null) {
            return null;
        }

        if (user.house.apt.id == 1 || user.type.admin || user.type.jaha || user.type.user) {
            return houseService.getAptFeeList(user.house.id);
        }

        return null;
    }

    @RequestMapping(value = "/api/apt/fee/avr/{date}", method = RequestMethod.GET)
    public @ResponseBody AptFeeAvr getAptFeeAvr(HttpServletRequest req, @PathVariable("date") String date) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (user.house == null || user.house.apt == null) {
            return null;
        }

        return houseService.getAptFeeAvr(user.house.apt.id, date, user.house.sizeMeter);
    }


    @RequestMapping(value = "/admin/fee/list", method = RequestMethod.GET)
    public String getFeeListPage(HttpServletRequest req, Model model) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<String> registeredDate = houseService.getAptFeeRegisteredDate(user.house.apt.id);

        model.addAttribute("date", registeredDate);

        return "admin/fee";
    }

    @SuppressWarnings("serial")
    @RequestMapping(value = "/admin/fee/{dong}/{ho}", method = RequestMethod.GET)
    public String getFeeForAdmin(@PathVariable("dong") String dong, @PathVariable("ho") String ho, HttpServletRequest req, Model model) {

        if (dong.equals("0")) {
            model.addAttribute("none", "none");
            return "admin/fee-view";
        }

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        // House house = houseService.getHouse(user.house.apt.id, dong, ho);
        List<House> houseList = this.houseService.getHouseList(user.house.apt.id, dong, ho);

        List<AptFee> list = null;
        if (houseList != null && !houseList.isEmpty()) {
            House house = houseList.get(0);

            list = houseService.getAptFeeList(house.id);

            if (!list.isEmpty()) {
                AptFee latestFee = list.get(0);
                Map<String, String> tempMap = new Gson().fromJson(latestFee.json, new TypeToken<Map<String, String>>() {}.getType());
                Set<String> keySet = tempMap.keySet();
                model.addAttribute("keySet", keySet);

                List<String> jsonList = Lists.transform(list, new Function<AptFee, String>() {
                    @Override
                    public String apply(AptFee input) {
                        return input.json;
                    }
                });
                model.addAttribute("jsonList", jsonList);
            }
        }

        model.addAttribute("list", list);
        String houseInfo = dong + "동 " + ho + "호";
        model.addAttribute("house", houseInfo);

        return "admin/fee-view";
    }

    @RequestMapping(value = "/jaha/apt", method = RequestMethod.GET)
    public String jahaAptPage(HttpServletRequest req, Model model, @RequestParam(value = "registered", required = false, defaultValue = "0") String registered,
            @RequestParam(value = "search", required = false) String search) {
        List<Apt> list = houseService.jahaGetAptList(registered);
        model.addAttribute("list", list);
        model.addAttribute("registered", registered);
        model.addAttribute("search", search);
        return "jaha/apt-list";
    }


    @RequestMapping(value = "/jaha/apt/create", method = RequestMethod.POST)
    public @ResponseBody String jahaCreateAptCustomer(@RequestParam(value = "addressCode") String addressCode) {

        if (houseService.aptExist(addressCode)) {
            return "EXIST";
        } else {
            houseService.jahaNewApt(addressCode);
            return "DONE";
        }
    }

    @RequestMapping(value = "/jaha/apt/register", method = RequestMethod.PUT)
    public @ResponseBody String jahaRegisterApt(HttpServletRequest req, @RequestBody String json) throws IOException {
        Long id = new JSONObject(json).getLong("id");
        Boolean registerBit = new JSONObject(json).getBoolean("register");

        Apt apt = houseService.getApt(id);
        if (registerBit) {
            apt.registeredApt = true;
        } else {
            apt.registeredApt = false;
        }

        // 미가입 아파트일 경우 위경도 좌표가 없는 경우가 많으므로 체크 후 없으면 업데이트
        if (apt.latitude == null || apt.longitude == null) {
            Locations.LatLng latLng = Locations.getLocationFromAddress(AddressConverter.toStringAddressOld(apt.address));
            if (latLng != null) {
                apt.latitude = latLng.lat;
                apt.longitude = latLng.lng;
            }
        }

        houseService.save(apt);

        return "";
    }

    @RequestMapping(value = "/jaha/apt/house/temp/{aptId}", method = RequestMethod.GET)
    public @ResponseBody String jahaRegisterApt(HttpServletRequest req, @PathVariable(value = "aptId") Long aptId) throws IOException {


        return "";
    }

    @RequestMapping(value = "/admin/apt/info", method = RequestMethod.GET)
    public String getAptInfoPage(HttpServletRequest req, Model model) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Apt apt = houseService.getApt(user.house.apt.id);
        AptMgtCorp aptMgtCorp = houseService.getAptMgtCorp(apt.id);

        model.addAttribute("aptInfo", apt.aptInfo);
        model.addAttribute("aptMgtCorp", aptMgtCorp);

        return "admin/apt-info";
    }

    @RequestMapping(value = "/admin/apt/info/update/basic", method = RequestMethod.POST)
    public String updateAptInfo(HttpServletRequest req, @RequestParam(value = "introduce", required = false) String introduce,
            @RequestParam(value = "aptOfficePhoneNumber", required = false) String aptOfficePhoneNumber, @RequestParam(value = "trafficInfo", required = false) String trafficInfo,
            @RequestParam(value = "trafficBusInfo", required = false) String trafficBusInfo, @RequestParam(value = "naverClientId", required = false) String naverClientId,
            @RequestParam(value = "naverClientSecret", required = false) String naverClientSecret, @RequestParam(value = "greetingOccupant", required = false) String greetingOccupant,
            @RequestParam(value = "greetingOffice", required = false) String greetingOffice, @RequestParam(value = "greetingSenior", required = false) String greetingSenior,
            @RequestParam(value = "displayAddress", required = false) String displayAddress, @RequestParam(value = "aptOfficeFaxNumber", required = false) String aptOfficeFaxNumber) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Apt apt = user.house.apt;
        AptInfo info = apt.aptInfo;

        if (info == null) {
            info = new AptInfo();
        }
        info.introduce = introduce;
        info.aptOfficePhoneNumber = aptOfficePhoneNumber;
        info.aptOfficeFaxNumber = aptOfficeFaxNumber;
        info.trafficInfo = trafficInfo;
        info.trafficBusInfo = trafficBusInfo;
        info.naverClientId = naverClientId;
        info.naverClientSecret = naverClientSecret;
        info.greetingOccupant = StringUtil.nvl(greetingOccupant, "");
        info.greetingOffice = StringUtil.nvl(greetingOffice, "");
        info.greetingSenior = StringUtil.nvl(greetingSenior, "");
        info.displayAddress = StringUtil.nvl(displayAddress, "");

        apt.aptInfo = houseService.saveAndFlush(info);
        apt.aptOfficePhoneNumberInner = aptOfficePhoneNumber;
        houseService.save(apt);

        return "redirect:/admin/apt/info";
    }

    @RequestMapping(value = "/admin/apt/info/update/contacts", method = RequestMethod.POST)
    public String updateAptInfo(HttpServletRequest req) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Apt apt = user.house.apt;

        AptInfo info = apt.aptInfo;
        if (info == null) {
            info = new AptInfo();
        }
        info = houseService.saveAndFlush(info);
        apt.aptInfo = info;

        houseService.deleteAptInfoContacts(info.contacts);
        info.contacts = Lists.newArrayList();

        String[] contactName = req.getParameterValues("contactName");
        String[] contactPhoneNumber = req.getParameterValues("contactPhoneNumber");

        for (int i = 0; i < 50; i++) {
            try {
                String name = contactName[i];
                String number = contactPhoneNumber[i];

                if (!name.isEmpty() && !number.isEmpty()) {
                    AptContact contact = new AptContact();
                    contact.name = name;
                    contact.phoneNumber = number;
                    info.contacts.add(contact);
                }
            } catch (Exception e) {
                // do nothing
            }
        }

        houseService.saveAndFlush(info);
        houseService.save(apt);

        return "redirect:/admin/apt/info";
    }

    @RequestMapping(value = "/admin/apt/info/update/logo", method = RequestMethod.POST)
    public String updateAptInfoLogo(HttpServletRequest req, @RequestParam(value = "aptId") Long aptId, @RequestParam(value = "file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                File dir = new File(String.format("/nas/EMaul/apt/image/logo/%s", aptId));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filename = file.getOriginalFilename();
                File dest = new File(dir, filename);
                dest.createNewFile();
                file.transferTo(dest);

                houseService.updateAptLogo(aptId, "/api/apt/image/logo/" + aptId + "/" + filename);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        return "redirect:/admin/apt/info";
    }

    @RequestMapping(value = "/admin/apt/info/update/photo", method = RequestMethod.POST)
    public String updateAptInfoPhoto(HttpServletRequest req, @RequestParam(value = "aptId") Long aptId, @RequestParam(value = "file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                File dir = new File(String.format("/nas/EMaul/apt/image/mainPhoto/%s", aptId));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filename = file.getOriginalFilename();
                File dest = new File(dir, filename);
                dest.createNewFile();
                file.transferTo(dest);

                houseService.updateAptPhoto(aptId, "/api/apt/image/mainPhoto/" + aptId + "/" + filename);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        return "redirect:/admin/apt/info";
    }

    @RequestMapping(value = "/api/apt/image/{imgType}/{aptId}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleFileRequest(@PathVariable("aptId") String aptId, @PathVariable("imgType") String imgType, @PathVariable("fileName") String fileName) {

        File toServeUp = new File("/nas/EMaul/apt/image", String.format("/%s/%s/%s", imgType, aptId, fileName));

        return Responses.getFileEntity(toServeUp, fileName);
    }

    @SuppressWarnings({"rawtypes", "serial", "unchecked"})
    @RequestMapping(value = "/user/apt/fee", method = RequestMethod.GET)
    public String getAptFeeForUser(HttpServletRequest req, Model model) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<AptFee> list = null; // houseService.getAptFeeList(user.house.id);

        if (user.house.apt.id == 1) {
            list = houseService.getAptFeeList(user.house.id);
        } else if (user.type.admin || user.type.jaha || user.type.user || user.type.houseHost) {
            List<AptFee> feeList = houseService.getAptFeeList(user.house.id);

            if (feeList == null || feeList.isEmpty()) {
                list = Lists.newArrayList();
            } else {
                AptFee latestFee = feeList.get(0);
                JSONObject jsonObject = new JSONObject(latestFee.json);
                String latestEnterHouseDate = jsonObject.optString("입주일자");
                if (latestEnterHouseDate == null) {
                    list = feeList;
                } else {
                    List<AptFee> ret = Lists.newArrayList();
                    for (AptFee aptFee : feeList) {
                        if (aptFee.date.compareTo(latestEnterHouseDate) >= 0) { // 아파트 관리비 부과월이 입주일자보다 큰 경우
                            ret.add(aptFee);
                        } else {
                            break;
                        }
                    }

                    list = ret;
                }
            }
        }

        if (!list.isEmpty()) {
            // 아파트 관리비 항목이 변경된 경우를 대비해서 전월 데이터와 합쳐서 각 월별로 keySet을 생성
            List keySetList = new ArrayList<>();
            int total = list.size();

            for (int i = 0; i < total; i++) {
                AptFee aptFee = list.get(i);

                if (aptFee != null) {
                    Map<String, String> aptFeeMap = new Gson().fromJson(aptFee.json, new TypeToken<Map<String, String>>() {}.getType());
                    Set<String> aptFeeKeySet = aptFeeMap.keySet();

                    if (i < total - 1) {
                        AptFee beforeAptFee = list.get(i + 1);
                        Map<String, String> beforeAptMap = new Gson().fromJson(beforeAptFee.json, new TypeToken<Map<String, String>>() {}.getType());
                        Set<String> beforeKeySet = beforeAptMap.keySet();

                        aptFeeKeySet.containsAll(beforeKeySet);
                    }

                    List tempList = new ArrayList(aptFeeKeySet);
                    Collections.sort(tempList);

                    keySetList.add(tempList);
                }
            }

            model.addAttribute("keySetList", keySetList);

            List<String> jsonList = Lists.transform(list, new Function<AptFee, String>() {
                @Override
                public String apply(AptFee input) {
                    return input.json;
                }
            });
            model.addAttribute("jsonList", jsonList);
        }

        model.addAttribute("list", list);
        model.addAttribute("leftSideMenu", "fee");

        return "user/fee-default";
    }

    @SuppressWarnings("serial")
    @RequestMapping(value = "/user/apt/fee/comparison", method = RequestMethod.GET)
    public String getAptFeeComparisonForUser(HttpServletRequest req, Model model) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<AptFeeAvr> avrList = houseService.getAptFeeAvrList(user.house.apt.id, user.house.sizeMeter);

        List<AptFee> list = houseService.getAptFeeList(user.house.id);

        if (!list.isEmpty()) {
            AptFee latestFee = list.get(0);
            Map<String, String> tempMap = new Gson().fromJson(latestFee.json, new TypeToken<Map<String, String>>() {}.getType());
            Set<String> keySet = tempMap.keySet();
            model.addAttribute("keySet", keySet);

            List<String> jsonList = Lists.transform(list, new Function<AptFee, String>() {
                @Override
                public String apply(AptFee input) {
                    return input.json;
                }
            });
            model.addAttribute("jsonList", jsonList);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < 5; i++) {
                try {
                    AptFee af = list.get(i);
                    Map<String, String> tempMap2 = new Gson().fromJson(af.json, new TypeToken<Map<String, String>>() {}.getType());
                    JSONObject obj = new JSONObject();
                    obj.put("label", af.date);
                    obj.put("data", tempMap2.get("당월부과금액"));
                    jsonArray.put(obj);
                } catch (Exception e) {
                    LOGGER.error("[관리비 전월비교 오류]", e);
                }
            }
            model.addAttribute("forChart", jsonArray.toString());
        }

        if (!avrList.isEmpty()) {
            List<String> jsonAvrList = Lists.transform(avrList, new Function<AptFeeAvr, String>() {
                @Override
                public String apply(AptFeeAvr input) {
                    return input.json;
                }
            });
            model.addAttribute("jsonAvrList", jsonAvrList);
        }

        model.addAttribute("list", list);
        model.addAttribute("leftSideMenu", "fee");


        return "user/fee-comparison";
    }

    /**
     * 관리비 > 관리비 푸시알림 목록화면 이동
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti-list", method = RequestMethod.GET)
    public String getFeePushNotiList(Model model, HttpServletRequest req) {

        String pageNum = StringUtils.defaultString(req.getParameter("pageNum"), "1");

        // User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        // model.addAttribute("user", user);

        model.addAttribute("pageNum", pageNum);

        return "admin/fee-push-noti-list";
    }

    /**
     * 관리비 > 관리비 푸시알림 목록화면 JSON
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti-list-json")
    @ResponseBody
    public Map<String, Object> getFeePushNotiListJson(Model model, HttpServletRequest req) {
        String pageNum = StringUtils.defaultString(req.getParameter("pageNum"), "1");
        int pageNumber = Integer.parseInt(pageNum) - 1;

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Pageable pageable = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        Page<AptFeePush> aptFeePushPage = this.houseService.getAptFeePushList(user.house.apt.id, pageable);

        // LOGGER.debug("* user.house.apt.id: " + user.house.apt.id);
        // LOGGER.debug("* aptFeePushPage.getSize(): " + aptFeePushPage.getSize());
        // LOGGER.debug("* aptFeePushPage.getNumber(): " + aptFeePushPage.getNumber());
        // LOGGER.debug("* aptFeePushPage.getNumberOfElements(): " +
        // aptFeePushPage.getNumberOfElements());
        // LOGGER.debug("* aptFeePushPage.getTotalElements(): " +
        // aptFeePushPage.getTotalElements());
        // LOGGER.debug("* aptFeePushPage.getTotalPages(): " + aptFeePushPage.getTotalPages());

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("size", aptFeePushPage.getSize());
        jsonMap.put("totalPage", aptFeePushPage.getTotalPages());
        jsonMap.put("totalRecord", aptFeePushPage.getTotalElements());
        jsonMap.put("list", aptFeePushPage.getContent());

        return jsonMap;
    }

    /**
     * 관리비 > 관리비 푸시알림 등록 또는 수정 화면 이동
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti-form")
    public String getFeePushNotiForm(Model model, HttpServletRequest req) {
        String id = StringUtils.defaultString(req.getParameter("id"), "0");

        if ("0".equals(id) || !StringUtils.isNumeric(id)) {
            model.addAttribute("id", null);
        } else {
            model.addAttribute("id", id);
        }

        return "admin/fee-push-noti-form";
    }

    /**
     * 관리비 > 관리비 푸시알림 등록 또는 수정 화면 JSON
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti-form-json")
    @ResponseBody
    public AptFeePush getFeePushNotiFormJson(Model model, HttpServletRequest req) {
        String id = StringUtils.defaultString(req.getParameter("id"), "0");
        return this.houseService.getAptFeePush(Long.parseLong(id));
    }

    /**
     * 관리비 > 관리비 푸시알림 등록
     *
     * @param model
     * @param req
     * @parma aptFeePush
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti/add")
    @ResponseBody
    public AptFeePush addFeePushNoti(Model model, HttpServletRequest req, AptFeePush aptFeePush) {
        long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        aptFeePush.setUser(user);
        aptFeePush.setApt(user.house.apt);

        // LOGGER.debug(aptFeePush.toString());

        return this.houseService.addAptFeePush(aptFeePush);
    }

    /**
     * 관리비 > 관리비 푸시알림 삭제
     *
     * @param model
     * @param req
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti/remove")
    @ResponseBody
    public AptFeePush removeFeePushNoti(Model model, HttpServletRequest req, Long id) {
        return this.houseService.removeAptFeePush(id);
    }

    /**
     * 관리비 > 관리비 푸시알림 수정
     *
     * @param model
     * @param req
     * @param aptFeePush
     * @return
     */
    @RequestMapping(value = "/admin/fee/push-noti/modify")
    @ResponseBody
    public AptFeePush modifyFeePushNoti(Model model, HttpServletRequest req, AptFeePush aptFeePush) {
        long userId = SessionAttrs.getUserId(req.getSession());
        aptFeePush.setModId(userId);

        LOGGER.debug(aptFeePush.toString());

        return this.houseService.modifyAptFeePush(aptFeePush);
    }

    @RequestMapping(value = "/admin/apt/scheduler/list", method = RequestMethod.GET)
    public String listAptSchedulerListAdmin(HttpServletRequest req, Model model) {
        return "admin/apt-scheduler-list";
    }

    @RequestMapping(value = "/admin/apt/scheduler/listData")
    @ResponseBody
    public String listAptSchedulerListDataAdmin(HttpServletRequest req, Model model, @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
            @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "searchItem", required = false) String searchItem, @RequestParam(value = "searchKeyWord", required = false) String searchKeyWord) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> result = houseService.getAptScheduler(user, pageNum, startDate, endDate, searchItem, searchKeyWord);

        JSONObject jo = new JSONObject(result);

        /*
         * //데이터 가공이 필요할경우 주석제거후 사용 List<AptScheduler> aptSchedulerList = (List<AptScheduler>)result.get("dataList"); JSONArray jsonArray = new JSONArray(); for (AptScheduler aptScheduler :
         * aptSchedulerList) { JSONObject obj = new JSONObject(); obj.put("id", aptScheduler.id); obj.put("notice_target", aptScheduler.notice_target); obj.put("notice_target_dong",
         * aptScheduler.notice_target_dong); obj.put("notice_target_ho", aptScheduler.notice_target_ho); obj.put("start_date", aptScheduler.start_date); obj.put("start_time", aptScheduler.start_time);
         * obj.put("end_date", aptScheduler.end_date); obj.put("end_time", aptScheduler.end_time); obj.put("title", aptScheduler.title); obj.put("content", aptScheduler.content);
         * obj.put("push_timing", aptScheduler.push_timing); obj.put("exposure_timing", aptScheduler.exposure_timing); obj.put("status", aptScheduler.status); obj.put("reg_id", aptScheduler.reg_id);
         * jsonArray.put(obj); }
         */

        return jo.toString();
    }

    @RequestMapping(value = "/admin/apt/scheduler/form", method = RequestMethod.GET)
    public String aptSchedulerFormAdmin(HttpServletRequest req, Model model, AptScheduler aptScheduler) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<String> dongs = houseService.getDongs(user.house.apt.id);

        model.addAttribute("noticeTargetList", commonService.findByCodeGroup("APT_SCH_NT"));
        model.addAttribute("dongs", dongs);
        model.addAttribute("id", aptScheduler.id);

        return "admin/apt-scheduler-form";
    }

    @RequestMapping(value = "/admin/apt/scheduler/form-data")
    @ResponseBody
    public String aptSchedulerFormDataAdmin(HttpServletRequest req, Model model, AptScheduler aptScheduler) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        AptScheduler result = houseService.getAptScheduler(aptScheduler.id, user.house.apt.id);
        if (result != null) {
            JSONObject jo = new JSONObject();

            if ("1".equals(result.pushTiming)) {
                jo.put("push_data", new JSONArray(commonService.getBatchScheduleGroup(result.getAptSchedulerPushBatchGroupKey())));
            }
            if ("1".equals(result.exposureTiming)) {
                jo.put("exposure_data", new JSONArray(commonService.getBatchScheduleGroup(result.getAptSchedulerExposureBatchGroupKey())));
            }

            jo.put("data", new JSONObject(result));

            return jo.toString();
        }
        return "{}";
    }

    @RequestMapping(value = "/admin/apt/dongs", method = RequestMethod.GET)
    @ResponseBody
    public String getTargetDong(HttpServletRequest req, @RequestParam(value = "aptId", required = false) Long aptId) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<String> dongs = houseService.getDongs(StringUtil.nvlLong(aptId, user.house.apt.id));

        List<String> temps1 = dongs.stream().filter(a -> StringUtils.isNumeric(a)).collect(Collectors.toList());
        List<String> temps2 = dongs.stream().filter(a -> !StringUtils.isNumeric(a)).collect(Collectors.toList());

        /**
         * hos에 문자도 있어서 compareTo할수없으므로 list를 숫자인거과 문자인거 분리해서 숫자인것만 compareTo처리로 sorting하고 문자열은 그냥 sorting 처리한다.
         */
        Collections.sort(temps1, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer lhs = Integer.valueOf(o1);
                Integer rhs = Integer.valueOf(o2);

                return Integer.valueOf(lhs / 100).compareTo(rhs / 100);
            }
        });

        Collections.sort(temps2);

        temps1.addAll(temps2);

        return new JSONArray(temps1).toString();
    }

    @RequestMapping(value = "/admin/apt/hos", method = RequestMethod.GET)
    @ResponseBody
    public String getTargetHo(HttpServletRequest req, @RequestParam(value = "aptId", required = false) Long aptId, @RequestParam(value = "dong", required = false) String dong) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<String> hos = houseService.getHos(StringUtil.nvlLong(aptId, user.house.apt.id), dong);

        List<String> tempHos1 = hos.stream().filter(a -> StringUtils.isNumeric(a)).collect(Collectors.toList());
        List<String> tempHos2 = hos.stream().filter(a -> !StringUtils.isNumeric(a)).collect(Collectors.toList());

        /**
         * hos에 문자도 있어서 compareTo할수없으므로 list를 숫자인거과 문자인거 분리해서 숫자인것만 compareTo처리로 sorting하고 문자열은 그냥 sorting 처리한다.
         */
        Collections.sort(tempHos1, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer lhs = Integer.valueOf(o1);
                Integer rhs = Integer.valueOf(o2);

                return Integer.valueOf(lhs / 100).compareTo(rhs / 100);
            }
        });

        Collections.sort(tempHos2);

        tempHos1.addAll(tempHos2);

        return new JSONArray(tempHos1).toString();
    }

    @RequestMapping(value = "/admin/apt/scheduler/save")
    @ResponseBody
    public String aptSchedulerSaveAdmin(HttpServletRequest req, AptScheduler aptScheduler) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        AptScheduler result = houseService.saveAptScheduler(req, user, aptScheduler);

        return String.valueOf(result.id);
    }

    @RequestMapping(value = "/admin/apt/scheduler/delete")
    @ResponseBody
    public String aptSchedulerDeleteAdmin(HttpServletRequest req, AptScheduler aptScheduler) {

        // User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        int result = houseService.deleteAptScheduler(aptScheduler);

        return String.valueOf(result);
    }

    // BatchSchedule에서 호출
    @RequestMapping(value = "/api/public/apt/scheduler/pushBatch")
    @ResponseBody
    public String aptSchedulerPushBatch(HttpServletRequest req, @RequestParam String id) {
        if (!StringUtils.isEmpty(id)) {
            AptScheduler aptScheduler = houseService.getAptScheduler(Long.parseLong(id));
            houseService.sendGcmAptScheduler(aptScheduler);
        }
        return "";
    }

    // BatchSchedule에서 호출
    @RequestMapping(value = "/api/public/apt/scheduler/exposureBatch")
    @ResponseBody
    public String aptSchedulerExposureBatch(HttpServletRequest req, @RequestParam String id) {
        if (!StringUtils.isEmpty(id)) {
            houseService.updateAptScheduleStatus(Long.parseLong(id));
        }
        return "";
    }


    @RequestMapping(value = "/admin/apt/mgt/update/corp", method = RequestMethod.POST)
    public String updateAptMgtCorp(HttpServletRequest req) {
        try {

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            Apt apt = user.house.apt;
            AptMgtCorp aptMgtCorp = houseService.getAptMgtCorp(apt.id);
            String mgtName = req.getParameter("mgtName");
            String mgtTel = req.getParameter("mgtTel");

            if (aptMgtCorp == null) {
                aptMgtCorp = new AptMgtCorp();
                aptMgtCorp.regDate = new Date();
                aptMgtCorp.regId = user.id;
                aptMgtCorp.aptId = apt.id;
            } else {
                aptMgtCorp.modDate = new Date();
                aptMgtCorp.modId = user.id;
            }
            aptMgtCorp.name = mgtName;
            aptMgtCorp.tel = mgtTel;

            houseService.saveAndFlush(aptMgtCorp);
        } catch (Exception e) {
            LOGGER.info("관리업체 업데이트 에러" + e);
        }
        return "redirect:/admin/apt/info";
    }

    @RequestMapping(value = "/apt/search/popup")
    public String getAptSearchPopup(Model model, HttpServletRequest req, @RequestParam(value = "mode", required = false) String mode) {
        model.addAttribute("sidoList", houseService.getSidoNames());
        model.addAttribute("mode", mode);
        return "user/apt-search-popup";
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "/apt/search/list-data")
    @ResponseBody
    public List<Map<String, Object>> getSearchAptList(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        List<Map<String, Object>> result = commonService.selectAddressAptList(params);

        return result;
    }

    @RequestMapping(value = "/api/public/apt/search/all-list-data")
    @ResponseBody
    public List<Map<String, Object>> getSearchAllAptList(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        if (StringUtil.isBlank(sidoNm) || StringUtil.isBlank(sggNm)) {
            LOGGER.info("<< /api/public/apt/search/all-list-data , required parameter is empty !! >>");
        }
        params.put("type", "EXCEPT_JOIN");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);
        List<Map<String, Object>> result = houseService.searchAptAll(params);

        return result;
    }

}
