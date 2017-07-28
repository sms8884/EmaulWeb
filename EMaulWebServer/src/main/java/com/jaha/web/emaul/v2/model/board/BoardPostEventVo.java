/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_post_event
 */
@Alias(value = "BoardPostEventVo")
public class BoardPostEventVo extends BoardPostRangeVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = -9057899014681191512L;

    /** 게시글일련번호 */
    private Long postId;

    /** 이벤트 시작일자(12자리, 연월일시분) */
    private String startDate;

    /** 이벤트 종료일자(12자리, 연월일시분) */
    private String endDate;

    /** 제목 카테고리 */
    private String titleCategory;

    /** 제목색상 */
    private String titleColor;

    /** 제목볼드여부 */
    private String titleBoldYn;

    /** 종료여부 */
    private String endYn;

    /** 종료안내 */
    private String endNotice;

    @Override
    public Long getPostId() {
        return postId;
    }

    @Override
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTitleCategory() {
        return titleCategory;
    }

    public void setTitleCategory(String titleCategory) {
        this.titleCategory = titleCategory;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitleBoldYn() {
        return titleBoldYn;
    }

    public void setTitleBoldYn(String titleBoldYn) {
        this.titleBoldYn = titleBoldYn;
    }

    public String getEndYn() {
        return endYn;
    }

    public void setEndYn(String endYn) {
        this.endYn = endYn;
    }

    public String getEndNotice() {
        return endNotice;
    }

    public void setEndNotice(String endNotice) {
        this.endNotice = endNotice;
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
