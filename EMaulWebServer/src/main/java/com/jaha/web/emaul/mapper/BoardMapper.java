package com.jaha.web.emaul.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;

/**
 * Created by shavrani on 16-10-25
 */
@Mapper
public interface BoardMapper {

    int insertBoardCategory(BoardCategory boardCategory);

    int insertBoardCategoryVo(BoardCategoryVo boardCategoryVo);

    int updateBoardCategoryVo(BoardCategoryVo boardCategoryVo);

    BoardCategoryVo selectBoardCategory(Long id);

}
