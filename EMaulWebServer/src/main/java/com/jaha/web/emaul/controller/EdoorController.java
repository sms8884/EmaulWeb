package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.exception.EmaulWebException;
import com.jaha.web.emaul.model.AptAp;
import com.jaha.web.emaul.model.AptApAccessAuth;
import com.jaha.web.emaul.model.AptApAccessDevice;
import com.jaha.web.emaul.model.AptApAccessLog;
import com.jaha.web.emaul.model.AptApMonitoringNoti;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.EdoorService;
import com.jaha.web.emaul.service.GcmService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.ExcelFileBuilder;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.Util;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * @author shavrani
 * @since 2016. 9. 2.
 * @version 1.0
 */
@Controller
public class EdoorController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private GcmService gcmService;
    @Autowired
    private EdoorService edoorService;
    @Autowired
    private HouseService houseService;


    /**
     * 자하권한 AP 목록
     *
     * @author shavrani 2016.10.05
     */
    @RequestMapping(value = "/jaha/apt/ap/list")
    public String getAptApList(Model model, HttpServletRequest req) {

        List<CommonCode> operationModeList = commonService.findByCodeGroup("AP_OPERATION_MODE");
        List<CommonCode> statusList = commonService.findByCodeGroup("AP_STATUS");
        model.addAttribute("operationModeList", operationModeList);
        model.addAttribute("statusList", statusList);

        return "jaha/apt-ap-list";
    }

    /**
     * 관리자권한 AP 목록
     *
     * @author shavrani 2016.09.28
     */
    @RequestMapping(value = "/admin/apt/ap/list")
    public String getAdminAptApList(Model model, HttpServletRequest req) {
        return "admin/apt-ap-list";
    }

    private List<Map<String, Object>> getParamApIds(String ids) {
        String apIds = StringUtil.nvl(ids);
        String[] arrApId = apIds.split(",");
        List<Map<String, Object>> idSearchList = new ArrayList<Map<String, Object>>();
        if (arrApId.length > 0) {
            for (int i = 0; i < arrApId.length; i++) {
                String apId = arrApId[i];
                String[] arrApId2 = apId.split("-");
                Map<String, Object> idSearchMap = new HashMap<String, Object>();
                if (arrApId2.length == 1) {
                    String id1 = StringUtil.trim(arrApId2[0]);
                    if (StringUtil.isNumeric(id1)) {
                        idSearchMap.put("id1", id1);
                        idSearchMap.put("type", "one");
                        idSearchList.add(idSearchMap);
                    }
                } else if (arrApId2.length == 2) {
                    String id1 = StringUtil.trim(arrApId2[0]);
                    String id2 = StringUtil.trim(arrApId2[1]);
                    if (StringUtil.isNumeric(id1) && StringUtil.isNumeric(id2)) {
                        idSearchMap.put("id1", id1);
                        idSearchMap.put("id2", id2);
                        idSearchMap.put("type", "two");
                        idSearchList.add(idSearchMap);
                    }
                }
            }
        }
        return idSearchList;
    }

    /**
     * 자하권한 AP 목록 데이터
     *
     * @author shavrani 2016.06.17
     */
    @RequestMapping(value = "/jaha/apt/ap/list-data")
    @ResponseBody
    public Map<String, Object> getJahaAptApListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String pageNum = StringUtil.nvl(params.get("pageNum"));
        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname, apid 컬럼 or 검색
        String aptId = StringUtil.nvl(params.get("aptId"));
        String aptSearchType = StringUtil.nvl(params.get("aptSearchType")); // 아파트 등록되지 않은 항목선택에만 쿼리에서 비교처리.
        String usePaging = StringUtil.nvl(params.get("usePaging")); // edoorService.getAptApList 에서 체크 param이 없으면 기본 페이징처리. N이면 페이징처리안함.
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apIds = StringUtil.nvl(params.get("apIds"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apId = StringUtil.nvl(params.get("apId"));
        String existExpIp = StringUtil.nvl(params.get("existExpIp"));// Y 이면 ip가 입력된것만 조회

        String _active = StringUtil.nvl(params.get("_active"));
        if (StringUtil.isBlank(_active)) {
            params.put("_active", true);
        }

        params.put("testAptId", Constants.AP_TEST_APT_ID);

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        List<Map<String, Object>> apIdSearchList = getParamApIds(apIds);
        params.put("apIds", apIdSearchList);

        Map<String, Object> result = edoorService.getAptApList(params);

        return result;
    }

    /**
     * @author shavrani 2016.10.28
     */
    @RequestMapping(value = "/jaha/apt/ap/history/data")
    @ResponseBody
    public List<AptAp> getAptApHistoryListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String apBeaconUuid = StringUtil.nvl(params.get("apBeaconUuid"));

        List<AptAp> aptApList = edoorService.getAptApAllList(params);

        return aptApList;
    }

    /**
     * 관리자권한 AP 목록 데이터
     *
     * @author shavrani 2016.06.17
     */
    @RequestMapping(value = "/admin/apt/ap/list-data")
    @ResponseBody
    public Map<String, Object> getAdminAptApListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        params.put("aptId", user.house.apt.id);// 관리자 계정의 아파트내에서만 조회
        String pageNum = StringUtil.nvl(params.get("pageNum"));
        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname, apid 컬럼 or 검색
        params.put("_active", true);

        Map<String, Object> result = edoorService.getAptApList(params);

        return result;
    }

    /**
     * session에 있는 apt내에서만 검색할수있는 ap 검색
     *
     * @author shavrani 2016.06.17
     */
    @RequestMapping(value = "/admin/apt/ap/all-list-data")
    @ResponseBody
    public List<AptAp> getAptApListAllData(Model model, HttpServletRequest req, @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aptId", user.house.apt.id);
        params.put("searchKeyword", searchKeyword);

        String _active = StringUtil.nvl(params.get("_active"));
        if (StringUtil.isBlank(_active)) {
            params.put("_active", true);
        }

        List<AptAp> aptApList = edoorService.getAptApAllList(params);

        return aptApList;
    }

    /**
     * @author shavrani 2016.06.20
     */
    @RequestMapping(value = "/jaha/apt/ap/delete")
    @ResponseBody
    public String getAptApDelete(Model model, HttpServletRequest req, @RequestParam(value = "deleteId") Long[] deleteIds) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = Maps.newHashMap();
        String result = "";
        if (deleteIds != null) {
            if (deleteIds.length > 0) {
                for (int i = 0; i < deleteIds.length; i++) {
                    // edoorService.deleteAptAp(deleteIds[i]);
                    // 삭제처리는 비활성화로 대체 처리.
                    edoorService.deactiveAptAp(deleteIds[i]);

                }
                result = "1";
            }
        }

        return result;
    }

    private List<Map<String, Object>> getSkipAuthList() {
        /** authSkipList 코드성데이터가 없어 직접기입. */
        List<Map<String, Object>> skipAuthList = new ArrayList<Map<String, Object>>();
        List<String> skipAuths = new ArrayList<String>();
        skipAuths.add("admin");
        skipAuths.add("gasChecker");

        for (int i = 0; i < skipAuths.size(); i++) {
            Map<String, Object> skipAuth = new HashMap<String, Object>();
            skipAuth.put("value", skipAuths.get(i));
            skipAuth.put("checked", false);
            skipAuthList.add(skipAuth);
        }
        return skipAuthList;
    }

    /**
     * @author shavrani 2016.06.20 아파트 AP 등록은 jaha권한만 되게 설정
     */
    @RequestMapping(value = "/jaha/apt/ap/form-pop")
    public String aptApForm(Model model, HttpServletRequest req) {
        // model.addAttribute("uuidCodeList", commonService.findByCodeGroup("AP_UUID"));
        model.addAttribute("skipAuthList", getSkipAuthList());

        List<CommonCode> operationModeList = commonService.findByCodeGroup("AP_OPERATION_MODE");
        List<CommonCode> statusList = commonService.findByCodeGroup("AP_STATUS");
        model.addAttribute("operationModeList", operationModeList);
        model.addAttribute("statusList", statusList);

        return "jaha/apt-ap-form-popup";
    }

    /**
     * @author shavrani 2016.06.20
     */
    @RequestMapping(value = "/jaha/apt/ap/save")
    @ResponseBody
    public String saveAptAp(Model model, HttpServletRequest req, @RequestParam(value = "jsonParam") String jsonParam) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = "";
        if (!StringUtil.isEmpty(jsonParam)) {

            Map<String, Object> params = new HashMap<String, Object>();
            JSONArray jsonArray = new JSONArray(jsonParam);
            int size = jsonArray.length();
            int resultSize = 0;

            for (int i = 0; i < size; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);

                AptAp aptAp = new AptAp();
                aptAp.aptId = StringUtil.nvlLong(jo.opt("aptId"), null);
                aptAp.regId = user.id;
                aptAp.apUuid = StringUtil.nvl(jo.opt("apUuid"));
                aptAp.apBeaconUuid = StringUtil.nvl(jo.opt("apBeaconUuid"));
                aptAp.apBeaconMajor = StringUtil.nvl(jo.opt("apBeaconMajor"));
                aptAp.apBeaconMinor = StringUtil.nvl(jo.opt("apBeaconMinor"));
                aptAp.apId = StringUtil.nvl(jo.opt("apId"));
                aptAp.apName = StringUtil.nvl(jo.opt("apName"));
                aptAp.apPassword = StringUtil.nvl(jo.opt("apPassword"));
                aptAp.sshPassword = StringUtil.nvl(jo.opt("sshPassword"));
                aptAp.rssiApp = StringUtil.nvlInt(jo.opt("rssiApp"), null);
                aptAp.status = "1";// 기본 1, 정상
                aptAp.operationMode = StringUtil.nvl(jo.opt("operationMode"));
                aptAp.memo = StringUtil.nvl(jo.opt("memo"));
                aptAp.skipAuth = StringUtil.nvl(jo.opt("skipAuth"));
                aptAp.wifiMac = StringUtil.nvl(jo.opt("wifiMac"));
                aptAp.gpiodelay = Constants.AP_DEFAULT_GPIODELAY;

                // 중복은 apBeaconUuid 체크
                params.clear();
                params.put("_apBeaconUuid", aptAp.apBeaconUuid);
                params.put("_active", true);
                AptAp existAptAp = edoorService.getAptAp(params);
                if (existAptAp == null) {
                    edoorService.saveAptAp(aptAp);
                    resultSize++;
                }
            }

            // 결과
            if (size > 0) {
                if (size == resultSize) {
                    result = "1";
                } else if (resultSize > 0) {
                    result = "2";
                }
            }
        }

        return result;
    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/jaha/apt/ap/sampleCsvFileDown")
    public ResponseEntity<String> aptApSampleCsvFileDown(HttpServletRequest req) {

        String sampleData = "AP UUID,AP_BEACON_UUID,AP ID,AP NAME,OPERATION MODE,AP PASSWORD,\n sample data 1,sample data 2,sample data 3,sample data 4,sample data 5,sample data 6";

        HttpHeaders header = new HttpHeaders();

        header.add("Content-Type", "text/csv; charset=MS949");// csv파일은 MS949로해야 한글 정상출력
        header.add("Content-Disposition", "attachment; filename=\"apt-ap-sample.csv\"");

        return new ResponseEntity<String>(sampleData, header, HttpStatus.CREATED);

    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/jaha/apt/ap/save-csv")
    public String saveAptApCsv(HttpServletRequest req, RedirectAttributes rda, @RequestParam(value = "csvFile") MultipartFile csvFile) throws IOException {

        String result = "0";

        if (csvFile != null) {

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            Map<String, Object> params = new HashMap<String, Object>();

            byte[] buf = csvFile.getBytes();
            String s = new String(buf, "MS949");
            List<String> aptApList = Splitter.on("\n").splitToList(s);

            if (aptApList != null) {
                // 0번째 row는 제목줄. 1번 index부터 데이터
                int size = aptApList.size();
                int resultSize = 0;
                for (int i = 1; i < aptApList.size(); i++) {

                    String aptApData = aptApList.get(i);
                    List<String> data = Splitter.on(",").trimResults().splitToList(aptApData);

                    /** csv파일의 row가 데이터가 없는 row도 마우스클릭만해도 공백이 입력되는듯 그래서 실제데이터가 일정갯수 이상일때만 존재하는 row로 판단하고 처리. */
                    if (data != null && data.size() >= 4) {
                        int idx = 0;
                        AptAp aptAp = new AptAp();
                        // aptAp.aptId = user.house.apt.id;
                        aptAp.regId = user.id;
                        aptAp.apUuid = data.get(idx++);
                        aptAp.apBeaconUuid = data.get(idx++);
                        aptAp.apId = data.get(idx++);
                        aptAp.apName = data.get(idx++);
                        aptAp.status = "1";// 상태값은 기본 1, 정상
                        aptAp.operationMode = data.get(idx++);
                        aptAp.gpiodelay = Constants.AP_DEFAULT_GPIODELAY;
                        String apPassword = data.get(idx++);

                        if (StringUtil.isEmpty(apPassword)) {
                            // password 입력이 안되어있으면 자동 생성.
                            String charaters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                            int pwSize = 12;
                            StringBuilder sb = new StringBuilder();
                            for (int k = 0; k < pwSize; k++) {
                                sb.append(charaters.charAt((int) Math.floor(Math.random() * charaters.length())));
                            }
                            aptAp.apPassword = sb.toString();
                        } else {
                            aptAp.apPassword = apPassword;
                        }

                        if (!StringUtil.isEmpty(aptAp.apUuid) && !StringUtil.isEmpty(aptAp.apBeaconUuid) && !StringUtil.isEmpty(aptAp.apName)) {
                            // 중복은 apBeaconUuid 체크
                            params.clear();
                            params.put("_apBeaconUuid", aptAp.apBeaconUuid);
                            params.put("_active", true);
                            AptAp existAptAp = edoorService.getAptAp(params);
                            if (existAptAp == null) {
                                AptAp saveAptAp = edoorService.saveAptAp(aptAp);
                                if (saveAptAp != null) {
                                    // 저장되었으면 생성된 id는 apId에 동일하게 저장해준다.
                                    saveAptAp.apId = StringUtil.nvl(saveAptAp.id);
                                    edoorService.saveAptAp(aptAp);
                                }
                                resultSize++;
                            }
                        }
                    }

                }

                // 결과
                if (size > 0) {
                    if (size == resultSize) {
                        result = "1";
                    } else if (resultSize > 0) {
                        result = "2";
                    }
                }
            }
        }

        rda.addFlashAttribute("csvFileSave", result);

        return "redirect:/jaha/apt/ap/form-pop";

    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/jaha/apt/ap/detail")
    public String getJahaAptApDetail(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("_active", true);
        AptAp aptAp = edoorService.getAptAp(params);
        model.addAttribute("data", aptAp);
        // model.addAttribute("uuidCodeList", commonService.findByCodeGroup("AP_UUID"));

        /** authSkipList 코드성데이터가 없어 직접기입. */
        List<Map<String, Object>> skipAuthList = getSkipAuthList();

        if (!StringUtil.isNull(aptAp.skipAuth)) {
            String[] arr = aptAp.skipAuth.split(",");

            int size = skipAuthList.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> skipAuth = skipAuthList.get(i);
                for (int j = 0; j < arr.length; j++) {
                    if (StringUtil.nvl(skipAuth.get("value")).equals(arr[j])) {
                        skipAuth.put("checked", true);
                        break;
                    }
                }
            }
        }

        model.addAttribute("skipAuthList", skipAuthList);

        List<CommonCode> operationModeList = commonService.findByCodeGroup("AP_OPERATION_MODE");
        List<CommonCode> statusList = commonService.findByCodeGroup("AP_STATUS");
        model.addAttribute("operationModeList", operationModeList);
        model.addAttribute("statusList", statusList);

        return "jaha/apt-ap-detail";
    }

    /**
     * @author shavrani 2016.11.18
     * @desc 삭제된 AP 상관없이 조회
     */
    @RequestMapping(value = "/jaha/apt/ap/all/detail")
    @ResponseBody
    public AptAp getJahaAptApAllDetail(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return edoorService.getAptAp(params);
    }

    /**
     * @author shavrani 2016.10.05
     */
    @RequestMapping(value = "/admin/apt/ap/detail")
    public String getAdminAptApDetail(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("_active", true);
        AptAp aptAp = edoorService.getAptAp(params);
        model.addAttribute("data", aptAp);

        /** authSkipList 코드성데이터가 없어 직접기입. */
        List<Map<String, Object>> skipAuthList = getSkipAuthList();

        if (!StringUtil.isNull(aptAp.skipAuth)) {
            String[] arr = aptAp.skipAuth.split(",");

            int size = skipAuthList.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> skipAuth = skipAuthList.get(i);
                for (int j = 0; j < arr.length; j++) {
                    if (StringUtil.nvl(skipAuth.get("value")).equals(arr[j])) {
                        skipAuth.put("checked", true);
                        break;
                    }
                }
            }
        }

        model.addAttribute("skipAuthList", skipAuthList);

        return "admin/apt-ap-detail";
    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/jaha/apt/ap/detail-save")
    @ResponseBody
    public String saveJahaAptApDetail(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id, @RequestParam(value = "apUuid") String apUuid,
            @RequestParam(value = "apBeaconUuid") String apBeaconUuid, @RequestParam(value = "apBeaconMajor") String apBeaconMajor, @RequestParam(value = "apBeaconMinor") String apBeaconMinor,
            @RequestParam(value = "apId") String apId, @RequestParam(value = "apName") String apName, @RequestParam(value = "apPassword") String apPassword, @RequestParam(value = "sshPassword",
                    required = false) String sshPassword, @RequestParam(value = "status") String status, @RequestParam(value = "operationMode") String operationMode, @RequestParam(value = "wifiMac",
                    required = false) String wifiMac, @RequestParam(value = "memo", required = false) String memo, @RequestParam(value = "rssiApp") Integer rssiApp, String[] skipAuth) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = "";

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("id", id);

        AptAp aptAp = edoorService.getAptAp(params);

        if (aptAp != null) {

            /** apBeaconUuid가 기존과 다르면 다르게입력한 apBeaconUuid가 중복되는지 체크 */
            if (!aptAp.apBeaconUuid.equals(apBeaconUuid)) {
                params.clear();
                params.put("_apBeaconUuid", apBeaconUuid);
                params.put("_active", true);
                AptAp existAptAp = edoorService.getAptAp(params);
                if (existAptAp != null) {
                    result = "2";
                }
            }

            // result가 ""이면 저장가능.
            if ("".equals(result)) {

                boolean isSendGcm = false;

                if (user.type.jaha) {

                    // 고장여부가 변경되었는지 체크
                    if (!StringUtil.nvl(aptAp.status).equals(status)) {
                        isSendGcm = true;
                    }

                    // 이 항목은 jaha권한만 수정할수있게 처리.
                    aptAp.apUuid = apUuid;
                    aptAp.apBeaconUuid = apBeaconUuid;
                    aptAp.apBeaconMajor = apBeaconMajor;
                    aptAp.apBeaconMinor = apBeaconMinor;
                    aptAp.apId = apId;
                    aptAp.apPassword = apPassword;
                    aptAp.sshPassword = sshPassword;
                    aptAp.rssiApp = rssiApp;
                    aptAp.status = status;
                    aptAp.operationMode = operationMode;
                    aptAp.wifiMac = wifiMac;
                    aptAp.memo = memo;
                }

                aptAp.apName = apName;

                if (skipAuth != null && skipAuth.length > 0) {
                    String skipAuthText = ""; // Arrays.toString(skipAuth);
                    for (int i = 0; i < skipAuth.length; i++) {
                        skipAuthText += i == 0 ? skipAuth[i] : "," + skipAuth[i];
                    }
                    aptAp.skipAuth = skipAuthText;
                } else {
                    aptAp.skipAuth = null;
                }

                edoorService.saveAptAp(aptAp);
                result = "1";

                if (isSendGcm == true) {
                    /** 고장여부가 변경되었으면 Gcm 전송 */
                    params.clear();
                    params.put("apId", aptAp.id);
                    params.put("periodDay", 7);// 최근 7일 동안의 출입로그에 기록이 있는사람만
                    params.put("testAptId", Constants.AP_TEST_APT_ID);
                    // params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
                    List<SimpleUser> gcmUserList = edoorService.selectPeriodApAccessLogUserNApAccessAuthUserList(params);

                    String androidValue = "e-door 기기의 정보가 변경되었습니다.\ne-door 정보 갱신을 위해\n재로그인 또는 앱을 종료후 다시 시작해주세요.";
                    String iosValue = "e-door 기기의 정보가 변경되었습니다.\n여기를 터치하여 e-door 정보를 갱신해주세요.";
                    gcmService.sendGcmFunction("e-door 기기의 정보 변경", androidValue, iosValue, Lists.newArrayList("updateApList"), gcmUserList, false);
                }

            }

        }

        return result;
    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/admin/apt/ap/detail-save")
    @ResponseBody
    public String saveAdminAptApDetail(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id, @RequestParam(value = "apName") String apName, String[] skipAuth) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = "";

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("id", id);

        AptAp aptAp = edoorService.getAptAp(params);

        if (aptAp != null) {
            aptAp.apName = apName;

            if (skipAuth != null && skipAuth.length > 0) {
                String skipAuthText = ""; // Arrays.toString(skipAuth);
                for (int i = 0; i < skipAuth.length; i++) {
                    skipAuthText += i == 0 ? skipAuth[i] : "," + skipAuth[i];
                }
                aptAp.skipAuth = skipAuthText;
            } else {
                aptAp.skipAuth = null;
            }

            edoorService.saveAptAp(aptAp);
            result = "1";
        }

        return result;
    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/admin/apt/ap/access-auth-list-data")
    @ResponseBody
    public List<AptApAccessAuth> getAptApAccessAuthListData(Model model, HttpServletRequest req, @RequestParam(value = "type") String type, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        List<AptApAccessAuth> aptApAccessAuthList = edoorService.getAptApAccessAuthList(id, type);
        return aptApAccessAuthList;
    }

    /**
     * @author shavrani 2016.10.04
     */
    @RequestMapping(value = "/admin/apt/ap/access-auth-address-pop")
    public String adminAptApAccessAuthAdress(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id, @RequestParam(value = "aptId", required = false) Long aptId) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Long _aptId = StringUtil.nvlLong(aptId, user.house.apt.id);
        List<String> dongs = houseService.getDongs(_aptId);
        model.addAttribute("dongs", dongs);
        model.addAttribute("aptApId", id);
        model.addAttribute("aptId", _aptId);

        return "admin/apt-ap-access-auth-address-popup";
    }

    /**
     * @author shavrani 2016.06.21
     */
    @RequestMapping(value = "/admin/apt/ap/access-auth-user-pop")
    public String aptApAccessAuthUser(Model model, HttpServletRequest req, @RequestParam(value = "id") Long id, @RequestParam(value = "aptId", required = false) Long aptId) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Long _aptId = StringUtil.nvlLong(aptId, user.house.apt.id);
        List<String> dongs = houseService.getDongs(_aptId);
        model.addAttribute("dongs", dongs);
        model.addAttribute("aptApId", id);
        model.addAttribute("aptId", _aptId);

        return "admin/apt-ap-access-auth-user-popup";
    }

    /**
     * @author shavrani 2016.06.22
     */
    @RequestMapping(value = "/admin/apt/ap/access-auth-save")
    @ResponseBody
    @Transactional
    public String saveAptApAccessAuth(Model model, HttpServletRequest req, @RequestParam(value = "aptApId") Long aptApId, @RequestParam(value = "type") String type,
            @RequestParam(value = "jsonParam") String jsonParam) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        boolean massageFlag = false;// type 1의 처리시 발생하는 메시지 flag

        String result = "";
        if (!StringUtil.isEmpty(jsonParam)) {

            Map<String, Object> params = new HashMap<String, Object>();

            if (!user.type.jaha) {
                // 자하권한이 아니면 자신의 아파트에 해당하는 ap만 처리
                params.put("aptId", user.house.apt.id);
            }

            params.put("id", aptApId);
            AptAp aptAp = edoorService.getAptAp(params);

            // ap데이터가 존재하면 저장.
            if (aptAp != null) {

                List<SimpleUser> gcmUserList = Lists.newArrayList();

                JSONArray jsonArray = new JSONArray(jsonParam);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    AptApAccessAuth aaaa = new AptApAccessAuth();
                    aaaa.aptApId = aptAp.id;
                    aaaa.type = type;
                    if ("1".equals(type)) {
                        aaaa.dong = jsonObject.getString("dong");
                        aaaa.ho = jsonObject.getString("ho");
                        aaaa.hoType = jsonObject.getString("hoType");
                        if ("1".equals(aaaa.hoType) && !"All".equals(aaaa.dong)) {
                            aaaa.hoLineDigit = jsonObject.getString("hoLineDigit");
                        }
                    } else if ("2".equals(type)) {
                        aaaa.user = userService.getUser(jsonObject.getLong("id"));
                    }
                    aaaa.regId = user.id;

                    if ("1".equals(type)) {
                        // type 1 동/호수 인경우 처리

                        AptApAccessAuth existAaaa = edoorService.getAptApAccessAuth(aptAp.id, aaaa.type, aaaa.dong, aaaa.ho, aaaa.hoType);
                        if (existAaaa == null) {
                            List<AptApAccessAuth> aptApAccessAuthList = edoorService.getAptApAccessAuthList(aptAp.id, type);

                            if (aptApAccessAuthList.size() == 1) {
                                AptApAccessAuth existCheckData = aptApAccessAuthList.get(0);
                                if ("All".equals(existCheckData.dong)) {
                                    // db data에 이미 전체가 저장되어있으면 다른 데이터는 입력받을 수 없음. break하여 프로세스종료
                                    result = "2";
                                    break;
                                }
                            }

                            if ("All".equals(aaaa.dong)) {
                                // 동이 전체이면 all 하나만 저장하게 모두삭제 처리
                                edoorService.deleteAptApAccessAuthType(aptApId, type);

                                // gcm 보낼 사람 목록
                                params.clear();
                                params.put("aptId", aptAp.aptId);
                                params.put("_active", true);
                                params.put("_notiEdoor", true);
                                gcmUserList.addAll(userService.selectUserList(params));
                            }

                            if (aptApAccessAuthList.size() > 0) {
                                boolean existAll = false;
                                for (int j = 0; j < aptApAccessAuthList.size(); j++) {
                                    AptApAccessAuth existCheckData = aptApAccessAuthList.get(j);

                                    // 동의 호가 전체인항목이 이미있으면 해당동의 호는 입력불가.
                                    if (existCheckData.dong.equals(aaaa.dong) && "All".equals(existCheckData.ho)) {
                                        existAll = true;
                                        break;
                                    }

                                    // 입력된 데이터 호가 조회한 데이터의 호라인에 포함되면 입력불가.
                                    if ("2".equals(aaaa.hoType)) {
                                        if (existCheckData.dong.equals(aaaa.dong) && "1".equals(existCheckData.hoType)) {
                                            int hoLineDigit = Integer.valueOf(existCheckData.hoLineDigit);// hoType이 1이면 hoLineDigit는 숫자값이 필수로 존재함.
                                            String hoStr = aaaa.ho.substring(aaaa.ho.length() - hoLineDigit, aaaa.ho.length());
                                            if (existCheckData.ho.equals(hoStr)) {
                                                existAll = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (existAll == true) {
                                    // ho만 전체. 다른 데이터는 진행하도록 continue
                                    // result = "3";
                                    massageFlag = true;
                                    continue;
                                }
                            }

                            /** 호 라인으로 설정한 데이터면 해당 호라인이 적용되는 데이터는 삭제 */
                            if (!"All".equals(aaaa.dong) && "1".equals(aaaa.hoType)) {
                                edoorService.deleteAptApAccessAuthTypeDongHoLine(aptApId, type, aaaa.dong, aaaa.ho);

                                // gcm 보낼 사람 목록
                                params.clear();
                                params.put("aptId", aptAp.aptId);
                                params.put("dong", aaaa.dong);
                                params.put("hoLine", aaaa.ho);
                                params.put("_active", true);
                                params.put("_notiEdoor", true);
                                gcmUserList.addAll(userService.selectUserList(params));
                            }

                            if (!"All".equals(aaaa.dong) && "2".equals(aaaa.hoType)) {
                                // gcm 보낼 사람 목록
                                params.clear();
                                params.put("aptId", aptAp.aptId);
                                params.put("dong", aaaa.dong);
                                params.put("ho", aaaa.ho);
                                params.put("_active", true);
                                params.put("_notiEdoor", true);
                                gcmUserList.addAll(userService.selectUserList(params));
                            }

                            /** 호가 all로 설정한 데이터면 해당 동의 호설정은 모두삭제 */
                            if (!"All".equals(aaaa.dong) && "All".equals(aaaa.ho)) {
                                edoorService.deleteAptApAccessAuthTypeDong(aptApId, type, aaaa.dong);

                                // gcm 보낼 사람 목록
                                params.clear();
                                params.put("aptId", aptAp.aptId);
                                params.put("dong", aaaa.dong);
                                params.put("_active", true);
                                params.put("_notiEdoor", true);
                                gcmUserList.addAll(userService.selectUserList(params));
                            }

                        } else {
                            // 중복데이터면 skip
                            continue;
                        }

                    } else if ("2".equals(type)) {
                        // type 2 사용자 인경우 처리

                        AptApAccessAuth existAaaa = edoorService.getAptApAccessAuth(aptAp.id, aaaa.type, aaaa.user.id);
                        if (existAaaa != null) {
                            // 중복데이터면 skip
                            continue;
                        } else {
                            // gcm 보낼 사람 목록
                            params.clear();
                            params.put("id", aaaa.user.id);
                            params.put("_active", true);
                            params.put("_notiEdoor", true);
                            gcmUserList.addAll(userService.selectUserList(params));
                        }
                    }

                    edoorService.saveAptApAccessAuth(aaaa);
                    result = "1";
                }

                String androidValue = "e-door 기기의 정보가 변경되었습니다.\ne-door 정보 갱신을 위해\n재로그인 또는 앱을 종료후 다시 시작해주세요.";
                String iosValue = "e-door 기기의 정보가 변경되었습니다.\n여기를 터치하여 e-door 정보를 갱신해주세요.";
                gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("updateApList"), gcmUserList, false);
            }

        }

        // 메시지를 주기위해 굳이 설정함. ( type 1의 ho가 전체가있어서 저장못했을시 그다음도 진행하기때문에 한번이라도 전체가있어서 저장못한 row가 있다면 메세지3번으로 처리 )
        if (massageFlag == true) {
            result = "3";
        }

        return result;
    }

    /**
     * @author shavrani 2016.06.20
     */
    @RequestMapping(value = "/admin/apt/ap/access-auth-delete")
    @ResponseBody
    public String deleteAptApAccessAuth(Model model, HttpServletRequest req, @RequestParam(value = "aptApId") Long aptApId, @RequestParam(value = "deleteId") Long[] deleteIds) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        String result = "";

        Map<String, Object> params = new HashMap<String, Object>();

        if (!user.type.jaha) {
            // 자하권한이 아니면 자신의 아파트에 해당하는 ap만 처리
            params.put("aptId", user.house.apt.id);
        }

        params.put("id", aptApId);
        AptAp aptAp = edoorService.getAptAp(params);

        if (aptAp != null) {
            if (deleteIds != null) {
                if (deleteIds.length > 0) {
                    for (int i = 0; i < deleteIds.length; i++) {
                        edoorService.deleteAptApAccessAuth(deleteIds[i]);
                    }
                    result = "1";

                    /*
                     * params.clear(); params.put("aptId", aptAp.aptId); params.put("_active", true); List<SimpleUser> gcmUserList = userService.selectUserList(params);
                     * 
                     * String androidValue = "e-door 기기의 정보가 변경되었습니다.\ne-door 정보 갱신을 위해\n재로그인 또는 앱을 종료후 다시 시작해주세요."; String iosValue = "e-door 기기의 정보가 변경되었습니다.\n여기를 터치하여 e-door 정보를 갱신해주세요.";
                     * gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("updateApList"), gcmUserList, false);
                     */
                }
            }
        }

        return result;
    }

    /**
     * @author shavrani 2016.08.09
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/list")
    public String getAptApAccessDeviceList(Model model, HttpServletRequest req) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        model.addAttribute("user", user);

        List<CommonCode> apAccessDeviceTypeList = commonService.findByCodeGroup("APTAP_AD");
        model.addAttribute("apAccessDeviceTypeList", apAccessDeviceTypeList);

        return "admin/apt-ap-access-device-list";
    }

    /**
     * @author shavrani 2016.08.09
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/list-data")
    @ResponseBody
    public PageImpl<AptApAccessDevice> getAptApAccessDeviceListData(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest req,
            @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        params.put("aptId", user.house.apt.id);
        params.put("_deactive", "N");
        Page<AptApAccessDevice> page = edoorService.getAptApAccessDeviceList(params, pageable);

        List<CommonCode> apAccessDeviceTypeList = commonService.findByCodeGroup("APTAP_AD");

        // 타입의 코드 명칭을 수동 입력
        List<AptApAccessDevice> list = page.getContent();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                AptApAccessDevice aaad = list.get(i);
                for (int j = 0; j < apAccessDeviceTypeList.size(); j++) {
                    CommonCode cc = apAccessDeviceTypeList.get(j);
                    if (cc.code.equals(aaad.type)) {
                        aaad.typeNm = cc.name;
                        break;
                    }
                }
            }
        }

        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }

    /**
     * @author shavrani 2016.08.09
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/form-pop")
    public String getAptApAccessDeviceForm(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        model.addAttribute("user", user);

        List<CommonCode> apAccessDeviceTypeList = commonService.findByCodeGroup("APTAP_AD");
        model.addAttribute("apAccessDeviceTypeList", apAccessDeviceTypeList);

        return "admin/apt-ap-access-device-form-popup";
    }

    /**
     * @author shavrani 2016.08.09
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/detail-pop")
    public String getAptApAccessDeviceDetail(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        model.addAttribute("user", user);

        List<CommonCode> apAccessDeviceTypeList = commonService.findByCodeGroup("APTAP_AD");
        model.addAttribute("apAccessDeviceTypeList", apAccessDeviceTypeList);

        params.put("aptId", user.house.apt.id);
        params.put("_deactive", "N");
        AptApAccessDevice aptApAccessDevice = edoorService.getAptApAccessDevice(params);
        model.addAttribute("aptApAccessDevice", aptApAccessDevice);

        // aptapid 콤마로 구분된 string split하여 조회
        if (!"".equals(StringUtil.nvl(aptApAccessDevice.aptApIds))) {
            String aptApIds = aptApAccessDevice.aptApIds;
            List<String> aptApIdList = Splitter.on(",").trimResults().splitToList(aptApIds);
            params.clear();
            params.put("aptApIdList", aptApIdList);
            params.put("_deactive", "N");
            List<AptAp> aptApList = edoorService.getAptApAllList(params);
            model.addAttribute("aptApList", aptApList);
        }

        return "admin/apt-ap-access-device-detail-popup";
    }

    /**
     * @author shavrani 2016.08.10
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/sampleCsvFileDown")
    public ResponseEntity<String> apAccessDeviceSampleCsvFileDown(HttpServletRequest req) {

        String sampleData = "TYPE,Access Key,소유자,사용자,메모,\n sample data 1,sample data 2,sample data 3,sample data 4,sample data 5";

        HttpHeaders header = new HttpHeaders();

        header.add("Content-Type", "text/csv; charset=MS949");// csv파일은 MS949로해야 한글 정상출력
        header.add("Content-Disposition", "attachment; filename=\"apt-ap-access-device-sample.csv\"");

        return new ResponseEntity<String>(sampleData, header, HttpStatus.CREATED);

    }

    /**
     * @author shavrani 2016.08.10
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/save")
    @Transactional
    @ResponseBody
    public String aptApAccessDeviceSave(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = "0";

        AptApAccessDevice aptApAccessDevice = new AptApAccessDevice();

        Long id = StringUtil.nvlLong(params.get("id"));
        String type = StringUtil.nvl(params.get("type"));
        String accessKey = StringUtil.nvl(params.get("accessKey"));
        Long userId = StringUtil.nvlLong(params.get("userId"));
        String memo = StringUtil.nvl(params.get("memo"));
        String secondUser = StringUtil.nvl(params.get("secondUser"));

        if ("".equals(type)) {
            logger.equals(" # parameter 'type' is null");
            return result;
        }
        if ("".equals(accessKey)) {
            logger.equals(" # parameter 'accessKey' is null");
            return result;
        }
        if (userId < 1) {
            logger.equals(" # parameter 'userId' is null");
            return result;
        }

        if (StringUtil.isNull(params.get("id"))) {

            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("accessKey", accessKey);// 새 map으로 accessKey로만 조회
            AptApAccessDevice existAaad = edoorService.existAptApAccessDevice(parameter);
            if (existAaad != null) {
                logger.equals(" # 중복된 accessKey [" + accessKey + "]");
                return "-1";
            }

            // 등록이면 등록자만 입력
            aptApAccessDevice.regUser = user;
        } else {
            // 수정이면 수정자와 id 입력
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("id", id);// 새 map으로 id로만 조회
            aptApAccessDevice = edoorService.existAptApAccessDevice(parameter);
            if (aptApAccessDevice == null) {
                logger.equals(" # 해당 id의 access device가 없음 [" + id + "]");
                return "-2";
            }
            aptApAccessDevice.modId = user.id;
            String originAccessKey = aptApAccessDevice.accessKey;
            if (!accessKey.equals(originAccessKey)) {
                parameter.clear();
                parameter.put("accessKey", accessKey);
                AptApAccessDevice existAaad = edoorService.existAptApAccessDevice(parameter);
                if (existAaad != null) {
                    logger.equals(" # 중복된 accessKey [" + accessKey + "]");
                    return "-1";
                }
            }
        }

        String aptApId = "";
        String[] aptApIds = req.getParameterValues("aptApId");
        if (aptApIds != null && aptApIds.length > 0) {
            for (int i = 0; i < aptApIds.length; i++) {
                aptApId += i == 0 ? aptApIds[i] : "," + aptApIds[i];
            }
        }

        aptApAccessDevice.type = type;
        aptApAccessDevice.accessKey = accessKey;
        aptApAccessDevice.memo = memo;
        aptApAccessDevice.secondUser = secondUser;
        aptApAccessDevice.aptApIds = aptApId;

        User paramUser = userService.getUser(userId);
        if (paramUser == null) {
            logger.debug(" # 존재하지않는 유저 ID [" + userId + "]");
        } else {
            aptApAccessDevice.user = paramUser;
            AptApAccessDevice saveResult = edoorService.saveAptApAccessDevice(aptApAccessDevice);
            if (saveResult != null) {
                result = "1";
            }
        }

        return result;

    }

    /**
     * apt ap 검색 팝업
     */
    @RequestMapping(value = "/admin/apt/ap/search/popup")
    public String aptApSearchPopup(HttpServletRequest req, Model model) {

        model.addAttribute("callback", req.getParameter("callback"));

        return "admin/apt-ap-search-popup";
    }

    /**
     * @author shavrani 2016.08.16
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/save-csv")
    @Transactional
    public String saveAptApAccessDeviceCsv(HttpServletRequest req, RedirectAttributes rda, @RequestParam(value = "csvFile") MultipartFile csvFile) throws IOException {

        String result = "0";

        if (csvFile != null) {

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            Map<String, Object> params = new HashMap<String, Object>();

            byte[] buf = csvFile.getBytes();
            String s = new String(buf, "MS949");
            List<String> aptApAccessDeviceList = Splitter.on("\n").splitToList(s);

            if (aptApAccessDeviceList != null) {
                // 0번째 row는 제목줄. 1번 index부터 데이터
                for (int i = 1; i < aptApAccessDeviceList.size(); i++) {

                    String aptApAccessDeviceData = aptApAccessDeviceList.get(i);
                    List<String> data = Splitter.on(",").trimResults().splitToList(aptApAccessDeviceData);

                    /** csv파일의 row가 데이터가 없는 row도 마우스클릭만해도 공백이 입력되는듯 그래서 실제데이터가 일정갯수 이상일때만 존재하는 row로 판단하고 처리. */
                    if (data != null && data.size() >= 4) {

                        AptApAccessDevice aaad = new AptApAccessDevice();
                        aaad.regUser = user;
                        aaad.type = data.get(0);
                        aaad.accessKey = data.get(1);
                        aaad.user = userService.getUser(Long.parseLong(data.get(2)));
                        aaad.secondUser = data.get(3);
                        aaad.memo = data.get(4);

                        if (!StringUtil.isEmpty(aaad.type) && !StringUtil.isEmpty(aaad.accessKey)) {
                            params.put("accessKey", aaad.accessKey);
                            AptApAccessDevice existAaad = edoorService.existAptApAccessDevice(params);
                            if (existAaad == null) {
                                edoorService.saveAptApAccessDevice(aaad);
                                result = "1";
                            }
                        }
                    }

                }
            }
        }

        rda.addFlashAttribute("csvFileSave", result);

        return "redirect:/admin/apt/ap/access/device/form-pop";

    }

    /**
     * @author shavrani 2016.08.16
     */
    @RequestMapping(value = "/admin/apt/ap/access/device/delete")
    @Transactional
    @ResponseBody
    public int deleteAptApAccessDevice(HttpServletRequest req, Model model, @RequestParam(value = "deleteIds") Long[] deleteIds) {
        int result = 0;
        if (deleteIds != null && deleteIds.length > 0) {
            for (int i = 0; i < deleteIds.length; i++) {
                result += edoorService.deleteAptApAccessDevice(deleteIds[i]);
            }
        }
        return result;
    }

    /**
     * @author shavrani 2016.08.31 firmware 파일 선택팝업
     */
    @RequestMapping(value = "/jaha/apt/ap/firmware/popup")
    public String aptApFirmwarePopup(HttpServletRequest req, Model model) {

        List<String> fileNames = new ArrayList<String>();
        File dir = new File("/nas/EMaul/aptAp/firmware");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        model.addAttribute("fileNames", fileNames);

        return "jaha/apt-ap-firmware-popup";
    }

    /**
     * @author shavrani 2016.08.31
     */
    @RequestMapping(value = "/jaha/apt/ap/firmware/file-down")
    public ResponseEntity<byte[]> aptApFirmwareFileDown(HttpServletRequest req, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String fileName = StringUtil.nvl(params.get("fileName"));

        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        File dir = new File("/nas/EMaul/aptAp/firmware");
        if (dir.exists()) {
            File downFile = null;
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        if (fileName.equals(file.getName())) {
                            downFile = file;
                            break;
                        }
                    }
                }
            }

            if (downFile != null) {
                return Responses.getFileEntity(downFile, downFile.getName());
            }
        }

        return null;
    }

    /**
     * @author shavrani 2016.08.31
     */
    @RequestMapping(value = "/jaha/apt/ap/firmware/update")
    @ResponseBody
    public Map<String, Object> aptApFirmwareUpdate(HttpServletRequest req, Model model, @RequestParam(value = "apList") List<String> apList, @RequestParam(value = "firmwareList") String[] firmwareList)
            throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> result = null;

        if (apList == null || apList.isEmpty() || firmwareList == null || firmwareList.length == 0) {
            return result;
        }

        result = edoorService.aptApFirmwareUpdate(user, apList, firmwareList);

        return result;
    }

    /**
     * @author shavrani 2016.08.31
     */
    @RequestMapping(value = "/jaha/apt/ap/system-data/update")
    @ResponseBody
    public Map<String, Object> aptApSystemDataUpdate(HttpServletRequest req, Model model, @RequestParam(value = "apList") List<String> apList,
            @RequestParam(value = "rssi", required = false) Integer rssi, @RequestParam(value = "keepon", required = false) Integer keepon,
            @RequestParam(value = "gpiodelay", required = false) Integer gpiodelay) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> result = null;

        // rssi는 minus 값, keepon은 밀리세컨 ( rssi와 keepon은 둘중에 하나라도 있으면 진행 )
        if (apList == null || apList.isEmpty() || (StringUtil.nvlInt(rssi) >= 0 && StringUtil.nvlInt(keepon) <= 0)) {
            return result;
        }

        result = edoorService.aptApSystemDataUpdate(user, apList, rssi, keepon, gpiodelay);

        return result;
    }

    /**
     * AP에 아파트 설정 팝업
     */
    @RequestMapping(value = "/jaha/apt/ap/apply-apt/popup")
    public String systemNoticeSave(HttpServletRequest req) {
        return "jaha/apt-ap-apply-apt-popup";
    }

    /**
     * AP에 아파트 지정
     *
     * @author shavrani 2016.10.05
     */
    @RequestMapping(value = "/jaha/apt/ap/save/apt")
    @ResponseBody
    public int aptApApplyApt(HttpServletRequest req, Model model, @RequestParam(value = "apId") Long[] apList, @RequestParam(value = "aptId") Long aptId) throws Exception {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        return edoorService.aptApApplyApt(user, apList, aptId);
    }

    /**
     * AP에 아파트 변경
     *
     * @author shavrani 2016.10.05
     */
    @RequestMapping(value = "/jaha/apt/ap/change-apt")
    @ResponseBody
    public Long aptApChangeApt(HttpServletRequest req, Model model, @RequestParam(value = "id") Long id, @RequestParam(value = "aptId") Long aptId) throws Exception {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Long result = edoorService.changeApt(user, id, aptId);
        return result;
    }

    /**
     * AP에 파일업로드와 업로드 경로지정, command 명령 전송, firmware version 수정
     *
     * @author shavrani 2016.11.08
     */
    @RequestMapping(value = "/jaha/apt/ap/system/remote/control")
    @ResponseBody
    public Map<String, Object> aptApSystemRemoteControl(HttpServletRequest req, Model model, @RequestParam(value = "apList") List<String> apList, @RequestParam(value = "fw_version") String fwVersion,
            @RequestParam(value = "fw_type", required = false) List<String> fwTypeList, @RequestParam(value = "cmd", required = false) List<String> cmdList, @RequestParam(value = "path",
                    required = false) List<String> pathList, @RequestParam(value = "files", required = false) List<MultipartFile> fileList) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> result = null;

        if (apList == null || apList.isEmpty()) {
            return result;
        }

        result = edoorService.aptApSystemRemoteControl(user, apList, fwVersion, fwTypeList, cmdList, pathList, fileList);

        return result;
    }

    /**
     * 테스트용 URL
     */
    @RequestMapping(value = "/api/public/ap/ip/update/test/{rssi}/{keepon}")
    @ResponseBody
    public Map<String, Object> apIpUpdateTest(HttpServletRequest req, Model model, @PathVariable(value = "rssi") String rssi, @PathVariable(value = "keepon") String keepon) throws Exception {
        System.out.println("apIpUpdateTest 시작 ~~~~~~~ " + keepon);
        Thread.sleep(5000);
        Map<String, Object> result = Maps.newHashMap();
        result.put("result", "Y");
        System.out.println("apIpUpdateTest 끝 ~~~~~~~ " + keepon);
        return result;
    }

    /**
     * 테스트용 URL
     */
    @RequestMapping(value = "/api/public/ap/ip/update/multipart/file/test")
    @ResponseBody
    public Map<String, Object> apIpUpdateTestMultiFile(HttpServletRequest req, Model model, @RequestParam(value = "files") List<MultipartFile> files) throws Exception {

        System.out.println("########### start 1");
        if (files == null) {
            System.out.println("# 파일널");
        } else {
            int size = files.size();
            if (size > 0) {
                for (MultipartFile multipartFile : files) {
                    System.out.println("          ##  file name : " + multipartFile.getOriginalFilename() + " , size : " + multipartFile.getSize());
                }
            } else {
                System.out.println(" ## file empty");
            }
        }
        System.out.println("########### end 1");

        Map<String, Object> result = Maps.newHashMap();
        result.put("result", "Y");
        return result;
    }

    /**
     * 테스트용 URL
     */
    @RequestMapping(value = "/api/public/ap/ip/update/multipart/file/test2")
    @ResponseBody
    public Map<String, Object> apIpUpdateTestMultiFile2(HttpServletRequest req, @RequestParam(value = "fw_version") String fwVersion,
            @RequestParam(value = "fw_type", required = false) List<String> fwTypeList, @RequestParam(value = "cmd", required = false) List<String> cmdList, @RequestParam(value = "path",
                    required = false) List<String> pathList, @RequestParam(value = "files", required = false) List<MultipartFile> fileList) throws Exception {

        for (String str : fwTypeList) {
            System.out.println(" ### fwtype : " + str);
        }

        for (String str : cmdList) {
            System.out.println(" ### cmd : " + str);
        }

        for (String str : pathList) {
            System.out.println(" ### path : " + str);
        }

        if (fileList == null) {
            System.out.println("# 파일이 없음");
        } else {
            int size = fileList.size();
            if (size > 0) {
                for (MultipartFile multipartFile : fileList) {
                    System.out.println("       ##  file name : " + multipartFile.getOriginalFilename() + " , size : " + multipartFile.getSize());
                }
            } else {
                System.out.println(" ## file empty");
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("result", "Y");
        return result;
    }

    /**
     * 자하권한 AP access log 목록
     *
     * @author shavrani 2016.11.14
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list")
    public String getJahaAptApAccessLogList(Model model, HttpServletRequest req) {
        return "jaha/apt-ap-access-log-list";
    }

    /**
     * 자하권한 AP access log 목록 데이터
     *
     * @author shavrani 2016.11.14
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list-data")
    @ResponseBody
    public Map<String, Object> getJahaAptApAccessLogListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params,
            @RequestParam(value = "apList", required = false) List<String> apList, @RequestParam(value = "userList", required = false) List<Long> userList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname, apid 컬럼 or 검색
        String aptId = StringUtil.nvl(params.get("aptId"));
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apId = StringUtil.nvl(params.get("apId"));
        String sDateTime = StringUtil.nvl(params.get("sDateTime"));
        String eDateTime = StringUtil.nvl(params.get("eDateTime"));
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);
        Integer limit = StringUtil.nvlInt(params.get("limit"), 100);
        params.put("inIds", apList);
        params.put("inUserIds", userList);

        String isActive = StringUtil.nvl(params.get("isActive"));
        if (!StringUtil.isBlank(isActive)) {
            if ("Y".equals(isActive)) {
                params.put("_active", true);
            } else if ("N".equals(isActive)) {
                params.put("_active", false);
            }
        }

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        List<AptApAccessLog> aaalList = edoorService.selectAptApAccessLogList(params);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", aaalList);

        if (nextToken == null) {
            params.remove("limit");
            int totalCount = edoorService.selectAptApAccessLogListCount(params);
            result.put("totalCount", totalCount);
        }

        return result;
    }

    private void isPartnerAuth(User user) {
        if (!user.type.jaha && !"apmanager@jahasmart.com".equals(user.getEmail())) {
            logger.info("해당페이지에 접근권한이 없습니다.");
            throw new EmaulWebException("해당페이지에 접근권한이 없습니다.");
        }
    }

    /**
     * 업체 AP 테스트 관리 리스트
     *
     * @author shavrani 2016.11.14
     */
    @RequestMapping(value = "/partner/apt/ap/access/log/inspection/list")
    public String getAptApAccessLogList(Model model, HttpServletRequest req) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        return "partner/gntel/apt-ap-access-log-inspection";
    }

    /**
     * 업체 AP 테스트 관리 리스트 데이터
     *
     * @author shavrani 2016.11.14
     */
    @RequestMapping(value = "/partner/apt/ap/access/log/inspection/list-data")
    @ResponseBody
    public List<Map<String, Object>> getAptApAccessLogInspectionListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params, @RequestParam(value = "apList",
            required = false) List<String> apList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String aptId = StringUtil.nvl(params.get("aptId"));
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apId = StringUtil.nvl(params.get("apId"));
        String apName = StringUtil.nvl(params.get("apName"));
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);
        Integer limit = StringUtil.nvlInt(params.get("limit"), 100);
        params.put("inIds", apList);

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        List<Map<String, Object>> result = edoorService.selectAptApAccessLogInspectionList(params);

        return result;
    }

    /**
     * 업체 AP 테스트 관리 리스트 데이터
     *
     * @author shavrani 2016.11.14
     */
    @RequestMapping(value = "/partner/apt/ap/access/log/inspection/detail/list")
    @ResponseBody
    public List<AptApAccessLog> getAptApAccessLogInspectionDetailList(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        Long id = StringUtil.nvlLong(params.get("id"), null);
        String type = StringUtil.nvl(params.get("type"));
        if (id == null || StringUtil.isBlank(type)) {
            logger.info("<< required parameter is empty >>");
            return null;
        }

        List<AptApAccessLog> result = edoorService.selectAptApAccessLogInspectionDetailList(params);

        return result;
    }

    /**
     * AP 검색 팝업
     */
    @RequestMapping(value = "/jaha/ap/search/popup")
    public String getApSearchPopup(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        String _aptId = StringUtil.nvl(params.get("_aptId"));
        if (!StringUtil.isBlank(_aptId)) {
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        return "jaha/ap-search-popup";
    }

    /**
     * AP 검색 팝업 데이터
     */
    @RequestMapping(value = "/jaha/ap/search/list-data")
    @ResponseBody
    public List<AptAp> getSearchApList(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname 컬럼 or 검색
        String aptId = StringUtil.nvl(params.get("aptId"));
        String aptSearchType = StringUtil.nvl(params.get("aptSearchType")); // 아파트 등록되지 않은 항목선택에만 쿼리에서 비교처리.
        String usePaging = StringUtil.nvl(params.get("usePaging")); // edoorService.getAptApList 에서 체크 param이 없으면 기본 페이징처리. N이면 페이징처리안함.
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apId = StringUtil.nvl(params.get("apId"));
        String existExpIp = StringUtil.nvl(params.get("existExpIp"));// Y 이면 ip가 입력된것만 조회

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        params.put("aptSearchType", "assign");// apt가 등록된 ap만 조회
        params.put("_active", true);

        List<AptAp> aptApList = edoorService.getAptApAllList(params);

        return aptApList;
    }

    /**
     * @author shavrani 2016.11.21
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/account/popup")
    public String aptApInspAccountPopup(HttpServletRequest req, Model model) {

        List<SimpleUser> qcUserList = edoorService.selectAptApInspAccountList("QC");// qc 테스트 계정 list
        List<SimpleUser> fieldUserList = edoorService.selectAptApInspAccountList("FD");// field 테스트 계정 list

        model.addAttribute("qcUserList", qcUserList);
        model.addAttribute("fieldUserList", fieldUserList);

        return "jaha/apt-ap-insp-account-popup";
    }

    /**
     * @author shavrani 2016.11.21
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/account/save")
    @ResponseBody
    public String aptApInspAccountSave(HttpServletRequest req, Model model, @RequestParam(value = "inspUser", required = false) List<Long> inspUserList, @RequestParam(value = "inspUserType",
            required = false) List<String> inspUserTypeList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String result = "N";

        List<Map<String, Object>> inspList = Lists.newArrayList();

        if (inspUserList != null) {
            int size = inspUserList.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> item = Maps.newHashMap();
                item.put("userId", inspUserList.get(i));
                item.put("type", inspUserTypeList.get(i));
                item.put("regId", user.id);
                inspList.add(item);
            }
        }

        int saveCnt = edoorService.saveInspAccount(inspList);
        if (saveCnt > 0) {
            result = "Y";
        }

        return result;
    }

    /**
     * @author shavrani 2016.11.28
     */
    @RequestMapping(value = "/apt/ap/inspection/list")
    public String aptApInspList(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        return "jaha/apt-ap-inspection-list";
    }

    private List<Integer> getBaseDateList(int baseDays) {
        List<Integer> baseDateList = Lists.newArrayList();
        for (int i = 1; i <= baseDays; i++) {
            baseDateList.add(i);
        }
        return baseDateList;
    }

    /**
     * @author shavrani 2016.11.30
     */
    @RequestMapping(value = "/apt/ap/inspection/daily/list-data")
    @ResponseBody
    public Map<String, Object> aptApInspDailyListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);

        if (StringUtil.isBlank(baseDate)) {
            return null;
        }

        if (baseDays != null) {
            params.put("baseDateList", getBaseDateList(baseDays));
        } else {
            return null;
        }

        Map<String, Object> result = Maps.newHashMap();

        List<Map<String, Object>> warningList = edoorService.selectAptApInspWarningAptList(params);
        result.put("dataList", warningList);

        // 전날 데이터검색이라서 처음검색 이후로 total count가 변할수 없음으로 처음 한번만 실행해도 무관
        if (nextToken == null) {
            params.remove("limit");
            int totalCount = edoorService.selectAptApInspWarningAptListCount(params);
            result.put("totalCount", totalCount);
        }

        return result;

    }

    /**
     * @author shavrani 2016.11.30
     */
    @RequestMapping(value = "/apt/ap/inspection/noti/list-data")
    @ResponseBody
    public Map<String, Object> aptApInspNotiListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);

        if (StringUtil.isBlank(baseDate)) {
            return null;
        }

        if (baseDays != null) {
            params.put("baseDateList", getBaseDateList(baseDays));
        } else {
            return null;
        }

        Map<String, Object> result = Maps.newHashMap();

        List<Map<String, Object>> warningList = edoorService.selectAptApInspNotiWarningAptList(params);
        result.put("dataList", warningList);

        // 전날 데이터검색이라서 처음검색 이후로 total count가 변할수 없음으로 처음 한번만 실행해도 무관
        if (nextToken == null) {
            params.remove("limit");
            int totalCount = edoorService.selectAptApInspNotiWarningAptListCount(params);
            result.put("totalCount", totalCount);
        }

        return result;

    }

    /**
     * @author shavrani 2016.11.30
     */
    @RequestMapping(value = "/apt/ap/inspection/daily/popup")
    public String aptApInspDailyEmptyPopup(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("aptId", aptId);
        model.addAttribute("baseDays", baseDays);

        if (!StringUtil.isBlank(aptId)) {
            params.put("_aptId", aptId);
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        return "jaha/apt-ap-insp-daily-popup";
    }

    /**
     * @author shavrani 2016.11.30
     */
    @RequestMapping(value = "/apt/ap/inspection/daily/empty/list-data")
    @ResponseBody
    public Map<String, Object> aptApInspDailyEmptyListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);

        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);

        if (StringUtil.isBlank(baseDate)) {
            return null;
        }

        if (baseDays != null) {
            params.put("baseDateList", getBaseDateList(baseDays));
        } else {
            return null;
        }

        StringBuilder headerHtml = new StringBuilder("");

        if (!StringUtil.isBlank(baseDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(baseDate));
                for (int i = 0; i < baseDays; i++) {
                    cal.add(Calendar.DATE, -1);
                    Date date = cal.getTime();
                    headerHtml.append("<td>");
                    headerHtml.append(sdf.format(date));
                    headerHtml.append("</td>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("headerHtml", headerHtml);
        result.put("resultList", edoorService.selectAptApInspDailyList(params));

        if (nextToken == null) {
            params.remove("limit");
            result.put("totalCount", edoorService.selectAptApInspDailyListCount(params));
        }

        return result;

    }

    /**
     * @author shavrani 2016.11.30
     */
    @RequestMapping(value = "/apt/ap/inspection/data-limit/popup")
    public String aptApInspDataLimitPopup(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("aptId", aptId);
        model.addAttribute("baseDays", baseDays);

        if (!StringUtil.isBlank(aptId)) {
            params.put("_aptId", aptId);
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        return "jaha/apt-ap-insp-data-limit-popup";
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/apt/ap/inspection/daily/data-limit/list-data")
    @ResponseBody
    public Map<String, Object> aptApInspDailyDataLimitListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);

        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);

        if (StringUtil.isBlank(baseDate)) {
            return null;
        }

        if (baseDays != null) {
            params.put("baseDateList", getBaseDateList(baseDays));
        } else {
            return null;
        }

        StringBuilder headerHtml = new StringBuilder("");

        if (!StringUtil.isBlank(baseDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                int tdWidth = (int) Math.floor(57 / baseDays);
                cal.setTime(sdf.parse(baseDate));
                for (int i = 0; i < baseDays; i++) {
                    cal.add(Calendar.DATE, -1);
                    Date date = cal.getTime();
                    headerHtml.append("<td style='width:" + tdWidth + "%;'>");
                    headerHtml.append(sdf.format(date));
                    headerHtml.append("</td>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("headerHtml", headerHtml);
        result.put("resultList", edoorService.selectAptApInspDataLimitList(params));

        if (nextToken == null) {
            params.remove("limit");
            result.put("totalCount", edoorService.selectAptApInspDataLimitListCount(params));
        }

        return result;

    }

    /**
     * @author shavrani 2016.12.06
     */
    @RequestMapping(value = "/apt/ap/inspection/noti/popup")
    public String aptApInspNotiPopup(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("aptId", aptId);
        model.addAttribute("baseDays", baseDays);

        if (!StringUtil.isBlank(aptId)) {
            params.put("_aptId", aptId);
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        return "jaha/apt-ap-insp-noti-popup";
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/apt/ap/inspection/noti/popup/list-data")
    @ResponseBody
    public Map<String, Object> aptApInspNotiPopupListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String baseDate = StringUtil.nvl(params.get("baseDate"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        Integer baseDays = StringUtil.nvlInt(params.get("baseDays"), null);
        Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);

        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);

        if (StringUtil.isBlank(baseDate)) {
            return null;
        }

        if (baseDays != null) {
            params.put("baseDateList", getBaseDateList(baseDays));
        } else {
            return null;
        }

        StringBuilder headerHtml = new StringBuilder("");

        if (!StringUtil.isBlank(baseDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(baseDate));
                for (int i = 0; i < baseDays; i++) {
                    cal.add(Calendar.DATE, -1);
                    Date date = cal.getTime();
                    headerHtml.append("<td colspan='2'>");
                    headerHtml.append(sdf.format(date));
                    headerHtml.append("</td>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("headerHtml", headerHtml);
        result.put("resultList", edoorService.selectAptApInspNotiList(params));

        if (nextToken == null) {
            params.remove("limit");
            result.put("totalCount", edoorService.selectAptApInspNotiListCount(params));
        }

        return result;

    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/apt/ap/monitoring/noti/list-data")
    @ResponseBody
    public List<AptApMonitoringNoti> aptApMonitoringNotiListData(HttpServletRequest req, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        isPartnerAuth(user);

        String id = StringUtil.nvl(params.get("id"));
        String aptId = StringUtil.nvl(params.get("aptId"));
        String date = StringUtil.nvl(params.get("date"));

        if (StringUtil.isBlank(id)) {
            return null;
        }
        if (StringUtil.isBlank(aptId)) {
            return null;
        }
        if (StringUtil.isBlank(date)) {
            return null;
        }

        return edoorService.selectAptApMonitoringNotiList(params);

    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/setting/data-limit/list")
    @ResponseBody
    public List<CommonCode> telecomDataLimitList(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        return commonService.findByCodeGroup("DATA_LIMIT");
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/setting/data-limit/save")
    @ResponseBody
    public String saveTelecomDataLimit(HttpServletRequest req, Model model, @RequestParam(value = "code") String[] code, @RequestParam(value = "dataLimit") String[] dataLimit, @RequestParam(
            value = "warningPercent") String[] warningPercent) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (code == null || code.length == 0) {
            return "N";
        }

        edoorService.saveSettingDataLimit(code, dataLimit, warningPercent);

        return "Y";
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/setting/send-mail-user/list")
    @ResponseBody
    public List<SimpleUser> monitoringSendMailUserList(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        return edoorService.selectAptApInspAccountList("MAIL");
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/jaha/apt/ap/inspection/setting/send-mail-user/save")
    @ResponseBody
    public String saveMonitoringSendMailUser(HttpServletRequest req, Model model, @RequestParam(value = "userId", required = false) List<Long> inspUserList) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        List<Map<String, Object>> inspList = Lists.newArrayList();

        if (inspUserList != null) {
            int size = inspUserList.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> item = Maps.newHashMap();
                item.put("userId", inspUserList.get(i));
                item.put("type", "MAIL");
                item.put("regId", user.id);
                inspList.add(item);
            }
        }

        edoorService.saveInspMailAccount(inspList);

        return "Y";
    }

    /**
     * @author shavrani 2016.12.05
     */
    @RequestMapping(value = "/jaha/apt/ap/access-auth/batch-save")
    @ResponseBody
    public Map<String, Object> apAccessAuthBatchSave(HttpServletRequest req, @RequestParam(value = "authFile") MultipartFile authFile, @RequestParam(value = "authSaveAptId") Long authSaveAptId) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        return edoorService.saveAptApAccessAuthFile(user, authFile, authSaveAptId);
    }

    /**
     * @author shavrani 2017-03-08
     * 
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list/count")
    @ResponseBody
    public int accessLogListCount(HttpServletRequest req, HttpServletResponse res, Model model, @RequestParam Map<String, Object> params,
            @RequestParam(value = "apList", required = false) List<String> apList, @RequestParam(value = "userList", required = false) List<Long> userList) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname, apid 컬럼 or 검색
        String aptId = StringUtil.nvl(params.get("aptId"));
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        String apId = StringUtil.nvl(params.get("apId"));
        String sDateTime = StringUtil.nvl(params.get("sDateTime"));
        String eDateTime = StringUtil.nvl(params.get("eDateTime"));
        params.put("inIds", apList);
        params.put("inUserIds", userList);

        String isActive = StringUtil.nvl(params.get("isActive"));
        if (!StringUtil.isBlank(isActive)) {
            if ("Y".equals(isActive)) {
                params.put("_active", true);
            } else if ("N".equals(isActive)) {
                params.put("_active", false);
            }
        }

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        params.remove("limit");
        params.remove("nextToken");

        return edoorService.selectAptApAccessLogListCount(params);

    }

    /**
     * 자하권한 AP access log 목록 데이터 엑셀 다운로드
     *
     * @author realsnake 2017.01.10
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list/excel-download")
    public void downloadExcelJahaAptApAccessLogListData(HttpServletRequest req, HttpServletResponse res, Model model, @RequestParam Map<String, Object> params, @RequestParam(value = "apList",
            required = false) List<String> apList, @RequestParam(value = "userList", required = false) List<Long> userList) throws Exception {

        // User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        // String searchKeyword = StringUtil.nvl(params.get("searchKeyword"));// beacon uuid, apname, apid 컬럼 or 검색
        // String aptId = StringUtil.nvl(params.get("aptId"));
        String ids = StringUtil.nvl(params.get("ids"));// ex) 1, 5-13 다중 범위선택 가능 parameter
        // String apId = StringUtil.nvl(params.get("apId"));
        String sDateTime = StringUtils.replace(StringUtil.nvl(params.get("sDateTime")), "+", " ");
        String eDateTime = StringUtils.replace(StringUtil.nvl(params.get("eDateTime")), "+", " ");
        params.put("sDateTime", sDateTime);
        params.put("eDateTime", eDateTime);
        // Long nextToken = StringUtil.nvlLong(params.get("nextToken"), null);
        // Integer limit = StringUtil.nvlInt(params.get("limit"), 100);
        params.put("inIds", apList);
        params.put("inUserIds", userList);

        params.remove("limit");
        params.remove("nextToken");

        String isActive = StringUtil.nvl(params.get("isActive"));
        if (!StringUtil.isBlank(isActive)) {
            if ("Y".equals(isActive)) {
                params.put("_active", true);
            } else if ("N".equals(isActive)) {
                params.put("_active", false);
            }
        }

        // id를 범위및 여러개 검색할수있게 처리.
        List<Map<String, Object>> idSearchList = getParamApIds(ids);
        params.put("ids", idSearchList);

        try {
            List<AptApAccessLog> accessLogList = edoorService.selectAptApAccessLogList(params);

            final String[] headers = {"순번", "아파트", "ID", "AP ID", "AP 이름", "회원ID", "사용자", "비콘카드 사용자", "출입시간", "출입완료여부", "메모", "단말모델", "단말OS"};
            final ExcelFileBuilder excelFileBuilder = new ExcelFileBuilder("Sheet1");
            excelFileBuilder.setHeaders(headers);

            if (accessLogList != null && accessLogList.size() > 0) {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                int accessLogSize = accessLogList.size();

                if (accessLogSize < 65536) {
                    for (AptApAccessLog accessLog : accessLogList) {
                        int col = 0;
                        excelFileBuilder.setDataValue(col++, accessLogSize--);
                        excelFileBuilder.setDataValue(col++, accessLog.aptName);
                        excelFileBuilder.setDataValue(col++, accessLog.apId);
                        excelFileBuilder.setDataValue(col++, accessLog.aptApId);
                        excelFileBuilder.setDataValue(col++, accessLog.apName);
                        excelFileBuilder.setDataValue(col++, String.valueOf(accessLog.userId));
                        excelFileBuilder.setDataValue(col++, accessLog.getFullName());
                        excelFileBuilder.setDataValue(col++, accessLog.secondUser);
                        excelFileBuilder.setDataValue(col++, sdf.format(accessLog.accessDate));
                        excelFileBuilder.setDataValue(col++, accessLog.success);
                        excelFileBuilder.setDataValue(col++, accessLog.memo);
                        excelFileBuilder.setDataValue(col++, accessLog.mobileDeviceModel);
                        excelFileBuilder.setDataValue(col++, accessLog.mobileDeviceOs);
                        excelFileBuilder.nextRow();
                    }
                } else {
                    int col = 0;
                    excelFileBuilder.setDataValue(col++, "검색결과 " + accessLogSize + "건으로 엑셀 최대 행사이즈(65535) 를 초과하였습니다. 다시 검색하여 주십시오.");
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.nextRow();
                }
            }

            excelFileBuilder.autoSizeColumns();
            Responses.downloadEexcel("AptApAccessLogListExcel-" + Util.getNowDate() + ".xls", excelFileBuilder, req, res);
        } catch (Exception e) {
            logger.error("<<아파트 AP 출입 로그 엑셀 다운로드 중 오류>>", e);
            throw e;
        }
    }

    /**
     * Edoor 서버에 구현된 method지만 web에서도 사용될수있을것 같아 동일하게 구현함.
     * 
     * @author shavrani 2017.01.11
     */
    @RequestMapping(value = "/api/public/apt/ap/monitoring/health/check")
    @ResponseBody
    public String aptApMonitoringHealthCheck(HttpServletRequest req, @RequestParam(value = "storagePeriod", required = false) Integer storagePeriod) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = Maps.newHashMap();
        params.put("aptSearchType", "notTestApt");
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
        params.put("existExpIp", "Y");
        params.put("_active", true);

        List<AptAp> apList = edoorService.getAptApAllList(params);

        if (storagePeriod == null) {
            storagePeriod = 7;// 기본 7일간 유지
        }

        Map<String, Object> result = edoorService.aptApMonitoringHealthCheck(user, apList, storagePeriod);

        return "OK";
    }

    /**
     * 팝업등에서 검색조건 물고가기위한 ap데이터 항목 정리.
     */
    private List<Map<String, Object>> setApListParam(List<Long> apList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("aptSearchType", "notTestApt");
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
        params.put("inIds", apList);
        params.put("_active", true);
        List<AptAp> aptApList = edoorService.getAptApAllList(params);
        List<Map<String, Object>> itemList = Lists.newArrayList();
        aptApList.forEach(item -> {
            Map<String, Object> apItem = Maps.newHashMap();
            apItem.put("id", item.id);
            apItem.put("apId", item.apId);
            apItem.put("apName", item.apName);
            apItem.put("apBeaconUuid", item.apBeaconUuid);
            itemList.add(apItem);
        });

        return itemList;
    }

    /**
     * @author shavrani 2017.01.24
     */
    @RequestMapping(value = "/jaha/apt/ap/health/check/popup")
    public String aptApHealthCheckPopup(Model model, HttpServletRequest req, @RequestParam(value = "apList", required = false) List<Long> apList) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (apList != null && !apList.isEmpty()) {
            model.addAttribute("apList", setApListParam(apList));
        }

        return "jaha/apt-ap-realtime-healthcheck-popup";
    }

    /**
     * @author shavrani 2017.01.24
     */
    @RequestMapping(value = "/jaha/apt/ap/health/check")
    @ResponseBody
    public Map<String, Object> aptApHealthCheck(HttpServletRequest req, @RequestParam(value = "aptId", required = false) Long aptId, @RequestParam(value = "apList", required = false) List<Long> apList) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Map<String, Object> params = Maps.newHashMap();
        // 실시간 health check는 모든 아파트가 가능하게 처리.
        // params.put("aptSearchType", "notTestApt");
        // params.put("testAptId", Constants.AP_TEST_APT_ID);
        // params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
        // params.put("existExpIp", "Y");// 주석처리. 유저가 직접 실행할경우 ip없는항은 ip설정이 안되어있다고 결과에 보여주기위해 시스템에서 ip 있는항목만 검색되게 처리하지않음.
        if (aptId != null) {
            params.put("aptId", aptId);
        }
        if (apList != null && !apList.isEmpty()) {
            params.put("inIds", apList);
        }
        params.put("_active", true);

        List<AptAp> aptApList = edoorService.getAptApAllList(params);

        Map<String, Object> result = edoorService.aptApRealTimeHealthCheck(user, aptApList);

        return result;
    }

    /**
     * @author shavrani 2017.01.16
     */
    @RequestMapping(value = "/jaha/apt/ap/monitoring/health/check/list")
    public String aptApHealthCheckList(HttpServletRequest req) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        return "jaha/apt-ap-health-check-list";
    }

    @RequestMapping(value = "/jaha/apt/ap/monitoring/health/check/list-data")
    @ResponseBody
    public Map<String, Object> aptApHealthCheckListData(HttpServletRequest req, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "apList", required = false) List<String> apList) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pageSize);
        params.put("pagingHelper", pagingHelper);

        params.put("inIds", apList);

        List<String> baseDateList = Lists.newArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar sCal = Calendar.getInstance();
            sCal.setTime(sdf.parse(sDate));

            Calendar eCal = Calendar.getInstance();
            eCal.setTime(sdf.parse(eDate));

            while (eCal.after(sCal) || sCal.equals(eCal)) {
                baseDateList.add(sdf.format(eCal.getTime()));
                eCal.add(Calendar.DATE, -1);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        params.put("baseDateList", baseDateList);
        params.put("aptSearchType", "notTestApt");
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
        // params.put("status", "1"); 1 : 장애가 포함된항목, 2 : 모두정상인항목

        List<Map<String, Object>> monitoringAliveList = edoorService.selectAptApHealthCheckList(params);
        int totalRecordCount = edoorService.selectAptApHealthCheckListCount(params);
        pagingHelper.setTotalRecordCount(totalRecordCount);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", monitoringAliveList);
        result.put("pagingHelper", pagingHelper);

        return result;
    }

    @RequestMapping(value = "/jaha/apt/ap/monitoring/health/check/list/excel-download")
    @ResponseBody
    public void aptApHealthCheckListExcel(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> params,
            @RequestParam(value = "apList", required = false) List<String> apList) throws Exception {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        params.put("inIds", apList);

        List<String> baseDateList = Lists.newArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar sCal = Calendar.getInstance();
            sCal.setTime(sdf.parse(sDate));

            Calendar eCal = Calendar.getInstance();
            eCal.setTime(sdf.parse(eDate));

            while (eCal.after(sCal) || sCal.equals(eCal)) {
                baseDateList.add(sdf.format(eCal.getTime()));
                eCal.add(Calendar.DATE, -1);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        params.put("baseDateList", baseDateList);
        params.put("aptSearchType", "notTestApt");
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        params.put("excludeAptId", Constants.AP_EXCLUDE_APT_ID);
        // params.put("status", "1"); 1 : 장애가 포함된항목, 2 : 모두정상인항목

        List<Map<String, Object>> monitoringAliveList = edoorService.selectAptApHealthCheckList(params);


        try {
            List<String> headers = Lists.newArrayList("No.", "아파트", "ID", "AP Id", "AP 이름");

            for (String baseDate : baseDateList) {
                headers.add(baseDate);
            }

            ExcelFileBuilder excelFileBuilder = new ExcelFileBuilder("Sheet1");
            excelFileBuilder.setHeaders(headers.toArray(new String[headers.size()]));

            if (monitoringAliveList != null && monitoringAliveList.size() > 0) {

                int size = monitoringAliveList.size();

                if (size < 65536) {
                    int no = size;
                    for (Map<String, Object> item : monitoringAliveList) {
                        int col = 0;
                        excelFileBuilder.setDataValue(col++, no--);
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(item.get("aptName")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(item.get("id")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(item.get("apId")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(item.get("apName")));

                        for (int i = 1; i <= baseDateList.size(); i++) {
                            String day = StringUtil.nvl(item.get("day" + i));
                            day = day.replace(",", "\n\r");
                            excelFileBuilder.setDataValue(col++, day);
                        }

                        excelFileBuilder.nextRow();
                    }
                } else {
                    int col = 0;
                    excelFileBuilder.setDataValue(col++, "검색결과 " + size + "건으로 엑셀 최대 행사이즈(65535) 를 초과하였습니다. 다시 검색하여 주십시오.");
                    excelFileBuilder.nextRow();
                }
            }

            excelFileBuilder.autoSizeColumns();
            Responses.downloadEexcel("AptApHealthCheckListExcel-" + Util.getNowDate() + ".xls", excelFileBuilder, req, res);
        } catch (Exception e) {
            logger.error("<<아파트 AP Health Check 엑셀 다운로드 중 오류>>", e);
            throw e;
        }


    }

    /**
     * 자하권한 AP access log 목록 팝업
     *
     * @author shavrani 2017.01.24
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list/popup")
    public String getJahaAptApAccessLogListPopup(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) {

        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        Long apId = StringUtil.nvlLong(params.get("apId"), null);
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        if (aptId != null) {
            params.put("_aptId", aptId);// commonService.selectAddressAptList를 활용하려면 _aptId로 검색해야함. ( 언더바aptId )
            List<Map<String, Object>> resultList = commonService.selectAddressAptList(params);
            if (resultList != null && !resultList.isEmpty()) {
                model.addAttribute("apt", resultList.get(0));
            }
        }

        if (apId != null) {
            List<Long> apList = Lists.newArrayList(apId);
            model.addAttribute("apList", setApListParam(apList));
        }

        if (!StringUtil.isBlank(sDate) && !StringUtil.isBlank(eDate)) {
            model.addAttribute("sDate", sDate);
            model.addAttribute("eDate", eDate);
        }

        return "jaha/apt-ap-access-log-list-popup";
    }

    /**
     * 자하권한 AP access log 목록 팝업 데이터
     *
     * @author shavrani 2017.01.24
     */
    @RequestMapping(value = "/jaha/apt/ap/access/log/list/popup-data")
    @ResponseBody
    public Map<String, Object> getJahaAptApAccessLogListPopupData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params, PagingHelper pagingHelper, @RequestParam(
            value = "apList", required = false) List<String> apList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pageSize);
        params.put("pagingHelper", pagingHelper);

        params.put("inIds", apList);

        List<AptApAccessLog> aaalList = edoorService.selectAptApAccessLogList(params);
        int totalRecordCount = edoorService.selectAptApAccessLogListCount(params);

        pagingHelper.setTotalRecordCount(totalRecordCount);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", aaalList);
        result.put("pagingHelper", pagingHelper);

        return result;
    }



}
