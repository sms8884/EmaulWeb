package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaha.web.emaul.exception.EmaulWebException;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.model.UserType;
import com.jaha.web.emaul.model.Vote;
import com.jaha.web.emaul.model.VoteForm;
import com.jaha.web.emaul.model.VoteItem;
import com.jaha.web.emaul.model.VoteKey;
import com.jaha.web.emaul.model.VoteOfflineResult;
import com.jaha.web.emaul.model.VoteSearchValidator;
import com.jaha.web.emaul.model.Voter;
import com.jaha.web.emaul.model.VoterOffline;
import com.jaha.web.emaul.model.VoterSecurity;
import com.jaha.web.emaul.model.th.VoteOfflinePrint;
import com.jaha.web.emaul.model.th.VoteResult;
import com.jaha.web.emaul.repo.VoteItemRepository;
import com.jaha.web.emaul.repo.VoteKeyRepository;
import com.jaha.web.emaul.repo.VoteRepository;
import com.jaha.web.emaul.repo.VoterOfflineRepository;
import com.jaha.web.emaul.repo.VoterRepository;
import com.jaha.web.emaul.repo.VoterSecurityRepository;
import com.jaha.web.emaul.service.GcmService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.PhoneAuthService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.service.VoteService;
import com.jaha.web.emaul.util.PageWrapper;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.Thumbnails;
import com.jaha.web.emaul.util.VoteCrypto;
import com.jaha.web.emaul.util.VoteStatusUtil;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAction;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.util.PushUtils;


/**
 * Created by doring on 15. 2. 25..
 */
@Controller
public class VoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoteController.class);

    @Autowired
    private VoteService voteService;
    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private GcmService gcmService;

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteKeyRepository voteKeyRepository;
    @Autowired
    private VoteItemRepository voteItemRepository;
    @Autowired
    private VoterSecurityRepository voterSecurityRepository;

    @Autowired
    private VoterRepository voterRepository;
    @Autowired
    private VoterOfflineRepository voterOfflineRepository;

    @Autowired
    private PhoneAuthService phoneAuthService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VoteSearchValidator searchValidator;

    // [START] 광고 푸시 추가 by realsnake 2016.09.19
    @Autowired
    private PushUtils pushUtils;
    // [END]

    @RequestMapping(value = "/api/vote/app-sign-image/{voteId}/{fileBaseName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleAppSignImageRequest(@PathVariable("voteId") Long voteId, @PathVariable("fileBaseName") String fileBaseName) {

        File toServeUp = new File("/nas/EMaul/vote/sign-image", String.format("%s/%s.jpg", voteId, fileBaseName));

        return Responses.getFileEntity(toServeUp, fileBaseName + ".jpg");
    }


    @RequestMapping(value = "/api/vote/sign-image/{voteId}/{userId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleSignImageRequest(@PathVariable("voteId") Long voteId, @PathVariable("userId") Long userId) {

        File toServeUp = new File("/nas/EMaul/vote/sign-image", String.format("%s/%s.jpg", voteId, userId));

        return Responses.getFileEntity(toServeUp, String.valueOf(userId) + ".jpg");
    }

    @RequestMapping(value = "/api/vote/image/{voteId}/{fileBaseName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleVoteImageRequest(@PathVariable("voteId") Long voteId, @PathVariable("fileBaseName") String fileBaseName) {

        File toServeUp = new File("/nas/EMaul/vote/vote-image", String.format("%s/%s.jpg", voteId, fileBaseName));

        return Responses.getFileEntity(toServeUp, fileBaseName + ".jpg");
    }

    @RequestMapping(value = "/api/vote/item-image/{voteId}/{fileBaseName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleVoteItemImageRequest(@PathVariable("voteId") Long voteId, @PathVariable("fileBaseName") Long fileBaseName) {

        File toServeUp = new File("/nas/EMaul/vote/vote-item-image", String.format("%s/%s.jpg", voteId, fileBaseName));

        return Responses.getFileEntity(toServeUp, fileBaseName + ".jpg");
    }

    @RequestMapping(value = "/admin/vote/{type}/list", method = RequestMethod.GET)
    public String listVoteAdmin(HttpServletRequest req, @PathVariable(value = "type") String type, Pageable pageable, Model model) {
        sendSecuritySms();

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "regDate"));

        PageWrapper<Vote> votes = null;
        if ("vote".equals(type)) {
            votes = new PageWrapper<Vote>(voteService.getVotes(user.house.apt.id, pageRequest), "/admin/vote/vote/list");
        } else if ("poll".equals(type)) {
            votes = new PageWrapper<Vote>(voteService.getPolls(user.house.apt.id, pageRequest), "/admin/vote/poll/list");
        }
        List<Vote> voteList = votes.getContent();
        for (Vote vote : voteList) {
            vote.statusKor = VoteStatusUtil.getPublicStatusText(vote.status);
        }

        model.addAttribute("page", votes);
        model.addAttribute("type", type);
        return "admin/vote-list";
    }

    @RequestMapping(value = "/jaha/vote/vote/list", method = RequestMethod.GET)
    public String listVoteJaha(HttpServletRequest req, @ModelAttribute VoteForm.Search searchForm, Pageable pageable, Model model, BindingResult bindingResult) {
        searchValidator.validate(searchForm, bindingResult);

        if (bindingResult.hasErrors())
            throw new EmaulWebException(bindingResult.getGlobalError().getDefaultMessage());

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "regDate"));

        PageWrapper<Vote> votes = new PageWrapper<>(voteService.getVotes(searchForm, pageRequest), "/jaha/vote/vote/list");

        List<Vote> voteList = votes.getContent();
        voteList.stream().forEach(vote -> vote.statusKor = VoteStatusUtil.getPublicStatusText(vote.status));

        model.addAttribute("page", votes);
        model.addAttribute("type", "vote");
        model.addAttribute("searchForm", searchForm);

        return "jaha/vote-list-eh";
    }


    @RequestMapping(value = "/admin/{type}/create", method = RequestMethod.GET)
    public String createVote(HttpServletRequest req, Model model, @PathVariable(value = "type") String type) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<String> dongs = houseService.getDongs(user.house.apt.id);

        model.addAttribute("type", type);
        model.addAttribute("dongs", dongs);

        return "admin/vote-create";
    }

    @RequestMapping(value = "/admin/vote/ajax/voteKeys", method = RequestMethod.GET)
    @ResponseBody
    public String getAvailableVoteKeys(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<VoteKey> voteKeys = voteService.getVoteKeysAvailable(user.house.apt.id);

        String json = new Gson().toJson(voteKeys);
        JSONArray obj = new JSONArray(json);

        return obj.toString();
    }

    @RequestMapping(value = "/admin/vote/modify/{voteId}", method = RequestMethod.GET)
    public String modifyVote(HttpServletRequest req, Model model, @PathVariable(value = "voteId") Long voteId) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<String> dongs = houseService.getDongs(user.house.apt.id);

        Vote vote = voteService.getVote(voteId);
        List<String> userTypeList = Lists.newArrayList();
        if (vote.jsonArrayTargetUserTypes != null && !vote.jsonArrayTargetUserTypes.isEmpty()) {
            userTypeList = new Gson().fromJson(vote.jsonArrayTargetUserTypes, new TypeToken<List<String>>() {}.getType());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH시 mm분", Locale.KOREA);

        model.addAttribute("dongs", dongs);
        model.addAttribute("vote", vote);
        model.addAttribute("startDate", sdf.format(vote.startDate));
        model.addAttribute("endDate", sdf.format(vote.endDate));
        model.addAttribute("startDateUtc", vote.startDate.getTime());
        model.addAttribute("endDateUtc", vote.endDate.getTime());
        model.addAttribute("userTypeList", userTypeList);

        return "admin/vote-modify";
    }

    @RequestMapping(value = "/admin/vote/modify/{voteId}", method = RequestMethod.POST)
    public String modifyVote(@PathVariable(value = "voteId") Long voteId, MultipartHttpServletRequest req) throws IOException, ServletException, ParseException {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        Map<String, String[]> paramMap = req.getParameterMap();
        Map<String, MultipartFile> fileMap = req.getFileMap();

        String type = paramMap.get("type")[0];

        Vote vote = voteService.getVote(voteId);

        if (!user.type.jaha && !user.house.apt.id.equals(vote.targetApt)) {
            return "redirect:/error";
        }

        fillVoteData(false, user, vote, paramMap, fileMap);

        return "redirect:/admin/vote/" + type + "/list";
    }

    @RequestMapping(value = "/admin/vote/create", method = RequestMethod.POST)
    public String createVote(MultipartHttpServletRequest req) throws IOException, ServletException, ParseException {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        Map<String, String[]> paramMap = req.getParameterMap();
        Map<String, MultipartFile> fileMap = req.getFileMap();

        String type = paramMap.get("type")[0];

        Vote vote = new Vote();
        fillVoteData(true, user, vote, paramMap, fileMap);

        if (type.equals("vote")) {
            Boolean checkCreateNoti = false;
            if (paramMap.get("checkCreateNoti") != null) {
                checkCreateNoti = "on".equalsIgnoreCase(paramMap.get("checkCreateNoti")[0]);
            }
            if (checkCreateNoti) {
                List<User> userList = getNotVoteList(vote);
                List<Long> userIds = Lists.newArrayList();
                for (User users : userList) {
                    if (users.setting.notiAlarm) {
                        userIds.add(users.id);
                    }
                }

                //////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
                // GcmSendForm form = new GcmSendForm();
                // Map<String, String> msg = Maps.newHashMap();
                // msg.put("type", "action");
                // // msg.put("action", "emaul://vote-detail?id=" + vote.id);
                // msg.put("action", "emaul://vote");
                // if (vote.startDate.getTime() <= new Date().getTime()) {
                // msg.put("titleResId", "vote_started");
                // msg.put("value", "'" + vote.title + "' 전자투표가 시작되었습니다.");
                // } else {
                // msg.put("titleResId", "new_vote_registed");
                // SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
                // String startDate = sdf.format(vote.startDate);
                // msg.put("value", startDate + "부터 전자투표가 진행될 예정입니다. 후보자 공약을 확인하세요.");
                // }
                // form.setUserIds(Lists.newArrayList(userIds));
                // form.setMessage(msg);
                //
                // gcmService.sendGcm(form);

                String title = null;
                String value = null;

                List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.ALARM, userIds);

                if (vote.startDate.getTime() <= new Date().getTime()) {
                    title = PushMessage.VOTE_STARTED_TITLE.getValue();
                    value = String.format(PushMessage.VOTE_STARTED_BODY.getValue(), vote.title);
                } else {
                    title = PushMessage.VOTE_TOBE_TITLE.getValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
                    String startDate = sdf.format(vote.startDate);
                    value = String.format(PushMessage.VOTE_TOBE_BODY.getValue(), startDate);
                }

                this.pushUtils.sendPush(PushGubun.VOTE, title, value, PushAction.VOTE.getValue(), null, false, targetUserList);
                //////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
            }
        }
        return "redirect:/admin/vote/" + type + "/list";
    }

    private Vote fillVoteData(boolean isNewVote, User user, Vote vote, Map<String, String[]> paramMap, Map<String, MultipartFile> fileMap) throws ParseException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH시 mm분", Locale.KOREA);

        String type = paramMap.get("type")[0];
        // default, candidate, YN
        String typeOption = paramMap.get("typeOptions")[0];
        String title = paramMap.get("title")[0];
        String description = paramMap.get("description")[0];
        Date startDate = sdf.parse(paramMap.get("startDate")[0]);
        Date endDate = sdf.parse(paramMap.get("endDate")[0]);
        String question = paramMap.get("question-" + typeOption)[0];
        String targetDong = paramMap.get("voteTarget")[0];
        String selectedHoList = paramMap.get("selected-target-ho")[0];
        String votersCount = paramMap.get("voters-count")[0];

        // 보안투표
        String enableSecurity = "N";
        if (paramMap.get("enableSecurity") != null) {
            enableSecurity = paramMap.get("enableSecurity")[0];
        }
        String vkId = null;
        if (paramMap.get("vk_id") != null) {
            vkId = paramMap.get("vk_id")[0];
        }

        String securityLevel = null;
        if (paramMap.get("securityLevel") != null) {
            securityLevel = paramMap.get("securityLevel")[0];
        }

        Boolean showNumber = true;
        if (!"YN".equalsIgnoreCase(typeOption) && !"Y".equalsIgnoreCase(typeOption)) {
            // 찬반, 동의형이 아니면..
            String onOff = paramMap.get("showNumbers-" + typeOption) == null ? "off" : paramMap.get("showNumbers-" + typeOption)[0];
            showNumber = "on".equalsIgnoreCase(onOff);
        }

        List<MultipartFile> baseImages = Lists.newArrayList();
        for (int i = 1; i <= 5; i++) {
            MultipartFile file = fileMap.get("base-img-" + i);
            if (file != null && !file.isEmpty()) {
                baseImages.add(file);
            }
        }
        Boolean houseLimited = true;
        if (paramMap.get("houseLimited") != null) {
            houseLimited = "off".equalsIgnoreCase(paramMap.get("houseLimited")[0]);
        }

        Boolean offlineAvailable = false;
        if (!"vote".equals(type)) {
            offlineAvailable = true;
        } else if (paramMap.get("offline_available") != null) {
            offlineAvailable = "on".equalsIgnoreCase(paramMap.get("offline_available")[0]);
        }

        vote.title = title;
        vote.description = description;
        vote.type = voteService.getVoteType(type.toLowerCase(), typeOption); // 여기서 DB조회
        vote.startDate = startDate;
        vote.endDate = endDate;
        vote.question = question;
        vote.numberEnabled = showNumber;
        vote.houseLimited = houseLimited;
        vote.offlineAvailable = offlineAvailable;
        vote.jsonArrayTargetHo = "";
        vote.jsonArrayTargetUserTypes = "";
        vote.votersCount = Longs.tryParse(votersCount);
        if (vote.votersCount == null) {
            vote.votersCount = 0l;
        }
        vote.enableSecurity = enableSecurity;
        vote.securityLevel = securityLevel;

        // 작성시 알림 전송이면 pushSendDate 업데이트
        try {
            if ("on".equalsIgnoreCase(paramMap.get("checkCreateNoti")[0])) {
                vote.pushSendDate = new Date();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field[] userTypeFields = UserType.class.getDeclaredFields();
        List<String> voteUserTypes = Lists.newArrayList();
        for (Field userTypeField : userTypeFields) {
            if ("userId".equals(userTypeField.getName()) || "typeMap".equals(userTypeField.getName())) {
                continue;
            }
            if (paramMap.get("chk-user-type-" + userTypeField.getName()) != null) {
                voteUserTypes.add(userTypeField.getName());
            }
        }
        vote.jsonArrayTargetUserTypes = new Gson().toJson(voteUserTypes);
        if (selectedHoList != null && !selectedHoList.isEmpty()) {
            vote.jsonArrayTargetHo = selectedHoList;
        }
        if (isNewVote) {
            vote.status = "ready";
            vote.visible = true;
            vote.rangeSido = "";
            vote.rangeSigungu = "";
            vote.regDate = new Date();
            vote.targetApt = user.house.apt;
            vote.voteResultAvailable = false;
        }
        vote.multipleChoice = false;
        vote.targetDong = "all".equalsIgnoreCase(targetDong) ? "" : targetDong;
        if (isNewVote) {
            vote.imageCount = baseImages.size();
        } else {
            if (baseImages.size() != 0) {
                vote.imageCount = baseImages.size();
            }
        }

        List<VoteItem> oldVoteItems = Lists.newArrayList();
        if (!isNewVote && vote.items != null && !vote.items.isEmpty()) {
            oldVoteItems = vote.items;
        }

        List<String> commitment = Lists.newArrayList();
        List<String> profile = Lists.newArrayList();
        List<String> voteItems = Lists.newArrayList();
        for (int i = 1; i < 500; i++) {
            String[] values = paramMap.get("text-" + typeOption + "-" + i);
            if (values == null) {
                break;
            }
            voteItems.add(values[0]);
        }
        if ("candidate".equalsIgnoreCase(typeOption) || "candidate_single".equalsIgnoreCase(typeOption)) {
            for (int i = 1; i < 500; i++) {
                String[] values = paramMap.get("commitment-" + typeOption + "-" + i);
                if (values == null) {
                    break;
                }
                commitment.add(values[0]);
            }
            for (int i = 1; i < 500; i++) {
                String[] values = paramMap.get("profile-" + typeOption + "-" + i);
                if (values == null) {
                    break;
                }
                profile.add(values[0]);
            }
        }

        vote.items = Lists.newArrayList();
        if ("YN".equalsIgnoreCase(typeOption)) {
            // 찬성반대형
            VoteItem voteItem = new VoteItem();
            voteItem.parentId = vote.id;
            voteItem.title = "찬성";
            voteItem.isSubjective = false;
            voteItem.commitment = null;
            voteItem.imageCount = 0;

            vote.items.add(voteItem);

            voteItem = new VoteItem();
            voteItem.parentId = vote.id;
            voteItem.title = "반대";
            voteItem.isSubjective = false;
            voteItem.commitment = null;
            voteItem.imageCount = 0;

            vote.items.add(voteItem);

            vote.numberEnabled = false;
        } else if ("Y".equalsIgnoreCase(typeOption)) {
            // 동의형
            VoteItem voteItem = new VoteItem();
            voteItem.parentId = vote.id;
            voteItem.title = "서명";
            voteItem.isSubjective = false;
            voteItem.commitment = null;
            voteItem.imageCount = 0;
            vote.items.add(voteItem);
            vote.numberEnabled = false;
        } else {
            final int voteItemCount = voteItems.size();
            for (int i = 0; i < voteItemCount; i++) {
                VoteItem voteItem = new VoteItem();
                voteItem.parentId = vote.id;
                voteItem.title = voteItems.get(i);
                voteItem.isSubjective = false;
                if ("candidate".equalsIgnoreCase(typeOption) || "candidate_single".equalsIgnoreCase(typeOption)) {
                    voteItem.commitment = commitment.get(i);
                    voteItem.profile = profile.get(i);
                    voteItem.imageCount = 1;
                } else {
                    voteItem.imageCount = 0;
                    if (!"vote".equals(type)) { // poll
                        String[] strSubjective = paramMap.get("is-subjective-" + (i + 1));
                        voteItem.isSubjective = strSubjective != null && "on".equalsIgnoreCase(strSubjective[0]);
                    }
                }

                vote.items.add(voteItem);
            }

            if ("candidate_single".equalsIgnoreCase(typeOption)) {
                VoteItem voteItem = new VoteItem();
                voteItem.parentId = vote.id;
                voteItem.title = "반대";
                voteItem.isSubjective = false;
                voteItem.commitment = null;
                voteItem.imageCount = 0;

                vote.items.add(voteItem);
            }
        }

        if (vote.enableSecurity.equals("Y")) {
            VoteKey voteKey = voteService.getVoteKey(Longs.tryParse(vkId));
            voteKey.startDt = vote.startDate;
            voteKey.endDt = vote.endDate;
            vote.voteKey = voteKey;
            // 해당 키에 연동된 투표 전부 변경
            List<Vote> voteList = voteRepository.findByVoteKey(voteKey);
            for (Vote tmpVote : voteList) {
                tmpVote.startDate = vote.startDate;
                tmpVote.endDate = vote.endDate;
                voteService.saveAndFlush(tmpVote);
            }
        } else {
            vote.voteKey = null;
        }

        vote = voteService.saveAndFlush(vote);

        MultipartFile file1 = fileMap.get("doc_file1");
        MultipartFile file2 = fileMap.get("doc_file2");
        if (file1 != null && !file1.isEmpty()) {
            vote.file1 = saveVoteDocFile(vote.id, file1);
        } else if (!isNewVote) {
            vote.file1 = null;
        }
        if (file2 != null && !file2.isEmpty()) {
            vote.file2 = saveVoteDocFile(vote.id, file2);
        } else if (!isNewVote) {
            vote.file2 = null;
        }

        voteService.updateVoteFiles(vote.id, vote.file1, vote.file2);

        if (!oldVoteItems.isEmpty()) {
            voteService.deleteVoteItems(oldVoteItems);
        }

        // image 저장
        if (isNewVote || !baseImages.isEmpty()) {
            int imageIndex = 0;
            for (MultipartFile baseImage : baseImages) {
                File dir = new File("/nas/EMaul/vote/vote-image/" + vote.id);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dest = new File(dir, String.format("%s.jpg", imageIndex++));
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                baseImage.transferTo(dest);
                Thumbnails.create(dest);
            }
        }

        if (isNewVote) {
            int imageIndex = 0;
            for (MultipartFile baseImage : baseImages) {
                File dir = new File("/nas/EMaul/vote/vote-image/" + vote.id);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dest = new File(dir, String.format("%s.jpg", imageIndex++));
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                baseImage.transferTo(dest);
                Thumbnails.create(dest);
            }
        }

        if ("candidate".equalsIgnoreCase(typeOption) || "candidate_single".equalsIgnoreCase(typeOption)) {
            File dir = new File("/nas/EMaul/vote/vote-item-image/" + vote.id);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (int i = 1; i <= 500; i++) {

                MultipartFile file = null;
                if ("candidate".equalsIgnoreCase(typeOption)) {
                    file = fileMap.get("img-candidate-" + i);
                } else {
                    file = fileMap.get("img-candidate_single-" + i);
                }
                if (file == null) {
                    break;
                }
                File dest = new File(dir, String.format("%s.jpg", (i - 1)));
                if (file.isEmpty()) {
                    if (isNewVote) {
                        File candidate = new File("/nas/EMaul/user", "anonymous.png");
                        FileUtils.copyFile(candidate, dest);
                    }
                } else {
                    file.transferTo(dest);
                }
            }
        }

        return vote;
    }

    /**
     *
     * @param voteId
     * @param file
     * @return url without base address
     */
    private String saveVoteDocFile(Long voteId, MultipartFile file) {
        long voteParentNum = voteId / 1000l;

        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && !originalFileName.isEmpty()) {
            try {
                File dir = new File(String.format("/nas/EMaul/vote/file/%s/%s", voteParentNum, voteId));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(dir, originalFileName);
                dest.createNewFile();
                file.transferTo(dest);

                return "/api/vote/file/" + voteId + "/" + originalFileName;
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }

        return null;
    }

    @RequestMapping(value = "/admin/vote/visible/{voteId}/{val}", method = RequestMethod.PUT)
    public @ResponseBody String setVoteVisibility(@PathVariable(value = "voteId") Long voteId, @PathVariable(value = "val") String val) {

        Vote vote = voteService.getVote(voteId);
        vote.visible = "1".equals(val);

        voteService.saveAndFlush(vote);

        return "";
    }

    @RequestMapping(value = "/admin/vote/detail/{voteId}", method = RequestMethod.GET)
    public @ResponseBody String getVoteDetail(@PathVariable(value = "voteId") Long voteId) throws JsonProcessingException {
        Vote vote = voteService.getVote(voteId);
        LOGGER.debug("status : " + vote.status);
        // vote.status = VoteStatusUtil.getPublicStatusText(vote.status);
        String statusText = VoteStatusUtil.getPublicStatusText(vote.status);

        List<User> notVoteUsers = getNotVoteList(vote);
        List<Long> userIds = Lists.newArrayList();
        for (User users : notVoteUsers) {
            if (users.setting.notiAlarm) {
                userIds.add(users.id);
            }
        }

        String json = new Gson().toJson(vote);
        JSONObject obj = new JSONObject(json);
        obj.put("regDate", vote.regDate.getTime());
        obj.put("startDate", vote.startDate.getTime());
        obj.put("endDate", vote.endDate.getTime());
        obj.put("notVoteUserCnt", userIds.size());
        obj.put("statusText", statusText);
        obj.put("pushDate", vote.pushSendDate != null ? (Long) vote.pushSendDate.getTime() : "");
        return obj.toString();
    }

    @RequestMapping(value = "/admin/vote/delete/{type}/{voteId}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteVote(@PathVariable(value = "type") String type, @PathVariable(value = "voteId") Long voteId) {
        voteService.delete(voteId);
        return "";
    }

    @RequestMapping(value = "/admin/vote/list/{voteId}/result", method = RequestMethod.GET)
    public String getVoteResult(@PathVariable(value = "voteId") final Long voteId, Model model) {
        Vote vote = voteRepository.findOne(voteId);
        final VoteResult result = new VoteResult();

        List<Voter> voters = voterRepository.findByVoteId(voteId, new Sort(Sort.Direction.DESC, "voteDate"));
        result.voterList = Lists.transform(voters, new Function<Voter, VoteResult.Voter>() {
            @Override
            public VoteResult.Voter apply(Voter input) {
                VoteResult.Voter voter = new VoteResult.Voter();
                voter.name = input.user.getFullName();
                voter.signUrl = input.signImageUri;
                voter.userId = input.user.id;
                voter.dong = input.user.house.dong;
                voter.ho = input.user.house.ho;
                voter.phone = input.user.getPhone();
                if (input.registerType.equals("tablet")) {
                    voter.name = input.voterName;
                    voter.dong = input.dong;
                    voter.ho = input.ho;
                    voter.phone = "현장투표";
                }
                voter.voteDate = input.voteDate.toString();
                return voter;
            }
        });

        result.voterCount = result.voterList.size();

        List<VoteItem> voteItems = voteItemRepository.findByParentId(voteId);
        result.statuses = Lists.transform(voteItems, new Function<VoteItem, VoteResult.VoteItemStatus>() {
            @Override
            public VoteResult.VoteItemStatus apply(VoteItem input) {
                VoteResult.VoteItemStatus item = new VoteResult.VoteItemStatus();
                item.name = input.title;

                if (vote.enableSecurity.equals("Y")) {
                    List<VoterSecurity> voterSecurities = voterSecurityRepository.findByVoteIdAndItemId(voteId, input.id, new Sort(Sort.Direction.DESC, "regDt"));
                    item.count = voterSecurities.size();
                } else {
                    List<Voter> votersByItems = voterRepository.findByVoteIdAndVoteItemId(voteId, input.id, new Sort(Sort.Direction.DESC, "voteDate"));
                    item.count = votersByItems.size();
                }

                float rate = item.count / (float) result.voterCount * 100;
                item.rate = String.format("%.2f", rate) + "%";
                return item;
            }
        });

        String firstItemName = result.statuses.get(0).name;
        model.addAttribute("first", firstItemName);

        final VoteResult.VoteItemStatus mostVoted = Collections.max(result.statuses, (o1, o2) -> o1.count.compareTo(o2.count));
        List<VoteResult.VoteItemStatus> mostVotedUsers = Lists.newArrayList(Collections2.filter(result.statuses, input -> input.count >= mostVoted.count));

        model.addAttribute("max", mostVotedUsers);

        // LOGGER.debug("vote result : " + new Gson().toJson(result));

        model.addAttribute("result", result);
        //
        // model.addAttribute("voteItems", voteItems);
        //
        // Map<Long, Integer> countMap = Maps.newHashMap();
        // int totalVoters = 0;
        // for (VoteItem voteItem : voteItems) {
        // List<Voter> votersByItems = voteService.getVotersByItem(voteId, voteItem.id);
        // countMap.put(voteItem.id, votersByItems.size());
        // totalVoters += votersByItems.size();
        // }
        // model.addAttribute("votersCountMap", countMap);
        // model.addAttribute("totalVoters", totalVoters);
        //
        // List<Voter> voters = voteService.getVoters(voteId);
        // model.addAttribute("voters", voters);
        //
        model.addAttribute("vote", vote);


        if (vote.voteResultAvailable) {
            VoteOfflineResult voteOfflineResult = voteService.getOfflineResult(vote.id);
            final Map<Long, Long> voteItemResult = new Gson().fromJson(voteOfflineResult.jsonMapVoteItemResult, new TypeToken<Map<Long, Long>>() {}.getType());

            final VoteOfflinePrint offlinePrint = new VoteOfflinePrint();

            offlinePrint.offVoterCount = 0L;
            for (VoteItem v : voteItems) {
                if (voteItemResult.get(v.id) != null) {
                    offlinePrint.offVoterCount += voteItemResult.get(v.id);
                }
            }

            offlinePrint.offStatuses = Lists.transform(voteItems, new Function<VoteItem, VoteOfflinePrint.VoteItemOffStatus>() {
                @Override
                public VoteOfflinePrint.VoteItemOffStatus apply(VoteItem input) {
                    VoteOfflinePrint.VoteItemOffStatus item = new VoteOfflinePrint.VoteItemOffStatus();
                    item.name = input.title;
                    if (voteItemResult.get(input.id) != null) {
                        item.count = voteItemResult.get(input.id);// vote.votersCount;
                    } else {
                        item.count = 0l;
                    }
                    float rate = item.count / (float) offlinePrint.offVoterCount * 100;
                    item.rate = String.format("%.2f", rate) + "%";
                    return item;
                }
            });

            offlinePrint.additionVoterCount = result.voterCount + offlinePrint.offVoterCount;

            offlinePrint.additionStatuses = new ArrayList<>();

            for (int i = 0; i < result.statuses.size(); i++) {
                VoteOfflinePrint.VoteItemAdditionStatus additionStatus = new VoteOfflinePrint.VoteItemAdditionStatus();
                additionStatus.name = result.statuses.get(i).name;
                additionStatus.count = result.statuses.get(i).count + offlinePrint.offStatuses.get(i).count;
                float rate = additionStatus.count / (float) offlinePrint.additionVoterCount * 100;
                additionStatus.rate = String.format("%.2f", rate) + "%";
                offlinePrint.additionStatuses.add(additionStatus);
            }
            if (vote.offlineAvailable) {
                model.addAttribute("off", offlinePrint);
            } else {
                model.addAttribute("off", null);
            }



            final VoteOfflinePrint.VoteItemAdditionStatus additionMostVoted = Collections.max(offlinePrint.additionStatuses, (o1, o2) -> o1.count.compareTo(o2.count));
            List<VoteOfflinePrint.VoteItemAdditionStatus> additionMostVotedUsers =
                    Lists.newArrayList(Collections2.filter(offlinePrint.additionStatuses, input -> input.count >= additionMostVoted.count));

            model.addAttribute("max", additionMostVotedUsers);

        }

        List<VoterOffline> voterOfflines = voterOfflineRepository.findByVoteId(voteId);
        model.addAttribute("voterOfflines", voterOfflines);

        boolean resultState = true;
        if (vote.type.main.equals("vote")) {
            if (vote.status.equals("done")) {
                if (vote.enableSecurity.equals("Y") && vote.decYn.equals("N")) {
                    resultState = false;
                }
            } else {
                resultState = false;
            }
        }
        model.addAttribute("resultState", resultState);
        return "admin/vote-result";
    }


    @RequestMapping(value = "/{auth}/vote/realtime/{voteId}", method = RequestMethod.GET)
    public String getVoteRealtime(@PathVariable(value = "auth") String auth, @PathVariable(value = "voteId") Long voteId, Model model) {
        Vote vote = voteService.getVote(voteId);

        List<Long> voteItemIds = Lists.transform(vote.items, new Function<VoteItem, Long>() {
            @Override
            public Long apply(VoteItem input) {
                return input.id;
            }
        });


        Map<Long, Long> resultMap = Maps.newHashMap();
        Date resultRegDate = null;
        String voteResultText = "";
        String resultRegUser = "";
        String resultVoterFile = "";
        if (vote.voteResultAvailable) {
            VoteOfflineResult result = voteService.getOfflineResult(vote.id);
            resultMap = new Gson().fromJson(result.jsonMapVoteItemResult, new TypeToken<Map<Long, Long>>() {}.getType());
            User user = userService.getUser(result.regUserId);
            resultRegDate = result.regDate;
            voteResultText = result.resultText;
            resultRegUser = user.getFullName() + "(" + user.getEmail() + ")";
            if (result.votersFname != null) {
                resultVoterFile = result.votersFname;
            }
        }

        model.addAttribute("vote", vote);
        model.addAttribute("itemIds", voteItemIds);
        model.addAttribute("voteResultMap", resultMap);
        model.addAttribute("voteResultRegDate", resultRegDate != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(resultRegDate) : "에러");
        model.addAttribute("voteResultText", voteResultText);
        model.addAttribute("resultRegUser", resultRegUser);
        model.addAttribute("resultVoterFile", resultVoterFile);

        return auth.equals("jaha") ? "jaha/vote-realtime" : "admin/vote-realtime";
    }

    // TODO 경로, 위치 검토
    @RequestMapping(value = "/admin/vote/create/target/{aptId}/{dong}/{sort}", method = RequestMethod.GET)
    @ResponseBody
    String getTargetHo(@PathVariable(value = "aptId") Long aptId, @PathVariable(value = "dong") String dong, @PathVariable(value = "sort") String sort) {
        List<String> hos = houseService.getHos(aptId, dong);

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

        if ("1".equals(sort)) {

            Collections.sort(tempHos1, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Integer lhs = Integer.valueOf(o1);
                    Integer rhs = Integer.valueOf(o2);
                    return Integer.valueOf(lhs % 100).compareTo(rhs % 100);
                }
            });

        }

        Collections.sort(tempHos2);
        tempHos1.addAll(tempHos2);

        return new JSONArray(tempHos1).toString();
    }

    @RequestMapping(value = "/admin/vote/available", method = RequestMethod.GET)
    public @ResponseBody String isVoteAvailable(@RequestParam(value = "voteId") Long voteId, @RequestParam(value = "aptId") Long aptId, @RequestParam(value = "dong") String dong,
            @RequestParam(value = "ho") String ho) throws JsonProcessingException {
        Voter voter = voteService.getOnlineVoted(voteId, aptId, dong, ho);
        VoterOffline voterOffline = voteService.getOfflineVoted(voteId, aptId, dong, ho);

        Vote vote = voteService.getVote(voteId);
        if (!vote.targetApt.equals(aptId) || (vote.targetDong != null && !vote.targetDong.isEmpty() && !vote.targetDong.equalsIgnoreCase(dong))
                || (vote.jsonArrayTargetHo != null && !vote.jsonArrayTargetHo.isEmpty() && !vote.jsonArrayTargetHo.contains("\"" + ho + "\""))) {
            return "INVALID_VOTE_ID";
        }

        ObjectMapper mapper = new ObjectMapper();

        if (voter != null) {
            return mapper.writeValueAsString(voter);
        }

        if (voterOffline != null) {
            return mapper.writeValueAsString(voterOffline);
        }

        return "";
    }

    @RequestMapping(value = "/admin/vote/offline/join", method = RequestMethod.GET)
    public @ResponseBody String isVoteAvailable(@RequestParam(value = "voteId") Long voteId, @RequestParam(value = "aptId") Long aptId, @RequestParam(value = "dong") String dong,
            @RequestParam(value = "ho") String ho, @RequestParam(value = "name") String name) throws JsonProcessingException {
        VoterOffline vo = voteService.putOfflineVoter(voteId, aptId, dong, ho, name);
        if (vo == null) {
            return "";
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(vo);
    }

    @RequestMapping(value = "/admin/vote/offline/result/save", method = RequestMethod.POST)
    public @ResponseBody String saveOfflineVoteResult(HttpServletRequest req, @RequestParam(value = "json") String json,
            @RequestParam(value = "votersFile", required = false) MultipartFile votersFile) {
        LOGGER.debug("json : " + json);
        JSONObject obj = new JSONObject(json);
        Long aptId = obj.getLong("aptId");
        Long voteId = obj.getLong("voteId");
        JSONObject itemResult = obj.getJSONObject("itemResult");

        Vote vote = voteService.getVote(voteId);
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.jaha && !vote.targetApt.equals(user.house.apt.id)) {
            return null;
        }

        Date now = new Date();

        VoteOfflineResult offlineResult = new VoteOfflineResult();
        offlineResult.aptId = aptId;
        offlineResult.regDate = now;
        offlineResult.regUserId = SessionAttrs.getUserId(req.getSession());
        offlineResult.voteId = voteId;
        offlineResult.resultText = obj.getString("voteResultText");
        if (votersFile != null) {
            LOGGER.debug("votersFname : " + votersFile.getOriginalFilename());
            offlineResult.votersFname = votersFile.getOriginalFilename();

            try {
                // File dir = new File(String.format("/Users/imac1/Documents/offline_result")); //
                // 테스트에만 사용함.
                File dir = new File(String.format("/nas/EMaul/vote/offline_voter/%s", voteId));

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // File dest = new File(dir, String.format("%d_%d_%s_%s_%s.jpg",
                // voteId,user.id,dong,ho, Util.getRandomString(4)));
                File dest = new File(dir, String.format("%s.list", voteId));
                dest.createNewFile();
                votersFile.transferTo(dest);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }

        Map<Long, Long> voteItemCountMap = Maps.newHashMap();
        Set<String> keySet = itemResult.keySet();

        for (String key : keySet) {
            voteItemCountMap.put(Longs.tryParse(key), itemResult.getLong(key));
        }
        offlineResult.jsonMapVoteItemResult = new Gson().toJson(voteItemCountMap);

        voteService.saveAndFlush(offlineResult);

        vote.voteResultAvailable = true;
        voteService.saveAndFlush(vote);

        return "success";
    }

    @RequestMapping(value = "/api/vote/offline/votersFile/{voteId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleVoterFileRequest(@PathVariable("voteId") Long voteId) {

        VoteOfflineResult voteOfflineResult = voteService.getOfflineResult(voteId);

        // File toServeUp = new File("/Users/imac1/Documents/offline_result",
        // String.format("%s.list", voteId)); //테스트
        File toServeUp = new File(String.format("/nas/EMaul/vote/offline_voter/%s", voteId), String.format("%s.list", voteId));

        return Responses.getFileEntity(toServeUp, voteOfflineResult.votersFname);
    }

    @RequestMapping(value = "/admin/vote/offline/result/updateVotersFile", method = RequestMethod.POST)
    public @ResponseBody String updateVotersFile(HttpServletRequest req, @RequestParam(value = "voteId") Long voteId, @RequestParam(value = "votersFile", required = false) MultipartFile votersFile) {
        VoteOfflineResult voteOfflineResult = voteService.getOfflineResult(voteId);
        if (votersFile != null) {
            voteOfflineResult.votersFname = votersFile.getOriginalFilename();
            try {
                // File toServeUp = new File("/Users/imac1/Documents/offline_result",
                // String.format("%s.list", voteId)); //테스트
                File toServeUp = new File(String.format("/nas/EMaul/vote/offline_voter/%s", voteId), String.format("%s.list", voteId));
                toServeUp.delete();

                // File dir = new File(String.format("/Users/imac1/Documents/offline_result")); //
                // 테스트에만 사용함.
                File dir = new File(String.format("/nas/EMaul/vote/offline_voter/%s", voteId));

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(dir, String.format("%s.list", voteId));
                dest.createNewFile();
                votersFile.transferTo(dest);

            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        voteService.saveAndFlush(voteOfflineResult);
        return "success";
    }

    @RequestMapping(value = "/admin/vote/offline/result/reset", method = RequestMethod.POST)
    public @ResponseBody String resetOfflineVoteResult(HttpServletRequest req, @RequestBody String json) {
        JSONObject obj = new JSONObject(json);
        Long voteId = obj.getLong("voteId");

        Vote vote = voteService.getVote(voteId);
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.jaha && !vote.targetApt.equals(user.house.apt.id)) {
            return null;
        }

        voteService.deleteOfflineResult(vote.id);

        try {
            File toServeUp = new File("/Users/imac1/Documents/offline_result", String.format("%s.list", voteId)); // 테스트
            // File toServeUp = new File(String.format("/nas/EMaul/vote/offline_voter/%s", voteId),
            // String.format("%s.list", voteId));
            toServeUp.delete();

        } catch (Exception e) {
            LOGGER.error("", e);
        }

        vote.voteResultAvailable = false;
        voteService.saveAndFlush(vote);

        return "success";
    }

    @RequestMapping(value = "/jaha/user/vote-sms-target", method = RequestMethod.GET)
    public @ResponseBody String isVoteAvailable(@RequestParam(value = "voteId") Long voteId) throws JsonProcessingException {
        Vote vote = voteService.getVote(voteId);

        List<UserPrepass> matchUsers = Lists.newArrayList();
        if (vote.jsonArrayTargetHo != null && !vote.jsonArrayTargetHo.isEmpty()) {
            List<String> hoList = new Gson().fromJson(vote.jsonArrayTargetHo, new TypeToken<List<String>>() {}.getType());
            matchUsers = voteService.getUserPrepass(vote.targetApt, vote.targetDong, hoList);
        } else if (vote.targetDong != null && !vote.targetDong.isEmpty()) {
            matchUsers = voteService.getUserPrepass(vote.targetApt, vote.targetDong);
        } else {
            matchUsers = voteService.getUserPrepass(vote.targetApt);
        }

        final List<Voter> votedOnline = voteService.getVoters(voteId);
        final List<VoterOffline> votedOffline = voteService.getOfflineVoters(voteId);

        List<UserPrepass> filteredUsers = Lists.newArrayList(Collections2.filter(matchUsers, new Predicate<UserPrepass>() {
            @Override
            public boolean apply(UserPrepass input) {
                for (Voter voter : votedOnline) {
                    if (input.dong.equals(voter.dong) && input.ho.equals(voter.ho)) {
                        return false;
                    }
                }
                for (VoterOffline voterOffline : votedOffline) {
                    if (input.dong.equals(voterOffline.dong) && input.ho.equals(voterOffline.ho)) {
                        return false;
                    }
                }
                return true;
            }
        }));

        List<String> allUsers = Lists.transform(matchUsers, new Function<UserPrepass, String>() {
            @Override
            public String apply(UserPrepass input) {
                return String.format("%s동 %s호 %s (%s)", input.dong, input.ho, input.fullName, input.phone);
            }
        });
        List<String> notVotedList = Lists.transform(filteredUsers, new Function<UserPrepass, String>() {
            @Override
            public String apply(UserPrepass input) {
                return String.format("%s동 %s호 %s (%s)", input.dong, input.ho, input.fullName, input.phone);
            }
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("all", Sets.newHashSet(allUsers));
        jsonObject.put("not_voted", Sets.newHashSet(notVotedList));

        return jsonObject.toString();
    }

    // 보안투표 관련 추가사항들 - 강지만 2016-03-04
    @RequestMapping(value = "/admin/vote_key/list", method = RequestMethod.GET)
    public String listVoteKey(HttpServletRequest req, Pageable pageable, Model model) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "vkId"));

        PageWrapper<VoteKey> voteKeys = new PageWrapper<VoteKey>(voteService.getVoteKeys(user.house.apt.id, "Y", pageRequest), "/admin/vote_key/list");
        List<VoteKey> voteKeyList = voteKeys.getContent();

        for (VoteKey voteKey : voteKeyList) {
            List<Vote> voteList = voteRepository.findByVoteKey(voteKey);
            for (Vote vote : voteList) {
                // if (vote.decYn.equals("Y")) {
                if ("Y".equals(vote.decYn)) {
                    voteKey.openStatus = 1;
                    break;
                }
            }
        }

        model.addAttribute("page", voteKeys);
        return "admin/vote-key-list";
    }

    @RequestMapping(value = "/admin/vote_key/detail/{voteKeyId}", method = RequestMethod.GET)
    public String detailVoteKey(HttpServletRequest req, Model model, @PathVariable(value = "voteKeyId") Long vkId) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        List<String> dongs = houseService.getDongs(user.house.apt.id);

        VoteKey voteKey = voteKeyRepository.findOne(vkId);
        List<Vote> voteList = voteRepository.findByVoteKey(voteKey);
        for (Vote vote : voteList) {
            if (vote.decYn.equals("Y")) {
                voteKey.openStatus = 1;
                break;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH시 mm분", Locale.KOREA);


        model.addAttribute("voteKey", voteKey);
        model.addAttribute("voteList", voteList);
        model.addAttribute("startDate", sdf.format(voteKey.startDt));
        model.addAttribute("endDate", sdf.format(voteKey.endDt));

        return "admin/vote-key-detail";
    }

    @RequestMapping(value = "/admin/vote_key/ajax/open/{vkId}", method = RequestMethod.GET)
    @ResponseBody
    public String getAvailableVoteKeys(HttpServletRequest req, Model model, @PathVariable(value = "vkId") Long vkId) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        VoteKey voteKey = voteKeyRepository.findOne(vkId);
        // LOGGER.debug("keyGrantDec : " + voteKey.keyGrantDec);
        List<Vote> voteList = voteRepository.findByVoteKey(voteKey);
        VoteCrypto voteCrypto = new VoteCrypto();

        int decCnt = 0;
        int encCnt = 0;
        for (Vote vote : voteList) {
            boolean voteState = true;
            if (vote.decYn.equals("N")) {
                List<VoterSecurity> voterSecurities = voterSecurityRepository.findByVoteId(vote.id, new Sort(Sort.Direction.DESC, "regDt"));
                for (VoterSecurity voterSecurity : voterSecurities) {
                    boolean decState = false;
                    try {
                        VoteCrypto.Item item = voteCrypto.decrypt(voterSecurity.itemIdEnc, voteKey.keyGrantDec);
                        // LOGGER.debug("itemIdEnc : " + voterSecurity.itemIdEnc);
                        // LOGGER.debug("vote_id : " + item.voteId);
                        // LOGGER.debug("item_id : " + item.voteItemId);
                        for (VoteItem tmpItem : vote.items) {
                            if (item.voteItemId == tmpItem.id) {
                                decState = true;
                                voterSecurity.itemId = item.voteItemId;
                                voterSecurityRepository.saveAndFlush(voterSecurity);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.debug(e.toString());
                    }
                    if (decState == false) {
                        voteState = false;
                        break;
                    }
                }
                if (voteState == true) {
                    vote.decYn = "Y";
                    voteService.saveAndFlush(vote);
                    decCnt++;
                }
                encCnt++;
            }
        }
        voteKey.uptDt = new Date();
        voteKeyRepository.saveAndFlush(voteKey);

        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("encCnt", encCnt);
        result.put("decCnt", decCnt);

        String json = new Gson().toJson(result);
        JSONObject obj = new JSONObject(json);

        return obj.toString();
    }

    @RequestMapping(value = "/api/vote-key/image/{fileBaseName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleSignImageRequest(@PathVariable("fileBaseName") String fileBaseName) {
        File toServeUp = new File("/nas/EMaul/vote/key/sign_image", String.format("%s.jpg", fileBaseName)); // 실서버
        // File toServeUp = new File("/Users/imac1/Documents/sign_image", String.format("%s.jpg",
        // fileBaseName) ); //테스트
        return Responses.getFileEntity(toServeUp, fileBaseName + ".jpg");
    }

    // 비정상 보안 투표 문자 발송
    public void sendSecuritySms() {
        List<Vote> voteList = voteRepository.findBySecurityCheckStateGreaterThanAndSecurityNoticeState(0, false);
        LOGGER.debug("sms vote cnt : " + voteList.size());

        if (voteList.size() > 0) {
            String sql = "SELECT a.* FROM user a, user_type b WHERE a.id = b.user_id and b.jaha = b'1'";
            List<User> jahaUser = jdbcTemplate.query(sql, new Object[] {}, (rs, rowNum) -> {
                User user = new User();
                user.id = rs.getLong("id");
                user.setPhone(rs.getString("phone"));
                return user;
            });

            for (Vote vote : voteList) {
                String errorStr = "";
                if (vote.securityCheckState == 1) {
                    errorStr = "[Error: 투표자와 기표건수 오류]\n";
                } else if (vote.securityCheckState == 2) {
                    errorStr = "[Error: 기표정보 길이 불일치]\n";
                }

                for (User user : jahaUser) {
                    phoneAuthService.sendMsgNow(user.getPhone(), "028670816", errorStr + String.format("%s(%s), 투표ID:%s", vote.targetApt.name, vote.targetApt.id, vote.id), "", "");
                }
                vote.securityNoticeState = true;
                voteService.saveAndFlush(vote);
            }

        }
    }

    // 투표 대상자에게 참여 독려 문자 발송
    @RequestMapping(value = "/admin/vote/sendNoti")
    @ResponseBody
    public String sendVoteNoti(HttpServletRequest req, Model model, @RequestParam(value = "voteId") Long voteId, @RequestParam(value = "voteMsg") String voteMsg) {
        if (voteId == null) {
            throw new EmaulWebException("투표 아이디가 없습니다.");
        }
        Vote vote = voteService.getVote(Long.valueOf(voteId));
        List<User> userList = getNotVoteList(vote);
        List<Long> userIds = Lists.newArrayList();
        for (User user : userList) {
            if (user.setting.notiAlarm) {
                userIds.add(user.id);
            }
            LOGGER.debug("noti user : " + user.id);
        }

        //////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////
        // GcmSendForm form = new GcmSendForm();
        // Map<String, String> msg = Maps.newHashMap();
        // msg.put("type", "action");
        // msg.put("titleResId", "vote_request");
        //
        // msg.put("value", voteMsg == "" ? " '" + vote.title + " '에 참여하세요" : voteMsg);
        // // msg.put("action", "emaul://vote-detail?id=" + vote.id);
        // msg.put("action", "emaul://vote");
        // form.setUserIds(Lists.newArrayList(userIds));
        // form.setMessage(msg);
        //
        // gcmService.sendGcm(form);

        List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.ALARM, userIds);

        String title = PushMessage.VOTE_REQ_TITLE.getValue();
        String value = (StringUtils.isBlank(voteMsg)) ? String.format(PushMessage.VOTE_REQ_BODY.getValue(), vote.title) : voteMsg;

        this.pushUtils.sendPush(PushGubun.VOTE, title, value, PushAction.VOTE.getValue(), null, false, targetUserList);
        //////////////////////////////////////////////////// GCM변경, 20161025, 전강욱 ////////////////////////////////////////////////////

        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("sendCnt", userIds.size());

        String json = new Gson().toJson(result);
        JSONObject obj = new JSONObject(json);

        // 푸시 sendDate update
        vote.pushSendDate = new Date();
        voteService.saveAndFlush(vote);

        return obj.toString();
    }


    // 투표 대상자 목록 : 이미 투표에 참여한 회원 제외
    public List<User> getNotVoteList(Vote vote) {
        List<User> voterList = Lists.newArrayList();

        if (vote == null || vote.targetApt == null || vote.targetApt.id == null) {
            return voterList;
        }

        LOGGER.debug(">>> as-is dong : " + vote.targetDong);
        LOGGER.debug(">>> as-is jsonArrayTargetHo : " + vote.jsonArrayTargetHo);
        LOGGER.debug(">>> as-is targetApt.id) : " + vote.targetApt.id);

        if (vote.targetDong == null || vote.targetDong.isEmpty()) {
            voterList = userService.getAllAptUsers(vote.targetApt.id);
        } else {
            List<String> hoList = Lists.newArrayList();
            if (vote.jsonArrayTargetHo != null && !vote.jsonArrayTargetHo.isEmpty()) {
                hoList = new Gson().fromJson(vote.jsonArrayTargetHo, new TypeToken<List<String>>() {}.getType());
            }
            voterList = userService.getUsersByDongAndHoIn(vote.targetApt.id, vote.targetDong, hoList);
        }

        LOGGER.debug(">>> as-is voterList : " + voterList.size());

        final List<Voter> votedOnline = voteService.getVoters(vote.id);
        final List<VoterOffline> votedOffline = voteService.getOfflineVoters(vote.id);

        LOGGER.debug(">>> as-is votedOnline : " + votedOnline.size());
        LOGGER.debug(">>> as-is votedOffline : " + votedOffline.size());

        List<User> filteredUsers = Lists.newArrayList(Collections2.filter(voterList, new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                for (Voter voter : votedOnline) {
                    if (input.house.dong.equals(voter.dong) && input.house.ho.equals(voter.ho)) {
                        LOGGER.debug(">>> input.house.dong : " + input.house.dong + "/" + voter.dong + "/" + voter.ho);
                        return false;
                    }
                }
                if (!vote.multipleChoice) {
                    for (VoterOffline voterOffline : votedOffline) {
                        if (input.house.dong.equals(voterOffline.dong) && input.house.ho.equals(voterOffline.ho)) {
                            LOGGER.debug(">>> input.house.dong : " + input.house.dong + "/" + voterOffline.dong + "/" + voterOffline.ho);
                            return false;
                        }
                    }
                }
                return true;
            }
        }));

        LOGGER.debug(">>> as-is filteredUsers : " + filteredUsers.size());

        return filteredUsers;
    }

    /**
     * @author shavrani 2016-06-28
     */
    @RequestMapping(value = "/api/vote/sign-video/{voteId}/{userId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleSignVideoRequest(HttpServletRequest req, @PathVariable("voteId") Long voteId, @PathVariable("userId") Long userId) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (user.type.jaha) {
            File toServeUp = new File("/nas/EMaul/vote/sign-video", String.format("%s/%s.mp4", voteId, userId));
            // File toServeUp = new File("C:\\nas\\EMaul\\vote\\sign-video\\",
            // String.format("%s\\%s.mp4", voteId, userId));

            return Responses.getFileEntity(toServeUp, String.valueOf(userId) + ".mp4");
        }

        return Responses.getFileEntity(null, "empty");
    }

    /**
     * @author shavrani 2016-06-28
     */
    @RequestMapping(value = "/jaha/vote/sign-video-popup", method = RequestMethod.GET)
    public String handleSigequest(Model model, @RequestParam(value = "voteId") String voteId, @RequestParam(value = "userId") String userId) {

        model.addAttribute("voteId", voteId);
        model.addAttribute("userId", userId);

        return "admin/sign-video-popup";
    }


}
