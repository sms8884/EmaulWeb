/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_comment
 */
@Alias(value = "BoardCommentVo")
public class BoardCommentVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = -1718270500353303868L;

    /** 댓글일련번호 */
    private Long id;

    /** 게시글일련번호 */
    private Long postId;

    /** 댓글내용 */
    private String content;

    /** 차단여부 */
    private Boolean blocked;

    /** 답글횟수 */
    private Integer replyCount;

    /** 노출여부 */
    private String displayYn;

    /** 요청IP */
    private String reqIp;

    /** 등록자아이디 */
    private Long userId;

    /** 등록일시 */
    private Date regDate;

    /** 수정자아이디 */
    private Long modId;

    /** 수정일시 */
    private Date modDate;

    /** 테이블칼럼아님, 댓글 등록자명 */
    private String fullName;

    /** 테이블칼럼아님, 댓글 등록자 닉네임 */
    private String nickname;

    /** 테이블칼럼아님, 댓글 등록자 동 */
    private String dong;

    /** 테이블칼럼아님, 댓글 등록자 호 */
    private String ho;

    /** 테이블칼럼아님, 댓글 아파트이름 */
    private String aptName;

    /** 테이블칼럼아님, 댓글 읽음처리여부ID */
    private String readId;

    /** 댓글 달린 게시판이름 */
    private String categoryName;

    /** 댓글 게시물의 제목 */
    private String postTitle;

    /** 댓글 일련번호 Array (리스트 체크박스용) */
    private Long[] ids;


    /** 게시글 댓글의 답글 목록 */
    private List<BoardCommentReplyVo> boardCommentReplyList;

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public String getDisplayYn() {
        return displayYn;
    }

    public void setDisplayYn(String displayYn) {
        this.displayYn = displayYn;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Long getModId() {
        return modId;
    }

    public void setModId(Long modId) {
        this.modId = modId;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public List<BoardCommentReplyVo> getBoardCommentReplyList() {
        return boardCommentReplyList;
    }

    public void setBoardCommentReplyList(List<BoardCommentReplyVo> boardCommentReplyList) {
        this.boardCommentReplyList = boardCommentReplyList;
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

    /**
     * @return the aptName
     */
    public String getAptName() {
        return aptName;
    }

    /**
     * @param aptName the aptName to set
     */
    public void setAptName(String aptName) {
        this.aptName = aptName;
    }

    /**
     * @return the readId
     */
    public String getReadId() {
        return readId;
    }

    /**
     * @param readId the readId to set
     */
    public void setReadId(String readId) {
        this.readId = readId;
    }

    /**
     * @return the ids
     */
    public Long[] getIds() {
        return ids;
    }

    /**
     * @param ids the ids to set
     */
    public void setIds(Long[] ids) {
        this.ids = ids;
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
