/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.v2.model.common.CommonDto;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
@Alias(value = "BoardDto")
public class BoardDto extends CommonDto implements Serializable {

    /** SID */
    private static final long serialVersionUID = 5652974626189970920L;

    /** 게시판 카테고리 데이터 */
    private BoardCategoryVo boardCategory;

    /** 게시판 카테고리 아이디 */
    private Long searchCategoryId;

    /** 게시판 게시글 아이디 */
    private Long searchPostId;

    /** 게시판 댓글 아이디 */
    private Long searchCommentId;

    /** 노출여부 */
    private String searchDisplayYn;

    /** 차단여부 */
    private Boolean searchBlocked;

    /** 게시판 목록 상단 고정 여부 */
    private Boolean searchTopFix;

    /** 사진이미지 파일들 */
    private MultipartFile[] imageFiles;

    /** 첨부파일들 */
    private MultipartFile[] attachFiles;

    /** 스크롤 페이지 형식일 경우 마지막 게시글 아이디 */
    private Long lastPostId;

    /** 스크롤 페이지 형식일 경우 마지막 댓글 아이디 */
    private Long lastCommentId;

    /** 파일업로드(파일풀경로형태, 단체게시판, 이벤트게시판 등 신규 게시판의 신규 파일업로드 형태) */
    private String[] uploadedFiles;

    /** DB 정렬 칼럼들 */
    private String sortColumns;

    /** 서브 카테고리 */
    private String searchSubCategory;

    /** 검색 타입 조건 */
    private String type;

    /** 금칙어 목록 */
    private List<CommonCode> codeList;



    /**
     * @return the codeList
     */
    public List<CommonCode> getCodeList() {
        return codeList;
    }

    /**
     * @param codeList the codeList to set
     */
    public void setCodeList(List<CommonCode> codeList) {
        this.codeList = codeList;
    }

    public BoardCategoryVo getBoardCategory() {
        return boardCategory;
    }

    public void setBoardCategory(BoardCategoryVo boardCategory) {
        this.boardCategory = boardCategory;
    }

    public Long getSearchCategoryId() {
        return searchCategoryId;
    }

    public void setSearchCategoryId(Long searchCategoryId) {
        this.searchCategoryId = searchCategoryId;
    }

    public Long getSearchPostId() {
        return searchPostId;
    }

    public void setSearchPostId(Long searchPostId) {
        this.searchPostId = searchPostId;
    }

    public Long getSearchCommentId() {
        return searchCommentId;
    }

    public void setSearchCommentId(Long searchCommentId) {
        this.searchCommentId = searchCommentId;
    }

    public String getSearchDisplayYn() {
        return searchDisplayYn;
    }

    public void setSearchDisplayYn(String searchDisplayYn) {
        this.searchDisplayYn = searchDisplayYn;
    }

    public Boolean getSearchBlocked() {
        return searchBlocked;
    }

    public void setSearchBlocked(Boolean searchBlocked) {
        this.searchBlocked = searchBlocked;
    }

    public Boolean getSearchTopFix() {
        return searchTopFix;
    }

    public void setSearchTopFix(Boolean searchTopFix) {
        this.searchTopFix = searchTopFix;
    }

    public MultipartFile[] getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(MultipartFile[] imageFiles) {
        this.imageFiles = imageFiles;
    }

    public MultipartFile[] getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(MultipartFile[] attachFiles) {
        this.attachFiles = attachFiles;
    }

    public Long getLastPostId() {
        return lastPostId;
    }

    public void setLastPostId(Long lastPostId) {
        this.lastPostId = lastPostId;
    }

    public Long getLastCommentId() {
        return lastCommentId;
    }

    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }

    public String[] getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(String[] uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public String getSortColumns() {
        return sortColumns;
    }

    public void setSortColumns(String sortColumns) {
        this.sortColumns = sortColumns;
    }

    public String getSearchSubCategory() {
        return searchSubCategory;
    }

    public void setSearchSubCategory(String searchSubCategory) {
        this.searchSubCategory = searchSubCategory;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
