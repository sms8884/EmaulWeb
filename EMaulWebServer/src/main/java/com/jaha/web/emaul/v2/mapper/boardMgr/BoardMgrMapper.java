/**
 *
 */
package com.jaha.web.emaul.v2.mapper.boardMgr;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardMgrPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostReadVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.common.FileInfoVo;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post
 */
@Mapper
public interface BoardMgrMapper {


    /**
     * board_post의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardMgrPostListCount(BoardDto param);

    /**
     * board_post의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardMgrPostVo> selectBoardMgrPostList(BoardDto param);

    /**
     * 숨김글 총 레코드 수
     */
    public int selectBoardBlindPostListCount(BoardDto param);

    /**
     * 숨김글 목록 조회
     */
    public List<BoardMgrPostVo> selectBoardBlindPostList(BoardDto param);

    /**
     * 스펨글 총 레코드 수
     */
    public int selectBoardSpamPostListCount(BoardDto param);

    /**
     * 스펨글 목록 조회
     */
    public List<BoardMgrPostVo> selectBoardSpamPostList(BoardDto param);

    /**
     * 사용자 작성 게시글 갯수 조회
     */
    public int selectBoardPostCountByUserId(Long userId);

    /**
     * 사용자 작성 댓글 갯수 조회
     */
    public int selectBoardCommentCountByUserId(Long userId);

    /**
     * 파일 그룹 조회
     */
    public List<FileInfoVo> getFileGroup(String fileGroupKey);

    /**
     * board_comment의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardCommentListCount(BoardDto param);

    /**
     * board_comment의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardCommentVo> selectBoardCommentList(BoardDto param);

    /**
     * 댓글 숨김 상태 수정 blocked
     */
    public int boardCommentDisplayUpdate(BoardCommentVo boardCommentVo);

    /**
     * 게시글 숨김 상태 수정 blindYn
     */
    public int boardPostBlindYnUpdate(BoardPostVo boardPostVo);

    /**
     * 게시글 총 레코드 수
     */
    public int selectBoardPostListCount(BoardDto param);

    /**
     * 게시글 목록 조회
     */
    public List<BoardMgrPostVo> selectBoardPostList(BoardDto param);


    /**
     * 게시글 읽음처리 조회
     */
    public BoardPostReadVo getPostRead(BoardPostReadVo boardPostReadVo);

    /**
     * 게시글 읽음처리 등록
     */
    public int setPostRead(BoardPostReadVo boardPostReadVo);

    /**
     * 금칙어 삭제 처리
     */
    public int boardWordUseYnUpdate(String[] cdIds);

}
