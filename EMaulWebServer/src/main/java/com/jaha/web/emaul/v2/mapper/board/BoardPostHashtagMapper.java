/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostHashtagVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called board_post_hashtag
 */
@Mapper
public interface BoardPostHashtagMapper {

    /**
     * board_post_hashtag에 데이타를 입력한다.
     */
    public void insertBoardPostHashtag(BoardPostHashtagVo param);

    /**
     * board_post_hashtag에 데이타를 수정한다.
     */
    public void updateBoardPostHashtag(BoardPostHashtagVo param);

    /**
     * board_post_hashtag의 데이타를 삭제한다.
     */
    public void deleteBoardPostHashtag(Long param);

    /**
     * board_post_hashtag의 상세 데이타를 조회한다.
     */
    public BoardPostHashtagVo selectBoardPostHashtag(Long param);

    /**
     * board_post_hashtag의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectBoardPostHashtagListCount(BoardDto param);

    /**
     * board_post_hashtag의 목록을 검색조건에 맞게 조회한다.
     */
    public List<BoardPostHashtagVo> selectBoardPostHashtagList(BoardDto param);

}
