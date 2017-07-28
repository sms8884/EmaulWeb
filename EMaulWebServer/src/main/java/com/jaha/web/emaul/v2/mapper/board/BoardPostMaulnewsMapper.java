/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostMaulnewsVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post_maulnews
 */
@Mapper
public interface BoardPostMaulnewsMapper {

    /**
     * board_post_maulnews에 데이타를 입력한다.
     */
    public void insertBoardPostMaulnews(BoardPostMaulnewsVo param);

    /**
     * board_post_maulnews에 데이타를 수정한다.
     */
    public void updateBoardPostMaulnews(BoardPostMaulnewsVo param);

    /**
     * board_post_maulnews의 데이타를 삭제한다.
     */
    public void deleteBoardPostMaulnews(Long param);

    /**
     * board_post_maulnews의 상세 데이타를 조회한다.
     */
    public BoardPostMaulnewsVo selectBoardPostMaulnews(Long param);

    /**
     * board_post_maulnews의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostMaulnewsListCount(BoardDto param);

    /**
     * board_post_maulnews의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostMaulnewsVo> selectBoardPostMaulnewsList(BoardDto param);

}
