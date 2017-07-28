/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_post_read
 */
@Alias(value = "BoardPostReadVo")
public class BoardPostReadVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 9068480544381132611L;

    /** 일련번호 */
    private Long id;

    /** 카테고리 아이디 */
    private Long categoryId;

    /** 게시물 아이디 */
    private Long postId;

    /** 댓글 아이디 */
    private Long commentId;

    /** 사용자 아이디 */
    private Long userId;

    /** 읽은 날자 */
    private Date readDate;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

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
     * @return the readDate
     */
    public Date getReadDate() {
        return readDate;
    }

    /**
     * @return the commentId
     */
    public Long getCommentId() {
        return commentId;
    }

    /**
     * @param commentId the commentId to set
     */
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    /**
     * @param readDate the readDate to set
     */
    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }



}
