/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.board;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.service.board.BoardService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : BoardController.java
 * Description : 공지사항/커뮤니티 등 게시판 처리
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 22.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 9. 22.
 * @version 1.0
 */
@Controller("v2BoardController")
public class BoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    /**
     * 게시글 글 등록
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param userAuthType admin / user / group-admin / jaha
     * @return
     */
    @RequestMapping(value = {"/v2/{userAuthType}/board/notice/create", "/v2/{userAuthType}/board/community/create"}, method = RequestMethod.POST)
    public String createBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardPostVo boardPost,
            @PathVariable(value = "userAuthType") String userAuthType) {
        try {
            boardDto.setSearchCategoryId(boardPost.getCategoryId());

            // 게시글 등록
            this.boardService.regBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));
        } catch (Exception e) {
            LOGGER.error("<<게시글 등록 중 오류>>", e);
        }

        return "redirect:/v2/" + userAuthType + "/board/" + boardDto.getBoardCategory().getType() + "/list/" + boardPost.getCategoryId();
    }

    /**
     * 게시글 글 상세 조회
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryId
     * @param postId
     * @return
     */
    @RequestMapping(value = {"/v2/{userAuthType}/board/notice/read/{categoryId}/{postId}", "/v2/{userAuthType}/board/community/read/{categoryId}/{postId}",
            "/v2/{userAuthType}/board/tts/read/{categoryId}/{postId}", "/v2/{userAuthType}/board/complaint/read/{categoryId}/{postId}"})
    public String findBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryId") Long categoryId, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(categoryId);

            // 게시글 상세 조회
            BoardPostWrapper<?> boardPost = this.boardService.findBoardPost(boardDto, postId, false);

            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("post", boardPost.get());

            // 사용자 화면에서만 댓글목록을 제거한다.
            if ("N".equals(boardDto.getBoardCategory().getCommentDisplayYn())) {
                if (!"user".equals(userAuthType)) {
                    model.addAttribute("boardCommentList", boardPost.get().getBoardCommentList());
                }
            } else {
                model.addAttribute("boardCommentList", boardPost.get().getBoardCommentList());
            }
            model.addAttribute("leftSideMenu", "board");
        } catch (Exception e) {
            LOGGER.error("<<게시글 상세 조회 중 오류>>", e);
        }

        if (!"jaha".equals(userAuthType) && !"group-admin".equals(userAuthType) && !"event".equals(userAuthType) && !"system-notice".equals(userAuthType)) {
            return "v2/" + userAuthType + "/board/common/read";
        } else {
            return "v2/" + userAuthType + "/board/" + boardDto.getBoardCategory().getType() + "/read";
        }

    }

    /**
     * 게시글 수정 페이지 이동
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryId
     * @param postId
     * @return
     */
    @RequestMapping(value = {"/v2/{userAuthType}/board/notice/modify-form/{categoryId}/{postId}", "/v2/{userAuthType}/board/community/modify-form/{categoryId}/{postId}",
            "/v2/{userAuthType}/board/tts/modify-form/{categoryId}/{postId}", "/v2/{userAuthType}/board/complaint/modify-form/{categoryId}/{postId}"}, method = RequestMethod.GET)
    public String moveBoardPostModifyForm(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryId") Long categoryId, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(categoryId);

            // 수정화면의 게시글 상세 조회
            BoardPostWrapper<?> boardPost = this.boardService.findBoardPost(boardDto, postId, true);
            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("post", boardPost.get());
            model.addAttribute("leftSideMenu", "board");
        } catch (Exception e) {
            LOGGER.error("<<게시글 수정 페이지 이동 중 오류>>", e);
        }

        String viewPath = "";
        if (!"jaha".equals(userAuthType) && !"group-admin".equals(userAuthType) && !"event".equals(userAuthType) && !"system-notice".equals(userAuthType)) {
            viewPath = "v2/" + userAuthType + "/board/common/modify-form";
        } else {
            viewPath = "v2/" + userAuthType + "/board/" + boardDto.getBoardCategory().getType() + "/modify-form";
        }


        return viewPath;
    }

    /**
     * 게시글 글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryId
     * @param postId
     * @return
     */
    @RequestMapping(value = {"/v2/{userAuthType}/board/notice/modify/{categoryId}/{postId}", "/v2/{userAuthType}/board/community/modify/{categoryId}/{postId}"}, method = RequestMethod.POST)
    public String modifyBoardPost(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardPostVo boardPost,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryId") Long categoryId, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(categoryId);
            boardPost.setId(postId);

            // 게시글 수정
            this.boardService.modifyBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));

            // 수정화면에서의 이동
            redirectAttr.addFlashAttribute("fromWhere", "modify");
        } catch (Exception e) {
            LOGGER.error("<<게시글 수정 중 오류>>", e);
        }

        return "redirect:/v2/" + userAuthType + "/board/" + boardDto.getBoardCategory().getType() + "/read/" + categoryId + "/" + postId;
    }

}
