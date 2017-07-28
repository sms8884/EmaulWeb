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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostEventVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.service.board.BoardService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : SystemBoardController.java
 * Description : 이벤트/FAQ/시스템 공지사항 처리 게시판, 카테고리 타입이 하나만 존재하며 자하권한에만 쓰기권한이 있음
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
@Controller
public class SystemBoardController {

    @Value("${event.board.category.id}")
    private Long eventBoardCategoryId;

    @Value("${faq.board.category.id}")
    private Long faqBoardCategoryId;

    @Value("${system.notice.board.category.id}")
    private Long systemNoticeBoardCategoryId;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemBoardController.class);

    @Autowired
    private BoardService boardService;

    /**
     * 이벤트 게시글 글 등록
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/event/create", method = RequestMethod.POST)
    public String createEventBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardPostEventVo boardPost) {
        try {
            boardDto.setSearchCategoryId(this.eventBoardCategoryId);

            // 게시글 등록
            this.boardService.regBoardPost(boardDto, new BoardPostWrapper<BoardPostEventVo>(boardPost));
        } catch (Exception e) {
            LOGGER.error("<<이벤트 게시판 게시글 등록 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/event/list";
    }

    /**
     * 이벤트 게시글 글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/event/modify/{postId}", method = RequestMethod.POST)
    public String modifyEventBoardPost(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto,
            BoardPostEventVo boardPost, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(this.eventBoardCategoryId);

            boardPost.setId(postId);

            // 게시글 수정
            this.boardService.modifyBoardPost(boardDto, new BoardPostWrapper<BoardPostEventVo>(boardPost));

            // 수정화면에서의 이동
            redirectAttr.addFlashAttribute("fromWhere", "modify");
        } catch (Exception e) {
            LOGGER.error("<<이벤트 게시판 게시글 수정 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/event/read/" + postId;
    }

    /**
     * FAQ 게시글 글 등록
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/faq/create", method = RequestMethod.POST)
    public String createFaqBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardPostVo boardPost) {
        try {
            boardDto.setSearchCategoryId(this.faqBoardCategoryId);

            // 게시글 등록
            this.boardService.regBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));
        } catch (Exception e) {
            LOGGER.error("<<FAQ 게시판 게시글 등록 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/faq/list";
    }

    /**
     * FAQ 게시글 글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/faq/modify/{postId}", method = RequestMethod.POST)
    public String modifyFaqBoardPost(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto,
            BoardPostVo boardPost, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(this.faqBoardCategoryId);

            boardPost.setId(postId);

            // 게시글 수정
            this.boardService.modifyBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));

            // 수정화면에서의 이동
            redirectAttr.addFlashAttribute("fromWhere", "modify");
        } catch (Exception e) {
            LOGGER.error("<<FAQ 게시판 게시글 수정 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/faq/read/" + postId;
    }

    /**
     * e마을 공지사항(시스템 공지사항) 게시글 글 등록
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/system-notice/create", method = RequestMethod.POST)
    public String createGroupBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto, BoardPostVo boardPost) {
        try {
            boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);

            // 게시글 등록
            this.boardService.regBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));
        } catch (Exception e) {
            LOGGER.error("<<e마을 공지사항(시스템 공지사항) 게시판 게시글 등록 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/system-notice/list";
    }

    /**
     * e마을 공지사항(시스템 공지사항) 게시글 글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardPost
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/jaha/board/system-notice/modify/{postId}", method = RequestMethod.POST)
    public String modifyGroupBoardPost(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, @ModelAttribute("boardDto") BoardDto boardDto,
            BoardPostVo boardPost, @PathVariable(value = "postId") Long postId) {
        try {
            boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);

            boardPost.setId(postId);

            // 게시글 수정
            this.boardService.modifyBoardPost(boardDto, new BoardPostWrapper<BoardPostVo>(boardPost));

            // 수정화면에서의 이동
            redirectAttr.addFlashAttribute("fromWhere", "modify");
        } catch (Exception e) {
            LOGGER.error("<<e마을 공지사항(시스템 공지사항) 게시판 게시글 수정 중 오류>>", e);
        }

        return "redirect:/v2/jaha/board/system-notice/read/" + postId;
    }

}
