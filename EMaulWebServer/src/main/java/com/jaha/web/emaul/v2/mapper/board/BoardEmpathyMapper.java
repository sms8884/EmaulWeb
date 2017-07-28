/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardEmpathyVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_empathy
 */
@Mapper
public interface BoardEmpathyMapper {

    /**
     * board_empathy에 데이타를 입력한다.
     */
    public void insertBoardEmpathy(BoardEmpathyVo param);

    /**
     * board_empathy에 데이타를 수정한다.
     */
    public void updateBoardEmpathy(BoardEmpathyVo param);

    /**
     * board_empathy의 데이타를 삭제한다.
     */
    public void deleteBoardEmpathy(Long param);

    /**
     * board_empathy의 상세 데이타를 조회한다.
     */
    public BoardEmpathyVo selectBoardEmpathy(Long param);

    /**
     * board_empathy의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardEmpathyListCount(BoardDto param);

    /**
     * board_empathy의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardEmpathyVo> selectBoardEmpathyList(BoardDto param);

}
