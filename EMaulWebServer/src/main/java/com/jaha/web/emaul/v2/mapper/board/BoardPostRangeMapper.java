/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostRangeVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post_range
 */
@Mapper
public interface BoardPostRangeMapper {

    /**
     * board_post_range에 데이타를 입력한다.
     */
    public void insertBoardPostRange(BoardPostRangeVo param);

    /**
     * board_post_range에 데이타를 수정한다.
     */
    public void updateBoardPostRange(BoardPostRangeVo param);

    /**
     * board_post_range의 푸시발송날짜를 수정한다.
     */
    public void updatePushSendDate(Long param);

    /**
     * board_post_range의 상세 데이타를 조회한다.
     */
    public BoardPostRangeVo selectOnlyBoardPostRange(Long param);

    /**
     * board_post + board_post_range의 상세 데이타를 조회한다.
     */
    public BoardPostRangeVo selectBoardPostRange(Long param);

    /**
     * board_post_range의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostRangeListCount(BoardDto param);

    /**
     * board_post_range의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostRangeVo> selectBoardPostRangeList(BoardDto param);

    /**
     * board_post_range의 예약 푸시알림 발송 배치 목록을 조회한다.
     */
    public List<BoardPostRangeVo> selectBoardPostRangeBatchList(String searchOpenDate);

}
