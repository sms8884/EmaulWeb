/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.boardMgr;

import java.util.List;

import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardMgrPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostReadVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;

/**
 * <pre>
 * Class Name : BoardMgrService.java
 * Description : 게시판 모니터링 서비스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * </pre>
 *
 * @version 1.0
 */
public interface BoardMgrService {

    /**
     * 게시글 목록 조회
     *
     * @param param
     * @return
     * @throws Exception
     */
    List<? extends BoardMgrPostVo> findBoardMgrPostList(BoardDto param) throws Exception;

    /**
     * 사용자 작성 게시물 갯수 조회
     *
     * @param userId
     * @return
     * @throws Exception
     */
    int boardPostCountByUserId(Long userId) throws Exception;

    /**
     * 사용자 작성 댓글 갯수 조회
     *
     * @param userId
     * @return
     * @throws Exception
     */
    int boardCommentCountByUserId(Long userId) throws Exception;

    /**
     * 게시글 상세 조회
     *
     * @param postId
     * @return
     * @throws Exception
     */
    BoardPostWrapper<?> findBoardMgrPost(BoardDto param, Long postId) throws Exception;


    /**
     * 댓글 숨김상태 변경
     *
     * @param boardCommentVo
     * @return
     * @throws Exception
     */
    int boardCommentDisplayUpdate(BoardCommentVo boardCommentVo) throws Exception;

    /**
     * 게시글 숨김상태 변경
     *
     * @param boardPostVo
     * @return
     * @throws Exception
     */
    int boardPostBlindYnUpdate(BoardPostVo boardPostVo) throws Exception;


    /**
     * 게시글 모니터링 읽음 처리
     *
     * @param boardPostReadVo
     * @return
     * @throws Exception
     */
    int setPostRead(BoardPostReadVo boardPostReadVo) throws Exception;


    /**
     * 금칙어 삭제처리
     * 
     * @param commonCode
     * @return
     * @throws Exception
     */
    int boardWordUseYnUpdate(CommonCode commonCode) throws Exception;

    List<BoardCommentVo> getPostCommentList(BoardDto param) throws Exception;



}
