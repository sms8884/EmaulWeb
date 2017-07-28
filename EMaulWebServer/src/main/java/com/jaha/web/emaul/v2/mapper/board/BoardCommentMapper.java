/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_comment
 */
@Mapper
public interface BoardCommentMapper {

    /**
     * board_comment에 데이타를 입력한다.
     */
    public void insertBoardComment(BoardCommentVo param);

    /**
     * board_comment의 데이타를 수정한다.
     */
    public void updateBoardComment(BoardCommentVo param);

    /**
     * board_comment 노출여부 수정
     */
    public void updateDisplayYn(BoardCommentVo param);

    /**
     * board_comment의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardCommentListCount(BoardDto param);

    /**
     * board_comment의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardCommentVo> selectBoardCommentList(BoardDto param);

    /**
     * board_comment 차단
     */
    public void updateBlocked(BoardCommentVo param);

    /**
     * 답글수 감소
     */
    public void updateReplyCount(Long param);

    /**
     * board_comment 상세 조회
     */
    public BoardCommentVo selectBoardComment(Long param);


    /**
     * 단체관리자 > 댓글 일괄 목록 조회 갯수
     *
     * @param param
     * @return
     */
    public int selectGroupAdminBoardCommentListCount(BoardDto param);

    /**
     * 단체관리자 > 댓글 일괄 목록 조회
     *
     * @param param
     * @return
     */
    public List<BoardCommentVo> selectGroupAdminBoardCommentList(BoardDto param);

    /**
     * 게시글 모니터링> 댓글 조회갯수
     *
     * @param param
     * @return
     */
    public int selectBoardMgrCommentListCount(BoardDto param);

    /**
     * 게시글 모니터링 > 댓글 목록조회
     * 
     * @param param
     * @return
     */
    public List<BoardCommentVo> selectBoardMgrCommentList(BoardDto param);



}
