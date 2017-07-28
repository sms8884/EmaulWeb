/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 11. 4.
 */
package com.jaha.web.emaul.v2.service.board;

import java.util.List;

import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;

/**
 * <pre>
 * Class Name : BoardCategoryService.java
 * Description : 게시판 카테고리 서비스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 11. 4.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 11. 4.
 * @version 1.0
 */
public interface BoardCategoryService {

    ////////////////////////////////////////////////////////////////////////////////// 게시판 카테고리 //////////////////////////////////////////////////////////////////////////////////
    /**
     * 게시판 카테고리 목록 조회
     *
     * @param aptId
     * @return
     * @throws Exception
     */
    List<BoardCategoryVo> findBoardCategoryList(Long aptId) throws Exception;

    /**
     * 게시판 카테고리 상세 조회
     *
     * @param categoryId
     * @return
     * @throws Exception
     */
    BoardCategoryVo findBoardCategory(Long categoryId) throws Exception;
    ////////////////////////////////////////////////////////////////////////////////// 게시판 카테고리 //////////////////////////////////////////////////////////////////////////////////

}
