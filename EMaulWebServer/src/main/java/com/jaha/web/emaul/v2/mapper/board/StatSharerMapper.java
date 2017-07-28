/**
 *
 */
package com.jaha.web.emaul.v2.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.StatSharerVo;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Mapper class mapped db-table called stat_sharer
 */
@Mapper
public interface StatSharerMapper {

    /**
     * stat_sharer에 데이타를 입력한다.
     */
    public void insertStatSharer(StatSharerVo param);

    /**
     * stat_sharer에 데이타를 수정한다.
     */
    public void updateStatSharer(StatSharerVo param);

    /**
     * stat_sharer의 데이타를 삭제한다.
     */
    public void deleteStatSharer(Long param);

    /**
     * stat_sharer의 상세 데이타를 조회한다.
     */
    public StatSharerVo selectStatSharer(Long param);

    /**
     * stat_sharer의 총 레코드 수를 검색조건에 맞게 조회한다.
     */
    public int selectStatSharerListCount(BoardDto param);

    /**
     * stat_sharer의 목록을 검색조건에 맞게 조회한다.
     */
    public List<StatSharerVo> selectStatSharerList(BoardDto param);

}
