/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.board;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.v2.constants.BoardConstants.BoardType;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.service.board.BoardCategoryService;
import com.jaha.web.emaul.v2.service.board.BoardService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : SystemCommonBoardController.java
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
public class SystemCommonBoardController {

    @Value("${event.board.category.id}")
    private Long eventBoardCategoryId;

    @Value("${faq.board.category.id}")
    private Long faqBoardCategoryId;

    @Value("${system.notice.board.category.id}")
    private Long systemNoticeBoardCategoryId;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemCommonBoardController.class);

    @Autowired
    private BoardCategoryService boardCategoryService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommonService commonService;

    /**
     * 게시글 목록 조회
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/list")
    public String findBoardPostList(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType) {
        try {
            if (BoardType.EVENT.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.eventBoardCategoryId);
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
            } else if (BoardType.FAQ.getCode().equals(categoryType)) {
                model.addAttribute("subCategoryList", this.commonService.findByCodeGroup("FAQ_SUBCATEGORY"));
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                boardDto.setSearchCategoryId(this.faqBoardCategoryId);
            } else if (BoardType.SYSTEM_NOTICE.getCode().equals(categoryType)) {
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);
            }

            if ("user".equals(userAuthType)) {
                boardDto.setSearchDisplayYn("Y");
            }

            // 게시글 목록 조회
            List<?> boardPostList = this.boardService.findBoardPostList(boardDto);

            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("boardPostList", boardPostList);
        } catch (Exception e) {
            LOGGER.error("<<시스템 게시판 게시글 조회 중 오류>>", e);
        }

        String viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/list";

        return viewPath;
    }

    /**
     * 게시글 글 등록 페이지 이동
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/create-form", method = RequestMethod.GET)
    public String moveBoardPostForm(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType) {
        try {
            if (BoardType.EVENT.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.eventBoardCategoryId);
            } else if (BoardType.FAQ.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.faqBoardCategoryId);
                model.addAttribute("subCategoryList", this.commonService.findByCodeGroup("FAQ_SUBCATEGORY"));
            } else if (BoardType.SYSTEM_NOTICE.getCode().equals(categoryType)) {
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);
            }

            BoardCategoryVo boardCategory = this.boardCategoryService.findBoardCategory(boardDto.getSearchCategoryId());
            model.addAttribute("category", boardCategory);
        } catch (Exception e) {
            LOGGER.error("<<시스템 게시판 게시글 등록 페이지 이동 중 오류>>", e);
        }

        String viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/create-form";

        return viewPath;
    }

    /**
     * 게시글 글 상세 조회
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/read/{postId}")
    public String findBoardPost(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "postId") Long postId) {
        try {
            if (BoardType.EVENT.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.eventBoardCategoryId);
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                if ("public".equals(userAuthType)) {
                    boardDto.setSearchDisplayYn("Y");
                }
            } else if (BoardType.FAQ.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.faqBoardCategoryId);
                model.addAttribute("subCategoryList", this.commonService.findByCodeGroup("FAQ_SUBCATEGORY"));
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
            } else if (BoardType.SYSTEM_NOTICE.getCode().equals(categoryType)) {
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);
            }

            // 게시글 상세 조회
            BoardPostWrapper<?> boardPost = this.boardService.findBoardPost(boardDto, postId, false);

            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("post", boardPost.get());
            model.addAttribute("boardCommentList", boardPost.get().getBoardCommentList());
        } catch (Exception e) {
            LOGGER.error("<<시스템 게시판 게시글 상세 조회 중 오류>>", e);
        }

        return "v2/" + userAuthType + "/board/" + categoryType + "/read";
    }

    /**
     * 게시글 수정 페이지 이동
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/modify-form/{postId}", method = RequestMethod.GET)
    public String moveBoardPostModifyForm(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "postId") Long postId) {
        try {
            if (BoardType.EVENT.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.eventBoardCategoryId);
            } else if (BoardType.FAQ.getCode().equals(categoryType)) {
                boardDto.setSearchCategoryId(this.faqBoardCategoryId);
                model.addAttribute("subCategoryList", this.commonService.findByCodeGroup("FAQ_SUBCATEGORY"));
            } else if (BoardType.SYSTEM_NOTICE.getCode().equals(categoryType)) {
                model.addAttribute("leftSideMenu", "system"); // userAuthType user에서 사용
                boardDto.setSearchCategoryId(this.systemNoticeBoardCategoryId);
            }

            // 수정화면의 게시글 상세 조회
            BoardPostWrapper<?> boardPost = this.boardService.findBoardPost(boardDto, postId, true);
            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("post", boardPost.get());
        } catch (Exception e) {
            LOGGER.error("<<시스템 게시판 게시글 수정 페이지 이동 중 오류>>", e);
        }

        String viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/modify-form";

        return viewPath;
    }

    /**
     * 게시글 글 삭제
     *
     * @param req
     * @param boardDto
     * @param boardPost
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/remove/{postId}")
    public String modifyBoardPost(HttpServletRequest req, BoardDto boardDto, BoardPostVo boardPost, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "postId") Long postId) {
        try {
            boardPost.setId(postId);

            // 게시글 삭제
            this.boardService.removeBoardPost(boardDto, boardPost);
        } catch (Exception e) {
            LOGGER.error("<<시스템 게시판 게시글 삭제 중 오류>>", e);
        }

        return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/list";
    }

}
