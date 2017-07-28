package com.jaha.web.emaul.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.util.HtmlUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.BoardComment;
import com.jaha.web.emaul.model.BoardCommentReply;
import com.jaha.web.emaul.model.BoardPost;
import com.jaha.web.emaul.model.BoardPostAirWord;
import com.jaha.web.emaul.model.BoardTag;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.Hashtag;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.spec.BoardSpecification;
import com.jaha.web.emaul.service.BoardCategoryService;
import com.jaha.web.emaul.service.BoardService;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.GroupAdminService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.AddressConverter;
import com.jaha.web.emaul.util.Blurs;
import com.jaha.web.emaul.util.CommonUtil;
import com.jaha.web.emaul.util.HttpUtils;
import com.jaha.web.emaul.util.PageWrapper;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.ScrollPage;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.TagUtils;
import com.jaha.web.emaul.util.Thumbnails;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAction;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.util.PushUtils;

/**
 * Created by doring on 15. 3. 9..
 */
@Controller
public class BoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;
    // @Autowired
    // private GcmService gcmService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private BoardCategoryService boardCategoryService;

    // [START] 단체관리자 기능 추가 by PNS 2016.09.19
    @Autowired
    private GroupAdminService groupAdminService;
    // [END]

    @Autowired
    private HttpUtils httpUtils;

    @Value("${file.path.temp}")
    private String tempFilePath;

    @Value("${file.path.board.image}")
    private String boardImageFilePath;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // [START] 광고 푸시 추가 by realsnake 2016.10.24
    @Autowired
    private PushUtils pushUtils;
    // [END]

    @Deprecated
    @RequestMapping(value = "/api/board/categories/{type}", method = RequestMethod.GET)
    public @ResponseBody List<BoardCategory> listCategory(HttpServletRequest req, @PathVariable(value = "type") String type) {

        if ("community".equalsIgnoreCase(type)) {
            List<BoardCategory> categories = boardService.getCategories(SessionAttrs.getUserId(req.getSession()), "community", "N");
            categories.addAll(0, boardService.getCategories(SessionAttrs.getUserId(req.getSession()), "notice", "N"));
            return categories;
        }
        return boardService.getCategories(SessionAttrs.getUserId(req.getSession()), type, "N");
    }

    @Deprecated
    @RequestMapping(value = "/api/board/post/{postId}", method = RequestMethod.GET)
    public @ResponseBody BoardPost handleGetPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        BoardPost post = boardService.get(user, postId);
        post.user = userService.convertToPublicUser(post.user);

        return post;
    }

    @RequestMapping(value = "/api/board/post/write", method = RequestMethod.POST)
    public @ResponseBody BoardPost writePost(HttpServletRequest req, @RequestParam(value = "content", required = false, defaultValue = "") String content, @RequestParam(value = "title",
            required = false, defaultValue = "") String title, @RequestParam(value = "categoryId") Long categoryId, @RequestParam(value = "rangeAll", required = false) String rangeAll, @RequestParam(
            value = "topFix", required = false) String topFix, @RequestParam(value = "rangeSido", required = false) String rangeSido,
            @RequestParam(value = "rangeSigungu", required = false) String rangeSigungu, @RequestParam(value = "notification", required = false) String notification, @RequestParam(value = "files",
                    required = false) MultipartFile[] files, @RequestParam(value = "files2", required = false) MultipartFile[] files2) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        // LOGGER.info("<<게시글을 등록합니다. 사용자아이디: {}>>", user.id);
        BoardCategory category = boardService.getCategory(categoryId);
        // LOGGER.info("<<카테고리 정보를 조회합니다. 카테고리아이디: {}>>", category.id);

        try {
            Long userAptId = user.house.apt.id;
            Long categoryAptId = category.apt.id;

            if (!userAptId.equals(categoryAptId)) {
                LOGGER.info("<<사용자 로그인아이디({})의 아파트아이디({})와 게시판카테고리의 아파트아이디({}) 상이하여 게시글 등록 강제 종료>>", user.id, userAptId, categoryAptId);
                return null;
            }
        } catch (Exception e) {
            LOGGER.info("<<게시글 등록 중 오류 발생>>", e);
        }

        if (user.house.apt.id != 576) { // 사원 전용 아파트는 권한 체크 제외
            if (!category.isUserWritable(user)) {
                return null;
            }
        }

        List<MultipartFile> fileList = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileList.add(file);
            }
        }

        BoardPost post = new BoardPost();
        post.user = user;
        post.category = boardService.getCategory(categoryId);
        post.regDate = new Date();

        ////////////////////////////////////////////////////////////////// 게시판 상단고정(최대3개) ////////////////////////////////////////////////////////////////////
        if ("on".equalsIgnoreCase(topFix)) {
            Boolean tmp = true;
            List<BoardPost> topList = boardService.getTopFixBoardPost(categoryId, tmp);
            if (topList.size() == 3) {
                BoardPost tmpPost = topList.get(0);
                tmpPost.topFix = false;
                boardService.saveAndFlush(tmpPost);
                post.topFix = "on".equalsIgnoreCase(topFix);
            } else {
                post.topFix = "on".equalsIgnoreCase(topFix);
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // String contentWithoutHtml = content;

        if ("html".equals(post.category.contentMode)) {
            content =
                    String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                            + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", content);
        }
        post.content = content;
        post.title = title == null || title.isEmpty() ? null : title;
        post.imageCount = fileList.size();
        try {
            if (user.type.jaha) {
                post.rangeAll = "on".equalsIgnoreCase(rangeAll); // 자하권한만 전체 공개 가능
            } else {
                post.rangeAll = false;
            }
        } catch (Exception e) {
            LOGGER.info("<<게시글 등록 중 오류 발생>>", e);
        }
        post.rangeSido = rangeSido == null ? "" : rangeSido;
        post.rangeSigungu = rangeSigungu == null ? "" : rangeSigungu;
        post.viewCount = 0l;
        post.displayYn = "Y";

        post = boardService.saveAndFlush(post);

        if (!fileList.isEmpty()) {
            long postId = post.id;
            long postParentNum = post.id / 1000l;
            final int len = fileList.size();
            for (int i = 0; i < len; i++) {
                try {
                    File dir = new File(String.format(this.boardImageFilePath, postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File dest = new File(dir, String.format("%s.jpg", i));
                    dest.createNewFile();
                    fileList.get(i).transferTo(dest);
                    Thumbnails.create(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (files2 != null && files2.length != 0) {
            long postId = post.id;
            long postParentNum = post.id / 1000l;
            final int len = files2.length;

            for (int i = 0; i < len; i++) {
                String originalFileName = files2[i].getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    continue;
                }
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File dest = new File(dir, originalFileName);
                    dest.createNewFile();
                    files2[i].transferTo(dest);

                    if (i == 0) {
                        post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    } else if (i == 1) {
                        post.file2 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        post = boardService.saveAndFlush(post);
        // LOGGER.info("<<게시글을 DB에 저장합니다. 게시글아이디: {}>>", post.id);

        if (notification != null && "on".equalsIgnoreCase(notification) && "notice".equalsIgnoreCase(post.category.type)) { // 공지사항 푸시 발송
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            // List<User> users = Lists.newArrayList();
            // if (post.rangeAll) {
            // users = userService.getAllUsers();
            // } else {
            // users = userService.getAllAptUsers(user.house.apt.id);
            // }
            // List<User> notificationApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            // @Override
            // public boolean apply(User input) {
            // return input.setting.notiAlarm;
            // }
            // }));
            //
            // List<Long> notificationApplyUserIds = Lists.transform(notificationApplyUsers, new Function<User, Long>() {
            // @Override
            // public Long apply(User input) {
            // return input.id;
            // }
            // });

            // GcmSendForm form = new GcmSendForm();
            // Map<String, String> msg = Maps.newHashMap();
            // msg.put("type", "action");
            // msg.put("titleResId", "notice");
            // msg.put("value", TagUtils.removeTag(post.content).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY));
            // msg.put("action", "emaul://post-detail?id=" + post.id);
            // form.setUserIds(notificationApplyUserIds);
            // form.setMessage(msg);
            //
            // LOGGER.info("<<공지사항 푸시 발송을 시작합니다>>");
            // gcmService.sendGcm(form);
            // LOGGER.info("<<공지사항 푸시 발송을 종료합니다>>");

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.APT, PushAlarmSetting.BOARD, Lists.newArrayList(user.house.apt.id));
            String value = TagUtils.removeTag(post.content).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
            String action = String.format(PushAction.BOARD.getValue(), post.id);

            // this.pushUtils.sendPush(PushGubun.BOARD_NOTICE, "notice", value, action, String.valueOf(post.id), true, targetUserList);
            this.pushUtils.sendPush(PushGubun.BOARD_NOTICE, post.title, value, action, String.valueOf(post.id), false, targetUserList);
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
        }

        if ("complaint".equals(post.category.type)) { // 관리자들에게 민원 푸시 발송
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            // List<User> users = userService.getAllAptUsers(user.house.apt.id);
            //
            // List<User> applyAdmins = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            //
            // @Override
            // public boolean apply(User input) {
            // return input.type.admin;
            // }
            // }));
            //
            // List<Long> applyAdminIds = Lists.transform(applyAdmins, new Function<User, Long>() {
            // @Override
            // public Long apply(User input) {
            // return input.id;
            // }
            // });
            //
            // GcmSendForm form = new GcmSendForm();
            // Map<String, String> msg = Maps.newHashMap();
            // msg.put("type", "action");
            // msg.put("title", "새로운 민원이 접수되었습니다.");
            // msg.put("value", contentWithoutHtml);
            // msg.put("action", "emaul://post-detail?id=" + post.id);
            // form.setUserIds(applyAdminIds);
            // form.setMessage(msg);
            //
            // gcmService.sendGcm(form);

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetAdminList(PushAlarmSetting.BOARD, Lists.newArrayList(user.house.apt.id));
            title = PushMessage.BOARD_COMPLAINT_TITLE.getValue();
            String value = TagUtils.removeTag(post.content).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
            String action = String.format(PushAction.BOARD.getValue(), post.id);

            this.pushUtils.sendPush(PushGubun.BOARD_COMPLAINT, title, value, action, String.valueOf(post.id), false, targetUserList);
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
        } else if ("community".equals(post.category.type)) {
            // 글등록 후 push 발송여부가 Y인 게시판 카테고리
            if ("Y".equals(post.category.pushAfterWrite)) {
                //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
                // List<User> users = userService.getAllAptUsers(user.house.apt.id);
                //
                // GcmSendForm form = new GcmSendForm();
                // Map<String, String> msg = Maps.newHashMap();
                // msg.put("type", "action");
                // msg.put("title", post.category.name + " 게시판에 " + post.user.getFullName() + "님의 새로운 글이 등록되었습니다.");
                // msg.put("value", contentWithoutHtml);
                // msg.put("action", "emaul://post-detail?id=" + post.id);
                // form.setUserIds(Lists.transform(users, input -> input.id));
                // form.setMessage(msg);
                //
                // gcmService.sendGcm(form);

                List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.APT, PushAlarmSetting.BOARD, Lists.newArrayList(user.house.apt.id));
                title = String.format(PushMessage.BOARD_POST_REG.getValue(), post.category.name, post.user.getFullName());
                String value = TagUtils.removeTag(post.content).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
                String action = String.format(PushAction.BOARD.getValue(), post.id);

                this.pushUtils.sendPush(PushGubun.BOARD_POST, title, value, action, String.valueOf(post.id), false, targetUserList);
                //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            }
        }

        post.user = userService.convertToPublicUser(post.user);
        return post;
    }

    @Deprecated
    @RequestMapping(value = "/api/board/post/list16", method = RequestMethod.GET)
    public @ResponseBody List<BoardPost> getLatest16Posts(HttpServletRequest req) {
        List<BoardPost> posts = boardService.getFirst16Posts(SessionAttrs.getUserId(req.getSession()), "N");
        for (BoardPost post : posts) {
            post.user = userService.convertToPublicUser(post.user);
        }
        return posts;
    }

    @RequestMapping(value = "/api/board/comment/write", method = RequestMethod.POST)
    public @ResponseBody BoardComment writeComment(HttpServletRequest req, @RequestBody String json) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // 방문자인 경우 차단
        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        JSONObject obj = new JSONObject(json);

        BoardComment cmt = new BoardComment();
        cmt.content = obj.getString("content");
        cmt.post = boardService.get(user, obj.getLong("postId"));
        cmt.regDate = new Date();
        cmt.user = userService.getUser(userId);

        cmt = boardService.save(cmt);
        cmt.user = userService.convertToPublicUser(cmt.user);

        if (!userId.equals(cmt.post.user.id)) {
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            // if (cmt.post.user.setting.notiPostReply) {
            // // send replied notification
            // GcmSendForm form = new GcmSendForm();
            // Map<String, String> msg = Maps.newHashMap();
            // msg.put("type", "action");
            // // msg.put("titleResId", "new_comment_replied");
            // msg.put("value", cmt.content);
            // msg.put("action", "emaul://post-detail?id=" + cmt.post.id);
            //
            // if ("complaint".equals(cmt.post.category.type)) {
            // msg.put("title", "접수하신 민원의 답변이 등록되었습니다.");
            // } else {
            // msg.put("titleResId", "new_comment_replied"); // 푸시의 제목을 앱에서 지정
            // }
            //
            // form.setUserIds(Lists.newArrayList(cmt.post.user.id));
            // form.setMessage(msg);
            //
            // gcmService.sendGcm(form);
            // }

            Long postId = cmt.post.id;
            String categoryType = cmt.post.category.type;

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.BOARD, Lists.newArrayList(cmt.post.user.id));

            PushGubun pushGubun = null;
            String title = null;
            String value = cmt.content;
            String action = String.format(PushAction.BOARD.getValue(), postId);
            boolean titleResIdYn = false;

            if ("complaint".equals(categoryType)) {
                title = PushMessage.BOARD_COMPLAINT_COMMENT_TITLE.getValue();
                pushGubun = PushGubun.BOARD_COMPLAINT;
            } else {
                title = PushMessage.BOARD_COMMENT_REG.getValue();
                pushGubun = PushGubun.BOARD_COMMENT;
                titleResIdYn = true;
            }

            this.pushUtils.sendPush(pushGubun, title, value, action, String.valueOf(postId), titleResIdYn, targetUserList);
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
        }

        cmt.post.user = userService.convertToPublicUser(cmt.post.user);

        return cmt;
    }

    @RequestMapping(value = "/api/board/comment/reply/write", method = RequestMethod.POST)
    public @ResponseBody BoardCommentReply writeCommentReply(HttpServletRequest req, @RequestBody String json) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        JSONObject obj = new JSONObject(json);

        BoardCommentReply reply = new BoardCommentReply();
        reply.content = obj.getString("content");
        reply.comment = boardService.getComment(obj.getLong("commentId"));
        reply.regDate = new Date();
        reply.user = userService.getUser(userId);

        reply = boardService.save(reply);
        reply.user = userService.convertToPublicUser(reply.user);

        if (!userId.equals(reply.comment.user.id)) {
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            // if (reply.comment.user.setting.notiPostReply) {
            // // send replied notification
            // GcmSendForm form = new GcmSendForm();
            // Map<String, String> msg = Maps.newHashMap();
            // msg.put("type", "action");
            // msg.put("titleResId", "new_comment_replied");
            // msg.put("value", reply.content);
            //// msg.put("action", "emaul://post-detail?id=" + reply.comment.id);
            // msg.put("action", "emaul://comment-detail?id=" + reply.comment.id + "&postId=" + reply.comment.post.id);
            // form.setUserIds(Lists.newArrayList(reply.comment.user.id));
            // form.setMessage(msg);
            //
            // gcmService.sendGcm(form);
            // }

            Long commentId = reply.comment.id;
            Long postId = reply.comment.post.id;

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.BOARD, Lists.newArrayList(reply.comment.user.id));
            String title = PushMessage.BOARD_REPLY_REG.getValue();
            String value = reply.content;
            String action = String.format(PushAction.BOARD_COMMENT.getValue(), commentId, postId);

            this.pushUtils.sendPush(PushGubun.BOARD_REPLY, title, value, action, String.valueOf(postId), true, targetUserList);
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
        }

        reply.comment.user = userService.convertToPublicUser(reply.comment.user);

        return reply;

    }

    @RequestMapping(value = "/api/board/comment/reply/delete/{replyId}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteCommentReply(HttpServletRequest req, @PathVariable(value = "replyId") Long replyId) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        return boardService.deleteCommentReply(userId, replyId) ? "1" : "0";
    }

    @RequestMapping(value = "/api/board/comment/list", method = RequestMethod.GET)
    public @ResponseBody ScrollPage<BoardComment> getComments(HttpServletRequest req, @RequestParam(value = "postId") Long postId, @RequestParam(value = "lastCommentId", required = false,
            defaultValue = "0") Long lastCommentId) {

        // 방문자인 경우 차단
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        ScrollPage<BoardComment> page = boardService.getComments(SessionAttrs.getUserId(req.getSession()), postId, lastCommentId);

        List<BoardComment> list = page.getContent();
        for (BoardComment boardComment : list) {
            boardComment.post = null;
            boardComment.user = userService.convertToPublicUser(boardComment.user);
        }

        return page;
    }

    @RequestMapping(value = "/api/board/post/delete/{postId}", method = RequestMethod.DELETE)
    public @ResponseBody String deletePost(HttpServletRequest req, @PathVariable(value = "postId") Long postId) {
        return boardService.deletePost(SessionAttrs.getUserId(req.getSession()), postId) ? "1" : "0";
    }

    @RequestMapping(value = "/api/board/comment/delete/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteComment(HttpServletRequest req, @PathVariable(value = "commentId") Long commentId) {

        // 방문자인 경우 차단
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        return boardService.deleteComment(SessionAttrs.getUserId(req.getSession()), commentId) ? "1" : "0";
    }

    @RequestMapping(value = "/api/board/post/image/{postId}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleImageRequest(@PathVariable("postId") String postId, @PathVariable("fileName") String fileName) {

        // File toServeUp = new File("/nas/EMaul/board/post/image", String.format("/%s/%s/%s", Long.valueOf(postId) / 1000l, postId, fileName));
        File toServeUp = new File(String.format(boardImageFilePath, Long.valueOf(postId) / 1000l, postId), fileName);

        return Responses.getFileEntity(toServeUp, postId + "-" + fileName);
    }

    @RequestMapping(value = "/api/board/post/file/{postId}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleFileRequest(@PathVariable("postId") String postId, @PathVariable("fileName") String fileName) {

        File toServeUp = new File("/nas/EMaul/board/post/file", String.format("/%s/%s/%s", Long.valueOf(postId) / 1000l, postId, fileName));

        return Responses.getFileEntity(toServeUp, fileName);
    }

    /**
     * 아파트별로 이미지와 파일을 리턴한다. 서수원자이 홈페이지에서 이관한 데이터, 이미지, 파일을 처리하기 위해서... (2016.07.04)
     *
     * @author PNS
     * @param aptId
     * @param type
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/api/public/files/{aptId}/{type}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleFileRequestByApt(@PathVariable("aptId") String aptId, @PathVariable("type") String type, @PathVariable("fileName") String fileName) {

        try {
            String decFilename = URLDecoder.decode(fileName, "utf-8");
            File file = new File("/nas/EMaul/files", String.format("/%s/%s/%s", aptId, type, decFilename));
            // Local PC TEST
            // File file = new File("D:\\nas\\EMaul\\files", String.format("\\%s\\%s\\%s", aptId,
            // type, decFilename));

            return Responses.getFileEntity(file, aptId + "-" + fileName);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    @RequestMapping(value = "/admin/board/comment/{commentId}/reply", method = RequestMethod.GET)
    public @ResponseBody String getCommentReply(HttpServletRequest req, @PathVariable(value = "commentId") Long commentId) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        ScrollPage<BoardCommentReply> replyWrapper = boardService.getCommentReplies(userId, commentId);

        StringBuilder sb = new StringBuilder();

        List<BoardCommentReply> replies = replyWrapper.getContent();

        sb.append("<div id=\"reply-form-").append(commentId).append("\">\n").append("<textarea class=\"form-control\" rows=\"3\"\n").append(" id=\"text-comment-").append(commentId).append("\"")
                .append(" style=\"resize: none; margin-bottom: 5px\"></textarea>\n").append("<div class=\"button-position\">\n").append("<span id=\"_cmtMaxMsg-").append(commentId)
                .append("\" style=\"display:none;\"></span>").append("<span id=\"_cmtLength-").append(commentId).append("\"></span>").append("<a class=\"btn btn-sm btn-outline btn-danger\"\n")
                .append(" style=\"margin-bottom: 6px\" href=\"javascript:writeCommentReply(").append(commentId).append(")\">등록</a>\n").append("</div>").append("</div>");
        if (replies != null && !replies.isEmpty()) {
            for (BoardCommentReply reply : replies) {

                sb.append("<div id=\"reply-").append(reply.id).append("\" style=\"background-color: white; border-radius: 4px; padding: 5px; margin: 5px\">\n")
                        .append("    <p><i class=\"fa fa-share fa-flip-vertical\"></i> <strong> ").append(HtmlUtils.htmlEscape(reply.user.getFullName())).append("(")
                        .append(reply.user.getNickname() == null ? "익명" : HtmlUtils.htmlEscape(reply.user.getNickname().name)).append(") ")
                        .append(HtmlUtils.htmlEscape(reply.user.house == null ? "" : reply.user.house.dong)).append("동 ").append(sdf.format(reply.regDate)).append(" </strong>\n")
                        .append("        <a class=\"btn btn-xs btn-outline btn-danger\" ").append("           href=\"javascript:deleteReply(").append(String.format("%s", reply.id))
                        .append(")\">삭제</a>\n").append("    </p>\n").append("    <pre style=\"background: none; border: none\">\n").append(HtmlUtils.htmlEscape(reply.content)).append("</pre>")
                        .append("</div>");
            }
        }
        return sb.toString();
    }

    @RequestMapping(value = "/admin/board/post/list/{categoryId}", method = RequestMethod.GET)
    public String getBoardList(HttpServletRequest req, @PathVariable(value = "categoryId") Long categoryId, Model model, @PageableDefault(sort = {"topFix", "regDate"}, direction = Direction.DESC,
            size = 20) Pageable pageable) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardCategory category = boardService.getCategory(categoryId);
        model.addAttribute("category", category);

        String searchItem = StringUtil.nvl(req.getParameter("searchItem"), "");
        String searchKeyword = StringUtil.nvl(req.getParameter("searchKeyword"), "");

        String viewPath = null;

        if ("complaint".equals(category.type)) {
            viewPath = "admin/board-" + category.type + "-list";
        } else if ("tts".equals(category.type)) {
            viewPath = "admin/board-" + category.type + "-list";
            String naverClientId = user.house.apt.aptInfo.naverClientId;
            model.addAttribute("naverClientId", naverClientId);

        } else {

            Specifications<BoardPost> specs = Specifications.where(BoardSpecification.categoryIdEq(categoryId)).and(BoardSpecification.displayYn("Y"));

            if (StringUtils.isNotBlank(searchItem) && StringUtils.isNotBlank(searchKeyword)) {
                if ("title".equals(searchItem)) {
                    specs = specs.and(BoardSpecification.titleLike(searchKeyword));
                } else if ("content".equals(searchItem)) {
                    specs = specs.and(BoardSpecification.contentLike(searchKeyword));
                } else if ("nickname".equals(searchItem)) {
                    specs = specs.and(BoardSpecification.nicknameLike(searchKeyword));
                }
            }

            Page<BoardPost> postList = boardService.getPostByAdmin(specs, pageable);

            if (postList != null) {
                PageWrapper<BoardPost> page = new PageWrapper<BoardPost>(postList, "/admin/board/post/list/" + categoryId, req);

                for (BoardPost b : page.getContent()) {
                    b.writerName = boardService.makeUserNameForPost(b.user, b.category.getUserPrivacy());
                }
                model.addAttribute("page", page);
            }

            model.addAttribute("searchItem", searchItem);
            model.addAttribute("searchKeyword", searchKeyword);

            viewPath = "admin/board-list";
        }

        return viewPath;
    }

    /**
     * 게시판 > 게시판 목록화면 JSON
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/admin/board/post/list-json/{categoryId}")
    @ResponseBody
    public Map<String, Object> getBoardListJson(Model model, HttpServletRequest req, @PathVariable(value = "categoryId") Long categoryId) {
        String pageNum = StringUtils.defaultString(req.getParameter("pageNum"), "1");
        int pageNumber = Integer.parseInt(pageNum) - 1;

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // BoardCategory category = boardService.getCategory(categoryId);

        PageRequest pageRequest = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE, new Sort(Sort.Direction.DESC, "regDate"));
        Page<BoardPost> postList = boardService.getPosts(user, categoryId, pageRequest);

        if (postList != null) {
            for (BoardPost b : postList.getContent()) {
                b.writerName = boardService.makeUserNameForPost(b.user, b.category.getUserPrivacy());
            }
        }

        // LOGGER.debug("* user.house.apt.id: " + user.house.apt.id);
        // LOGGER.debug("* aptFeePushPage.getSize(): " + postList.getSize());
        // LOGGER.debug("* aptFeePushPage.getNumber(): " +
        // postList.getNumber());
        // LOGGER.debug("* aptFeePushPage.getNumberOfElements(): " +
        // postList.getNumberOfElements());
        // LOGGER.debug("* aptFeePushPage.getTotalElements(): " +
        // postList.getTotalElements());
        // LOGGER.debug("* aptFeePushPage.getTotalPages(): " +
        // postList.getTotalPages());

        Map<String, Object> jsonMap = new ConcurrentHashMap<String, Object>();
        jsonMap.put("size", postList.getSize());
        jsonMap.put("totalPage", postList.getTotalPages());
        jsonMap.put("totalRecord", postList.getTotalElements());
        jsonMap.put("list", postList.getContent());

        return jsonMap;
    }

    @RequestMapping(value = "/admin/board/post/create/{categoryId}", method = RequestMethod.GET)
    public String createBoardPost(HttpServletRequest req, @PathVariable(value = "categoryId") Long categoryId, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        BoardCategory category = boardService.getCategory(categoryId);

        model.addAttribute("category", category);
        /* model.addAttribute("user", user); */
        String viewPath = null;

        if ("complaint".equals(category.type)) {
            viewPath = "admin/board-" + category.type + "-form";
        } else if ("tts".equals(category.type)) {
            viewPath = "admin/board-" + category.type + "-form";
        } else if ("notice".equals(category.type)) {
            viewPath = "admin/board-" + category.type + "-form";
        } else {
            viewPath = "admin/board-create";
        }

        return viewPath;
    }

    @RequestMapping(value = "/admin/board/post/write", method = RequestMethod.POST)
    public String writePostAdmin(HttpServletRequest req, @RequestParam(value = "title", required = false, defaultValue = "") String title, @RequestParam(value = "content", required = false,
            defaultValue = "") String content, @RequestParam(value = "categoryId") Long categoryId, @RequestParam(value = "rangeAll", required = false) String rangeAll, @RequestParam(
            value = "notification", required = false) String notification, @RequestParam(value = "topFix", required = false) String topFix,
            @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value = "files2", required = false) MultipartFile[] files2) {

        BoardPost result = writePost(req, content, title, categoryId, rangeAll, topFix, null, null, notification, files, files2);
        return "redirect:/admin/board/post/list/" + categoryId;
    }

    @RequestMapping(value = "/admin/board/post/modify/{postId}", method = RequestMethod.POST)
    public String modifyPost(HttpServletRequest req, @RequestParam(value = "title", required = false, defaultValue = "") String title, @PathVariable(value = "postId") Long postId, @RequestParam(
            value = "content", required = false, defaultValue = "") String content, @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "topFix", required = false) String topFix, @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "files2", required = false) MultipartFile[] files2) {

        List<MultipartFile> fileList = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileList.add(file);
            }
        }

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);



        BoardPost post = boardService.get(user, postId);
        //////////////////////////////////////////////////////////// 게시판 상단고정(최대3개) ////////////////////////////////////////////////////////////
        if ("on".equalsIgnoreCase(topFix)) {

            if (post.topFix == true) {
            } else {
                Boolean tmp = true;
                List<BoardPost> topList = boardService.getTopFixBoardPost(categoryId, tmp);
                if (topList.size() == 3) {
                    BoardPost tmpPost = topList.get(0);
                    tmpPost.topFix = false;
                    boardService.saveAndFlush(tmpPost);
                    post.topFix = "on".equalsIgnoreCase(topFix);
                } else {
                    post.topFix = "on".equalsIgnoreCase(topFix);
                }
            }
        }
        /* 고정해제 */
        if (post.topFix == true) {
            if (!"on".equalsIgnoreCase(topFix)) {
                post.topFix = false;
            }
        }
        //////////////////////////////////////////////////////////// 게시판 상단고정(최대3개) ////////////////////////////////////////////////////////////

        if ("html".equals(post.category.contentMode)) {
            content =
                    String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                            + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", content);
        }
        post.title = title;
        post.content = content;

        if (files2 != null && files2.length != 0) {
            if (!files2[0].isEmpty() || !files2[1].isEmpty()) {
                post.file1 = null;
                post.file2 = null;
            }

            long postParentNum = post.id / 1000l;
            final int len = files2.length;

            for (int i = 0; i < len; i++) {
                String originalFileName = files2[i].getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    continue;
                }
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File dest = new File(dir, originalFileName);
                    dest.createNewFile();
                    files2[i].transferTo(dest);

                    if (i == 0) {
                        post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    } else if (i == 1) {
                        post.file2 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!fileList.isEmpty()) {
            post.imageCount = fileList.size();
            boardService.deletePostImages(post);

            long postParentNum = post.id / 1000l;
            final int len = fileList.size();

            for (int i = 0; i < len; i++) {
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/image/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File dest = new File(dir, String.format("%s.jpg", i));
                    dest.createNewFile();
                    fileList.get(i).transferTo(dest);
                    Thumbnails.create(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        boardService.saveAndFlush(post);

        return "redirect:/admin/board/post/list/" + categoryId;
    }

    @RequestMapping(value = "/admin/board/post/modify/{postId}", method = RequestMethod.GET)
    public String pageModifyBoardPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardPost post = boardService.get(user, postId);

        if ("html".equals(post.category.contentMode)) {
            int start = post.content.indexOf("<body>");

            if (start > 0) {
                post.contentOnlyBody = TagUtils.extractBody(post.content);
            }

            if (post.content.indexOf("<!DOCTYPE html>") > -1) {
                post.contentOnlyBody = post.contentOnlyBody.replaceAll("(\r\n|\n)", StringUtils.EMPTY);
            }
        }

        String viewPath = null;

        if ("notice".equals(post.category.type))
            viewPath = "admin/board-notice-modify";
        else
            viewPath = "admin/board-modify";

        model.addAttribute("post", post);

        return viewPath;
    }

    @RequestMapping(value = "/admin/board/post/read/{postId}", method = RequestMethod.GET)
    public String readBoardPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        String categoryType = StringUtils.defaultString(req.getParameter("categoryType"), "");
        String categoryId = StringUtils.defaultString(req.getParameter("categoryId"), "0");

        String viewPath = null;

        if ("tts".equals(categoryType)) {
            model.addAttribute("id", postId);
            BoardCategory category = boardService.getCategory(Long.valueOf(categoryId));
            model.addAttribute("category", category);
            viewPath = "admin/board-tts-form";
            if (req.getParameter("detailYn") != null) {
                model.addAttribute("detailYn", req.getParameter("detailYn"));
            }

            if (req.getParameter("repeat") != null) {
                BoardPost post = boardService.get(user, postId);
                model.addAttribute("repeat", req.getParameter("repeat"));
                model.addAttribute("airReserveDate", post.airReserveDate);
                model.addAttribute("airReserveYn", post.airReserveYn);


                viewPath = "admin/board-tts-popup";

            }
        } else {
            BoardPost post = boardService.get(user, postId);

            if ("html".equals(post.category.contentMode)) {
                int start = post.content.indexOf("<body>");
                if (start > 0) {
                    start = start + 6;
                    int end = post.content.indexOf("</body>");
                    // post.content = post.content.substring(start, end);
                    post.contentOnlyBody = post.content.substring(start, end);
                }

                if (post.content.indexOf("<!DOCTYPE html>") > -1) {
                    post.contentOnlyBody = post.contentOnlyBody.replaceAll("(\r\n|\n)", StringUtils.EMPTY);
                }
            }
            post.writerName = boardService.makeUserNameForPost(post.user, post.category.getUserPrivacy());
            model.addAttribute("post", post);
            model.addAttribute("category", post.category);
            model.addAttribute("boardCategoryType", post.category.type);

            if ("notice".equals(post.category.type)) {
                viewPath = "admin/board-notice-read";
            } else {
                viewPath = "admin/board-read";
            }
        }

        return viewPath;
    }

    @RequestMapping(value = "/admin/board/post/read-reply/{postId}", method = RequestMethod.GET)
    public @ResponseBody String readBoardPostReply(HttpServletRequest req, @PathVariable(value = "postId") Long postId, @RequestParam(value = "nextPageToken", required = false) Long nextPageToken,
            @RequestParam(value = "categoryType", required = false) String categoryType) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        ScrollPage<BoardComment> commentsWrapper = boardService.getComments(userId, postId, nextPageToken);

        StringBuilder sb = new StringBuilder();

        List<BoardComment> comments = commentsWrapper.getContent();

        if (comments == null || comments.isEmpty()) {
            if ("complaint".equals(categoryType)) {
                sb.append("<div class=\"well well-sm\">\n" + "    <h5>").append("답변전입니다. 답변을 등록해주세요</h5>\n" + "</div>");
            } else {
                sb.append("<div class=\"well well-sm\">\n" + "    <h5>").append("댓글이 없습니다</h5>\n" + "</div>");
            }
        } else {
            for (BoardComment comment : comments) {

                String commentReplyGuide = "답글" + String.format("%s", comment.replyCount);
                sb.append("<div id=\"comment-").append(comment.id).append("\" class=\"well well-sm\">\n").append("    <p><strong>").append(HtmlUtils.htmlEscape(comment.user.getFullName()))
                        .append(" (").append(comment.user.getNickname() == null ? "익명" : HtmlUtils.htmlEscape(comment.user.getNickname().name)).append(") ")
                        .append(HtmlUtils.htmlEscape(comment.user.house == null ? "" : comment.user.house.dong)).append("동 ").append(sdf.format(comment.regDate)).append(" </strong>\n");

                // 자하권한과 본인의 댓글만 삭제 : 2016.11.09 cyt
                // if (comment.user.id == user.id) {
                sb.append("        <a class=\"btn btn-xs btn-outline btn-danger\" ").append("           href=\"javascript:deleteComment(").append(String.format("%s", comment.id))
                        .append(")\">삭제</a>\n");
                // }


                sb.append("    </p>\n").append("    <pre style=\"background: none; border: none\">\n").append(HtmlUtils.htmlEscape(comment.content)).append("</pre>")
                        .append("        <a id =\"button-reply-").append(comment.id).append("\" class=\"button-reply btn btn-sm btn-danger\" ")
                        .append("           href=\"javascript:showCommentReply(").append(String.format("%s", comment.id)).append(")\">").append(comment.replyCount == 0 ? "답글쓰기" : commentReplyGuide)
                        .append(" &nbsp;&nbsp;<i class=\"fa fa-sort-down\"></i>").append("</a>\n").append("        <a id=\"button-reply-hide-").append(comment.id)
                        .append("\" class=\"button-reply-hide btn btn-sm btn-danger\" ").append("           href=\"javascript:hideCommentReply(").append(String.format("%s", comment.id))
                        .append(")\" style=\"display: none\">").append(comment.replyCount == 0 ? "답글쓰기" : commentReplyGuide).append(" &nbsp;&nbsp;<i class=\"fa fa-sort-up\"></i>").append("</a>\n")
                        .append("</div>");
            }
            if (comments.size() == 20) {
                String commentTitle = null;

                if ("complaint".equals(categoryType)) {
                    commentTitle = "답변";
                } else {
                    commentTitle = "댓글";
                }

                sb.append("<a id=\"read-more\" href=\"javascript:appendComments(").append(String.format("%s,%s", postId, commentsWrapper.getNextPageToken())).append(")\" class=\"text-center\">\n")
                        .append("    <h4>").append(commentTitle).append(" 더 보기</h4>\n").append("</a>");
            }
        }

        return sb.toString();
    }

    @RequestMapping(value = "/admin/board/post/comment/delete", method = RequestMethod.DELETE)
    public @ResponseBody String deleteBoardPostReply(HttpServletRequest req, @RequestParam(value = "commentId") Long commentId) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        return boardService.deleteComment(userId, commentId) ? "success" : "failed";
    }

    @RequestMapping(value = "/user/board/post/delete/{postId}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteBoardPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        return boardService.deletePost(userId, postId) ? "success" : "failed";
    }

    @RequestMapping(value = "/admin/board/manage", method = RequestMethod.GET)
    public String getBoardManagePage(Model model) {

        // 파라미터-아파트코드드, 리턴-게시 목록, 권한.

        return "admin/board-manage";
    }

    // TODO : 뉴스개편하면서 today가 news로 변경된듯함... 기존 today관련해서 사용안하는듯함 확인후에 삭제 요망 by PNS
    @RequestMapping(value = "/jaha/board/today/list", method = RequestMethod.GET)
    public String jahaAdminTodayPage(HttpServletRequest req, Pageable pageable, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // LOGGER.debug("pageable : " + pageable);

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "id"));

        Page<BoardPost> todayList = boardService.getAllTodayPosts(user, pageRequest);

        if (todayList != null) {
            PageWrapper<BoardPost> page = new PageWrapper<BoardPost>(todayList, "/jaha/board/today/list");
            model.addAttribute("page", page);
        }

        return "jaha/today-list";
    }

    @RequestMapping(value = "/jaha/board/today/modify/{postId}", method = RequestMethod.GET)
    public String jahaAdminTodayModify(@PathVariable(value = "postId") Long postId, HttpServletRequest req, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리 정보를 받아온다.
        model.addAttribute("categoryList", categoryList);
        BoardPost post = boardService.get(user, postId);

        model.addAttribute("tags", boardService.getBoardTags());
        model.addAttribute("post", post);
        model.addAttribute("postTags", Lists.transform(post.tags, new Function<BoardTag, String>() {
            @Override
            public String apply(BoardTag input) {
                return input.name;
            }
        }));

        return "jaha/today-modify";
    }

    @RequestMapping(value = "/jaha/board/today/delete/{postId}", method = RequestMethod.DELETE)
    public @ResponseBody String jahaAdminTodayDelete(@PathVariable(value = "postId") Long postId, HttpServletRequest req) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (!user.type.jaha) {
            return "";
        }

        LOGGER.debug("requested delete today post");
        boardService.deletePost(userId, postId);

        return "success";
    }

    @RequestMapping(value = "/api/board/today/image/upload", method = RequestMethod.POST)
    public @ResponseBody String handleTodayImageUpload(@RequestParam(value = "upload", required = false) MultipartFile img) {

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
            obj.put("url", "/api/board/today/image/" + dest.getName());

            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    @RequestMapping(value = "/api/board/today/image/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleTodayTempImageView(@PathVariable("fileName") String fileName) {

        File toServeUp = new File("/nas/EMaul/temp", fileName);

        return Responses.getFileEntity(toServeUp, fileName);
    }

    @RequestMapping(value = "/jaha/board/today/create", method = RequestMethod.GET)
    public String jahaAdminTodayCreatePage(Model model) {
        List<BoardTag> tags = boardService.getBoardTags();
        model.addAttribute("tags", tags);
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리 정보를 받아온다.
        model.addAttribute("categoryList", categoryList);
        return "jaha/today-create";
    }

    @RequestMapping(value = "/jaha/board/today/write", method = RequestMethod.POST)
    public String writeToday(HttpServletRequest req, @RequestParam(value = "title") String title, @RequestParam(value = "img-title") MultipartFile imgTitle, @RequestParam(value = "tags",
            required = false) String[] tags, @RequestParam(value = "contents", required = false, defaultValue = "") String contents,
            @RequestParam(value = "range-all", required = false) String rangeAll, @RequestParam(value = "sido", required = false) String rangeSido,
            @RequestParam(value = "sigungu", required = false) String rangeSigungu) throws IOException, ServletException {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (!user.type.jaha) {
            return null;
        }

        List<BoardTag> boardTags = Lists.newArrayList();
        if (tags != null && tags.length != 0) {
            boardTags = Lists.transform(Lists.newArrayList(tags), input -> {
                BoardTag tag = boardService.getBoardTag(input);
                if (tag == null) {
                    tag = new BoardTag();
                    tag.name = input;
                    boardService.save(tag);
                }
                return tag;
            });
        }

        BoardCategory category = boardService.getTodayCategory(user.house.apt.id, "N");

        String formattedContent =
                String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                        + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", contents);

        BoardPost boardPost = new BoardPost();
        boardPost.category = category;
        boardPost.content = formattedContent;

        if ("on".equalsIgnoreCase(rangeAll)) {
            boardPost.rangeAll = true;
            boardPost.rangeSido = "";
            boardPost.rangeSigungu = "";
        } else {
            boardPost.rangeSido = rangeSido;
            boardPost.rangeSigungu = rangeSigungu;
        }
        boardPost.regDate = new Date();
        boardPost.title = title;
        boardPost.user = user;
        boardPost.imageCount = imgTitle == null ? 0 : 1;
        boardPost.tags = boardTags;
        boardPost.viewCount = 0l;

        boardPost = boardService.saveAndFlush(boardPost);

        boardService.updateTodayContent(boardPost.id, saveAndRefreshPostImageFiles(boardPost.id, formattedContent, imgTitle));

        return "redirect:/jaha/board/today/list";
    }

    @RequestMapping(value = "/api/board/today/modify", method = RequestMethod.POST)
    public String modifyToday(HttpServletRequest req, @RequestParam(value = "postId") Long postId, @RequestParam(value = "title") String title,
            @RequestParam(value = "img-title") MultipartFile imgTitle, @RequestParam(value = "tags", required = false) String[] tags, @RequestParam(value = "contents", required = false,
                    defaultValue = "") String contents, @RequestParam(value = "range-all", required = false) String rangeAll, @RequestParam(value = "sido", required = false) String rangeSido,
            @RequestParam(value = "sigungu", required = false) String rangeSigungu) throws IOException, ServletException {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (!user.type.jaha) {
            return null;
        }

        BoardPost boardPost = boardService.get(user, postId);
        List<BoardTag> boardTags = Lists.newArrayList();
        if (tags != null && tags.length != 0) {
            boardTags = Lists.transform(Lists.newArrayList(tags), input -> {
                BoardTag tag = boardService.getBoardTag(input);
                if (tag == null) {
                    tag = new BoardTag();
                    tag.name = input;
                    boardService.save(tag);
                }
                return tag;
            });
        }

        String formattedContent =
                String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                        + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", contents);

        boardPost.content = formattedContent;
        if ("on".equalsIgnoreCase(rangeAll)) {
            boardPost.rangeAll = true;
            boardPost.rangeSido = "";
            boardPost.rangeSigungu = "";
        } else {
            boardPost.rangeSido = rangeSido;
            boardPost.rangeSigungu = rangeSigungu;
        }
        boardPost.title = title;
        boardPost.tags = Lists.newArrayList(boardTags);

        boardPost.content = saveAndRefreshPostImageFiles(postId, formattedContent, imgTitle);
        boardService.saveAndFlush(boardPost);

        return "redirect:/jaha/board/today/list";
    }

    private String saveAndRefreshPostImageFiles(Long postId, String formattedContent, MultipartFile imgTitle) throws IOException {
        long postParentNum = postId / 1000l;
        File dir = new File(String.format(boardImageFilePath, postParentNum, postId));

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (imgTitle != null && !imgTitle.isEmpty()) {
            try {
                File dest = new File(dir, "0.jpg");
                dest.createNewFile();
                imgTitle.transferTo(dest);
                File thumb = Thumbnails.create(dest);
                Blurs.blur(thumb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 나머지 이미지 게시글 폴더로 이동
        List<String> imgSrc = TagUtils.getImgSrc(formattedContent);
        for (String src : imgSrc) {
            if (src.startsWith("/api/board/post/image")) {
                continue;
            }
            String fileName = FilenameUtils.getName(src);

            File tempImageFile = new File(tempFilePath, fileName);
            if (tempImageFile.exists()) { // 기존 파일이 temp에 있으면..
                String renamedSrc = src.replace("/api/board/today/image/", "/api/board/post/image/" + postId + "/"); // 위
                File destImageFile = new File(dir, fileName);
                FileUtils.copyFile(tempImageFile, destImageFile);
                tempImageFile.delete();
                formattedContent = formattedContent.replace(src, renamedSrc); // 아래
            }
        }
        formattedContent = TagUtils.replaceImageSizeToHeightNull(formattedContent);
        formattedContent = TagUtils.replaceImageSizeToWidth100(formattedContent);

        return formattedContent;
    }

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

    @RequestMapping(value = "/user/board/post/list/{categoryId}", method = RequestMethod.GET)
    public String getBoardListForUser(HttpServletRequest req, @PathVariable(value = "categoryId") Long categoryId, Model model, Pageable pageable) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        BoardCategory category = boardService.getCategory(categoryId);

        PageRequest pageRequest;
        if ("notice".equals(category.type)) {
            pageRequest = new PageRequest(pageable.getPageNumber(), 10, new Sort(Sort.Direction.DESC, "topFix", "regDate"));
        } else {
            pageRequest = new PageRequest(pageable.getPageNumber(), 10, new Sort(Sort.Direction.DESC, "regDate"));

        }
        // PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), 10, new
        // Sort(Sort.Direction.DESC, "regDate"));
        Page<BoardPost> postList = null;
        if ("tts".equals(category.type) && user.type.user) {
            postList = boardService.getPostsTTS(user, categoryId, pageRequest);
        } else {
            postList = boardService.getPosts(user, categoryId, pageRequest);

        }

        if (postList != null) {
            PageWrapper<BoardPost> page = new PageWrapper<BoardPost>(postList, "/user/board/post/list/" + categoryId);
            for (BoardPost b : page.getContent()) {
                // String content = b.content.trim();
                // String[] splited = content.split("\n");
                // b.content = splited[0];

                b.writerName = boardService.makeUserNameForPost(b.user, b.category.getUserPrivacy());
            }
            model.addAttribute("page", page);
            model.addAttribute("category", category);
        }

        Long postSize = boardService.getPostSize(categoryId);
        model.addAttribute("postSize", postSize);
        model.addAttribute("leftSideMenu", "board");

        return "user/board-list";
    }

    @RequestMapping(value = "/user/board/post/create/{categoryId}", method = RequestMethod.GET)
    public String createBoardPostForUser(@PathVariable(value = "categoryId") Long categoryId, Model model) {
        BoardCategory category = boardService.getCategory(categoryId);

        String viewPath = null;

        if ("notice".equals(category.type))
            viewPath = "user/board-notice-create";
        else
            viewPath = "user/board-create";


        model.addAttribute("category", category);
        model.addAttribute("leftSideMenu", "board");

        return viewPath;
    }

    @RequestMapping(value = "/user/board/post/write", method = RequestMethod.POST)
    public String writePostForUser(HttpServletRequest req, @RequestParam(value = "content", required = false, defaultValue = "") String content, @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "title", required = false, defaultValue = "") String title, @RequestParam(value = "rangeAll", required = false) String rangeAll, @RequestParam(
                    value = "notification", required = false) String notification, @RequestParam(value = "topFix", required = false) String topFix,
            @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value = "files2", required = false) MultipartFile[] files2) {

        BoardPost result = writePost(req, content, title, categoryId, rangeAll, topFix, null, null, notification, files, files2);
        if (result == null) {
            System.out.println("resultnull" + result);
        }
        return "redirect:/user/board/post/list/" + categoryId;
    }

    @RequestMapping(value = "/user/board/post/read/{postId}", method = RequestMethod.GET)
    public String readBoardPostForUser(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardPost post = boardService.get(user, postId);

        if (post.viewCount == null) {
            post.viewCount = 0L;
        }
        // 조회수가 2씩 증가되어 주석처리, boardService.get 메소드에 조회수 증가 처리가 있음.
        // post.viewCount++;
        // boardService.saveAndFlush(post);

        if ("html".equals(post.category.contentMode)) {
            int start = post.content.indexOf("<body>");
            if (start > 0) {
                start = start + 6;
                int end = post.content.indexOf("</body>");
                // post.content = post.content.substring(start, end);
                post.contentOnlyBody = post.content.substring(start, end);
            } else {
                post.contentOnlyBody = post.content;
            }

            if (post.content.indexOf("<!DOCTYPE html>") > -1) {
                post.contentOnlyBody = post.contentOnlyBody.replaceAll("(\r\n|\n)", StringUtils.EMPTY);
            }
        }

        post.writerName = boardService.makeUserNameForPost(post.user, post.category.getUserPrivacy());

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("leftSideMenu", "board");

        // 방문자인 경우 차단
        if (user == null || user.type == null || user.type.anonymous == true)
            return "user/board-read-noauth";

        return "user/board-read";
    }

    @RequestMapping(value = "/user/board/post/read-reply/{postId}", method = RequestMethod.GET)
    public @ResponseBody String readBoardPostReplyForUser(HttpServletRequest req, @PathVariable(value = "postId") Long postId,
            @RequestParam(value = "nextPageToken", required = false) Long nextPageToken, @RequestParam(value = "categoryType", required = false) String categoryType) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        LOGGER.debug("readBoardPostReply : user.type.anonymous = " + user.type.anonymous);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        ScrollPage<BoardComment> commentsWrapper = boardService.getComments(userId, postId, nextPageToken);

        StringBuilder sb = new StringBuilder();

        List<BoardComment> comments = commentsWrapper.getContent();

        if (comments == null || comments.isEmpty()) {
            if ("complaint".equals(categoryType)) {
                sb.append("<div class=\"board-border\">\n" + "    <h5>답변전입니다. 답변을 등록해주세요</h5>\n" + "</div>");
            } else {
                sb.append("<div class=\"board-border\">\n" + "    <h5>첫 댓글을 달아주세요</h5>\n" + "</div>");
            }
        } else {
            for (BoardComment comment : comments) {
                String commentReplyGuide = "답글" + String.format("%s", comment.replyCount);
                String userName = boardService.makeUserNameForPost(comment.user, comment.post.category.getUserPrivacy());

                sb.append("<div id=\"comment-").append(comment.id).append("\" class=\"board-border\">\n").append("    <p><strong>").append(" ").append(userName).append(" ")
                        .append(sdf.format(comment.regDate)).append(" </strong>\n");

                // 자하권한과 본인의 댓글만 삭제 : 2016.11.09 cyt
                if (comment.user.id == user.id) {
                    sb.append("        <a class=\"btn2 btn2-sm\" ").append("           href=\"javascript:deleteComment(").append(String.format("%s", comment.id)).append(")\">삭제</a>\n")
                            .append("    </p>\n");
                }

                sb.append("    <pre style=\"background: none; border: none\">\n").append(HtmlUtils.htmlEscape(comment.content)).append("</pre>").append("        <a id =\"button-reply-")
                        .append(comment.id).append("\" class=\"button-reply btn2 btn2-sm\" ").append("           href=\"javascript:showCommentReply(").append(String.format("%s", comment.id))
                        .append(")\">").append(comment.replyCount == 0 ? "답글쓰기" : commentReplyGuide).append(" &nbsp;&nbsp;<i class=\"fa fa-sort-down\"></i>").append("</a>\n")
                        .append("        <a id=\"button-reply-hide-").append(comment.id).append("\" class=\"button-reply-hide btn2 btn2-sm\" ")
                        .append("           href=\"javascript:hideCommentReply(").append(String.format("%s", comment.id)).append(")\" style=\"display: none\">")
                        .append(comment.replyCount == 0 ? "답글쓰기" : commentReplyGuide).append(" &nbsp;&nbsp;<i class=\"fa fa-sort-up\"></i>").append("</a>\n").append("</div>");
            }
            if (comments.size() == 20) {
                String commentTitle = null;

                if ("complaint".equals(categoryType)) {
                    commentTitle = "답변";
                } else {
                    commentTitle = "댓글";
                }

                sb.append("<a id=\"read-more\" href=\"javascript:appendComments(").append(String.format("%s,%s", postId, commentsWrapper.getNextPageToken())).append(")\" class=\"text-center\">\n")
                        .append("    <h4>" + commentTitle + " 더 보기</h4>\n").append("</a>");
            }
        }

        return sb.toString();
    }

    @RequestMapping(value = "/user/board/comment/{commentId}/reply", method = RequestMethod.GET)
    public @ResponseBody String getCommentReplyForUser(HttpServletRequest req, @PathVariable(value = "commentId") Long commentId) {

        Long userId = SessionAttrs.getUserId(req.getSession());

        // 방문자인 경우 차단
        User user = userService.getUser(userId);

        if (user == null || user.type == null || user.type.anonymous == true)
            return null;

        ScrollPage<BoardCommentReply> replyWrapper = boardService.getCommentReplies(userId, commentId);

        StringBuilder sb = new StringBuilder();

        List<BoardCommentReply> replies = replyWrapper.getContent();

        sb.append("<div id=\"reply-form-").append(commentId).append("\">\n").append("<textarea class=\"form-control2\" rows=\"3\"\n").append(" id=\"text-comment-").append(commentId).append("\"")
                .append(" style=\"resize: none; margin-bottom: 5px\"></textarea>\n").append("<div class=\"button-position\">\n").append("<span id=\"_cmtMaxMsg-").append(commentId)
                .append("\" style=\"display:none;\"></span>").append("<span id=\"_cmtLength-").append(commentId).append("\"></span>").append("<a class=\"btn2 btn2-sm\"\n")
                .append(" style=\"margin-bottom: 6px\" href=\"javascript:writeCommentReply(").append(commentId).append(")\">등록</a>\n").append("</div>").append("</div>");
        if (replies != null && !replies.isEmpty()) {
            for (BoardCommentReply reply : replies) {

                String userName = boardService.makeUserNameForPost(reply.user, reply.comment.post.category.getUserPrivacy());

                sb.append("<div id=\"reply-").append(reply.id).append("\" class=\"board-border\" style=\"background-color: #fcfcfc\">\n")
                        .append("    <p><i class=\"fa fa-level-up fa-rotate-90\"></i> <strong> ").append(" ").append(userName).append(" ").append(sdf.format(reply.regDate)).append(" </strong>\n");
                // 자하권한과 본인의 댓글만 삭제 : 2016.11.09 cyt
                if (reply.user.id == user.id) {
                    sb.append("        <a class=\"btn2 btn2-sm\" ").append("           href=\"javascript:deleteReply(").append(String.format("%s", reply.id)).append(")\">삭제</a>\n");
                }
                sb.append("    </p>\n").append("    <pre style=\"background: none; border: none\">\n").append(HtmlUtils.htmlEscape(reply.content)).append("</pre>").append("</div>");
            }
        }
        return sb.toString();
    }

    @RequestMapping(value = "/user/board/post/modify/{postId}", method = RequestMethod.GET)
    public String pageModifyBoardPostForUser(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        // 뉴스카테고리 정보를 받아온다.
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG");
        model.addAttribute("categoryList", categoryList);

        BoardPost post = boardService.get(user, postId);

        if ("html".equals(post.category.contentMode)) {
            int start = post.content.indexOf("<body>");

            if (start > 0) {
                post.contentOnlyBody = TagUtils.extractBody(post.content);
            }

            if (post.content.indexOf("<!DOCTYPE html>") > -1) {
                post.contentOnlyBody = post.contentOnlyBody.replaceAll("(\r\n|\n)", StringUtils.EMPTY);
            }
        }

        String viewPath = null;

        if ("notice".equals(post.category.type))
            viewPath = "user/board-notice-modify";
        else
            viewPath = "user/board-modify";

        model.addAttribute("post", post);
        model.addAttribute("leftSideMenu", "board");

        return viewPath;
    }

    @RequestMapping(value = "/user/board/post/modify/{postId}", method = RequestMethod.POST)
    public String modifyPostForUser(HttpServletRequest req, @RequestParam(value = "title", required = false, defaultValue = "") String title, @PathVariable(value = "postId") Long postId,
            @RequestParam(value = "content", required = false, defaultValue = "") String content, @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "topFix", required = false) String topFix, @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "files2", required = false) MultipartFile[] files2) {

        String result = modifyPost(req, title, postId, content, categoryId, topFix, files, files2);

        return "redirect:/user/board/post/read/" + postId;

    }

    // 오늘 뉴스 리스트 조회 (구)
    @RequestMapping(value = "/user/board/today/list")
    public String getTodayMainForUser(HttpServletRequest req, Pageable pageable, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardCategory boardCategory = boardService.getTodayCategory(user.house.apt.id, "N");

        if (boardCategory != null) {
            Long categoryId = boardCategory.id;

            List<BoardPost> headerPosts = boardService.getFirst4Todays(user, categoryId);
            Page<BoardPost> todayList = null;
            if (headerPosts.size() >= 4) {
                PageRequest request = new PageRequest(pageable.getPageNumber(), 5, new Sort(Sort.Direction.DESC, "regDate"));
                todayList = boardService.getPostsLessThan(user, categoryId, headerPosts.get(headerPosts.size() - 1).regDate, request, 4);
            }

            if (todayList != null) {
                PageWrapper<BoardPost> page = new PageWrapper<BoardPost>(todayList, "/user/board/today/list");
                model.addAttribute("page", page);
            }

            Long postSize = boardService.getPostSize(categoryId);
            model.addAttribute("postSize", postSize);
            List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("leftSideMenu", "today");

            model.addAttribute("headerPosts", headerPosts);
            LOGGER.debug("head size : " + headerPosts.size());
        }

        return "user/today-list";
    }

    // 마을 뉴스 리스트 조회 (신))
    @RequestMapping(value = "/user/board/news/list")
    public String getALLNewsForUser(HttpServletRequest req, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        String searchColumn = StringUtils.defaultString(req.getParameter("searchColumn"), "");
        String searchKeyword = StringUtils.defaultString(req.getParameter("searchKeyword"), "");
        String pageNum = StringUtils.defaultString(req.getParameter("page"), "0");
        String pageSize = StringUtils.defaultString(req.getParameter("size"), "5");
        String hashtag = StringUtils.defaultString(req.getParameter("hashtag"), StringUtils.EMPTY);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);
        if (userId != null) {
            User user = userService.getUser(userId);
            user.house.apt.strAddress = AddressConverter.toStringAddress(user.house.apt.address);
            user.house.apt.strAddressOld = AddressConverter.toStringAddressOld(user.house.apt.address);
            List<BoardCategory> categories = boardService.getCategories(userId, "notice", "N");
            categories.addAll(boardService.getCategories(userId, "community", "N"));
            categories.addAll(boardService.getCategories(userId, "tts", "N")); // 방송 게시판은 라이선스 문제로 인하여
            // categories.addAll(boardService.getCategories(userId, "complaint"));

            model.addAttribute("user", user);
            model.addAttribute("categories", categories);
            model.addAttribute("addressCode", user.house.apt.address.건물관리번호);
        }

        params.put("searchColumn", searchColumn);
        params.put("searchKeyword", searchKeyword);
        int totalcount = boardService.selectUserNewsListCount(params); // 총 카운트
        // 리스트 탑 4개가져오기
        params.put("startNum", 0);
        params.put("endNum", 4);
        params.put("pageSize", pageSize); // 페이지 사이즈
        params.put("hashtag", hashtag);
        List<Map<String, Object>> headerPosts = boardService.selectUserNewsList(params);
        // Page<BoardPost> todayList = null;

        Page<Map<String, Object>> todayList = null;

        if (headerPosts.size() >= 4) {
            int skipNum = 4;
            Pageable pageable = new PageRequest(Integer.parseInt(pageNum), Integer.parseInt(pageSize)); // 페이지 넘버, 페이지 사이즈
            params.put("pageNum", pageable.getPageNumber() != 0 ? pageable.getPageNumber() + 1 : 1); // 페이지 넘버
            params.put("skipNum", skipNum); // 건너띌 페이지사이즈
            CommonUtil.setSkipPagingParams(params);
            // 리스트 4개 제외하고 전체 가져오기
            List<Map<String, Object>> list = boardService.selectUserNewsList(params);
            Page<Map<String, Object>> page = new PageImpl<Map<String, Object>>(list, pageable, totalcount - 4); // 제한 갯수
            todayList = page;

            // int totalRecordCount = boardService.selectNewsListCount(params); //총 갯수
            // int totalPage = (int)
            // Math.ceil(totalRecordCount/pageable.getPageSize()); //페이지수
        }
        String pageUrl = "/user/board/news/list/";
        PageWrapper<Map<String, Object>> page = null;
        if (todayList != null) {
            page = new PageWrapper<Map<String, Object>>(todayList, pageUrl);
        }
        // Long postSize = boardService.getPostSize(categoryId);
        model.addAttribute("page", page);
        model.addAttribute("pageUrl", pageUrl);
        model.addAttribute("postSize", totalcount);
        model.addAttribute("params", params);


        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("leftSideMenu", "today");
        model.addAttribute("categoryList", categoryList);
        // 카테고리 이름
        model.addAttribute("categoryName", "전체");
        model.addAttribute("headerPosts", headerPosts);

        // LOGGER.debug("head size : " + headerPosts.size());

        return "user/news-list";
    }

    // 마을 뉴스 리스트 조회 (신) 카테고리별
    @RequestMapping(value = "/user/board/news/list/{newscategory}")
    public String getTodayCategoryForUser(HttpServletRequest req, Model model, @PathVariable(value = "newscategory") String newscategory) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        if (userId != null) {
            User user = userService.getUser(userId);
            user.house.apt.strAddress = AddressConverter.toStringAddress(user.house.apt.address);
            user.house.apt.strAddressOld = AddressConverter.toStringAddressOld(user.house.apt.address);
            List<BoardCategory> categories = boardService.getCategories(userId, "notice", "N");
            categories.addAll(boardService.getCategories(userId, "community", "N"));
            // categories.addAll(boardService.getCategories(userId, "tts", "N")); // 방송 게시판은 라이선스 문제로 인하여 이번 운영반영에서 제외되므로 주석처리
            // categories.addAll(boardService.getCategories(userId, "complaint", "N"));

            model.addAttribute("user", user);
            model.addAttribute("categories", categories);
            model.addAttribute("addressCode", user.house.apt.address.건물관리번호);
        }

        String searchColumn = StringUtils.defaultString(req.getParameter("searchColumn"), "");
        String searchKeyword = StringUtils.defaultString(req.getParameter("searchKeyword"), "");
        String pageNum = StringUtils.defaultString(req.getParameter("page"), "0");
        String pageSize = StringUtils.defaultString(req.getParameter("size"), "5");
        String hashtag = StringUtils.defaultString(req.getParameter("hashtag"), StringUtils.EMPTY);
        Map<String, Object> params = new HashMap<String, Object>();

        // 리스트 탑 4개가져오기
        // List<BoardPost> headerPosts = boardService.getFirst4Todays(user, categoryId);
        params.put("user_id", userId);
        params.put("searchCategory", newscategory);
        params.put("searchColumn", searchColumn);
        params.put("searchKeyword", searchKeyword);

        int totalcount = boardService.selectUserNewsListCount(params); // 총 카운트
        params.put("startNum", 0);
        params.put("endNum", 4);
        params.put("pageSize", pageSize);
        params.put("hashtag", hashtag);
        List<Map<String, Object>> headerPosts = boardService.selectUserNewsList(params);
        // Page<BoardPost> todayList = null;

        Page<Map<String, Object>> todayList = null;

        if (headerPosts.size() >= 4) {
            Pageable pageable = new PageRequest(Integer.parseInt(pageNum), Integer.parseInt(pageSize)); // 페이지 넘버, 페이지 사이즈
            params.put("pageNum", pageable.getPageNumber() != 0 ? pageable.getPageNumber() + 1 : 1); // 페이지 넘버
            params.put("skipNum", 4); // 건너띌 페이지사이즈

            CommonUtil.setSkipPagingParams(params);
            // 리스트 4개 제외하고 전체 가져오기
            List<Map<String, Object>> list = boardService.selectUserNewsList(params);
            Page<Map<String, Object>> page = new PageImpl<Map<String, Object>>(list, pageable, totalcount - 4); // 제한 갯수

            todayList = page;
            // int totalRecordCount = boardService.selectNewsListCount(params); //총 갯수
            // int totalPage = (int)
            // Math.ceil(totalRecordCount/pageable.getPageSize()); //페이지수
        }
        String pageUrl = "/user/board/news/list/" + newscategory;
        PageWrapper<Map<String, Object>> page = null;
        if (todayList != null) {
            page = new PageWrapper<Map<String, Object>>(todayList, pageUrl);
        }
        // Long postSize = boardService.getPostSize(categoryId);
        model.addAttribute("pageUrl", pageUrl);
        model.addAttribute("page", page);
        model.addAttribute("postSize", totalcount);
        model.addAttribute("params", params);
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("leftSideMenu", "today");
        // 카테고리 이름
        model.addAttribute("categoryName", commonService.findByCode(newscategory).name);

        model.addAttribute("headerPosts", headerPosts);
        // LOGGER.debug("head size : " + headerPosts.size());
        return "user/news-list";
    }

    @RequestMapping(value = "/user/board/today/read/{postId}", method = RequestMethod.GET)
    public String readBoardTodayForUser(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardPost post = boardService.get(user, postId);
        int start = post.content.indexOf("<body>");
        if (start > 0) {
            start = start + 6;
            int end = post.content.indexOf("</body>");
            // post.content = post.content.substring(start, end);
            post.contentOnlyBody = post.content.substring(start, end);
        }
        model.addAttribute("post", post);
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("leftSideMenu", "today");
        List<Hashtag> hashtags = boardService.fetchHashTags(postId);
        model.addAttribute("hashtags", hashtags);

        try {
            model.addAttribute("categoryName", commonService.findByCode(post.newsCategory).name); // 카테고리
        } catch (Exception e) {

        }

        return "user/today-read";
    }

    @RequestMapping(value = "/today/public/{postId}", method = RequestMethod.GET)
    public String readBoardTodayForPublic(@PathVariable(value = "postId") Long postId, Model model) {

        BoardPost post = boardService.get(postId);

        int start = post.content.indexOf("<body>") + 6;
        int end = post.content.indexOf("</body>");
        if (end > -1) {
            // post.content = post.content.substring(start, end);
            post.contentOnlyBody = post.content.substring(start, end);
        }

        model.addAttribute("post", post);

        return "user/today-share";
    }

    /* 오늘 > 리스트 */
    @RequestMapping(value = "/jaha/board/news/list", method = RequestMethod.GET)
    public String jahaAdminNewsListPage(HttpServletRequest req, Model model) {
        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리정보리스트
        model.addAttribute("categoryList", categoryList);
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar cal = Calendar.getInstance();//
        cal.add(Calendar.MONTH, -1); // 오늘 날짜를 기준 1개월 전.
        String searchSdate = dFormat.format(cal.getTime());
        String searchEdate = dFormat.format(new Date());

        model.addAttribute("searchSdate", searchSdate);
        model.addAttribute("searchEdate", searchEdate);

        return "jaha/news-list";
    }

    @RequestMapping(value = "/jaha/board/news/list-json", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> jahaAdminNewsListData(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params) throws Exception {
        String pageSize = StringUtils.defaultString(req.getParameter("pageSize"), "15");

        // 페이지 설정
        CommonUtil.setPagingParams(params);

        Map<String, Object> jsonMap = new ConcurrentHashMap<String, Object>();

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        List<Map<String, Object>> resultList;

        // [START] 단체관리자 기능 추가 by PNS 2016.09.19
        // user가 jaha 권한일때만 제공했으나 단체관리자권한인 groupAdmin 일 경우도 추가함
        if (user == null || user.type == null || (!user.type.jaha && !user.type.groupAdmin)) {
            return null;
            // [END]

        } else {
            // [START] 단체관리자 기능 추가 by PNS 2016.09.19
            // 단체관리자용으로 해당 User의 ID값으로만 검색 또는 추후에 검색 기능 추가시에 공통으로 사용하게 수정 필요
            if (user.type.groupAdmin) {
                params.put("searchUserId", userId);
            }
            // [END]

            LOGGER.debug("/jaha/board/news/list-json/" + "requestData : " + params);
            resultList = boardService.selectNewsList(params);

            // 페이지 계산하고 넘겨주기
            int totalRecordCount = boardService.selectNewsListCount(params); // 총 갯수
            int totalPage = (int) Math.ceil(totalRecordCount / Integer.parseInt(pageSize)); // 페이지수
            // jsonMap.put("size", resultList.size());
            jsonMap.put("totalPage", totalPage);
            jsonMap.put("totalRecord", totalRecordCount);
            jsonMap.put("list", resultList);
        }
        return jsonMap;
    }

    /* 오늘 > 오늘뉴스 등록화면 이동 */
    @RequestMapping(value = "/jaha/board/news/write", method = RequestMethod.GET)
    public String jahaAdminNewsWritePage(HttpServletRequest req, Model model) {
        // [START] 단체관리자 기능 추가 by PNS 2016.09.19
        // user가 jaha 권한과 단체관리자권한인 groupAdmin 이 아닌 경우 차단
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (user == null || user.type == null || (!user.type.jaha && !user.type.groupAdmin)) {
            return null;
        }
        // [END]

        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리정보리스트
        model.addAttribute("categoryList", categoryList);

        List<CommonCode> newstypeList = commonService.findByCodeGroup("NEWS_TYPE"); // 뉴스타입정보리스트
        model.addAttribute("newstypeList", newstypeList);

        List<CommonCode> genderList = commonService.findByCodeGroup("GENDER"); // 성별정보리스트
        model.addAttribute("genderList", genderList);

        List<CommonCode> ageList = commonService.findByCodeGroup("AGE"); // 나이정보리스트
        model.addAttribute("ageList", ageList);

        // [START] 단체관리자 기능 추가 by PNS 2016.09.22
        if (user.type.groupAdmin) {
            if (user.type.groupAdmin && user.house != null && user.house.apt != null) {
                Long aptId = user.house.apt.id;
                List<String> sidoNameList = groupAdminService.getSidoNames(userId, aptId);// 시도이름 리스트
                model.addAttribute("sidoNameList", sidoNameList);

                if (sidoNameList != null) {
                    String sidoName = sidoNameList.get(0); // 현재구조는 1개만 등록하는 것으로

                    if (sidoName != null) {
                        List<HashMap> gugunNameList = groupAdminService.getGugunList(sidoName, userId, aptId); // 구군리스트

                        LOGGER.debug("* gugunNameList: {}", gugunNameList);

                        if (gugunNameList == null) { // 단체관리자용으로 권한설정된 구/군이 없다면 전체를 가져옴
                            gugunNameList = commonService.getGugunList(); // 구군리스트
                        }

                        model.addAttribute("gugunNameList", gugunNameList);
                    }
                }
            }
        } else {
            // TODO : address에서 distict로 가져오기 때문에 속도가 느림 ==> simple_address 에서 가져오는 방식으로 변경이 필요함 by PNS
            List<String> sidoNameList = houseService.getSidoNames();// 시도이름 리스트
            model.addAttribute("sidoNameList", sidoNameList);

            List<HashMap> gugunNameList = commonService.getGugunList(); // 구군리스트
            model.addAttribute("gugunNameList", gugunNameList);

        }
        // [END]

        return "jaha/news-normal-signup";
    }

    @RequestMapping(value = "/jaha/board/news/writeOk", method = RequestMethod.POST)
    public String jahaAdminNewsWrite(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params, @RequestParam(value = "img-title") MultipartFile imgTitle, @RequestParam(
            value = "hashtags", required = false) List<String> hashtags) throws Exception {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // [START] 단체관리자 기능 추가 by PNS 2016.09.19
        // user가 jaha 권한일때만 제공했으나 단체관리자권한인 groupAdmin 일 경우도 추가함
        if (user == null || user.type == null || (!user.type.jaha && !user.type.groupAdmin)) {
            return null;
        }
        // [END]

        BoardCategory category = boardService.getTodayCategory(user.house.apt.id, "N");

        if (category != null) {
            String contents = req.getParameter("content") != null ? req.getParameter("content").trim() : "";
            // 컨텐츠에서 이미지 받아오기.

            String formattedContent =
                    String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                            + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", contents);

            List<String> imgList = TagUtils.getImgSrc(contents);
            int image_count = 0;

            if (imgList != null) {
                image_count = imgList.size();
            }

            String range_sido = req.getParameter("range_sido") != null ? req.getParameter("range_sido").trim() : "";
            int range_all = req.getParameter("range_all") != null ? Integer.parseInt(req.getParameter("range_all").trim()) : 0;

            // jaha 권한 이중체크 추가(groupadmin권한인 경우 차단) by PNS 2016.09.20
            if (range_all == 1 && user.type.jaha) { // 모든 사용자 공개 되어있으면.. 시도 구군 정보 없게 처리
                range_sido = "";
                params.put("range_sido", "");
                params.put("range_sigungu", "");
            }

            if (!range_sido.equals("")) {
                range_all = 0;
            } else {
                range_all = 1; // TODO : 위에서 range_all 값을 가져오는데 시도값이 없으면 다시 세팅하는게 필요한지 검토가 필요 by PNS 2016.09.20
            }
            String file1 = "";
            String file2 = "";
            params.put("id", 0);
            params.put("content", formattedContent);
            params.put("range_all", range_all);
            params.put("image_count", image_count);
            params.put("category_id", category.id);
            params.put("user_id", userId);
            params.put("file1", file1);
            params.put("file2", file2);
            // MultipartFile imgTitle = (MultipartFile) params.get("img-title");
            Long id = boardService.insertNews(params, hashtags); // insert후 ID반환
            // id값은 넘긴 Map에 id로 셋팅 되므로. 위에 id값은 사용하지 않아요. 아래 params.get("id")를 사용
            String post_id = params.get("id").toString();

            // 파일 업로드하고. 이미지 경로 수정
            boardService.updateTodayContent(Long.parseLong(post_id), saveAndRefreshPostImageFiles(Long.parseLong(post_id), formattedContent, imgTitle));
        }

        return "redirect:/jaha/board/news/list";
        /* return "redirect:/jaha/board/news/read/" + post_id; */
    }

    /* 오늘 > 오늘뉴스 상세 */
    @RequestMapping(value = "/jaha/board/news/read/{postId}", method = RequestMethod.GET)
    public String jahaAdminNewsReadPage(HttpServletRequest req, Model model, @PathVariable(value = "postId") Long postId) {
        // [START] 단체관리자 기능 추가 by PNS 2016.09.22
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (user == null || user.type == null || (!user.type.jaha && !user.type.groupAdmin)) {
            return null;
        }
        // [END]

        List<CommonCode> categoryList = commonService.findByCodeGroup("NEWS_CTG"); // 뉴스카테고리정보리스트
        model.addAttribute("categoryList", categoryList);

        List<CommonCode> newstypeList = commonService.findByCodeGroup("NEWS_TYPE"); // 뉴스타입정보리스트
        model.addAttribute("newstypeList", newstypeList);

        List<CommonCode> genderList = commonService.findByCodeGroup("GENDER"); // 성별정보리스트
        model.addAttribute("genderList", genderList);

        List<CommonCode> ageList = commonService.findByCodeGroup("AGE"); // 나이정보리스트
        model.addAttribute("ageList", ageList);

        // 해당 글 받아오기
        Map<String, Object> post = boardService.getNews(postId);
        post.put("content", TagUtils.extractBody(StringUtil.nvl(post.get("content"))));
        model.addAttribute("post", post);

        List<Hashtag> hashtags = boardService.fetchHashTags(postId);
        model.addAttribute("hashtags", hashtags);

        // [START] 단체관리자 기능 추가 by PNS 2016.09.22
        String sidoName = (String) post.get("range_sido");

        if (user.type.groupAdmin) {
            if (user.type.groupAdmin && user.house != null && user.house.apt != null) {
                Long aptId = user.house.apt.id;
                List<String> sidoNameList = groupAdminService.getSidoNames(userId, aptId);// 시도이름 리스트
                model.addAttribute("sidoNameList", sidoNameList);

                if (sidoNameList != null) {
                    sidoName = sidoNameList.get(0); // 현재구조는 1개만 등록하는 것으로

                    if (sidoName != null) {
                        List<String> sigunguNameList = groupAdminService.getSigunguNames(sidoName, userId, aptId); // 구군리스트

                        if (sigunguNameList == null) { // 단체관리자용으로 권한설정된 구/군이 없다면 전체를 가져옴
                            sigunguNameList = houseService.getSigunguNames(sidoName);
                        }

                        model.addAttribute("sigunguList", sigunguNameList);
                    }
                }
            }
        } else {
            // TODO : address에서 distict로 가져오기 때문에 속도가 느림 ==> simple_address 에서 가져오는 방식으로 변경이 필요함 by PNS
            List<String> sidoNameList = houseService.getSidoNames();// 시도이름 리스트
            model.addAttribute("sidoNameList", sidoNameList);
            model.addAttribute("sigunguList", houseService.getSigunguNames(sidoName)); // 시군구리스트
        }
        // [END]

        return "jaha/news-normal-change";
    }

    @RequestMapping(value = "/jaha/board/news/updateOk", method = RequestMethod.POST)
    public String jahaAdminNewsUpdate(Model model, HttpServletRequest req, @RequestParam Map<String, Object> params, @RequestParam(value = "img-title") MultipartFile imgTitle, @RequestParam(
            value = "hashtags", required = false) List<String> hashtags) throws Exception {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        // [START] 단체관리자 기능 추가 by PNS 2016.09.19
        // user가 jaha 권한일때만 제공했으나 단체관리자권한인 groupAdmin 일 경우도 추가함
        if (user == null || user.type == null || (!user.type.jaha && !user.type.groupAdmin)) {
            return null;
        }
        // [END]

        BoardCategory category = boardService.getTodayCategory(user.house.apt.id, "N");

        if (category != null) {
            String contents = req.getParameter("content") != null ? req.getParameter("content").trim() : "";
            String formattedContent =
                    String.format("<!DOCTYPE html><html><head>" + "<meta charset=\"utf-8\"/>\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
                            + "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://emaul.co.kr/css/android.css\"/></head>" + "<body>%s</body></html>", contents);
            int image_count = 0;
            // 컨텐츠에서 이미지 받아오기.
            List<String> imgList = TagUtils.getImgSrc(contents);
            if (imgList != null) {
                image_count = imgList.size();
            }

            String range_sido = req.getParameter("range_sido") != null ? req.getParameter("range_sido").trim() : "";
            int range_all = req.getParameter("range_all") != null ? Integer.parseInt(req.getParameter("range_all").trim()) : 0;

            if (range_all == 1) { // 모든 사용자 공개 되어있으면.. 시도 구군 정보 없게 처리
                range_sido = "";
                params.put("range_sido", "");
                params.put("range_sigungu", "");
            }

            if (!range_sido.equals("")) {
                range_all = 0;
            } else {
                range_all = 1;
            }
            String file1 = "";
            String file2 = "";
            params.put("content", formattedContent);
            params.put("range_all", range_all);
            params.put("range_sido", range_sido);
            params.put("image_count", image_count);
            params.put("category_id", category.id);
            params.put("user_id", userId);
            params.put("file1", file1);
            params.put("file2", file2);
            boardService.updateNews(params, hashtags);
            String post_id = params.get("id").toString();
            // 파일 업로드하고. 이미지 경로 수정
            boardService.updateTodayContent(Long.parseLong(post_id), saveAndRefreshPostImageFiles(Long.parseLong(post_id), formattedContent, imgTitle));
        }

        return "redirect:/jaha/board/news/list/";
        // return "redirect:/jaha/board/news/read/" + params.get("id");
    }

    /* 오늘 > 오늘 뉴스삭제 */
    @RequestMapping(value = "/jaha/board/news/delete/{postId}", method = RequestMethod.GET)
    public String jahaAdminNewsDelete(HttpServletRequest req, Model model, @PathVariable(value = "postId") Long postId) {
        boardService.deleteNews(postId);
        return "redirect:/jaha/board/news/list";
    }

    // ////////////////////////////////////////////////////////////////////// 방송 게시판(게시판 확장) ////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/admin/board/post/write-json", method = RequestMethod.POST)
    @ResponseBody
    public BoardPost writePostAdminExt(HttpServletRequest req, @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "categoryId") Long categoryId, @RequestParam(value = "rangeAll", required = false) String rangeAll,
            @RequestParam(value = "notification", required = false) String notification, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value = "files2",
                    required = false) MultipartFile[] files2, @RequestParam(value = "voiceGubun", required = false) String voiceGubun,
            @RequestParam(value = "voiceVolume", required = false) int voiceVolume, @RequestParam(value = "pushSendYn", required = false) String pushSendYn) throws Exception {

        // LOGGER.debug("* voiceGubun: {}", voiceGubun);
        // LOGGER.debug("* voiceVolume: {}", voiceVolume);
        // LOGGER.debug("* pushSendYn: {}", pushSendYn);

        return this.writePostExt(req, content, null, categoryId, rangeAll, null, null, notification, files, files2, voiceGubun, voiceVolume, pushSendYn);
    }

    private BoardPost writePostExt(HttpServletRequest req, @RequestParam(value = "content", required = false, defaultValue = "") String content, @RequestParam(value = "title", required = false,
            defaultValue = "") String title, @RequestParam(value = "categoryId") Long categoryId, @RequestParam(value = "rangeAll", required = false) String rangeAll, @RequestParam(
            value = "rangeSido", required = false) String rangeSido, @RequestParam(value = "rangeSigungu", required = false) String rangeSigungu, @RequestParam(value = "notification",
            required = false) String notification, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value = "files2", required = false) MultipartFile[] files2,
            @RequestParam(value = "voiceGubun", required = false) String voiceGubun, @RequestParam(value = "voiceVolume", required = false) int voiceVolume, @RequestParam(value = "pushSendYn",
                    required = false) String pushSendYn) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        BoardCategory category = boardService.getCategory(categoryId);

        if (!category.isUserWritable(user)) {
            return null;
        }


        List<MultipartFile> fileList = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileList.add(file);
            }
        }

        BoardPost post = new BoardPost();
        post.user = user;
        post.category = boardService.getCategory(categoryId);
        post.regDate = new Date();
        post.content = content;
        post.title = title == null || title.isEmpty() ? null : title;
        post.imageCount = fileList.size();
        post.rangeAll = "on".equalsIgnoreCase(rangeAll);
        post.rangeSido = rangeSido == null ? "" : rangeSido;
        post.rangeSigungu = rangeSigungu == null ? "" : rangeSigungu;
        post.viewCount = 0l;
        post.displayYn = "Y";

        if ("tts".equals(post.category.type)) {
            BoardPostAirWord bpaw = new BoardPostAirWord();
            post.voiceGubun = voiceGubun;
            post.voiceVolume = 0;
            post.pushSendYn = pushSendYn;
            post.airReserveDate = "3003-10-10Am10:10";
            bpaw.setAccWordCount(content.length());
            bpaw.setCategoryId(categoryId);
            boardService.saveAndFlush(bpaw);

        }

        post = boardService.saveAndFlush(post);

        if (!fileList.isEmpty()) {
            long postId = post.id;
            long postParentNum = post.id / 1000l;
            final int len = fileList.size();
            for (int i = 0; i < len; i++) {
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/image/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File dest = new File(dir, String.format("%s.jpg", i));
                    dest.createNewFile();
                    fileList.get(i).transferTo(dest);
                    Thumbnails.create(dest);
                } catch (IOException e) {
                    LOGGER.error("* 게시판 이미지 파일 저장 중 오류", e.getMessage());
                    // throw e;
                }
            }
        }

        if (files2 != null && files2.length != 0) {
            long postId = post.id;
            long postParentNum = post.id / 1000l;
            final int len = files2.length;

            for (int i = 0; i < len; i++) {
                String originalFileName = files2[i].getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    continue;
                }
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File dest = new File(dir, originalFileName);
                    dest.createNewFile();
                    files2[i].transferTo(dest);

                    if (i == 0) {
                        post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    } else if (i == 1) {
                        post.file2 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    }
                } catch (IOException e) {
                    LOGGER.error("* 게시판 파일 저장 중 오류", e.getMessage());
                    // throw e;
                }
            }
        }

        // 방송게시판의 경우 post.file1이 오디오파일 다운로드 경로
        if ("tts".equals(post.category.type)) {
            // 음성 파일 생성
            long postId = post.id;
            long postParentNum = post.id / 1000l;

            // /4 test, api/board/post/file/3941/3941.wav
            String originalFileName = postId + ".mp3";

            try {
                Apt apt = houseService.getApt(user.house.apt.id);
                if (apt.aptInfo == null || StringUtils.isBlank(apt.aptInfo.naverClientId) || StringUtils.isBlank(apt.aptInfo.naverClientSecret)) {
                    throw new Exception("<<네이버 TTS API 클라이언트 아이디와 비밀번호는 필수입니다>>");
                }

                File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(dir, originalFileName);
                dest.createNewFile();

                // 음성파일 API 연동 및 MP3 파일 저장
                this.httpUtils.saveMp3OfNaver(apt.aptInfo.naverClientId, apt.aptInfo.naverClientSecret, post.voiceGubun, post.content, dest);

                post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
            } catch (IOException e) {
                LOGGER.error("<<방송 게시판 오디오 파일 저장 중 오류>>", e);
            }
        }

        post = boardService.saveAndFlush(post); // ??? 왜 2번을 하지?

        if (notification != null && "on".equalsIgnoreCase(notification) && "notice".equalsIgnoreCase(post.category.type)) {
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
            // List<User> users = Lists.newArrayList();
            // if (post.rangeAll) {
            // users = userService.getAllUsers();
            // } else {
            // users = userService.getAllAptUsers(user.house.apt.id);
            // }
            // List<User> notificationApplyUsers = Lists.newArrayList(Collections2.filter(users, new Predicate<User>() {
            // @Override
            // public boolean apply(User input) {
            // return input.setting.notiAlarm;
            // }
            // }));
            //
            // List<Long> notificationApplyUserIds = Lists.transform(notificationApplyUsers, input -> input.id);
            // GcmSendForm form = new GcmSendForm();
            // Map<String, String> msg = Maps.newHashMap();
            // msg.put("type", "action");
            // msg.put("titleResId", "notice");
            // msg.put("value", post.content);
            // msg.put("action", "emaul://post-detail?id=" + post.id);
            // form.setUserIds(notificationApplyUserIds);
            // form.setMessage(msg);
            //
            // gcmService.sendGcm(form);

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.APT, PushAlarmSetting.BOARD, Lists.newArrayList(user.house.apt.id));
            String value = TagUtils.removeTag(post.content).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
            String action = String.format(PushAction.BOARD.getValue(), post.id);

            this.pushUtils.sendPush(PushGubun.BOARD_AIR, title, value, action, String.valueOf(post.id), false, targetUserList);
            //////////////////////////////////////////////////// GCM변경, 20161024, 전강욱 ////////////////////////////////////////////////////
        }

        post.user = userService.convertToPublicUser(post.user);

        return post;
    }

    @RequestMapping(value = "/admin/board/post/read-json/{postId}")
    @ResponseBody
    public BoardPost readBoardPostExt(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        return boardService.get(user, postId);
    }

    @RequestMapping(value = "/admin/board/post/modify-airSendDate-json/{postId}")
    @ResponseBody
    public void modifyAirSendDateOfTts(HttpServletRequest req, @PathVariable(value = "postId") Long postId, Model model) {
        this.boardService.modifyAirSendDateOfTts(postId);
    }

    @RequestMapping(value = "/admin/board/post/modify-ext/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public BoardPost modifyPostExt(HttpServletRequest req, @PathVariable(value = "postId") Long postId, @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "categoryId") Long categoryId, @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "files2", required = false) MultipartFile[] files2, @RequestParam(value = "voiceGubun", required = false) String voiceGubun, @RequestParam(value = "voiceVolume",
                    required = false) int voiceVolume, @RequestParam(value = "pushSendYn", required = false) String pushSendYn) {

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        BoardPost post = boardService.get(user, postId);
        post.content = content;
        post.voiceGubun = voiceGubun;
        post.voiceVolume = 0;
        post.pushSendYn = pushSendYn;
        post.modId = userId;
        post.modDate = new Date();
        if ("tts".equals(post.category.type)) {
            BoardPostAirWord bpaw = new BoardPostAirWord();
            bpaw.setAccWordCount(content.length());
            bpaw.setCategoryId(categoryId);
            boardService.saveAndFlush(bpaw);
        }

        String preContent = StringUtils.defaultString(req.getParameter("preContent"), StringUtils.EMPTY);

        if (files2 != null && files2.length != 0) {
            if (!files2[0].isEmpty() || !files2[1].isEmpty()) {
                post.file1 = null;
                post.file2 = null;
            }

            long postParentNum = post.id / 1000l;
            final int len = files2.length;

            for (int i = 0; i < len; i++) {
                String originalFileName = files2[i].getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    continue;
                }
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File dest = new File(dir, originalFileName);
                    dest.createNewFile();
                    files2[i].transferTo(dest);

                    if (i == 0) {
                        post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    } else if (i == 1) {
                        post.file2 = "/api/board/post/file/" + postId + "/" + originalFileName;
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }

        List<MultipartFile> fileList = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileList.add(file);
            }
        }

        if (!fileList.isEmpty()) {
            post.imageCount = fileList.size();
            boardService.deletePostImages(post);

            long postParentNum = post.id / 1000l;
            final int len = fileList.size();

            for (int i = 0; i < len; i++) {
                try {
                    File dir = new File(String.format("/nas/EMaul/board/post/image/%s/%s", postParentNum, postId));
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File dest = new File(dir, String.format("%s.jpg", i));
                    dest.createNewFile();
                    fileList.get(i).transferTo(dest);
                    Thumbnails.create(dest);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }

        // 방송게시판의 경우 post.file1이 오디오파일 다운로드 경로
        if ("tts".equals(post.category.type) && !content.equals(preContent)) {
            // 음성 파일 생성
            long postParentNum = post.id / 1000l;

            // /4 test, api/board/post/file/3941/3941.wav
            String originalFileName = postId + ".mp3";

            try {
                Apt apt = houseService.getApt(user.house.apt.id);
                if (apt.aptInfo == null || StringUtils.isBlank(apt.aptInfo.naverClientId) || StringUtils.isBlank(apt.aptInfo.naverClientSecret)) {
                    throw new Exception("<<네이버 TTS API 클라이언트 아이디와 비밀번호는 필수입니다>>");
                }

                File dir = new File(String.format("/nas/EMaul/board/post/file/%s/%s", postParentNum, postId));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(dir, originalFileName);
                dest.createNewFile();

                // 음성파일 API 연동 및 MP3 파일 저장
                this.httpUtils.saveMp3OfNaver(apt.aptInfo.naverClientId, apt.aptInfo.naverClientSecret, post.voiceGubun, post.content, dest);

                post.file1 = "/api/board/post/file/" + postId + "/" + originalFileName;
            } catch (Exception e) {
                LOGGER.error("<<방송 게시판 오디오 파일 저장 중 오류>>", e);
            }
        }

        boardService.saveAndFlush(post);

        return post;
    }

    @RequestMapping(value = "/admin/board/categories", method = RequestMethod.GET)
    public String getBoardCategories(HttpSession session, Model model) {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        List<BoardCategory> boardCategories = boardCategoryService.fetchBoardCategories(user.house.apt);

        model.addAttribute("boardCategories", boardCategories);

        return "admin/board-categories";
    }

    @RequestMapping(value = "/admin/board/categories", method = RequestMethod.POST)
    public String modifyUserPrivacy(HttpSession session, @RequestParam(name = "userPrivacy") List<UserPrivacy> userPrivacies, @RequestParam(name = "ord") List<String> ords,
            @RequestParam(name = "id") List<String> ids) {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        boardCategoryService.modifyBoardCategories(user.house.apt, ids, userPrivacies, ords);

        return "redirect:/admin/board/categories";
    }


    /**
     * 게시판 생성 화면
     *
     * @param session
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/board/categories/create-form", method = RequestMethod.GET)
    public String boardCategoryCreateForm(HttpSession session, Model model, BoardCategoryVo boardCategoryVo) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        if (!user.type.jaha) {
            throw new Exception("<<< 자하 권한만 가능합니다. >>>");
        }
        // COMMON_CODE 의 USE_YN 컨트롤
        List<CommonCode> typeList = commonService.findByCodeGroup("BOARD_CATEGORY"); // 게시판 카테고리 리스트
        model.addAttribute("typeList", typeList);
        model.addAttribute("boardCategoryVo", boardCategoryVo);
        model.addAttribute("job", "create");

        return "jaha/board-categories-form";
    }


    /**
     * 게시판 등록 처리
     *
     * @param session
     * @param model
     * @param boardCategoryVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/board/categories/create", method = RequestMethod.POST)
    public String boardCategoryCreate(HttpSession session, Model model, BoardCategoryVo boardCategoryVo, @RequestParam(value = "r-type") String[] rType, @RequestParam(value = "w-type") String[] wType)
            throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        if (!user.type.jaha) {
            throw new Exception("<<< 자하 권한만 가능합니다. >>>");
        }
        List<String> rTypes = Lists.newArrayList(rType);
        List<String> wTypes = Lists.newArrayList(wType);

        boardCategoryVo.setJsonArrayReadableUserType(new Gson().toJson(rTypes));
        boardCategoryVo.setJsonArrayWritableUserType(new Gson().toJson(wTypes));
        boardCategoryVo.setAptId(user.house.apt.id);
        boardCategoryVo.setUserId(user.id);

        LOGGER.debug(">>> boardCategoryVo : " + boardCategoryVo.toString());
        boardService.insertBoardCategory(boardCategoryVo);

        return "redirect:/admin/board/categories";
    }

    /**
     * 게시판 수정 화면
     *
     * @param session
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/board/categories/update-form/{id}", method = RequestMethod.GET)
    public String boardCategoryUpdateForm(HttpSession session, Model model, BoardCategoryVo boardCategoryVo, @PathVariable("id") Long id) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        if (!user.type.jaha) {
            throw new Exception("<<< 자하 권한만 가능합니다. >>>");
        }

        List<CommonCode> typeList = commonService.findByCodeGroup("BOARD_CATEGORY"); // 게시판 카테고리 리스트
        model.addAttribute("typeList", typeList);
        model.addAttribute("boardCategoryVo", boardService.getBoardCategory(id));
        model.addAttribute("job", "update");

        return "jaha/board-categories-form";
    }


    /**
     * 게시판 수정 처리
     *
     * @param session
     * @param model
     * @param boardCategoryVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/board/categories/update", method = RequestMethod.POST)
    public String boardCategoryUpdate(HttpSession session, Model model, BoardCategoryVo boardCategoryVo, @RequestParam(value = "r-type") String[] rType, @RequestParam(value = "w-type") String[] wType)
            throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        if (!user.type.jaha) {
            throw new Exception("<<< 자하 권한만 가능합니다. >>>");
        }
        List<String> rTypes = Lists.newArrayList(rType);
        List<String> wTypes = Lists.newArrayList(wType);

        boardCategoryVo.setJsonArrayReadableUserType(new Gson().toJson(rTypes));
        boardCategoryVo.setJsonArrayWritableUserType(new Gson().toJson(wTypes));
        boardCategoryVo.setAptId(user.house.apt.id);
        boardCategoryVo.setModId(user.id);

        LOGGER.debug(">>> boardCategoryVo : " + boardCategoryVo.toString());
        boardService.updateBoardCategory(boardCategoryVo);

        return "redirect:/admin/board/categories";
    }


    /**
     * 방송 설정 업데이트
     *
     * @param req
     * @param postId
     * @param airReserveYn
     * @param pushSendYn
     * @param airReserveDate
     * @param airReserveCancelYn
     * @return
     */
    @RequestMapping(value = "/admin/board/broadCastSeting/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public BoardPost broadCastSeting(HttpServletRequest req, Model model, @PathVariable(value = "postId") Long postId, @RequestParam(value = "airReserveYn", required = false) String airReserveYn,
            @RequestParam(value = "pushSendYn", required = false) String pushSendYn, @RequestParam(value = "airReserveDate", required = false) String airReserveDate, @RequestParam(
                    value = "airReserveCancelYn", required = false) String airReserveCancelYn) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        airReserveDate = airReserveDate.replaceAll("\\p{Z}", ""); // 예약날짜 공백제거
        pushSendYn = pushSendYn.replace(",", "");

        if (req.getParameter("repeat") != null) {// 반복설정 파라미터
            model.addAttribute("repeat", req.getParameter("repeat"));
        }

        BoardPost post = boardService.get(user, postId);
        post.pushSendYn = pushSendYn;
        post.airReserveYn = airReserveYn;
        post.airReserveDate = airReserveDate;
        post.displayYn = "Y";
        boardService.saveAndFlush(post);

        return post;
    }

    @RequestMapping(value = "/admin/board/post/delete/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public BoardPost deleteBroadPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId) {
        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);
        BoardPost post = boardService.get(user, postId);
        post.displayYn = "N";
        boardService.saveAndFlush(post);
        return post;
    }

    @RequestMapping(value = "/admin/board/post/totalContent/{categoryId}", method = RequestMethod.GET)
    @ResponseBody
    public int totalContentLength(HttpServletRequest req, @PathVariable(value = "categoryId") Long categoryId) {
        return boardService.getTotalContentLength(categoryId);
    }


    /**
     * 게시물 블라인드 처리 [자하권한]
     *
     * @param req
     * @param postId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/admin/board/post/blind/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public boolean blindBroadPost(HttpServletRequest req, @PathVariable(value = "postId") Long postId, @RequestBody String json) throws Exception {

        JSONObject obj = new JSONObject(json);

        Long userId = SessionAttrs.getUserId(req.getSession());
        User user = userService.getUser(userId);

        if (!user.type.jaha) {
            LOGGER.error("<< 블라인드 처리 권한 오류 >>");
            // throw new Exception("<< 블라인드 처리 권한 오류 >>");
            return false;
        }

        try {
            BoardPost post = boardService.get(user, postId);
            LOGGER.debug(">>> post : " + post);
            post.blindYn = obj.getString("blindYn");
            boardService.saveAndFlush(post);
            return true;
        } catch (Exception e) {
            LOGGER.error("<< 블라인드 처리 오류 >>");
            LOGGER.error("<< " + e.getMessage() + " >>");
            return false;
        }
    }



}
