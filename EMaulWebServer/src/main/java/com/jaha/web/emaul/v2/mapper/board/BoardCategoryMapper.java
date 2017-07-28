/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_category
 */
@Mapper
public interface BoardCategoryMapper {

    /**
     * board_category에 데이타를 입력한다.
     */
    public void insertBoardCategory(BoardCategoryVo param);

    /**
     * board_category에 데이타를 수정한다.
     */
    public void updateBoardCategory(BoardCategoryVo param);

    /**
     * board_category 노출여부 수정
     */
    public void updateDelYn(BoardCategoryVo param);

    /**
     * board_category의 상세 데이타를 조회한다.
     */
    public BoardCategoryVo selectBoardCategory(Long param);

    /**
     * board_category의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardCategoryListCount(BoardDto param);

    /**
     * board_category의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardCategoryVo> selectBoardCategoryList(BoardDto param);

}
