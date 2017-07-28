package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.model.AppVersion;
import com.jaha.web.emaul.model.CodeGroup;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.GcmSendForm;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.GcmService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.ParcelService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;

/**
 * Created by doring on 15. 4. 2..
 */
@Controller
public class CommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private CommonService commonService;
    @Autowired
    private GcmService gcmService;
    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private ParcelService parcelService;


    @Value("${file.path.temp}")
    private String tempFilePath;

    @Value("${file.path.editor.image}")
    private String editorImagePath;

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @RequestMapping(method = RequestMethod.GET, value = "/api/public/common/app-version/{kind}")
    public @ResponseBody AppVersion handleAppVersionRequest(@PathVariable(value = "kind") String kind) {
        return commonService.getAppVersion(kind);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/jaha/gcm/send")
    public @ResponseBody String handleGcmSend(HttpServletRequest req, @RequestBody String json) {

        JSONObject obj = new JSONObject(json);
        Map<String, String> msg = Maps.newHashMap();
        msg.put("type", obj.getString("type"));
        msg.put("title", obj.getString("title"));
        msg.put("value", obj.getString("message"));
        msg.put("action", obj.getString("action"));

        boolean isTargetAll = obj.getBoolean("isTargetAll");

        GcmSendForm form = new GcmSendForm();
        form.setMessage(msg);
        if (isTargetAll) {
            form.setSendToAll(false);
        } else {
            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            List<House> houses = houseService.getHouses(user.house.apt.id);
            List<Long> houseIds = Lists.transform(houses, input -> input.id);
            List<User> users = userService.getUsersByHouseIn(houseIds);
            form.setUserIds(Lists.transform(users, input -> input.id));
        }
        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description 아파트관리비 GCM 푸쉬 발송
     *
     * @param req
     * @param json
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/common/gcm/aptfee-send")
    @ResponseBody
    public String handleGcmAptFeeSend(HttpServletRequest req, @RequestBody String json) {
        JSONObject obj = new JSONObject(json);
        Map<String, String> msg = Maps.newHashMap();
        msg.put("type", obj.getString("type"));
        msg.put("title", obj.getString("title"));
        msg.put("value", obj.getString("message"));
        msg.put("action", obj.getString("action"));

        GcmSendForm form = new GcmSendForm();
        form.setSendToAll(false);
        form.setMessage(msg);

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        List<House> houses = houseService.getHouses(user.house.apt.id);
        List<Long> houseIds = Lists.transform(houses, input -> input.id);

        List<User> users = userService.getUsersByHouseIn(houseIds);

        List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.setting.notiFeePush; // 관리비 푸시알림 설정을 한 사용자만
            }
        }));
        List<Long> pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);

        form.setUserIds(pushApplyUserIds);

        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description 발송자의 아파트 주민 대상으로만 GCM 푸쉬 발송, 현재는 notiAlarm이 true인 사용자에게만 발송함
     *
     * @param req
     * @param json
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/common/gcm/send-per-apt")
    @ResponseBody
    public String handleGcmSendPerApt(HttpServletRequest req, @RequestBody String json) {
        JSONObject obj = new JSONObject(json);
        Map<String, String> msg = Maps.newHashMap();
        msg.put("type", obj.getString("type"));
        msg.put("title", obj.getString("title"));
        msg.put("value", obj.getString("message"));
        msg.put("action", obj.getString("action"));
        final String target = obj.getString("target"); // notiAlarm

        if (StringUtils.isBlank(target)) {
            LOGGER.info("* 푸시 대상(notiAlarm 등)이 존재하지 않아 발송 작업을 중단합니다!");
            return null;
        }

        GcmSendForm form = new GcmSendForm();
        form.setSendToAll(false);
        form.setMessage(msg);

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        List<House> houses = houseService.getHouses(user.house.apt.id);
        List<Long> houseIds = Lists.transform(houses, input -> input.id);

        List<User> users = userService.getUsersByHouseIn(houseIds);

        List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.setting.notiAlarm; // 푸시알림 설정을 한 사용자만
            }
        }));
        List<Long> pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);

        form.setUserIds(pushApplyUserIds);

        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description target(users/user/admin)과 id로 GCM 푸쉬 발송하기
     *
     * @param req
     * @param json
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/common/gcm/send/{target}/{id}")
    @ResponseBody
    public String handleGcmSendByAptId(HttpServletRequest req, @PathVariable(value = "target") final String target, @PathVariable(value = "id") final Long id,
            @RequestParam(value = "json", required = true) final String json, @RequestParam(value = "setting", required = true) final String setting) {
        JSONObject obj = new JSONObject(json);
        Map<String, String> msg = Maps.newHashMap();
        msg.put("type", obj.getString("type"));
        msg.put("title", obj.getString("title"));
        msg.put("value", obj.getString("message"));
        msg.put("action", obj.getString("action"));

        GcmSendForm form = new GcmSendForm();
        form.setSendToAll(false);
        form.setMessage(msg);

        List<Long> pushApplyUserIds = null;

        if ("users".equals(target)) {
            List<House> houses = houseService.getHouses(id); // 아파트 아이디
            List<Long> houseIds = Lists.transform(houses, input -> input.id);

            List<User> users = userService.getUsersByHouseIn(houseIds);

            List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
                @Override
                public boolean apply(User input) {
                    if ("notiFeePush".equals(setting)) {
                        return input.setting.notiFeePush; // 관리비 푸시알림 설정을 한 사용자만
                    } else if ("notiAlarm".equals(setting)) {
                        return input.setting.notiAlarm;
                    } else if ("notiAirPollution".equals(setting)) {
                        return input.setting.notiAirPollution;
                    } else if ("notiParcel".equals(setting)) {
                        return input.setting.notiParcel;
                    } else if ("notiPostCommentReply".equals(setting)) {
                        return input.setting.notiPostCommentReply;
                    } else if ("notiPostReply".equals(setting)) {
                        return input.setting.notiPostReply;
                    } else {
                        return false;
                    }
                }
            }));
            pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);
        } else if ("user".equals(target)) {
            pushApplyUserIds = new ArrayList<Long>();
            pushApplyUserIds.add(id); // 사용자 아이디
        } else if ("admin".equals(target)) {
            List<User> users = userService.getAllAptUsers(id); // 아파트 아이디

            List<User> applyAdmins = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {

                @Override
                public boolean apply(User input) {
                    return input.type.admin;
                }

            }));
            pushApplyUserIds = Lists.transform(applyAdmins, input -> input.id);
        }

        form.setUserIds(pushApplyUserIds);

        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    @Deprecated
    @RequestMapping(value = "/test/gcm/send")
    @ResponseBody
    public String testGcmSend(HttpServletRequest req, @RequestBody(required = false) String json) {
        if (StringUtils.isEmpty(json)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"").append("type").append("\"").append(":").append("\"").append("\"");
            sb.append(",").append("\"").append("title").append("\"").append(":").append("\"").append("\"");
            sb.append(",").append("\"").append("message").append("\"").append(":").append("\"").append("\"");
            sb.append(",").append("\"").append("action").append("\"").append(":").append("\"").append("\"");
            sb.append(",").append("\"").append("isTargetAll").append("\"").append(":").append("\"").append("false").append("\"");
            sb.append("}");
            json = sb.toString();
        }
        JSONObject obj = new JSONObject(json);
        Map<String, String> msg = Maps.newHashMap();

        msg.put("type", StringUtils.defaultIfEmpty(obj.getString("type"), "action"));
        msg.put("title", StringUtils.defaultIfEmpty(obj.getString("title"), "관리비 푸시알림"));
        // msg.put("titleResId", "notice");
        msg.put("value", StringUtils.defaultIfEmpty(obj.getString("message"), "관리비가 부과되었습니다."));
        msg.put("action", StringUtils.defaultIfEmpty(obj.getString("action"), "emaul://aptfee")); // 관리비 > 관리비 조회 > 청구내역 조회

        boolean isTargetAll = obj.getBoolean("isTargetAll");
        LOGGER.debug("* 테스트를 위해 전체 발송은 강제로 false로 세팅");
        isTargetAll = false;

        GcmSendForm form = new GcmSendForm();
        form.setMessage(msg);

        if (isTargetAll) {
            // form.setSendToAll(true);
        } else {
            Long userId = 5334L; // 전강욱
            User user = userService.getUser(userId);
            List<House> houses = houseService.getHouses(user.house.apt.id);
            List<Long> houseIds = Lists.transform(houses, input -> input.id);
            List<User> users = userService.getUsersByHouseIn(houseIds);

            List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
                @Override
                public boolean apply(User input) {
                    return input.setting.notiFeePush;
                }
            }));
            List<Long> pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);

            form.setUserIds(pushApplyUserIds);
        }
        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    /**
     * @author shavrani 2016.05.31
     */
    @RequestMapping(value = "/api/common/editor/image/temp/upload", method = RequestMethod.POST)
    public @ResponseBody String editorImageUpload(@RequestParam(value = "upload", required = false) MultipartFile img) {

        deleteOldTempFiles();

        String originalFileName = img.getOriginalFilename();
        String ext = FilenameUtils.getExtension(originalFileName);
        try {
            File dir = new File(tempFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setWritable(true, false);
            }

            File dest = new File(dir, String.format("%s.%s", RandomKeys.make(16), ext));
            while (dest.exists()) {
                dest = new File(dir, String.format("%s.%s", RandomKeys.make(16), ext));
            }
            dest.createNewFile();
            dest.setReadable(true, false);
            dest.setWritable(true, false);

            img.transferTo(dest);

            JSONObject obj = new JSONObject();
            obj.put("fileName", dest.getName());
            obj.put("uploaded", 1);
            obj.put("url", "/api/common/temp/image/" + dest.getName());

            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    /**
     * @author shavrani 2016.05.31
     */
    private void deleteOldTempFiles() {
        File dir = new File(tempFilePath);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return System.currentTimeMillis() - pathname.lastModified() > 24 * 60 * 60 * 1000;
            }
        });
        if (files != null && files.length != 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * @author shavrani 2016.05.31
     */
    @RequestMapping(value = "/api/common/temp/image/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> editorTempImageView(@PathVariable("fileName") String fileName) {

        File toServeUp = new File(tempFilePath, fileName);

        return Responses.getFileEntity(toServeUp, fileName);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @RequestMapping(value = "/api/common/editor/image/{middlePath}/{id}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> editorImageView(@PathVariable("middlePath") String middlePath, @PathVariable("id") String id, @PathVariable("fileName") String fileName) {

        File toServeUp = new File(String.format(editorImagePath + "/%s/%s", middlePath, id), fileName);

        return Responses.getFileEntity(toServeUp, fileName);
    }

    /**
     * CKEditor 다이알로그 창에서 이미지 업로드
     *
     * @param request
     * @param response
     * @param upload
     */
    @RequestMapping(value = "/api/common/editor/image/temp/dialog-upload", method = RequestMethod.POST)
    public void editorImageUploadForCk(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "upload", required = false) MultipartFile upload) {
        PrintWriter printWriter = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        this.deleteOldTempFiles();

        String originalFileName = upload.getOriginalFilename();
        String ext = FilenameUtils.getExtension(originalFileName);

        try {
            File dir = new File(tempFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setWritable(true, false);
            }

            File dest = new File(dir, String.format("%s.%s", RandomKeys.make(16), ext));
            while (dest.exists()) {
                dest = new File(dir, String.format("%s.%s", RandomKeys.make(16), ext));
            }
            dest.createNewFile();
            dest.setReadable(true, false);
            dest.setWritable(true, false);

            upload.transferTo(dest);

            String callback = request.getParameter("CKEditorFuncNum");
            printWriter = response.getWriter();
            String fileUrl = "/api/common/temp/image/" + FilenameUtils.getName(dest.getCanonicalPath());

            LOGGER.debug("<<이미지 저장 경로>> {}", dest.getCanonicalPath());
            LOGGER.debug("<<이미지 URL 경로>> {}", "/api/common/temp/image/" + FilenameUtils.getName(dest.getCanonicalPath()));

            printWriter.println("<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" + callback + ", '" + fileUrl + "', ' 이미지를 업로드하였습니다. '" + ")</script>");
            printWriter.flush();
        } catch (Exception e) {
            LOGGER.error("<<CK에디터 이미지 파일 업로드 중 오류>> {}", e.getMessage());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    @Deprecated
    @RequestMapping(value = "/common/gcm/temp/send")
    @ResponseBody
    public String sendTempGcm(HttpServletRequest req) throws Exception {
        // int[] aptIds = {1, 191};

        Map<String, String> msg = Maps.newHashMap();

        msg.put("type", "action");
        // msg.put("titleResId", "notice");

        LOGGER.debug("<<springProfilesActive>> {}", springProfilesActive);
        if ("real".equals(springProfilesActive)) {
            msg.put("title", "오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("value", "오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("action", "emaul://today-detail?id=11442"); // 커뮤니티 이동 시 "emaul://post-detail?id=11442"
        } else if ("dev".equals(springProfilesActive)) {
            msg.put("title", "<<테스트-개발>>오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("value", "<<테스트-개발>>오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("action", "emaul://today-detail?id=8600"); // 커뮤니티 이동 시 "emaul://post-detail?id=8600"
        } else if ("local".equals(springProfilesActive)) {
            msg.put("title", "<<테스트-로컬>>오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("value", "<<테스트-로컬>>오페라 라 트라비아타 무료 초대 이벤트, 놓치면 손해!");
            msg.put("action", "emaul://today-detail?id=8600"); // 커뮤니티 이동 시 "emaul://post-detail?id=8600"
        }

        GcmSendForm form = new GcmSendForm();
        form.setMessage(msg);

        /**
         * List<User> users = new ArrayList<User>();
         *
         * for (int aptId : aptIds) { List<User> userList = this.userService.getAllAptUsers(Long.valueOf(aptId)); users.addAll(userList); }
         */
        List<User> users = this.userService.getAllUsers();

        List<User> pushApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.setting.notiAlarm;
            }
        }));
        List<Long> pushApplyUserIds = Lists.transform(pushApplyUsers, input -> input.id);
        List<Long> finalPushingUserIds = new ArrayList<Long>(new LinkedHashSet<Long>(pushApplyUserIds));

        for (Long finalPushingUserId : finalPushingUserIds) {
            LOGGER.debug("<<사용자ID>> {}", finalPushingUserId);
        }

        if ("real".equals(springProfilesActive)) {
            // List<Long> userIds = new ArrayList<Long>();
            // userIds.add(2808L);
            // userIds.add(5334L);
            // userIds.add(7274L);
            // form.setUserIds(userIds);
            form.setUserIds(finalPushingUserIds);
        } else if ("dev".equals(springProfilesActive)) {
            List<Long> userIds = new ArrayList<Long>();
            userIds.add(5334L);
            form.setUserIds(userIds);
        } else if ("local".equals(springProfilesActive)) {
            List<Long> userIds = new ArrayList<Long>();
            userIds.add(5334L);
            form.setUserIds(userIds);
        }

        gcmService.sendGcm(form);

        return "{\"result\":\"success\"}";
    }

    @RequestMapping(value = "/api/public/parcel-lockers-batch", method = RequestMethod.GET)
    @ResponseBody
    public String parcelLockersLongTermBatch() {

        try {
            this.parcelService.updateParcelDelStatus();
        } catch (Exception e) {
            LOGGER.error("<택배 삭제업데이트 에러>", e);
        }
        return "OK";
    }



    /**
     * App 친구초대 > App 다운로드 ios, android 공통 페이지
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/app/download")
    public String appDownload(HttpServletRequest request, Model model) {
        try {
            // library상 지속적으로 업데이트 되는 라이브러리를 찾지 못함 (spring-mobile gradle 삭제)
            String userAgent = request.getHeader("user-agent").toLowerCase();
            LOGGER.debug(">>> userAgent : " + userAgent);
            if (userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
                model.addAttribute("device", "ios");
            } else if (userAgent.contains("android")) {
                model.addAttribute("device", "android");
            } else {
                model.addAttribute("device", "pc");
            }

        } catch (Exception e) {
            LOGGER.error(">>> 앱 다운로드 페이지 오류", e);
        }

        return "download";
    }

    /**
     * 코드관리 > 코드그룹 조회
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/v2/jaha/code/group-list")
    public String jahaCodeGroupList(HttpServletRequest request, Model model, CodeGroup codeGroup) {
        try {

            User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

            if (!user.type.jaha) {
                LOGGER.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }

            model.addAttribute("codeGroupList", commonService.getCodeGroup(codeGroup));

        } catch (Exception e) {
            LOGGER.error("<<App 버전 리스트 조회 중 오류>>", e);
        }

        return "v2/jaha/code/code-list";
    }



    /**
     * 코드관리 > 공통코드 조회
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/v2/jaha/code/code-list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> jahaCodeList(HttpServletRequest request, @RequestBody String json) {

        Map<String, Object> map = new HashMap<>();
        JSONObject obj = new JSONObject(json);
        try {
            LOGGER.debug(">>> codeGroup : " + obj.getString("codeGroup"));
            map.put("codeList", this.commonService.findByCodeGroup(obj.getString("codeGroup")));
        } catch (Exception e) {
            LOGGER.error("<<< 공통코드 조회 오류 >>>", e);
        }
        return map;
    }


    /**
     * 코드그룹 등록
     *
     * @param request
     * @param codeGroup
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/jaha/code/set-code-group", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> setJahaCodegroup(HttpServletRequest request, CodeGroup codeGroup) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            LOGGER.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
        }

        try {
            LOGGER.debug(">>> codeGroup : " + codeGroup.getCodeGroup());
            LOGGER.debug(">>> name : " + codeGroup.getName());
            LOGGER.debug(">>>  description : " + codeGroup.getDescription());
            codeGroup.setRegId(String.valueOf(user.id));
            commonService.insertCodeGroup(codeGroup);

            resultMap.put("result", true);
            resultMap.put("codeGroup", codeGroup.getCodeGroup());
        } catch (Exception e) {
            resultMap.put("result", false);
        }

        return resultMap;
    }



    /**
     * CommonCode 등록
     *
     * @param request
     * @param commonCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/jaha/code/set-code", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> setJahaCode(HttpServletRequest request, CommonCode commonCode) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            LOGGER.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
        }

        try {


            LOGGER.debug(">>> codeGroup : " + commonCode.getCodeGroup());
            LOGGER.debug(">>> name : " + commonCode.getName());
            LOGGER.debug(">>>  code : " + commonCode.getCode());
            commonCode.setRegId(String.valueOf(user.id));
            commonService.insertCommonCode(commonCode);

            resultMap.put("result", true);
            resultMap.put("code", commonCode.getCode());
            resultMap.put("codeGroup", commonCode.getCodeGroup());
        } catch (Exception e) {
            resultMap.put("result", false);
        }
        return resultMap;
    }



}
