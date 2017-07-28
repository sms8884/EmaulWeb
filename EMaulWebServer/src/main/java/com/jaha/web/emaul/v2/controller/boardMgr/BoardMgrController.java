/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.boardMgr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.v2.constants.BoardConstants.BoardMgrType;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardMgrPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostReadVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.model.common.Search;
import com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : BoardMgrController.java
 * Description : 게시글 관리
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * </pre>
 *
 * @version 1.0
 */
@Controller("BoardMgrController")
public class BoardMgrController {

    private static final Logger logger = LoggerFactory.getLogger(BoardMgrController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BoardMgrService boardMgrService;

    @Autowired
    private CommonService commonService;

    /**
     * 숨김글 관리 목록
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param userAuthType admin / user / group-admin / jaha
     * @return
     */
    @RequestMapping(value = {"/v2/{userAuthType}/board-mgr/{type}/list"})
    public String findJahaBoardMgrList(HttpServletRequest request, Model model, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardMgrPostVo boardMgrPostVo,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "type") String type) {
        try {

            if (!"jaha".equals(userAuthType)) {
                logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
                throw new Exception(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            }

            User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
            if (!user.type.jaha) {
                logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
                throw new Exception(">>> 게시글 모니터링 접근 오류 - jaha");
            }

        } catch (Exception e) {
            logger.error(">>> 게시글 목록 조회 오류", e);
        }

        return "v2/jaha/boardMgr/" + type + "/list";
    }


    /**
     * 게시글 리스트 조회 Ajax
     *
     * @param request
     * @param model
     * @param boardMgrPostVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/{type}/select-list")
    @ResponseBody
    public Map<String, Object> findJahaBoardMgrSelectList(HttpServletRequest request, Model model, BoardDto boardDto, BoardMgrPostVo boardMgrPostVo,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "type") String type) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        boardDto.setType(type);
        // 읽음 글 조회용
        boardDto.setReadUserId(user.id);

        logger.debug(">>> pageNum : " + boardMgrPostVo.getPageNum());
        logger.debug(">>> pageSize : " + boardMgrPostVo.getPageSize());

        // 팀장님께 물어봐야겠다. 왜 안들어오지?
        PagingHelper pagingHelper = new PagingHelper();
        pagingHelper.setPageNum(boardMgrPostVo.getPageNum() == 0 ? 1 : boardMgrPostVo.getPageNum());
        pagingHelper.setPageSize(boardMgrPostVo.getPageSize() == 0 ? 100 : boardMgrPostVo.getPageSize());
        pagingHelper.setPageBlockSize(10);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        boardDto.setPagingHelper(pagingHelper);

        Search search = new Search();
        if (StringUtils.isNotBlank(request.getParameter("searchItem")) && StringUtils.isNotBlank(request.getParameter("searchKeyword"))) {
            search.setItem(request.getParameter("searchItem"));
            search.setKeyword(request.getParameter("searchKeyword"));
        }
        if (StringUtils.isNotBlank(request.getParameter("searchStartDate"))) {
            search.setStartDate(request.getParameter("searchStartDate"));
        }
        if (StringUtils.isNotBlank(request.getParameter("searchEndDate"))) {
            search.setEndDate(request.getParameter("searchEndDate"));
        }
        boardDto.getPagingHelper().setSearch(search);

        if (BoardMgrType.BLIND.getCode().equalsIgnoreCase(type) || BoardMgrType.POST.getCode().equalsIgnoreCase(type)) {
            // 숨김글
            resultMap.put("postList", boardMgrService.findBoardMgrPostList(boardDto));
            resultMap.put("pagingHelper", boardDto.getPagingHelper());
        } else if (BoardMgrType.SPAM.getCode().equalsIgnoreCase(type)) {
            // 스펨글 (게시글 제목 + 내용 + 댓글 내용 : 모두 조회하여 스펨글 필터링)
            // 여기 해야함
            resultMap.put("postList", boardMgrService.findBoardMgrPostList(boardDto));
            resultMap.put("pagingHelper", boardDto.getPagingHelper());
        } else if (BoardMgrType.COMMENT.getCode().equalsIgnoreCase(type)) {
            resultMap.put("postList", boardMgrService.getPostCommentList(boardDto));
            resultMap.put("pagingHelper", boardDto.getPagingHelper());
        }

        return resultMap;
    }


    /**
     * 사용자 정보 조회
     *
     * @param request
     * @param model
     * @param boardMgrPostVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/user/{id}")
    public String jahaBoardMonitorUser(HttpServletRequest request, Model model, @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "id") Long id) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        model.addAttribute("userInfo", userService.getUser(id));
        model.addAttribute("postCount", boardMgrService.boardPostCountByUserId(id));;
        model.addAttribute("commentCount", boardMgrService.boardCommentCountByUserId(id));;

        return "v2/jaha/boardMgr/common/user";
    }

    /**
     * 작성글 보기 - 사용자 작성 게시물 리스트
     *
     * @param request
     * @param model
     * @param userAuthType
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/{type}/list/{id}")
    public String jahaBoardMonitorUserPostListForm(HttpServletRequest request, Model model, @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "id") Long id,
            @PathVariable(value = "type") String type) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        model.addAttribute("searchUserId", id);

        if (BoardMgrType.COMMENT.getCode().equalsIgnoreCase(type)) {
            return "v2/jaha/boardMgr/common/comment-list";
        }
        return "v2/jaha/boardMgr/common/post-list";
    }



    /**
     * 사용자 작성글(댓글) 목록 조회
     *
     * @param request
     * @param model
     * @param boardDto
     * @param boardMgrPostVo
     * @param userAuthType
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/{type}/select-list/{id}")
    @ResponseBody
    public Map<String, Object> findJahaBoardMgrUserPostList(HttpServletRequest request, Model model, BoardDto boardDto, BoardMgrPostVo boardMgrPostVo,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "id") Long id, @PathVariable(value = "type") String type) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        boardDto.setSearchUserId(id);
        // 읽음 글 조회용
        boardDto.setReadUserId(user.id);

        logger.debug(">>> pageNum : " + boardMgrPostVo.getPageNum());
        logger.debug(">>> pageSize : " + boardMgrPostVo.getPageSize());

        // 팀장님께 물어봐야겠다. 왜 안들어오지?
        PagingHelper pagingHelper = new PagingHelper();
        pagingHelper.setPageNum(boardMgrPostVo.getPageNum() == 0 ? 1 : boardMgrPostVo.getPageNum());
        pagingHelper.setPageSize(boardMgrPostVo.getPageSize() == 0 ? 10 : boardMgrPostVo.getPageSize());
        pagingHelper.setPageBlockSize(10);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        boardDto.setPagingHelper(pagingHelper);

        Search search = new Search();
        if (StringUtils.isNotBlank(request.getParameter("searchItem")) && StringUtils.isNotBlank(request.getParameter("searchKeyword"))) {
            search.setItem(request.getParameter("searchItem"));
            search.setKeyword(request.getParameter("searchKeyword"));
        }
        if (StringUtils.isNotBlank(request.getParameter("searchStartDate"))) {
            search.setStartDate(request.getParameter("searchStartDate"));
        }
        if (StringUtils.isNotBlank(request.getParameter("searchEndDate"))) {
            search.setEndDate(request.getParameter("searchEndDate"));
        }
        boardDto.getPagingHelper().setSearch(search);

        if (BoardMgrType.COMMENT.getCode().equalsIgnoreCase(type)) {
            resultMap.put("postList", boardMgrService.getPostCommentList(boardDto));
            resultMap.put("pagingHelper", boardDto.getPagingHelper());
        } else {
            resultMap.put("postList", boardMgrService.findBoardMgrPostList(boardDto));
            resultMap.put("pagingHelper", boardDto.getPagingHelper());
        }

        return resultMap;
    }



    /**
     * 게시글 상세보기
     *
     * @param request
     * @param model
     * @param userAuthType
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/post/{id}")
    public String jahaBoardMonitorPost(HttpServletRequest request, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "id") Long postId) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        // 게시글 상세 조회
        BoardPostWrapper<?> boardPost = boardMgrService.findBoardMgrPost(boardDto, postId);
        model.addAttribute("category", boardDto.getBoardCategory());
        model.addAttribute("post", boardPost.get());
        model.addAttribute("boardCommentList", boardPost.get().getBoardCommentList());

        // -- 게시물 읽음처리 [s]
        try {
            BoardPostReadVo postRead = new BoardPostReadVo();
            postRead.setCategoryId(boardPost.get().getCategoryId());
            postRead.setPostId(boardPost.get().getId());
            postRead.setUserId(user.id);
            postRead.setReadDate(new Date());

            boardMgrService.setPostRead(postRead);

        } catch (Exception e) {
            logger.error(">>> 게시물 모니터링 읽음처리 오류 : " + e.getMessage());
        }
        // -- 게시물 읽음처리 [e]

        return "v2/jaha/boardMgr/common/post";
    }


    /**
     * 댓글 숨김처리 수정
     *
     * @param request
     * @param model
     * @param boardDto
     * @param boardCommentVo
     * @param userAuthType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/comment-display-update")
    @ResponseBody
    public int jahaCommentDisplayUpdate(HttpServletRequest request, Model model, BoardCommentVo boardCommentVo, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return -1;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return -1;
        }

        logger.debug(">>> commentId : " + boardCommentVo.getId() + " / displayYn : " + boardCommentVo.getDisplayYn());
        return boardMgrService.boardCommentDisplayUpdate(boardCommentVo);

    }

    /**
     * 댓글리스트 - 숨김 상태 변경 (LIST)
     * 
     * @param request
     * @param model
     * @param boardCommentVo
     * @param userAuthType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/comment-list-blind-update")
    @ResponseBody
    public int jahaCommentListDisplayUpdate(HttpServletRequest request, Model model, BoardCommentVo boardCommentVo, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return -1;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return -1;
        }

        logger.debug(">>> blockedYn : " + boardCommentVo.getBlocked() + " / length : " + boardCommentVo.getIds().length);

        if (boardCommentVo.getIds().length > 0) {
            return boardMgrService.boardCommentDisplayUpdate(boardCommentVo);
        } else {
            return -1;
        }
    }



    /**
     * 게시글 숨김처리 수정
     *
     * @param request
     * @param model
     * @param boardDto
     * @param boardCommentVo
     * @param userAuthType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/post-blind-update")
    @ResponseBody
    public int jahaPostBlindYnUpdate(HttpServletRequest request, Model model, BoardPostVo boardPostVo, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return -1;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return -1;
        }

        logger.debug(">>> postId : " + boardPostVo.getId() + " / blindYn : " + boardPostVo.getBlindYn());
        return boardMgrService.boardPostBlindYnUpdate(boardPostVo);
    }

    /**
     * 게시글 리스트 - 숨김 상태 변경 (LIST)
     *
     * @param request
     * @param model
     * @param boardPostVo
     * @param userAuthType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/post-list-blind-update")
    @ResponseBody
    public int jahaPostListBlindYnUpdate(HttpServletRequest request, Model model, BoardPostVo boardPostVo, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return -1;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return -1;
        }

        logger.debug(">>> blindYn : " + boardPostVo.getBlindYn() + " / length : " + boardPostVo.getIds().length);

        if (boardPostVo.getIds().length > 0) {
            return boardMgrService.boardPostBlindYnUpdate(boardPostVo);
        } else {
            return -1;
        }

        // String[] ids = request.getParameterValues("ids");
        // if (ids.length > 0) {
        // List<Long> longList = Lists.transform(Arrays.asList(ids), Longs.stringConverter());
        // for (Long a : longList) {
        // logger.debug(">>> a : " + a);
        // }
        //
        // } else {
        // return -1;
        // }

    }


    /**
     * 작성글 보기 - 사용자 작성 게시물 리스트
     *
     * @param request
     * @param model
     * @param userAuthType
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/word-list")
    public String jahaBoardMonitorWordListForm(HttpServletRequest request, Model model, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        return "v2/jaha/boardMgr/common/word-list";
    }



    /**
     * 금칙어 목록 조회
     *
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/select-word-list")
    @ResponseBody
    public List<CommonCode> findJahaBoardMgrWordList(HttpServletRequest request, Model model, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return null;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return null;
        }

        return commonService.findByCodeGroup("POST_WORD");
    }



    /**
     * 게시글 숨김처리 수정
     * 
     * @param request
     * @param model
     * @param boardDto
     * @param boardCommentVo
     * @param userAuthType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/{userAuthType}/board-mgr/common/word-list-update")
    @ResponseBody
    public int jahaPostWordUseYnUpdate(HttpServletRequest request, Model model, CommonCode commonCode, @PathVariable(value = "userAuthType") String userAuthType) throws Exception {

        if (!"jaha".equals(userAuthType)) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - authType : " + userAuthType);
            return -1;
        }

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return -1;
        }

        logger.debug(">>> cdIds : " + commonCode.getCdIds() + " / useYn : " + commonCode.getUse_yn());
        for (String a : commonCode.getCdIds()) {
            logger.debug(">>> a : " + a);
        }
        return boardMgrService.boardWordUseYnUpdate(commonCode);
    }


    /**
     * 댓글읽음처리
     * 
     * @param request
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param postId
     * @param commentId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/board-mgr/common/read/comment/{postId}/{commentId}")
    @ResponseBody
    public boolean jahaCommentRead(HttpServletRequest request, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId) throws Exception {

        boolean result = false;
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        if (!user.type.jaha) {
            logger.debug(">>> 게시글 모니터링 접근 오류 - jaha");
            return false;
        }

        // 게시글 상세 조회
        BoardPostWrapper<?> boardPost = boardMgrService.findBoardMgrPost(boardDto, postId);

        // -- 댓글 읽음처리 [s]
        try {
            BoardPostReadVo postRead = new BoardPostReadVo();
            postRead.setCategoryId(boardPost.get().getCategoryId());
            postRead.setPostId(0l); // 댓글읽음 처리할때는 postId 0
            postRead.setUserId(user.id);
            postRead.setCommentId(commentId);
            postRead.setReadDate(new Date());

            if (boardMgrService.setPostRead(postRead) > 0) {
                return true;
            }

        } catch (Exception e) {
            logger.error(">>> 게시물 모니터링 읽음처리 오류 : " + e.getMessage());
        }
        return false;

    }


}
