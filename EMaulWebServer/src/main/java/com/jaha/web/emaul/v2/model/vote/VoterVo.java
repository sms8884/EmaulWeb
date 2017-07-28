package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called voter + user + setting
 */
@Alias(value = "VoterVo")
public class VoterVo implements Serializable {

    private static final long serialVersionUID = 1274384814416340335L;

    // ---------- voter
    /** 투표 일련번호 */
    private Long id;
    /** 서명 이미지 */
    private String signImageUri;
    /** 사용자 설명 */
    private String userContent;
    /** 투표일 */
    private Date voteDate;
    /** 사용자 아이디 */
    private Long userId;
    /** 투표 아이디 */
    private Long voteId;
    /** 투표 항목 아이디 */
    private Long voteItemId;
    /** 아파트 아이디 */
    private Long aptId;
    /** 아파트 동 */
    private String dong;
    /** 아파트 호 */
    private String ho;
    /** 대리 투표자 이름 */
    private String mandateVoterName;
    /** 대리 투표 여부 */
    private Boolean mandated; // = false;
    /** 등록 타입 */
    // mobile : 온라인 모바일 투표
    // tablet-off : 오프라인 태블릿 투표
    private String registerType; // = "mobile";
    /** 투표자 명 */
    private String voterName;

    // ---------- setting
    private Boolean notiAlarm; // = true;
    private Boolean notiPostReply; // = true;
    private Boolean notiParcel; // = true;
    private Boolean notiPostCommentReply; // = true;
    /** 아파트관리비수신여부 */
    private Boolean notiFeePush; // = false;
    /** 대기오염알림수신여부 */
    private Boolean notiAirPollution; // = false;
    /** 방문주차알림수신여부 */
    private Boolean notiVisit; // = false;

    // ---------- 투표 결과용 추가 컬럼
    private String phone;


    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the notiAlarm
     */
    public Boolean getNotiAlarm() {
        return notiAlarm;
    }

    /**
     * @param notiAlarm the notiAlarm to set
     */
    public void setNotiAlarm(Boolean notiAlarm) {
        this.notiAlarm = notiAlarm;
    }

    /**
     * @return the notiPostReply
     */
    public Boolean getNotiPostReply() {
        return notiPostReply;
    }

    /**
     * @param notiPostReply the notiPostReply to set
     */
    public void setNotiPostReply(Boolean notiPostReply) {
        this.notiPostReply = notiPostReply;
    }

    /**
     * @return the notiParcel
     */
    public Boolean getNotiParcel() {
        return notiParcel;
    }

    /**
     * @param notiParcel the notiParcel to set
     */
    public void setNotiParcel(Boolean notiParcel) {
        this.notiParcel = notiParcel;
    }

    /**
     * @return the notiPostCommentReply
     */
    public Boolean getNotiPostCommentReply() {
        return notiPostCommentReply;
    }

    /**
     * @param notiPostCommentReply the notiPostCommentReply to set
     */
    public void setNotiPostCommentReply(Boolean notiPostCommentReply) {
        this.notiPostCommentReply = notiPostCommentReply;
    }

    /**
     * @return the notiFeePush
     */
    public Boolean getNotiFeePush() {
        return notiFeePush;
    }

    /**
     * @param notiFeePush the notiFeePush to set
     */
    public void setNotiFeePush(Boolean notiFeePush) {
        this.notiFeePush = notiFeePush;
    }

    /**
     * @return the notiAirPollution
     */
    public Boolean getNotiAirPollution() {
        return notiAirPollution;
    }

    /**
     * @param notiAirPollution the notiAirPollution to set
     */
    public void setNotiAirPollution(Boolean notiAirPollution) {
        this.notiAirPollution = notiAirPollution;
    }

    /**
     * @return the notiVisit
     */
    public Boolean getNotiVisit() {
        return notiVisit;
    }

    /**
     * @param notiVisit the notiVisit to set
     */
    public void setNotiVisit(Boolean notiVisit) {
        this.notiVisit = notiVisit;
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
     * @return the signImageUri
     */
    public String getSignImageUri() {
        return signImageUri;
    }

    /**
     * @param signImageUri the signImageUri to set
     */
    public void setSignImageUri(String signImageUri) {
        this.signImageUri = signImageUri;
    }

    /**
     * @return the userContent
     */
    public String getUserContent() {
        return userContent;
    }

    /**
     * @param userContent the userContent to set
     */
    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    /**
     * @return the voteDate
     */
    public Date getVoteDate() {
        return voteDate;
    }

    /**
     * @param voteDate the voteDate to set
     */
    public void setVoteDate(Date voteDate) {
        this.voteDate = voteDate;
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
     * @return the voteItemId
     */
    public Long getVoteItemId() {
        return voteItemId;
    }

    /**
     * @param voteItemId the voteItemId to set
     */
    public void setVoteItemId(Long voteItemId) {
        this.voteItemId = voteItemId;
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
     * @return the mandateVoterName
     */
    public String getMandateVoterName() {
        return mandateVoterName;
    }

    /**
     * @param mandateVoterName the mandateVoterName to set
     */
    public void setMandateVoterName(String mandateVoterName) {
        this.mandateVoterName = mandateVoterName;
    }

    /**
     * @return the mandated
     */
    public Boolean getMandated() {
        return mandated;
    }

    /**
     * @param mandated the mandated to set
     */
    public void setMandated(Boolean mandated) {
        this.mandated = mandated;
    }

    /**
     * @return the registerType
     */
    public String getRegisterType() {
        return registerType;
    }

    /**
     * @param registerType the registerType to set
     */
    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    /**
     * @return the voterName
     */
    public String getVoterName() {
        return voterName;
    }

    /**
     * @param voterName the voterName to set
     */
    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }



}
