/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called stat_sharer
 */
@Alias(value = "StatSharerVo")
public class StatSharerVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 7567683223968002783L;

    /** 게시글일련번호 */
    private Long postId;

    /** 공유자아이디 */
    private Long userId;

    /** 공유일시 */
    private Date sharedDate;

    /** SNS종류(FACEBOOK, KAKAOTALK 등) */
    private String sns;

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the sharedDate
     */
    public Date getSharedDate() {
        return sharedDate;
    }

    /**
     * @param sharedDate the sharedDate to set
     */
    public void setSharedDate(Date sharedDate) {
        this.sharedDate = sharedDate;
    }

    /**
     * @return the sns
     */
    public String getSns() {
        return sns;
    }

    /**
     * @param sns the sns to set
     */
    public void setSns(String sns) {
        this.sns = sns;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
