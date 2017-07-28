/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostEventVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post_event
 */
@Mapper
public interface BoardPostEventMapper {

    /**
     * board_post_event에 데이타를 입력한다.
     */
    public void insertBoardPostEvent(BoardPostEventVo param);

    /**
     * board_post_event에 데이타를 수정한다.
     */
    public void updateBoardPostEvent(BoardPostEventVo param);

    /**
     * board_post_event의 상세 데이타를 조회한다.
     */
    public BoardPostEventVo selectOnlyBoardPostEvent(Long param);

    /**
     * board_post_event의 상세 데이타를 조회한다.
     */
    public BoardPostEventVo selectBoardPostEvent(Long param);

    /**
     * board_post_event의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostEventListCount(BoardDto param);

    /**
     * board_post_event의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostEventVo> selectBoardPostEventList(BoardDto param);

    /**
     * board_post_event의 예약 푸시알림 발송 배치 목록을 조회한다.
     */
    public List<BoardPostEventVo> selectBoardPostEventBatchList(String searchOpenDate);

}
