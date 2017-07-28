/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post
 */
@Mapper
public interface BoardPostMapper {

    /**
     * board_post에 데이타를 입력한다.
     */
    public void insertBoardPost(BoardPostVo param);

    /**
     * board_post의 데이타를 수정한다.
     */
    public void updateBoardPost(BoardPostVo param);

    /**
     * board_post 노출여부 수정
     */
    public void updateDisplayYn(BoardPostVo param);

    /**
     * board_post의 상세 데이타를 조회한다.
     */
    public BoardPostVo selectBoardPost(Long param);

    /**
     * board_post의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostListCount(BoardDto param);

    /**
     * board_post의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostVo> selectBoardPostList(BoardDto param);

    /**
     * board_post 조회수 증가
     */
    public void updateViewCount(Long postId);

    /**
     * board_post 상단 고정 수정
     */
    public void updateTopFix(BoardPostVo param);

    /**
     * board_post 차단
     */
    public void updateBlocked(BoardPostVo param);

    /**
     * 댓글수 감소
     */
    public void updateCommentCount(Long param);

    /**
     * 이미지 삭제 시 이미지 카운트 감소
     */
    public void updateImageCount(BoardPostVo param);

    /**
     * 선택한 첨부파일 삭제
     */
    public void updateAttachFileNull(BoardPostVo param);

    /**
     * 게시물 댓글 표시여부 수정
     */
    public void updateCommentDisplayYn(BoardPostVo param);

    /**
     * 숨김 처리여부 수정
     */
    public void updateBlindYn(BoardPostVo param);

}
