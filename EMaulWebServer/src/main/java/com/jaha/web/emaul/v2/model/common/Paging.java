/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.model.common;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 * @설명 : 페이징, 메뉴, 로그인 등 공통 VO
 */
public class Paging implements Serializable {

    /** SID */
    private static final long serialVersionUID = -6569747806283501636L;

    /** 페이지 번호 */
    private int pageNum;
    /** 한 페이지 당 목록 수 */
    private int pageSize;
    /** 전체 목록 수 */
    private long totalRecordCount;

    /** 페이징(DB Query할 목록의) 끝 번호 */
    private int startNum;
    /** 페이징(DB Query할 목록의) 시작 번호 */
    private int endNum;

    /** 검색조건 */
    private Search search;
    /** 정렬조건 */
    private List<Sort> sortList;

    /** 목록 URL */
    private String listUrl;
    /** 한 페이지 당 페이징 수 */
    private int pageBlockSize;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public List<Sort> getSortList() {
        return sortList;
    }

    public void setSortList(List<Sort> sortList) {
        this.sortList = sortList;
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    public int getPageBlockSize() {
        return pageBlockSize;
    }

    public void setPageBlockSize(int pageBlockSize) {
        this.pageBlockSize = pageBlockSize;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
