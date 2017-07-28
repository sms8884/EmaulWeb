/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 11. 4.
 */
package com.jaha.web.emaul.v2.service.board;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.v2.mapper.board.BoardCategoryMapper;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;

/**
 * <pre>
 * Class Name : BoardCategoryServiceImpl.java
 * Description : Description
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
@Service("v2BoardCategoryService")
public class BoardCategoryServiceImpl implements BoardCategoryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoardCategoryMapper boardCategoryMapper;

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.board.BoardCategoryService#findBoardCategoryList(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<BoardCategoryVo> findBoardCategoryList(Long aptId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.board.BoardCategoryService#findBoardCategory(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardCategoryVo findBoardCategory(Long boardCategoryId) throws Exception {
        return this.boardCategoryMapper.selectBoardCategory(boardCategoryId);
    }

}
