package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote_key
 */
@Alias(value = "VoteKeyVo")
public class VoteKeyVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 6529510329892599102L;

    public final static int KEY_STATUS_WAIT = 1;
    public final static int KEY_STATUS_LINK = 2;
    public final static int KEY_STATUS_START = 3;
    public final static int KEY_STATUS_END = 4;

    /** 투표 보안키 아이디 */
    private Long vkId;
    /** 키 이름 */
    private String keyName;
    /** 아파트 아이디 */
    private Long aptId;
    /** 키 유효 시작일 (투표 시작일) */
    private Date startDt;
    /** 키 종료일 (투표 종료일) */
    private Date endDt;
    /** 사용여부 */
    private String useYn;
    /** 선거관리 위원장 이름 */
    private String adminName;
    /** 선거관리 위원장 이메일 */
    private String adminEmail;
    /** 투표 복호화 키 : 사용불가 -- 복호화 시 keyGrantDec 사용 */
    private String keyVoteDec;
    /** 투표 암호화 키 */
    private String keyVoteEnc;
    /** 투표 검증용 복호화 키 (암호화) */
    private String keyCheckDec;
    /** 투표 검증용 암호화 키 */
    private String keyCheckEnc;
    /** 선관위원 키 1 */
    private String keyBase1;
    /** 선관위원 키 2 */
    private String keyBase2;
    /** 선관위원 키 3 */
    private String keyBase3;
    /** 선관위원 키 1 생성자 명 */
    private String keyBase1Uname;
    /** 선관위원 키 2 생성자 명 */
    private String keyBase2Uname;
    /** 선관위원 키 3 생성자 명 */
    private String keyBase3Uname;
    /** 키 생성 서명파일 명 */
    private String createSignFname;
    /** 키 생성 서명파일 명 */
    private String grantSignFname;
    /** 검증용 요청 서명파일 명 */
    private String checkSignFname;
    /** 투표 복호화 키 (복호화된 키 : 개표 처리시 사용) */
    private String keyGrantDec;
    /** 키 승인 여부 (개표 승인) : Y - 복호화키 전송됨 / N - 복호화키 미전송 */
    private String keyGrantYn;
    /** 등록일 YYYY-mm-dd */
    private String regDt;
    /** 등록시간 HH:mm:ss */
    private String regTm;
    /** 업데이트 일자 */
    private Date uptDt;
    /** 보안등급 : S, A - 3명인증, B - 1명 인증 */
    private String keyLevel;

    /** DB필드 아님 */
    private int keyStatus;
    /** DB필드 아님 */
    private int openStatus = 0; // 개표 여부

    /** 아파트명 (DB필드 아님) */
    private String aptName;



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
     * @return the vkId
     */
    public Long getVkId() {
        return vkId;
    }

    /**
     * @param vkId the vkId to set
     */
    public void setVkId(Long vkId) {
        this.vkId = vkId;
    }

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName the keyName to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
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
     * @return the startDt
     */
    public Date getStartDt() {
        return startDt;
    }

    /**
     * @param startDt the startDt to set
     */
    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    /**
     * @return the endDt
     */
    public Date getEndDt() {
        return endDt;
    }

    /**
     * @param endDt the endDt to set
     */
    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    /**
     * @return the useYn
     */
    public String getUseYn() {
        return useYn;
    }

    /**
     * @param useYn the useYn to set
     */
    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    /**
     * @return the adminName
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * @param adminName the adminName to set
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * @return the adminEmail
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     * @param adminEmail the adminEmail to set
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * @return the keyVoteDec
     */
    public String getKeyVoteDec() {
        return keyVoteDec;
    }

    /**
     * @param keyVoteDec the keyVoteDec to set
     */
    public void setKeyVoteDec(String keyVoteDec) {
        this.keyVoteDec = keyVoteDec;
    }

    /**
     * @return the keyVoteEnc
     */
    public String getKeyVoteEnc() {
        return keyVoteEnc;
    }

    /**
     * @param keyVoteEnc the keyVoteEnc to set
     */
    public void setKeyVoteEnc(String keyVoteEnc) {
        this.keyVoteEnc = keyVoteEnc;
    }

    /**
     * @return the keyCheckDec
     */
    public String getKeyCheckDec() {
        return keyCheckDec;
    }

    /**
     * @param keyCheckDec the keyCheckDec to set
     */
    public void setKeyCheckDec(String keyCheckDec) {
        this.keyCheckDec = keyCheckDec;
    }

    /**
     * @return the keyCheckEnc
     */
    public String getKeyCheckEnc() {
        return keyCheckEnc;
    }

    /**
     * @param keyCheckEnc the keyCheckEnc to set
     */
    public void setKeyCheckEnc(String keyCheckEnc) {
        this.keyCheckEnc = keyCheckEnc;
    }

    /**
     * @return the keyBase1
     */
    public String getKeyBase1() {
        return keyBase1;
    }

    /**
     * @param keyBase1 the keyBase1 to set
     */
    public void setKeyBase1(String keyBase1) {
        this.keyBase1 = keyBase1;
    }

    /**
     * @return the keyBase2
     */
    public String getKeyBase2() {
        return keyBase2;
    }

    /**
     * @param keyBase2 the keyBase2 to set
     */
    public void setKeyBase2(String keyBase2) {
        this.keyBase2 = keyBase2;
    }

    /**
     * @return the keyBase3
     */
    public String getKeyBase3() {
        return keyBase3;
    }

    /**
     * @param keyBase3 the keyBase3 to set
     */
    public void setKeyBase3(String keyBase3) {
        this.keyBase3 = keyBase3;
    }

    /**
     * @return the keyBase1Uname
     */
    public String getKeyBase1Uname() {
        return keyBase1Uname;
    }

    /**
     * @param keyBase1Uname the keyBase1Uname to set
     */
    public void setKeyBase1Uname(String keyBase1Uname) {
        this.keyBase1Uname = keyBase1Uname;
    }

    /**
     * @return the keyBase2Uname
     */
    public String getKeyBase2Uname() {
        return keyBase2Uname;
    }

    /**
     * @param keyBase2Uname the keyBase2Uname to set
     */
    public void setKeyBase2Uname(String keyBase2Uname) {
        this.keyBase2Uname = keyBase2Uname;
    }

    /**
     * @return the keyBase3Uname
     */
    public String getKeyBase3Uname() {
        return keyBase3Uname;
    }

    /**
     * @param keyBase3Uname the keyBase3Uname to set
     */
    public void setKeyBase3Uname(String keyBase3Uname) {
        this.keyBase3Uname = keyBase3Uname;
    }

    /**
     * @return the createSignFname
     */
    public String getCreateSignFname() {
        return createSignFname;
    }

    /**
     * @param createSignFname the createSignFname to set
     */
    public void setCreateSignFname(String createSignFname) {
        this.createSignFname = createSignFname;
    }

    /**
     * @return the grantSignFname
     */
    public String getGrantSignFname() {
        return grantSignFname;
    }

    /**
     * @param grantSignFname the grantSignFname to set
     */
    public void setGrantSignFname(String grantSignFname) {
        this.grantSignFname = grantSignFname;
    }

    /**
     * @return the checkSignFname
     */
    public String getCheckSignFname() {
        return checkSignFname;
    }

    /**
     * @param checkSignFname the checkSignFname to set
     */
    public void setCheckSignFname(String checkSignFname) {
        this.checkSignFname = checkSignFname;
    }

    /**
     * @return the keyGrantDec
     */
    public String getKeyGrantDec() {
        return keyGrantDec;
    }

    /**
     * @param keyGrantDec the keyGrantDec to set
     */
    public void setKeyGrantDec(String keyGrantDec) {
        this.keyGrantDec = keyGrantDec;
    }

    /**
     * @return the keyGrantYn
     */
    public String getKeyGrantYn() {
        return keyGrantYn;
    }

    /**
     * @param keyGrantYn the keyGrantYn to set
     */
    public void setKeyGrantYn(String keyGrantYn) {
        this.keyGrantYn = keyGrantYn;
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

    /**
     * @return the regTm
     */
    public String getRegTm() {
        return regTm;
    }

    /**
     * @param regTm the regTm to set
     */
    public void setRegTm(String regTm) {
        this.regTm = regTm;
    }

    /**
     * @return the uptDt
     */
    public Date getUptDt() {
        return uptDt;
    }

    /**
     * @param uptDt the uptDt to set
     */
    public void setUptDt(Date uptDt) {
        this.uptDt = uptDt;
    }

    /**
     * @return the keyLevel
     */
    public String getKeyLevel() {
        return keyLevel;
    }

    /**
     * @param keyLevel the keyLevel to set
     */
    public void setKeyLevel(String keyLevel) {
        this.keyLevel = keyLevel;
    }

    /**
     * @return the keyStatus
     */
    public int getKeyStatus() {
        return keyStatus;
    }

    /**
     * @param keyStatus the keyStatus to set
     */
    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    /**
     * @return the openStatus
     */
    public int getOpenStatus() {
        return openStatus;
    }

    /**
     * @param openStatus the openStatus to set
     */
    public void setOpenStatus(int openStatus) {
        this.openStatus = openStatus;
    }

}
