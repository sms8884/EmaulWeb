/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostAirVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post_air
 */
@Mapper
public interface BoardPostAirMapper {

    /**
     * board_post_air에 데이타를 입력한다.
     */
    public void insertBoardPostAir(BoardPostAirVo param);

    /**
     * board_post_air에 데이타를 수정한다.
     */
    public void updateBoardPostAir(BoardPostAirVo param);

    /**
     * board_post_air의 데이타를 삭제한다.
     */
    public void deleteBoardPostAir(Long param);

    /**
     * board_post_air의 상세 데이타를 조회한다.
     */
    public BoardPostAirVo selectBoardPostAir(Long param);

    /**
     * board_post_air의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostAirListCount(BoardDto param);

    /**
     * board_post_air의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostAirVo> selectBoardPostAirList(BoardDto param);

}
