/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 11. 18.
 */
package com.jaha.web.emaul.v2.model.board;

/**
 * <pre>
 * Class Name : BoardPostWrapper.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 11. 18.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 11. 18.
 * @version 1.0
 */
public class BoardPostWrapper<T extends BoardPostVo> {

    private T boardPost;

    public BoardPostWrapper(T boardPost) {
        this.boardPost = boardPost;
    }

    public T get() {
        return boardPost;
    }

}
