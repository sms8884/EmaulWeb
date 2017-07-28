/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_post <br />
 *         게시글 모니터링용 POST VO
 */
@Alias(value = "BoardMgrPostVo")
public class BoardMgrPostVo extends BoardPostVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 2411039958291359594L;

    /** 카테고리 명 */
    private String categoryName;

    /** 댓글 첫번재 */
    private String commentContent;

    /** 댓글 첫번째 아이디 */
    private Long commentId;

    // 임시 페이지 아규먼트 (find 에서 안들어옴.......)
    private int pageNum;
    private int pageSize;

    /** 게시글 읽음처리 조회 */
    private Long postReadId;



    /**
     * @return the postReadId
     */
    public Long getPostReadId() {
        return postReadId;
    }

    /**
     * @param postReadId the postReadId to set
     */
    public void setPostReadId(Long postReadId) {
        this.postReadId = postReadId;
    }

    /**
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the commentContent
     */
    public String getCommentContent() {
        return commentContent;
    }

    /**
     * @param commentContent the commentContent to set
     */
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
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
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}
