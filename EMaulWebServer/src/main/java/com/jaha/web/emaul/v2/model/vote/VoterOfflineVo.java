package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called voter_offline
 */
@Alias(value = "VoterOfflineVo")
public class VoterOfflineVo implements Serializable {

    private static final long serialVersionUID = -8755705561328236324L;

    /** 투표 일련번호 */
    private Long id;
    /** 아파트 아이디 */
    private Long aptId;
    /** 아파트 동 */
    private String dong;
    /** 투표자명 */
    private String fullName;
    /** 아파트 호 */
    private String ho;
    /** 투표일 */
    private Date regDate;
    /** 투표 아이디 */
    private Long voteId;

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
     * @return the aptId
     */
    public Long getAptId() {
        return aptId;
    }

    /**
     * @param aptId the aptId to set
     */
    public void setAptId(Long aptId) {
        this.aptId = aptId;
    }

    /**
     * @return the dong
     */
    public String getDong() {
        return dong;
    }

    /**
     * @param dong the dong to set
     */
    public void setDong(String dong) {
        this.dong = dong;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the ho
     */
    public String getHo() {
        return ho;
    }

    /**
     * @param ho the ho to set
     */
    public void setHo(String ho) {
        this.ho = ho;
    }

    /**
     * @return the regDate
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the voteId
     */
    public Long getVoteId() {
        return voteId;
    }

    /**
     * @param voteId the voteId to set
     */
    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }


}
