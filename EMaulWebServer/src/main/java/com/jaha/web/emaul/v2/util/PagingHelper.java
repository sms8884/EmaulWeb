/**
 *
 */
package com.jaha.web.emaul.v2.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jaha.web.emaul.v2.model.common.Paging;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 * @설명 : 페이징 Helper
 */
public class PagingHelper extends Paging {

    /** SID */
    private static final long serialVersionUID = 3864334909507070327L;

    /** 기본 생성자 */
    public PagingHelper() {

    }

    /**
     * 페이징(DB Query할 목록의) 끝 번호를 반환한다.
     *
     * @return
     */
    public int calcEndNum() {
        // return super.getPageNum() * super.getPageSize(); // 오라클
        return super.getPageSize(); // 마이SQL
    }

    /**
     * 페이징(DB Query할 목록의) 시작 번호를 반환한다.
     *
     * @return
     */
    public int calcStartNum() {
        return ((super.getPageNum() - 1) * super.getPageSize());
    }

    /** 목록의 시작번호 */
    private long listStartNum;
    /** 총 페이지 수 */
    private int totalPageNum;
    /** 페이지 네비의 시작번호 */
    private int startPageBlockNum;
    /** 페이지 네비의 끝번호 */
    private int endPageBlockNum;
    /** 이전 페이지 존재 여부 */
    private boolean previousPage;
    /** 다음 페이지 존재 여부 */
    private boolean nextPage;

    /**
     * UI화면(JSP 또는 HTML)의 시작 번호를 반환한다.
     *
     * @return
     */
    public long getListStartNum() {
        this.listStartNum = super.getTotalRecordCount() - (super.getPageSize() * (super.getPageNum() - 1));
        return this.listStartNum;
    }

    /**
     * 전체 페이지 수를 반환한다.
     *
     * @return
     */
    public int getTotalPageNum() {
        this.totalPageNum = (int) (Math.ceil((double) super.getTotalRecordCount() / (double) super.getPageSize()));
        return this.totalPageNum;
    }

    /**
     * 페이지 블락 시작번호를 반환한다.
     *
     * @return
     */
    public int getStartPageBlockNum() {
        this.startPageBlockNum = super.getPageNum() / super.getPageBlockSize() + 1;

        if (super.getPageNum() % super.getPageBlockSize() == 0) {
            this.startPageBlockNum--;
        }

        this.startPageBlockNum = (this.startPageBlockNum - 1) * super.getPageBlockSize() + 1;

        return this.startPageBlockNum;
    }

    /**
     * 페이지 블락 끝번호를 반환한다.
     *
     * @return
     */
    public int getEndPageBlockNum() {
        this.endPageBlockNum = this.startPageBlockNum + super.getPageBlockSize() - 1;

        // 시작페이지 + pageBlockSize(5 또는 10 또는 기타 등등)이 마지막 페이지 보다 클경우
        if (this.endPageBlockNum > this.totalPageNum) {
            this.endPageBlockNum = this.totalPageNum;
        }

        return this.endPageBlockNum;
    }

    /**
     * 이전 페이지 존재유무를 반환한다.
     *
     * @return
     */
    public boolean isPreviousPage() {
        this.previousPage = (this.totalPageNum > 1 && super.getPageNum() > 1) ? true : false;
        return this.previousPage;
    }

    /**
     * 다음 페이지 존재유무를 반환한다.
     *
     * @return
     */
    public boolean isNextPage() {
        this.nextPage = ((this.totalPageNum > 1) && (super.getPageNum() < this.totalPageNum)) ? true : false;
        return this.nextPage;
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
