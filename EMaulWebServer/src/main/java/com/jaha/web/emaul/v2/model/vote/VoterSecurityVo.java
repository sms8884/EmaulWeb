package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called voter_security
 */
@Alias(value = "VoterSecurityVo")
public class VoterSecurityVo implements Serializable {

    private static final long serialVersionUID = -1335352931809030005L;

    /** UUID */
    private String viId;
    /** 투표 아이디 */
    private Long voteId;
    /** 투표내용 - 암호화 */
    private String itemIdEnc;
    /** 투표 항목 아이디 */
    private Long itemId;
    /** 투표 항목 파일이름 - 검증용 */
    private String itemIdChkFname;
    /** 투표일 */
    private String regDt;

    /**
     * @return the viId
     */
    public String getViId() {
        return viId;
    }

    /**
     * @param viId the viId to set
     */
    public void setViId(String viId) {
        this.viId = viId;
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

    /**
     * @return the itemIdEnc
     */
    public String getItemIdEnc() {
        return itemIdEnc;
    }

    /**
     * @param itemIdEnc the itemIdEnc to set
     */
    public void setItemIdEnc(String itemIdEnc) {
        this.itemIdEnc = itemIdEnc;
    }

    /**
     * @return the itemId
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the itemIdChkFname
     */
    public String getItemIdChkFname() {
        return itemIdChkFname;
    }

    /**
     * @param itemIdChkFname the itemIdChkFname to set
     */
    public void setItemIdChkFname(String itemIdChkFname) {
        this.itemIdChkFname = itemIdChkFname;
    }

    /**
     * @return the regDt
     */
    public String getRegDt() {
        return regDt;
    }

    /**
     * @param regDt the regDt to set
     */
    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }


}
