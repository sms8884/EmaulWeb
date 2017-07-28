/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.vote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAction;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.model.common.Search;
import com.jaha.web.emaul.v2.model.vote.VoteDto;
import com.jaha.web.emaul.v2.model.vote.VoteGroupVo;
import com.jaha.web.emaul.v2.model.vote.VoteKeyVo;
import com.jaha.web.emaul.v2.model.vote.VoteOfflineResultVo;
import com.jaha.web.emaul.v2.model.vote.VoteTypeVo;
import com.jaha.web.emaul.v2.model.vote.VoteVo;
import com.jaha.web.emaul.v2.model.vote.VoterOfflineVo;
import com.jaha.web.emaul.v2.model.vote.VoterVo;
import com.jaha.web.emaul.v2.service.vote.VoteAdminService;
import com.jaha.web.emaul.v2.util.PagingHelper;
import com.jaha.web.emaul.v2.util.PushUtils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * <pre>
 * Class Name : VoteAdminController.java
 * Description : Jaha권한 투표 개선
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 12.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 12.
 * @version 1.0
 */
@Controller
public class VoteAdminController {

    private static final Logger logger = LoggerFactory.getLogger(VoteAdminController.class);

    @Autowired
    private VoteAdminService voteAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private PushUtils pushUtils;

    /** JPA */
    @Autowired
    private HouseService houseService;


    @Value("${file.path.root}")
    private String rootFilePath;

    /**
     * 자하권한 > 투표 > 목록 조회 site : admin / jaha
     *
     * @param request
     * @param model
     * @param pagingHelper
     * @param voteDto
     * @param site
     * @return
     */
    @RequestMapping(value = "/v2/{site}/vote/list")
    public String adminVoteList(HttpServletRequest request, Model model, PagingHelper pagingHelper, VoteDto voteDto, @PathVariable(value = "site") String site) {
        try {

            logger.debug(">>> pagingHelper : " + pagingHelper.toString());

            // TODO : User정보 및 권한 공통 처리 Utility
            User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
            if ("jaha".equals(site)) {

                if (!user.type.jaha) {
                    logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                    throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                }

            } else if ("admin".equals(site)) {

                // 해당 아파트만 강제 조회
                logger.debug(">>> admin vote.target_apt : " + user.house.apt.id);
                voteDto.setAptId(user.house.apt.id);

            } else {
                logger.error("<<< 이마을 접근오류 : " + site + " >>>");
                throw new Exception("<<< 이마을 접근오류 : " + site + " >>>");
            }

            voteDto.setPagingHelper(pagingHelper);
            List<VoteVo> voteList = this.voteAdminService.selectJahaVoteList(voteDto);

            // 투표 종류 조회
            List<VoteTypeVo> voteTypeList = this.voteAdminService.selectVoteTypeList(voteDto);

            model.addAttribute("voteList", voteList);
            model.addAttribute("voteTypeList", voteTypeList);
            model.addAttribute("vote", voteDto);
            model.addAttribute("site", site);

        } catch (Exception e) {
            logger.error("<<투표 리스트 조회 중 오류>>", e);
        }

        return "v2/admin/vote/vote-list";
    }

    /**
     * 투표 등록화면
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/create", method = RequestMethod.GET)
    public String adminVoteForm(HttpServletRequest request, Model model) throws Exception {

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 생성권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 생성권한 오류 >>>");
        }

        // 투표 종류 조회
        List<VoteTypeVo> voteTypeList = this.voteAdminService.selectVoteTypeList(new VoteDto());
        model.addAttribute("voteTypeList", voteTypeList);
        model.addAttribute("job", "create");
        model.addAttribute("vote", new VoteVo()); // 빈값

        return "v2/admin/vote/vote-form";
    }

    /**
     * 투표 등록처리
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/createProc", method = RequestMethod.POST)
    public String adminVoteCreate(MultipartHttpServletRequest request, Model model, VoteVo voteVo) throws Exception {

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 생성권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 생성권한 오류 >>>");
        }

        // 아파트 유효성 : aptId는 세션에서 설정 (별도 체크 불필요)
        this.voteAdminService.setVote(true, request, voteVo);

        // ############# push ################
        if (!"0".equals(voteVo.getNotiType())) {
            List<Long> yetVoteUsers = this.voteAdminService.getYetVoteList(voteVo);

            String title = null;
            String value = null;
            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.ALARM, yetVoteUsers);

            if (voteVo.getStartDate().getTime() <= new Date().getTime()) {
                title = PushMessage.VOTE_STARTED_TITLE.getValue();
                value = String.format(PushMessage.VOTE_STARTED_BODY.getValue(), voteVo.getTitle());
            } else {
                title = PushMessage.VOTE_TOBE_TITLE.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
                String startDate = sdf.format(voteVo.getStartDate());
                value = String.format(PushMessage.VOTE_TOBE_BODY.getValue(), startDate);
            }
            this.pushUtils.sendPush(PushGubun.VOTE, title, value, PushAction.VOTE.getValue(), null, false, targetUserList);
        }
        // ############# push ################

        return "redirect:/v2/admin/vote/list";
    }

    /**
     * 투표 보안키 조회
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/getVoteKeys", method = RequestMethod.GET)
    @ResponseBody
    public List<VoteKeyVo> getAvailableVoteKeys(HttpServletRequest request, Model model) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteDto voteDto = new VoteDto();
        voteDto.setAptId(user.house.apt.id);

        return this.voteAdminService.getVoteKeyList(voteDto);
    }

    /**
     * 자하권한 > 투표목록 > 투표서식 다운로드 (버튼 팝업)
     *
     * @param req
     * @param model
     * @param voteDto
     * @return
     */
    @RequestMapping(value = "/v2/admin/vote/vote-doc-download-pop")
    public String adminVoteDocumentDownloadPopup(HttpServletRequest request, Model model, VoteDto voteDto) {

        return "v2/admin/vote/vote-doc-download-pop";
    }

    /**
     * 자하권한 > 투표목록 > 투표 상세보기 (제목 클릭)
     *
     * @param request
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/detail/{voteId}")
    public @ResponseBody Map<String, Object> adminVoteDetail(HttpServletRequest request, @PathVariable(value = "voteId") Long voteId) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        map.put("vote", vote);

        if (StringUtils.isNotEmpty(vote.getJsonArrayTargetGroup())) {
            // 선거구 대상 투표의 경우 선거구 명 출력을 위한 정보 조회
            List<Long> groupIdList = new Gson().fromJson(vote.getJsonArrayTargetGroup(), new TypeToken<List<Long>>() {}.getType());
            voteDto.setAptId(vote.getTargetApt());
            voteDto.setFullYn("Y"); // 전체조회 옵션
            voteDto.setGroupIdList(groupIdList);

            map.put("voteGroupList", this.voteAdminService.selectVoteGroupList(voteDto));
        }

        // 투표 접근권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        // 투표 사용여부 체크
        if (!"Y".equalsIgnoreCase(vote.getUseYn())) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        return map;
    }


    /**
     * 투표 수정화면
     *
     * @param request
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/update/{voteId}", method = RequestMethod.GET)
    public String adminVoteUpdate(HttpServletRequest request, Model model, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        Map<String, Object> voteMap = this.voteAdminService.getVoteInfo(voteDto);
        VoteVo pVoteVo = (VoteVo) voteMap.get("vote");

        // 투표 정보 체크
        if (pVoteVo.getId() == null) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        // 투표 접근권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            if (user.house.apt.id != pVoteVo.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        // 투표 사용여부 체크
        if (!"Y".equalsIgnoreCase(pVoteVo.getUseYn())) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        List<String> userTypeList = Lists.newArrayList();

        // 투표자 구분 {기존 데이터 유지위함}
        if (StringUtils.isNotEmpty(pVoteVo.getJsonArrayTargetUserTypes())) {
            userTypeList = new Gson().fromJson(pVoteVo.getJsonArrayTargetUserTypes(), new TypeToken<List<String>>() {}.getType());
        }

        // 선거구 명 출력용 조회
        if (StringUtils.isNotEmpty(pVoteVo.getJsonArrayTargetGroup())) {
            List<Long> groupIdList = new Gson().fromJson(pVoteVo.getJsonArrayTargetGroup(), new TypeToken<List<Long>>() {}.getType());
            voteDto.setAptId(pVoteVo.getTargetApt());
            voteDto.setFullYn("Y"); // 전체조회 옵션
            voteDto.setGroupIdList(groupIdList);

            model.addAttribute("voteGroupList", this.voteAdminService.selectVoteGroupList(voteDto));
        }

        // 투표 종류 조회
        List<VoteTypeVo> voteTypeList = this.voteAdminService.selectVoteTypeList(new VoteDto());
        model.addAttribute("voteTypeList", voteTypeList);
        model.addAttribute("vote", pVoteVo); // VoteVo
        model.addAttribute("voteItems", voteMap.get("voteItems")); // List<VoteItemVo>
        model.addAttribute("job", "update");
        model.addAttribute("userTypeList", userTypeList); // AS-IS 호환성 유지

        return "v2/admin/vote/vote-form";
    }


    /**
     * 투표정보 수정
     *
     * @param request
     * @param model
     * @param voteVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/updateProc", method = RequestMethod.POST)
    public String adminVoteUpdate(MultipartHttpServletRequest request, Model model, VoteVo voteVo) throws Exception {

        // 투표 정보 체크
        if (voteVo.getTargetApt() == null) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 수정권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 수정권한 오류 >>>");
        }

        if (!user.type.jaha) {
            if (user.house.apt.id != voteVo.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        // 아파트 유효성 : aptId는 세션에서 설정 (별도 체크 불필요)
        this.voteAdminService.setVote(false, request, voteVo);

        return "redirect:/v2/admin/vote/list";
    }



    /**
     * 투표 미 참여자 리스트 조회
     *
     * 자하권한 > 투표목록 > 투표 상세보기 > 투표 독려하기 (투표 독려하기 대상목록 조회)
     *
     * @param req
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/vote-check-yet-user/{voteId}")
    public @ResponseBody List<Long> adminVoteCheckYetUser(HttpServletRequest request, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo vote = this.voteAdminService.getVote(voteDto);

        // TODO : DB 로직 변경 예정
        // method name 은 AS-IS 유지 (getNotVoteList())
        List<Long> yetVoteUsers = this.voteAdminService.getYetVoteList(vote);

        return yetVoteUsers;
    }


    /**
     * 투표 독려메세지 푸쉬 발송여부 체크 하루에 1번만 발송<br/>
     * return : true - 이미 발송 / false : 발송하지 않음 (발송 가능)
     *
     * @param req
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/push-send-check/{voteId}")
    public @ResponseBody Map<String, Object> adminVotePushSendDateCheck(HttpServletRequest request, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo vote = this.voteAdminService.getVote(voteDto);

        Map<String, Object> map = new HashMap<>();

        if (vote.getPushSendDate() == null) {
            map.put("result", false);
        } else {
            try {
                String pushSendDate = new SimpleDateFormat("yyyy-MM-dd").format(vote.getPushSendDate());
                String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                if (pushSendDate.compareTo(nowDate) < 0) {
                    map.put("result", false);
                } else {
                    map.put("result", true); // 오늘 발송한 상태
                }
            } catch (Exception e) {
                map.put("result", false);
            }
        }

        return map;
    }


    /**
     * 투표 > 삭제 (vote.use_yn update)
     *
     * @param req
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/delete/{voteId}")
    public @ResponseBody Long adminVoteDelete(HttpServletRequest request, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo voteVo = this.voteAdminService.getVote(voteDto);

        // 투표 정보 체크
        if (voteVo.getTargetApt() == null) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 삭제권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 삭제권한 오류 >>>");
        }

        if (!user.type.jaha) {
            if (user.house.apt.id != voteVo.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        return this.voteAdminService.deleteVote(voteDto);
    }



    /**
     * 투표 대상자 참여독려 푸쉬 발송
     *
     * @param req
     * @param json
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/sendVotePush")
    @ResponseBody
    public Map<String, Object> sendVotePush(HttpServletRequest request, @RequestBody String json) throws Exception {

        JSONObject obj = new JSONObject(json);
        logger.debug(">>> voteId : " + obj.getLong("voteId"));
        logger.debug(">>> pushMsg : " + obj.getString("voteMsg"));

        VoteDto voteDto = new VoteDto();
        voteDto.setId(obj.getLong("voteId"));
        VoteVo vote = this.voteAdminService.getVote(voteDto);

        List<Long> yetVoteUsers = this.voteAdminService.getYetVoteList(vote);
        List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.ALARM, yetVoteUsers);
        String title = PushMessage.VOTE_REQ_TITLE.getValue();
        String value = (StringUtils.isBlank((obj.getString("voteMsg"))) ? String.format(PushMessage.VOTE_REQ_BODY.getValue(), vote.getTitle()) : obj.getString("voteMsg"));
        this.pushUtils.sendPush(PushGubun.VOTE, title, value, PushAction.VOTE.getValue(), null, false, targetUserList);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pushCount", targetUserList.size());

        // 푸시 sendDate update (vote.push_send_date)
        VoteVo uVote = new VoteVo();
        uVote.setId(vote.getId());
        uVote.setPushSendDate(new Date());
        this.voteAdminService.updateVotePushSendDate(uVote);

        return map;
    }



    /**
     * 투표 오프라인 집계완료 화면 <br />
     * getVoteRealtime
     *
     * @param request
     * @param model
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/complete/{voteId}", method = RequestMethod.GET)
    public String adminVoteComplete(HttpServletRequest request, Model model, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        Map<String, Object> voteMap = this.voteAdminService.getVoteInfo(voteDto);
        VoteVo pVoteVo = (VoteVo) voteMap.get("vote");

        // 투표 정보 체크
        if (pVoteVo.getId() == null) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        // 투표 접근권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            if (user.house.apt.id != pVoteVo.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        // 투표 사용여부 체크
        if (!"Y".equalsIgnoreCase(pVoteVo.getUseYn())) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        model.addAttribute("vote", pVoteVo); // VoteVo
        model.addAttribute("voteItems", voteMap.get("voteItems")); // List<VoteItemVo>

        // # v1 유지 :V /{auth}/vote/realtime/{voteId}
        if (pVoteVo.getVoteResultAvailable()) {

            VoteOfflineResultVo voteOfflineResultVo = this.voteAdminService.getVoteOfflineResult(pVoteVo.getId());
            Map<Long, Long> resultMap = new Gson().fromJson(voteOfflineResultVo.getJsonMapVoteItemResult(), new TypeToken<Map<Long, Long>>() {}.getType());
            User regUser = userService.getUser(voteOfflineResultVo.getRegUserId());

            model.addAttribute("voteOfflineResultVo", voteOfflineResultVo);
            model.addAttribute("voteResultMap", resultMap);
            model.addAttribute("regUser", regUser);
        }

        return "v2/admin/vote/vote-complete-pop";
    }



    /**
     * 이마을 투표 오프라인 집계 완료처리 <br />
     * v1 유지
     *
     * @param req
     * @param json
     * @param votersFile
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/completeProc", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> adminVoteCompleteProc(HttpServletRequest request, @RequestParam(value = "json") String json,
            @RequestParam(value = "votersFile", required = false) MultipartFile votersFile) throws Exception {

        logger.debug(">>> json : " + json);
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long aptId = obj.getLong("aptId");
        Long voteId = obj.getLong("voteId");
        JSONObject itemResult = obj.getJSONObject("itemResult");

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류");
                return resultMap;
            }
        }

        VoteOfflineResultVo voteOfflineResultVo = new VoteOfflineResultVo();
        voteOfflineResultVo.setVoteId(voteId);
        voteOfflineResultVo.setAptId(aptId);
        voteOfflineResultVo.setRegUserId(user.id);
        voteOfflineResultVo.setRegDate(new Date());
        voteOfflineResultVo.setResultText(obj.getString("voteResultText"));

        if (votersFile != null) {
            logger.debug("votersFname : " + votersFile.getOriginalFilename());
            voteOfflineResultVo.setVotersFname(votersFile.getOriginalFilename());

            try {
                File docDir = new File(String.format(rootFilePath + File.separator + "vote" + File.separator + "offline_voter" + File.separator + "%s", voteId));
                if (!docDir.exists()) {
                    docDir.mkdirs();
                }
                File dest = new File(docDir, String.format("%s.list", voteId));
                dest.createNewFile();
                votersFile.transferTo(dest);

                // 등록한 명부파일을 voter_offline에 등록한다. (파일 삭제는 집계 취소시 수행)
                Workbook workbook = Workbook.getWorkbook(dest);
                Sheet sheet = workbook.getSheet(0);
                // 행 길이
                int endIdx = sheet.getColumn(1).length - 1;

                List<VoterOfflineVo> voterOfflineList = new ArrayList<VoterOfflineVo>();

                for (int i = 1; i <= endIdx; i++) {
                    // 0행의 타이틀은 제외한다.
                    VoterOfflineVo voterOfflineVo = new VoterOfflineVo();
                    voterOfflineVo.setAptId(aptId);
                    voterOfflineVo.setVoteId(voteId);
                    voterOfflineVo.setRegDate(new Date());
                    voterOfflineVo.setFullName(sheet.getCell(0, i).getContents());
                    voterOfflineVo.setDong(sheet.getCell(1, i).getContents());
                    voterOfflineVo.setHo(sheet.getCell(2, i).getContents());

                    voterOfflineList.add(voterOfflineVo);
                }


                if (!voterOfflineList.isEmpty()) {

                    // DELETE
                    VoterOfflineVo paramVoterOfflineVo = new VoterOfflineVo();
                    paramVoterOfflineVo.setAptId(aptId);
                    paramVoterOfflineVo.setVoteId(voteId);
                    this.voteAdminService.deleteVoterOffline(paramVoterOfflineVo);

                    // INSERT
                    this.voteAdminService.insertVoterOfflineList(voterOfflineList);
                }



            } catch (Exception e) {
                resultMap.put("result", false);
                resultMap.put("message", e.getMessage());
                logger.error("<<< 이마을 투표 오프라인 집계처리 오류 >>>", e);
            }
        }

        // vote_offline_result insert
        Map<Long, Long> voteItemCountMap = Maps.newHashMap();
        Set<String> keySet = itemResult.keySet();
        for (String key : keySet) {
            voteItemCountMap.put(Longs.tryParse(key), itemResult.getLong(key));
        }
        voteOfflineResultVo.setJsonMapVoteItemResult(new Gson().toJson(voteItemCountMap));
        this.voteAdminService.insertVoteOfflineResult(voteOfflineResultVo);

        // vote update
        vote.setVoteResultAvailable(true);
        this.voteAdminService.updateVoteResult(vote);

        resultMap.put("result", true);
        resultMap.put("message", "success");

        return resultMap;
    }



    /**
     * 투표 오프라인 집계완료 리셋 처리 <br />
     * v1 유지
     *
     * @param req
     * @param json
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/resetProc", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> admimVoteReset(HttpServletRequest request, @RequestBody String json) throws Exception {

        logger.debug(">>> json : " + json);
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long voteId = obj.getLong("voteId");


        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류");
                return resultMap;
            }
        }

        // DELETE
        VoterOfflineVo paramVoterOfflineVo = new VoterOfflineVo();
        paramVoterOfflineVo.setAptId(vote.getTargetApt());
        paramVoterOfflineVo.setVoteId(voteId);
        this.voteAdminService.deleteVoterOffline(paramVoterOfflineVo);

        // vote_offline_result delete
        this.voteAdminService.deleteVoteOfflineResult(vote.getId());

        try {
            File docFile = new File(
                    String.format(rootFilePath + File.separator + "vote" + File.separator + "offline_voter" + File.separator + "%s" + File.separator + "%s", voteId, String.format("%s.list", voteId)));
            docFile.delete();
        } catch (Exception e) {
            resultMap.put("result", false);
            resultMap.put("message", e.getMessage());
            logger.error("<<< 이마을 투표 집계 리셋처리 오류 >>>", e);
        }

        // vote update
        vote.setVoteResultAvailable(false);
        this.voteAdminService.updateVoteResult(vote);

        resultMap.put("result", true);
        resultMap.put("message", "success");

        return resultMap;
    }



    /**
     * 투표 결과 보기
     *
     * @param request
     * @param model
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/result/{voteId}", method = RequestMethod.GET)
    public String adminVoteResult(HttpServletRequest request, Model model, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        Map<String, Object> voteMap = this.voteAdminService.getVoteResultInfo(voteDto);

        model.addAttribute("resultState", voteMap.get("resultState"));
        model.addAttribute("voteRate", voteMap.get("voteRate"));
        model.addAttribute("vote", voteMap.get("vote"));
        model.addAttribute("voterList", voteMap.get("voterList"));
        model.addAttribute("voteItemList", voteMap.get("voteItemList"));

        model.addAttribute("totalVoterCount", voteMap.get("totalVoterCount")); // 총 투표자수
        model.addAttribute("onlineVoterCount", voteMap.get("onlineVoterCount")); // 총 투표자수
        model.addAttribute("offlineVoterCount", voteMap.get("offlineVoterCount")); // 총 투표자수

        VoteVo vote = (VoteVo) voteMap.get("vote");

        if (StringUtils.isNotEmpty(vote.getJsonArrayTargetHo())) {
            List<String> hoList = new Gson().fromJson(vote.getJsonArrayTargetHo(), new TypeToken<List<String>>() {}.getType());
            model.addAttribute("voteHoList", hoList);
        }

        if (StringUtils.isNotEmpty(vote.getJsonArrayTargetGroup())) {
            // 선거구 대상 투표의 경우 선거구 명 출력을 위한 정보 조회
            List<Long> groupIdList = new Gson().fromJson(vote.getJsonArrayTargetGroup(), new TypeToken<List<Long>>() {}.getType());
            voteDto.setAptId(vote.getTargetApt());
            voteDto.setFullYn("Y"); // 전체조회 옵션
            voteDto.setGroupIdList(groupIdList);

            model.addAttribute("voteGroupList", this.voteAdminService.selectVoteGroupList(voteDto));
        }

        return "v2/admin/vote/vote-result";
    }


    /**
     * 투표결과 > 전자투표 참여목록 조회 <br />
     * 비동기
     *
     * @param request
     * @param json
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/selectOnlineVoterList")
    @ResponseBody
    public Map<String, Object> adminSelectOnlineVoterList(HttpServletRequest request, @RequestBody String json) throws Exception {

        logger.debug(">>> json : " + json);
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long voteId = obj.getLong("voteId");
        int pageNum = 1;
        try {
            pageNum = obj.getInt("pageNum");
        } catch (Exception e) {
            pageNum = 1;
        }
        int pageSize = 10;
        try {
            pageSize = obj.getInt("pageSize");
        } catch (Exception e) {
            pageSize = 10;
        }

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류");
                return resultMap;
            }
        }

        // tablet 분기 처리는 Query상에서 case로 처리 [현장투표 등 멘트처리...]
        // Paging parameter setting
        PagingHelper pagingHelper = new PagingHelper();
        Search search = new Search();
        pagingHelper.setSearch(search);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageBlockSize(10);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        voteDto.setPagingHelper(pagingHelper);

        List<VoterVo> voterList = this.voteAdminService.selectVoterList(voteDto);

        resultMap.put("result", true);
        resultMap.put("message", "success");
        resultMap.put("voterList", voterList);
        resultMap.put("pagingHelper", voteDto.getPagingHelper());

        return resultMap;
    }

    /**
     * 투표 오프라인 투표 참여목록 조회 (투표결과)
     *
     * @param request
     * @param json
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/selectOfflineVoterList")
    @ResponseBody
    public Map<String, Object> adminSelectOfflineVoterList(HttpServletRequest request, @RequestBody String json) throws Exception {

        logger.debug(">>> json : " + json);
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long voteId = obj.getLong("voteId");
        int pageNum = 1;
        try {
            pageNum = obj.getInt("pageNum");
        } catch (Exception e) {
            pageNum = 1;
        }
        int pageSize = 10;
        try {
            pageSize = obj.getInt("pageSize");
        } catch (Exception e) {
            pageSize = 10;
        }

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류");
                return resultMap;
            }
        }

        // Paging parameter setting
        PagingHelper pagingHelper = new PagingHelper();
        Search search = new Search();
        pagingHelper.setSearch(search);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageBlockSize(10);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        voteDto.setPagingHelper(pagingHelper);

        List<VoterOfflineVo> voterOfflineList = this.voteAdminService.selectOfflineVoterList(voteDto);

        resultMap.put("result", true);
        resultMap.put("message", "success");
        resultMap.put("pagingHelper", voteDto.getPagingHelper());
        resultMap.put("voterOfflineList", voterOfflineList);

        return resultMap;
    }


    /**
     * 투표자 목록 인쇄 <br/>
     * on / off {on} 으로 오프라인, 온라인 투표자 목록 분기처리
     *
     * @param request
     * @param model
     * @param on
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/print/{on}/{voteId}", method = RequestMethod.GET)
    public String adminVotePrint(HttpServletRequest request, Model model, @PathVariable(value = "on") String on, @PathVariable(value = "voteId") Long voteId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        voteDto.setFullYn("Y"); // 전체조회 옵션

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }
        }

        if ("off".equalsIgnoreCase(on)) {
            // 오프라인 투표자 목록
            List<VoterOfflineVo> voterOfflineList = this.voteAdminService.selectOfflineVoterList(voteDto);
            model.addAttribute("voterList", voterOfflineList);

        } else {
            // 온라인 투표자 목록
            List<VoterVo> voterList = this.voteAdminService.selectVoterList(voteDto);
            model.addAttribute("voterList", voterList);
        }

        model.addAttribute("pagingHelper", voteDto.getPagingHelper());
        model.addAttribute("vote", vote);
        model.addAttribute("on", on);

        return "v2/admin/vote/vote-print-pop";
    }


    /**
     * 투표자 목록 엑셀저장 <br/>
     * on / off {on} 으로 오프라인, 온라인 투표자 목록 분기처리
     *
     * @param request
     * @param model
     * @param on
     * @param voteId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/excel/{on}/{voteId}")
    public String adminVoteExcel(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable(value = "on") String on, @PathVariable(value = "voteId") Long voteId)
            throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteId);
        voteDto.setFullYn("Y"); // 전체조회 옵션

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }
        }

        // 오프라인 투표자 목록
        List<VoterOfflineVo> voterOfflineList = this.voteAdminService.selectOfflineVoterList(voteDto);
        // 온라인 투표자 목록
        List<VoterVo> voterList = this.voteAdminService.selectVoterList(voteDto);

        /**
         * TODO : File Download And Excel Util 작업
         */
        String filePath = String.format(rootFilePath + File.separator + "vote" + File.separator + "download_voter" + File.separator + "%s", voteId);
        String fileName = vote.getTitle() + "_" + "투표참여목록" + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls"; /* ("on".equals(on) ? "전자" : "오프라인") + */
        String fileFullPath = filePath + File.separator + fileName;

        File downLoadDirectory = new File(filePath);
        if (!downLoadDirectory.exists()) {
            downLoadDirectory.mkdirs();
        }
        logger.debug(">>> fileFullPath : " + fileFullPath);

        // Excel file write
        WritableWorkbook workbook = null;

        WritableCellFormat textFormat = new WritableCellFormat();
        textFormat.setAlignment(Alignment.CENTRE);
        textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 11);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat titleFormat = new WritableCellFormat(cellFont);
        titleFormat.setAlignment(Alignment.CENTRE);

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            workbook = Workbook.createWorkbook(new File(fileFullPath));
            WritableSheet onlineSheet = workbook.createSheet("전자투표 참여목록", 0);
            if (voterList.isEmpty()) {
                onlineSheet.addCell(new Label(1, 1, "전자투표 참여자 목록이 없습니다."));
            } else {
                onlineSheet.addCell(new Label(0, 0, "No", titleFormat));
                onlineSheet.addCell(new Label(1, 0, "투표시각", titleFormat));
                onlineSheet.addCell(new Label(2, 0, "동", titleFormat));
                onlineSheet.addCell(new Label(3, 0, "호", titleFormat));
                onlineSheet.addCell(new Label(4, 0, "휴대폰", titleFormat));
                onlineSheet.addCell(new Label(5, 0, "이름", titleFormat));
                for (int i = 0; i < voterList.size(); i++) {
                    VoterVo onlineVoter = voterList.get(i);
                    onlineSheet.addCell(new Label(0, (i + 1), String.valueOf((voterList.size() - i)), textFormat));
                    onlineSheet.addCell(new Label(1, (i + 1), format.format(onlineVoter.getVoteDate()), textFormat));
                    onlineSheet.addCell(new Label(2, (i + 1), onlineVoter.getDong(), textFormat));
                    onlineSheet.addCell(new Label(3, (i + 1), onlineVoter.getHo(), textFormat));
                    onlineSheet.addCell(new Label(4, (i + 1),
                            onlineVoter.getPhone().length() > 7 ? onlineVoter.getPhone().substring(0, 3) + "-****-" + onlineVoter.getPhone().substring(7) : "잘못된 전화번호", textFormat));
                    onlineSheet.addCell(new Label(5, (i + 1), onlineVoter.getVoterName(), textFormat));
                }
            }

            if (vote.getOfflineAvailable()) {
                WritableSheet offlineSheet = workbook.createSheet("오프라인투표 참여목록", 1);
                if (voterOfflineList.isEmpty()) {
                    offlineSheet.addCell(new Label(1, 1, "오프라인투표 참여자 목록이 없습니다."));
                } else {
                    offlineSheet.addCell(new Label(0, 0, "No", titleFormat));
                    offlineSheet.addCell(new Label(1, 0, "투표시각", titleFormat));
                    offlineSheet.addCell(new Label(2, 0, "동", titleFormat));
                    offlineSheet.addCell(new Label(3, 0, "호", titleFormat));
                    offlineSheet.addCell(new Label(4, 0, "이름", titleFormat));
                    for (int i = 0; i < voterOfflineList.size(); i++) {
                        VoterOfflineVo offlineVoter = voterOfflineList.get(i);
                        offlineSheet.addCell(new Label(0, (i + 1), String.valueOf((voterOfflineList.size() - i))));
                        offlineSheet.addCell(new Label(1, (i + 1), format.format(offlineVoter.getRegDate())));
                        offlineSheet.addCell(new Label(2, (i + 1), offlineVoter.getDong()));
                        offlineSheet.addCell(new Label(3, (i + 1), offlineVoter.getHo()));
                        offlineSheet.addCell(new Label(4, (i + 1), offlineVoter.getFullName()));
                    }
                }
            }

            workbook.write();
        } catch (Exception e) {
            logger.error("<<< WritableWorkbook exception : " + e.getMessage() + " >>>");
            throw new Exception("<<< WritableWorkbook exception >>>");
        } finally {
            workbook.close();
        }

        // File Download
        File downloadFile = new File(fileFullPath);
        if (!downloadFile.exists()) {
            logger.error("<<< 해당 파일이 존재하지 않습니다. >>>");
            throw new Exception("<<< 해당 파일이 존재하지 않습니다. >>>");
        }

        String mimeType = request.getSession().getServletContext().getMimeType(downloadFile.getName());
        if (mimeType == null || mimeType.length() == 0) {
            mimeType = "application/octet-stream;";
        }

        InputStream is = new FileInputStream(downloadFile);

        response.setContentType(mimeType + "; charset=euc-kr");
        String userAgent = request.getHeader("User-Agent");

        // attachment; 가 붙으면 IE의 경우 무조건 다운로드창이 뜬다. 상황에 따라 써야한다.
        if (userAgent != null && userAgent.indexOf("MSIE 5.5") > -1) {
            response.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(fileName, "UTF-8") + ";");
        } else if (userAgent != null && userAgent.indexOf("MSIE") > -1) {
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") + ";");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("euc-kr"), "latin1") + ";");
        }

        if (downloadFile.length() > 0) {
            response.setHeader("Content-Length", "" + downloadFile.length());
        }

        byte[] buffer = new byte[4096];
        BufferedInputStream fIn = null;
        BufferedOutputStream fOut = null;

        try {
            fIn = new BufferedInputStream(is);
            fOut = new BufferedOutputStream(response.getOutputStream());
            int read = 0;

            while ((read = fIn.read(buffer)) != -1) {
                fOut.write(buffer, 0, read);
            }
        } catch (Exception ex) {
            logger.error("<<< 파일 다운로드 오류 : " + ex.getMessage() + ">>>");
            throw new Exception("<<< 파일 다운로드 오류 >>>");
        } finally {
            try {
                fOut.close();
            } catch (Exception e) {
            }
            try {
                fIn.close();
            } catch (Exception e) {
            }
        }

        // 파일 삭제
        downloadFile.delete();
        return null;
    }



    /**
     * 투표 - 개표 (보안키 투표 리스트)
     *
     * @param request
     * @param model
     * @param pagingHelper
     * @param voteDto
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/key-list", method = RequestMethod.GET)
    public String listVoteKey(HttpServletRequest request, Model model, PagingHelper pagingHelper, VoteDto voteDto) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        VoteVo vote = this.voteAdminService.getVote(voteDto);
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 개표권한 오류 >>>");
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != vote.getTargetApt()) {
                logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }
        }

        voteDto.setAptId(user.house.apt.id);
        voteDto.setPagingHelper(pagingHelper);

        List<VoteKeyVo> voteKeyList = this.voteAdminService.selectVoteKeyList(voteDto);

        for (VoteKeyVo voteKey : voteKeyList) {
            // PagingHelper의 조회조건 삭제처리 (new)
            VoteDto inVoteDto = new VoteDto();
            PagingHelper p = new PagingHelper();
            p.setSearch(new Search());
            inVoteDto.setPagingHelper(p);
            inVoteDto.setVkId(voteKey.getVkId());
            inVoteDto.setFullYn("Y"); // 전체조회 옵션
            List<VoteVo> voteList = this.voteAdminService.selectVoteList(inVoteDto);

            // 투표키의 오픈여부 체크 (TODO : 해당 프로세스 개선필요)
            for (VoteVo voteVo : voteList) {
                if ("Y".equalsIgnoreCase(voteVo.getDecYn())) {
                    voteKey.setOpenStatus(1);
                    break;
                }
            }
        }

        model.addAttribute("voteKeyList", voteKeyList);

        return "v2/admin/vote/vote-key-list";
    }


    /**
     * 개표목록 상세
     *
     * @param request
     * @param model
     * @param voteDto
     * @param pagingHelper
     * @param vkId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/key-detail/{voteKeyId}", method = RequestMethod.GET)
    public String listVoteKey(HttpServletRequest request, Model model, VoteDto voteDto, PagingHelper pagingHelper, @PathVariable(value = "voteKeyId") Long vkId) throws Exception {

        // 보안키 조회
        voteDto.setVkId(vkId);
        VoteKeyVo voteKey = this.voteAdminService.getVoteKey(voteDto);

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 개표권한 오류 >>>");
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != voteKey.getAptId()) {
                logger.error("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
                throw new Exception("<<< 이마을 접근 권한오류 : " + user.type.jaha + " >>>");
            }
        }

        // 투표 리스트 조회
        voteDto.setPagingHelper(pagingHelper);
        voteDto.setFullYn("Y"); // 전체조회 옵션
        List<VoteVo> voteList = this.voteAdminService.selectVoteList(voteDto);

        // 투표키의 오픈여부 체크 (TODO : 해당 프로세스 개선필요)
        for (VoteVo vote : voteList) {
            if ("Y".equalsIgnoreCase(vote.getDecYn())) {
                voteKey.setOpenStatus(1);
                break;
            }
        }

        model.addAttribute("voteKey", voteKey);
        model.addAttribute("voteList", voteList);

        return "v2/admin/vote/vote-key-detail";
    }


    /**
     * 보안 키 개표처리
     *
     * @param request
     * @param model
     * @param pagingHelper
     * @param vkId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/vote-key-open/{vkId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAvailableVoteKeys(HttpServletRequest request, Model model, PagingHelper pagingHelper, @PathVariable(value = "vkId") Long vkId) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        VoteDto voteDto = new VoteDto();

        voteDto.setPagingHelper(pagingHelper);
        voteDto.setVkId(vkId);
        voteDto.setFullYn("Y");
        VoteKeyVo voteKey = this.voteAdminService.getVoteKey(voteDto);

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            resultMap.put("result", false);
            resultMap.put("message", "투표 접근권한 오류");
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            return resultMap;
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != voteKey.getAptId()) {
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류 [" + voteKey.getAptId() + "]");
                logger.error("<<< 이마을 투표 개표권한 오류  >>>");
                return resultMap;
            }
        }

        resultMap.put("result", true);
        resultMap.put("message", "success");
        resultMap.put("cnt", this.voteAdminService.openVoteKey(voteDto));

        return resultMap;
    }


    /**
     * 투표 대상 선택 [투표 등록 / 수정 시]
     *
     * @param request
     * @param model
     * @param aptId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/selectVoteUser/{aptId}")
    public String adminSelectVoteUser(HttpServletRequest request, Model model, @PathVariable(value = "aptId") Long aptId) throws Exception {

        logger.debug(">>> aptId : " + aptId);

        // 투표 접근권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            if (user.house.apt.id != aptId) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        /**
         * 선거구는 선거구 JOIN <br />
         * 그 외는 vote.dong, vote.ho
         */
        model.addAttribute("job", "create");
        model.addAttribute("aptId", aptId);
        model.addAttribute("vote", new VoteVo());

        return "v2/admin/vote/vote-select-user-pop";
    }

    /**
     * 아파트별 동 선택
     *
     * @param request
     * @param model
     * @param aptId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/selectAptDong/{aptId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> adminSelectAptDong(HttpServletRequest request, Model model, @PathVariable(value = "aptId") Long aptId) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            resultMap.put("result", false);
            resultMap.put("message", "투표 접근권한 오류");
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            return resultMap;
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != aptId) {
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류 [" + aptId + "]");
                logger.error("<<< 이마을 투표 개표권한 오류  >>>");
                return resultMap;
            }
        }

        resultMap.put("result", true);
        resultMap.put("message", "success");
        resultMap.put("dong", this.voteAdminService.getAptDong(aptId));

        return resultMap;
    }


    /**
     * 아파트 동별 호 조회 <br />
     * <br/>
     * 00 + 00 구조의 숫자형으로 호를 추출하여 호 선택창을 그린다. <br/>
     * 예 : 101호는 1층 01호 이므로 001 층 + 01호 <br/>
     * 예 : 1204호 는 12층 04호로 012 + 04로 추출하여 화면단에서 for 문 처리한다.<br/>
     * <br />
     * dong == 'all' : 전체 동
     *
     * @param request
     * @param model
     * @param aptId
     * @param dong
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/selectAptHo/{aptId}/{dong}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> adminSelectAptHo(HttpServletRequest request, Model model, @PathVariable(value = "aptId") Long aptId, @PathVariable(value = "dong") String dong) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            resultMap.put("result", false);
            resultMap.put("message", "투표 접근권한 오류");
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            return resultMap;
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != aptId) {
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류 [" + aptId + "]");
                logger.error("<<< 이마을 투표 개표권한 오류  >>>");
                return resultMap;
            }
        }

        resultMap.put("result", true);
        resultMap.put("message", "success");
        resultMap.put("ho", this.voteAdminService.getAptHo(aptId, dong)); // 아파트 동에 등록되어 있는 호

        // 선거구 등록시 "전체" 조회시에는 전체 대상 아파트의 호수를 구하며, 별도 화면구성이 없으므로, row, column은 조회하지 않는다.
        if (!"all".equals(dong)) {
            // 투표대상 선택 그리드를 구성하기 위한 데이터
            resultMap.put("row", this.voteAdminService.getAptHoRow(aptId, dong)); // 아파트 층수
            resultMap.put("column", this.voteAdminService.getAptHoColumn(aptId, dong)); // 아파트 최대 호수
        }


        return resultMap;
    }



    /**
     * 선거구 리스트
     *
     * @param request
     * @param model
     * @param pagingHelper
     * @param voteDto
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/group-list")
    public String adminVoteGroupList(HttpServletRequest request, Model model, PagingHelper pagingHelper, VoteDto voteDto) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            voteDto.setAptId(user.house.apt.id);
        }
        voteDto.setPagingHelper(pagingHelper);

        List<VoteGroupVo> voteGroupList = this.voteAdminService.selectVoteGroupList(voteDto);

        model.addAttribute("voteGroupList", voteGroupList);

        return "v2/admin/vote/vote-group-list";
    }


    /**
     * 아파트별 선거구 조회
     *
     * @param request
     * @param model
     * @param aptId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/group-list/{aptId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> adminSelectAptVoteGroup(HttpServletRequest request, Model model, @PathVariable(value = "aptId") Long aptId) throws Exception {

        logger.debug(">>> aptId : " + aptId);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            resultMap.put("result", false);
            resultMap.put("message", "투표 접근권한 오류");
            logger.error("<<< 이마을 투표 개표권한 오류  >>>");
            return resultMap;
        }
        if (!user.type.jaha) {
            if (user.house.apt.id != aptId) {
                resultMap.put("result", false);
                resultMap.put("message", "투표 접근권한 오류 [" + aptId + "]");
                logger.error("<<< 이마을 투표 개표권한 오류  >>>");
                return resultMap;
            }
        }

        resultMap.put("result", true);
        resultMap.put("message", "success");

        VoteDto voteDto = new VoteDto();
        voteDto.setFullYn("Y");
        voteDto.setAptId(aptId);

        List<VoteGroupVo> voteGroupList = this.voteAdminService.selectVoteGroupList(voteDto);
        resultMap.put("voteGroupList", voteGroupList);
        return resultMap;
    }


    /**
     * 선거구 등록 화면
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/group-create", method = RequestMethod.GET)
    public String adminVoteGroupCreate(HttpServletRequest request, Model model) throws Exception {

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 생성권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 생성권한 오류 >>>");
        }

        if (user.type.jaha) {
            // 아파트 검색조건 (자하권한)
            List<Apt> list = houseService.jahaGetAptList("0"); // 등록된 아파트만 조회
            logger.debug(">>> APT : " + list.size());
            model.addAttribute("list", list);
        }

        model.addAttribute("job", "create");
        model.addAttribute("aptId", user.house.apt.id);
        model.addAttribute("aptName", user.house.apt.name);
        model.addAttribute("voteGroup", new VoteGroupVo()); // 빈값

        return "v2/admin/vote/vote-group-form";
    }



    /**
     * 선거구 등록처리
     *
     * @param request
     * @param model
     * @param voteGroupVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/createGroupProc", method = RequestMethod.POST)
    public String adminVoteGroupCreate(HttpServletRequest request, Model model, VoteGroupVo voteGroupVo) throws Exception {

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 생성권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 생성권한 오류 >>>");
        }

        if ("dong".equalsIgnoreCase(voteGroupVo.getGroupType())) {

            if (voteGroupVo.getDongs().length > 1) {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 외 " + (voteGroupVo.getDongs().length - 1) + "개동");
            } else {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동");
            }

            voteGroupVo.setJsonArrayTarget(new Gson().toJson(voteGroupVo.getDongs()));
            logger.debug(">>> targetJson : " + voteGroupVo.getJsonArrayTarget());

        } else if ("ho".equalsIgnoreCase(voteGroupVo.getGroupType())) {

            if (voteGroupVo.getDongs().length > 1) {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 외 " + (voteGroupVo.getDongs().length - 1) + "개동 총 " + voteGroupVo.getVotersCount() + "세대");
            } else {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 " + voteGroupVo.getVotersCount() + "세대");
            }

        }

        voteGroupVo.setUserId(user.id);
        voteGroupVo.setRegDate(new Date());

        if (user.type.jaha) {

        } else {
            voteGroupVo.setTargetApt(user.house.apt.id);
        }


        this.voteAdminService.insertVoteGroup(voteGroupVo);

        return "redirect:/v2/admin/vote/group-list";
    }


    /**
     * 선거구 수정화면
     *
     * @param request
     * @param model
     * @param voteGroupId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/group-update/{voteGroupId}", method = RequestMethod.GET)
    public String adminVoteGroupUpdate(HttpServletRequest request, Model model, @PathVariable(value = "voteGroupId") Long voteGroupId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteGroupId);
        VoteGroupVo voteGroupVo = this.voteAdminService.getVoteGroup(voteDto);

        // 투표 정보 체크
        if (voteGroupVo.getId() == null) {
            logger.error("<<< 선거구 정보 오류 >>>");
            throw new Exception("<<< 선거구 정보 오류 >>>");
        }

        // 선거구 접근권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            if (user.house.apt.id != voteGroupVo.getTargetApt()) {
                logger.error("<<< 선거구 접근권한 오류 >>>");
                throw new Exception("<<< 선거구 접근권한 오류 >>>");
            }
        }

        // 선거구 사용여부 체크
        if (!"Y".equalsIgnoreCase(voteGroupVo.getUseYn())) {
            logger.error("<<< 선거구 정보 오류 >>>");
            throw new Exception("<<< 선거구 정보 오류 >>>");
        }

        model.addAttribute("job", "update");

        if (user.type.jaha) {
            model.addAttribute("aptId", voteGroupVo.getTargetApt());
            model.addAttribute("aptName", voteGroupVo.getAptName());
        } else {
            model.addAttribute("aptId", user.house.apt.id);
            model.addAttribute("aptName", user.house.apt.name);
        }
        model.addAttribute("voteGroup", voteGroupVo);

        return "v2/admin/vote/vote-group-form";
    }


    /**
     * 투표 선거구 수정
     *
     * @param request
     * @param model
     * @param voteGroupVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/updateGroupProc", method = RequestMethod.POST)
    public String adminVoteGroupUpdate(HttpServletRequest request, Model model, VoteGroupVo voteGroupVo) throws Exception {

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 생성권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 생성권한 오류 >>>");
        }


        if ("dong".equalsIgnoreCase(voteGroupVo.getGroupType())) {

            if (voteGroupVo.getDongs().length > 1) {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 외 " + (voteGroupVo.getDongs().length - 1) + "개동");
            } else {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동");
            }

            voteGroupVo.setJsonArrayTarget(new Gson().toJson(voteGroupVo.getDongs()));
            logger.debug(">>> targetJson : " + voteGroupVo.getJsonArrayTarget());

        } else if ("ho".equalsIgnoreCase(voteGroupVo.getGroupType())) {

            if (voteGroupVo.getDongs().length > 1) {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 외 " + (voteGroupVo.getDongs().length - 1) + "개동 총 " + voteGroupVo.getVotersCount() + "세대");
            } else {
                voteGroupVo.setDescription(voteGroupVo.getDongs()[0] + "동 " + voteGroupVo.getVotersCount() + "세대");
            }

        }

        voteGroupVo.setModId(user.id);
        voteGroupVo.setModName(user.getFullName());
        voteGroupVo.setModDate(new Date());

        this.voteAdminService.updateVoteGroup(voteGroupVo);

        return "redirect:/v2/admin/vote/group-list";
    }

    /**
     * 선거구 삭제
     *
     * @param request
     * @param voteGroupId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/group-delete/{voteGroupId}")
    public @ResponseBody Long adminVoteGroupDelete(HttpServletRequest request, @PathVariable(value = "voteGroupId") Long voteGroupId) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteGroupId);
        VoteGroupVo voteGroupVo = this.voteAdminService.getVoteGroup(voteDto);

        // 투표 정보 체크
        if (voteGroupVo.getTargetApt() == null) {
            logger.error("<<< 투표 정보 오류 >>>");
            throw new Exception("<<< 투표 정보 오류 >>>");
        }

        // 투표 생성권한 체크
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha && !user.type.admin) {
            logger.error("<<< 이마을 투표 삭제권한 오류  >>>");
            throw new Exception("<<< 이마을 투표 삭제권한 오류 >>>");
        }

        if (!user.type.jaha) {
            if (user.house.apt.id != voteGroupVo.getTargetApt()) {
                logger.error("<<< 투표 접근권한 오류 >>>");
                throw new Exception("<<< 투표 접근권한 오류 >>>");
            }
        }

        return this.voteAdminService.deleteVoteGroup(voteDto);
    }


    /**
     * 투표 날자 수정
     *
     * @param request
     * @param json
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/admin/vote/update-date/{voteId}")
    @ResponseBody
    public Map<String, Object> adminVoteDateUpdate(HttpServletRequest request, @RequestBody String json) throws Exception {

        Map<String, Object> map = new HashMap<>();
        JSONObject obj = new JSONObject(json);

        try {
            // 투표 생성권한 체크
            User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
            if (!user.type.jaha) {
                logger.error("<<< 이마을 투표 수정권한 오류  >>>");
                throw new Exception("<<< 이마을 투표 수정권한 오류 >>>");
            }

            VoteVo vote = new VoteVo();
            vote.setId(obj.getLong("voteId"));

            logger.debug(">>> voteId : " + obj.getLong("voteId"));
            logger.debug(">>> startDay : " + obj.getString("startDay"));
            logger.debug(">>> startHour : " + obj.getString("startHour"));
            logger.debug(">>> startMin : " + obj.getString("startMin"));
            logger.debug(">>> endDay : " + obj.getString("endDay"));
            logger.debug(">>> endHour : " + obj.getString("endHour"));
            logger.debug(">>> endMin : " + obj.getString("endMin"));
            logger.debug(">>> status : " + obj.getString("status"));

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String sDate = obj.getString("startDay").substring(0, 4) + "-" + obj.getString("startDay").substring(4, 6) + "-" + obj.getString("startDay").substring(6) + " " + obj.getString("startHour")
                    + ":" + obj.getString("startMin") + ":00";
            String eDate = obj.getString("endDay").substring(0, 4) + "-" + obj.getString("endDay").substring(4, 6) + "-" + obj.getString("endDay").substring(6) + " " + obj.getString("endHour") + ":"
                    + obj.getString("endMin") + ":00";

            vote.setStartDate(transFormat.parse(sDate));
            vote.setEndDate(transFormat.parse(eDate));
            vote.setStatus(obj.getString("status"));

            this.voteAdminService.updateVoteDate(vote);

            map.put("result", true);

        } catch (Exception e) {
            map.put("result", false);
            logger.error("<<< 이마을 투표 날자 수정 오류  >>>" + e.getMessage());
        }

        return map;

    }


    /**
     * 오프라인 투표자 명부 입력용 문서 다운로드
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/vote/document/offline", method = RequestMethod.GET)
    public ResponseEntity<byte[]> voteOfflineDocumentDownload(HttpServletRequest request) {

        File docDir = new File(String.format(rootFilePath + File.separator + "vote" + File.separator + "document" + File.separator + "offline-input.xls"));
        return Responses.getFileEntity(docDir, "투표오프라인입력양식.xls");
    }
}
