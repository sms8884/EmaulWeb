package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote_item
 */
@Alias(value = "VoteItemVo")
public class VoteItemVo implements Serializable {

    private static final long serialVersionUID = -3922195902596422116L;

    /** 투표항목 아이디 */
    private Long id;
    /** 투표 아이디 : vote.id */
    private Long parentId;
    private Integer imageCount;
    private Boolean isSubjective; // 설문용
    /** 투표 항목 */
    private String title;
    private String commitment;
    private String profile;

    /** 투표 결과보기용 항목별 통계계산용 (DB컬럼 아님) */
    private int onlineCount; // 투표자수
    private int offlineCount;// 오프라인 투표자수
    private String voteRate; // 투표비율
    private Boolean isWinner; // 당선여부



    /**
     * @return the isWinner
     */
    public Boolean getIsWinner() {
        return isWinner;
    }

    /**
     * @param isWinner the isWinner to set
     */
    public void setIsWinner(Boolean isWinner) {
        this.isWinner = isWinner;
    }

    /**
     * @return the voteRate
     */
    public String getVoteRate() {
        return voteRate;
    }

    /**
     * @param voteRate the voteRate to set
     */
    public void setVoteRate(String voteRate) {
        this.voteRate = voteRate;
    }

    /**
     * @return the onlineCount
     */
    public int getOnlineCount() {
        return onlineCount;
    }

    /**
     * @param onlineCount the onlineCount to set
     */
    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    /**
     * @return the offlineCount
     */
    public int getOfflineCount() {
        return offlineCount;
    }

    /**
     * @param offlineCount the offlineCount to set
     */
    public void setOfflineCount(int offlineCount) {
        this.offlineCount = offlineCount;
    }

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
     * @return the parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the imageCount
     */
    public Integer getImageCount() {
        return imageCount;
    }

    /**
     * @param imageCount the imageCount to set
     */
    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    /**
     * @return the isSubjective
     */
    public Boolean getIsSubjective() {
        return isSubjective;
    }

    /**
     * @param isSubjective the isSubjective to set
     */
    public void setIsSubjective(Boolean isSubjective) {
        this.isSubjective = isSubjective;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the commitment
     */
    public String getCommitment() {
        return commitment;
    }

    /**
     * @param commitment the commitment to set
     */
    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }



}
