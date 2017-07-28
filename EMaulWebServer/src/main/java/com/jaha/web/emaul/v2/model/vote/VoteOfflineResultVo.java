package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote_offline_result
 */
@Alias(value = "VoteOfflineResultVo")
public class VoteOfflineResultVo implements Serializable {

    private static final long serialVersionUID = 4657066692124544142L;

    /** 투표 일련버호 */
    private Long voteId;
    /** 아파트 아이디 */
    private Long aptId;
    private String jsonMapVoteItemResult;
    /** 등록일시 */
    private Date regDate;
    private Long regUserId;
    private String resultText;
    private String votersFname;

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
     * @return the jsonMapVoteItemResult
     */
    public String getJsonMapVoteItemResult() {
        return jsonMapVoteItemResult;
    }

    /**
     * @param jsonMapVoteItemResult the jsonMapVoteItemResult to set
     */
    public void setJsonMapVoteItemResult(String jsonMapVoteItemResult) {
        this.jsonMapVoteItemResult = jsonMapVoteItemResult;
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
     * @return the regUserId
     */
    public Long getRegUserId() {
        return regUserId;
    }

    /**
     * @param regUserId the regUserId to set
     */
    public void setRegUserId(Long regUserId) {
        this.regUserId = regUserId;
    }

    /**
     * @return the resultText
     */
    public String getResultText() {
        return resultText;
    }

    /**
     * @param resultText the resultText to set
     */
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    /**
     * @return the votersFname
     */
    public String getVotersFname() {
        return votersFname;
    }

    /**
     * @param votersFname the votersFname to set
     */
    public void setVotersFname(String votersFname) {
        this.votersFname = votersFname;
    }


}
