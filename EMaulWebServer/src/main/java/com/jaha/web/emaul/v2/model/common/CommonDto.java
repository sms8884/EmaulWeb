/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 13.
 */
package com.jaha.web.emaul.v2.model.common;

import java.io.Serializable;

import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : CommonDto.java
 * Description : 공통 Dto
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 13.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 13.
 * @version 1.0
 */
public class CommonDto implements Serializable {

    /** SID */
    private static final long serialVersionUID = 4026539935212986947L;

    /** 페이징 */
    private Paging pagingHelper;

    /** 읽음글 조회용 사용자 아이디 */
    private Long readUserId;

    /** 사용자 아이디 */
    private Long searchUserId;

    /** 하우스(세대) 아이디 */
    private Long searchHouseId;

    /** 사용자 아파트 아이디 */
    private Long searchAptId;

    // /** 사용자 데이터 */
    // private User user;

    private LoginSession loginSession;

    /**
     * @return the readUserId
     */
    public Long getReadUserId() {
        return readUserId;
    }

    /**
     * @param readUserId the readUserId to set
     */
    public void setReadUserId(Long readUserId) {
        this.readUserId = readUserId;
    }

    public Paging getPagingHelper() {
        if (pagingHelper == null) {
            pagingHelper = new PagingHelper();
            pagingHelper.setStartNum(0);
            pagingHelper.setEndNum(100);
        }
        return pagingHelper;
    }

    public void setPagingHelper(Paging pagingHelper) {
        this.pagingHelper = pagingHelper;
    }

    public Long getSearchUserId() {
        return searchUserId;
    }

    public void setSearchUserId(Long searchUserId) {
        this.searchUserId = searchUserId;
    }

    public Long getSearchHouseId() {
        return searchHouseId;
    }

    public void setSearchHouseId(Long searchHouseId) {
        this.searchHouseId = searchHouseId;
    }

    public Long getSearchAptId() {
        return searchAptId;
    }

    public void setSearchAptId(Long searchAptId) {
        this.searchAptId = searchAptId;
    }

    public LoginSession getLoginSession() {
        return loginSession;
    }

    public void setLoginSession(LoginSession loginSession) {
        this.loginSession = loginSession;
    }

    // public User getUser() {
    // return user;
    // }
    //
    // public void setUser(User user) {
    // this.user = user;
    // }

}
