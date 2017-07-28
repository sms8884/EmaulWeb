/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.board;

import java.util.List;

import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;

/**
 * <pre>
 * Class Name : BoardService.java
 * Description : 게시판 서비스
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
public interface BoardService {

    ////////////////////////////////////////////////////////////////////////////////// 공지사항/커뮤니티 //////////////////////////////////////////////////////////////////////////////////
    /**
     * 게시글 목록 조회
     *
     * @param param
     * @return
     * @throws Exception
     */
    List<? extends BoardPostVo> findBoardPostList(BoardDto param) throws Exception;

    /**
     * 게시글 저장
     *
     * @param param
     * @param boardPost
     * @return
     * @throws Exception
     */
    void regBoardPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception;

    /**
     * 게시글 읽기
     *
     * @param param
     * @param boardPost
     * @param isModifying
     * @return
     * @throws Exception
     */
    BoardPostWrapper<?> findBoardPost(BoardDto param, Long postId, boolean isModifying) throws Exception;

    /**
     * 게시글 수정
     *
     * @param param
     * @param boardPost
     * @return
     * @throws Exception
     */
    void modifyBoardPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception;

    /**
     * 게시글 삭제
     *
     * @param param
     * @param boardPost
     * @return
     * @throws Exception
     */
    void removeBoardPost(BoardDto param, BoardPostVo boardPost) throws Exception;
    ////////////////////////////////////////////////////////////////////////////////// 공지사항/커뮤니티 //////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////// 공통 //////////////////////////////////////////////////////////////////////////////////
    /**
     * 댓글 등록
     *
     * @param param
     * @param boardComment
     * @throws Exception
     */
    void regBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception;

    /**
     * 댓글 수정
     *
     * @param param
     * @param boardComment
     * @throws Exception
     */
    void modifyBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception;

    /**
     * 댓글 삭제
     *
     * @param param
     * @param boardComment
     * @throws Exception
     */
    void removeBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception;

    /**
     * 답글 등록
     *
     * @param param
     * @param boardCommentReply
     * @throws Exception
     */
    void regBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception;

    /**
     * 답글 삭제
     *
     * @param param
     * @param boardCommentReply
     * @throws Exception
     */
    void removeBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception;

    /**
     * 이미지 삭제(이미지 카운트 감소)
     *
     * @param boardPost
     * @throws Exception
     */
    void removeBoardPostImage(BoardPostVo boardPost) throws Exception;

    /**
     * 첨부파일 삭제
     *
     * @param boardPost
     * @param fileName
     * @throws Exception
     */
    void removeBoardPostAttach(BoardPostVo boardPost, String fileName) throws Exception;

    /**
     * 첨부파일 삭제
     *
     * @param postId
     * @param fileKey
     * @throws Exception
     */
    void removeBoardPostAttach(Long postId, Long fileKey) throws Exception;
    ////////////////////////////////////////////////////////////////////////////////// 공통 //////////////////////////////////////////////////////////////////////////////////


    /**
     * 단체관리자용 카테고리별 댓글 목록 조회
     *
     * @param param
     * @return
     * @throws Exception
     */
    List<BoardCommentVo> selectGroupAdminBoardCommentList(BoardDto param) throws Exception;

    /**
     * 예약된 게시글 푸시 발송
     *
     * @throws Exception
     */
    void sendPushReservedPostBatch() throws Exception;


    /**
     * 게시글 댓글 표시여부 수정
     *
     * @param postId
     * @param commentDisplayYn
     */
    void updateCommentDisplayYn(BoardPostVo param);

    /**
     * 게시글 숨김 처리여부 수정
     *
     * @param param
     */
    void updateBlindYn(BoardPostVo param);

    /**
     * 답글 수정
     *
     * @param param
     * @param boardCommentReply
     * @throws Exception
     */
    void modifyBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception;

}
