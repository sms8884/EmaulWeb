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
 *         This Domain class mapped db-table called board_post_maulnews
 */
@Alias(value = "BoardPostMaulnewsVo")
public class BoardPostMaulnewsVo extends BoardPostVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 7359112421887517758L;

    /** 게시글일련번호 */
    private Long postId;

    /** 뉴스구분(CODE:NEWS_TYPE) */
    private String newsType;

    /** 뉴스카테고리(CODE:NEWS_CTG) */
    private String newsCategory;

    /** 잠금화면출력여부 */
    private String slideYn;

    /** 성별(CODE:GENDER) */
    private String gender;

    /** 나이(CODE:AGE) */
    private String age;

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
     * @return the newsType
     */
    public String getNewsType() {
        return newsType;
    }

    /**
     * @param newsType the newsType to set
     */
    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    /**
     * @return the newsCategory
     */
    public String getNewsCategory() {
        return newsCategory;
    }

    /**
     * @param newsCategory the newsCategory to set
     */
    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    /**
     * @return the slideYn
     */
    public String getSlideYn() {
        return slideYn;
    }

    /**
     * @param slideYn the slideYn to set
     */
    public void setSlideYn(String slideYn) {
        this.slideYn = slideYn;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the age
     */
    public String getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(String age) {
        this.age = age;
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
