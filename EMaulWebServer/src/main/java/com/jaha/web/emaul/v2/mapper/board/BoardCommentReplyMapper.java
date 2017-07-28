/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_comment_reply
 */
@Mapper
public interface BoardCommentReplyMapper {

    /**
     * board_comment_reply에 데이타를 입력한다.
     */
    public void insertBoardCommentReply(BoardCommentReplyVo param);

    /**
     * board_comment_reply에 데이타를 수정한다.
     */
    public void updateBoardCommentReply(BoardCommentReplyVo param);

    /**
     * board_comment_reply 노출여부 수정
     */
    public void updateDisplayYn(BoardCommentReplyVo param);

    /**
     * board_comment_reply의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardCommentReplyVo> selectBoardCommentReplyList(BoardDto param);

    /**
     * board_comment_reply 차단
     */
    public void updateBlocked(BoardCommentReplyVo param);

}
