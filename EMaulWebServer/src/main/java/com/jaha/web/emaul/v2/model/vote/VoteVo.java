package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.model.BaseSecuModel;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote
 */
@Alias(value = "VoteVo")
public class VoteVo extends BaseSecuModel implements Serializable {

    private static final long serialVersionUID = 2472219865464216838L;

    /** 투표 일련번호 */
    private Long id;
    private String description;
    /** 투표 종료일 */
    private Date endDate;
    private Integer imageCount;
    private Boolean multipleChoice;
    /** 번호 표시여부 */
    private Boolean numberEnabled;
    /** 기본형 번호표시 여부 */
    private Boolean numberEnabled1;
    /** 기본형 번호표시 여부 */
    private Boolean numberEnabled2;

    private String question;
    private Boolean rangeAll; // false
    private String rangeSido; // ""
    private String rangeSigungu; // ""'
    /** 등록일 */
    private Date regDate;
    /** 투표 시작일 */
    private Date startDate;
    /** 투표 상태 */
    private String status;
    /** 투표 상태 출력값 */
    private String statusText;
    /** 아파트 아이디 */
    private Long targetApt;
    /** 아파트 명 */
    private String aptName;
    private String targetDong; // ""

    /** 제목 */
    private String title;
    private Boolean visible;

    private Long typeId;
    /** 복수투표 허용여부 */
    private Boolean houseLimited;
    /** 투표대상 호 목록 */
    private String jsonArrayTargetHo;
    /** 투표대상 선거구 목록 */
    private String jsonArrayTargetGroup;
    /** 집계처리 완료여부 */
    private Boolean voteResultAvailable;
    /** 투표자 타입 */
    private String jsonArrayTargetUserTypes;
    /** 유권자 수 */
    private Long votersCount; // 0l
    /** 첨부파일 1 */
    private String file1;
    /** 첨부파일 2 */
    private String file2;
    /** 투표구분 : 일반투표 / 보안투표 */
    private String enableSecurity;
    /** 보안 등급 */
    private String securityLevel;
    /** 보안키 아이디 */
    private Long vkId;
    private String decYn; // "N"
    /** 오프라인 투표여부 */
    private Boolean offlineAvailable;
    private Integer securityCheckState; // 0
    private Boolean securityNoticeState; // false
    private Date pushSendDate;

    /** 사용여부 : N (삭제 / 미사용) */
    private String useYn;

    /** 파일 필수 확인여부 (투표 전 첨부파일 필수 확인 : Y/N) */
    private String fileCheckYn;

    /**
     * 투표 알람 설정 타입 (2번등 추가될 수 있으므로 Char)<br />
     * 0 : 전송안함, 1 : 즉시전송, 2: 예약전송
     */
    private String notiType;

    /**
     * 투표알림 제목
     */
    private String notiMsg;

    /** 보안 키 */
    private VoteKeyVo voteKey;

    /** 등록자 아이디 */
    private Long userId;
    /** 등록자 명 */
    private String userName;
    /** 수정자 아이디 */
    private Long modId;
    /** 수정자 명 */
    private String modName;
    /** 수정일시 */
    private Date modDate;
    /** 사진첨부 1 */
    private String photo1;
    private String photo2;
    private String photo3;
    private String photo4;
    private String photo5;

    // 투표 기간 설정용 임시 필드 [non db column / 파라미터로만 사용.]
    private String startDay;
    private String startHour;
    private String startMin;
    private String endDay;
    private String endHour;
    private String endMin;
    // 투표자 구분
    private String userType;
    // 투표 항목 (title)
    private String[] title1; // 기본형 투표항목

    private String[] title5; // 단일후보형 후보이름
    private String[] commitment5; // 단일후보형 후보 공약
    private String[] profile5; // 단일후보형 후보 프로필

    private String[] title2; // 복수후보형 후보이름
    private String[] commitment2; // 복수후보형 후보 공약
    private String[] profile2; // 복수후보형 후보 프로필

    /** 보안투표 - 선관위원장 이름 (화면 출력용) */
    private String adminName;
    /** 보안투표 - 선관위원장 이메일 이름 (화면 출력용) */
    private String adminEmail;

    /**
     * 미참여자 리스트 추출용 조건절 세팅 컬럼 <br/>
     * (voteAdmin-mapper.xml / getYetVoteList)
     */
    // 동/호 별 -- 호 리스트
    private List<String> hoList;
    // 동 별 -- 동 리스트
    private List<String> dongList;
    // 선거구별 -- 선거구 아이디 리스트
    private List<Long> groupList;



    /**
     * @return the dongList
     */
    public List<String> getDongList() {
        return dongList;
    }

    /**
     * @param dongList the dongList to set
     */
    public void setDongList(List<String> dongList) {
        this.dongList = dongList;
    }

    /**
     * @return the hoList
     */
    public List<String> getHoList() {
        return hoList;
    }

    /**
     * @param hoList the hoList to set
     */
    public void setHoList(List<String> hoList) {
        this.hoList = hoList;
    }

    /**
     * @return the groupList
     */
    public List<Long> getGroupList() {
        return groupList;
    }

    /**
     * @param groupList the groupList to set
     */
    public void setGroupList(List<Long> groupList) {
        this.groupList = groupList;
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
     * @return the jsonArrayTargetGroup
     */
    public String getJsonArrayTargetGroup() {
        return jsonArrayTargetGroup;
    }

    /**
     * @param jsonArrayTargetGroup the jsonArrayTargetGroup to set
     */
    public void setJsonArrayTargetGroup(String jsonArrayTargetGroup) {
        this.jsonArrayTargetGroup = jsonArrayTargetGroup;
    }

    /**
     * @return the photo1
     */
    public String getPhoto1() {
        return photo1;
    }

    /**
     * @param photo1 the photo1 to set
     */
    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    /**
     * @return the photo2
     */
    public String getPhoto2() {
        return photo2;
    }

    /**
     * @param photo2 the photo2 to set
     */
    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    /**
     * @return the photo3
     */
    public String getPhoto3() {
        return photo3;
    }

    /**
     * @param photo3 the photo3 to set
     */
    public void setPhoto3(String photo3) {
        this.photo3 = photo3;
    }

    /**
     * @return the photo4
     */
    public String getPhoto4() {
        return photo4;
    }

    /**
     * @param photo4 the photo4 to set
     */
    public void setPhoto4(String photo4) {
        this.photo4 = photo4;
    }

    /**
     * @return the photo5
     */
    public String getPhoto5() {
        return photo5;
    }

    /**
     * @param photo5 the photo5 to set
     */
    public void setPhoto5(String photo5) {
        this.photo5 = photo5;
    }

    /**
     * @return the title2
     */
    public String[] getTitle2() {
        return title2;
    }

    /**
     * @param title2 the title2 to set
     */
    public void setTitle2(String[] title2) {
        this.title2 = title2;
    }

    /**
     * @return the commitment2
     */
    public String[] getCommitment2() {
        return commitment2;
    }

    /**
     * @param commitment2 the commitment2 to set
     */
    public void setCommitment2(String[] commitment2) {
        this.commitment2 = commitment2;
    }

    /**
     * @return the profile2
     */
    public String[] getProfile2() {
        return profile2;
    }

    /**
     * @param profile2 the profile2 to set
     */
    public void setProfile2(String[] profile2) {
        this.profile2 = profile2;
    }

    /**
     * @return the title5
     */
    public String[] getTitle5() {
        return title5;
    }

    /**
     * @param title5 the title5 to set
     */
    public void setTitle5(String[] title5) {
        this.title5 = title5;
    }

    /**
     * @return the commitment5
     */
    public String[] getCommitment5() {
        return commitment5;
    }

    /**
     * @param commitment5 the commitment5 to set
     */
    public void setCommitment5(String[] commitment5) {
        this.commitment5 = commitment5;
    }

    /**
     * @return the profile5
     */
    public String[] getProfile5() {
        return profile5;
    }

    /**
     * @param profile5 the profile5 to set
     */
    public void setProfile5(String[] profile5) {
        this.profile5 = profile5;
    }

    /**
     * @return the title1
     */
    public String[] getTitle1() {
        return title1;
    }

    /**
     * @param title1 the title1 to set
     */
    public void setTitle1(String[] title1) {
        this.title1 = title1;
    }


    /**
     * @return the modId
     */
    public Long getModId() {
        return modId;
    }

    /**
     * @param modId the modId to set
     */
    public void setModId(Long modId) {
        this.modId = modId;
    }

    /**
     * @return the modName
     */
    public String getModName() {

        if (this.modId == null) {
            return "-";
        } else {
            this.modName = descString(modName);
            return this.modName;
        }
    }

    /**
     * @param modName the modName to set
     */
    public void setModName(String modName) {
        this.modName = modName;
    }

    /**
     * @return the modDate
     */
    public Date getModDate() {
        return modDate;
    }

    /**
     * @param modDate the modDate to set
     */
    public void setModDate(Date modDate) {
        this.modDate = modDate;
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
     * @return the userName <br />
     *         기존 데이터는 없으므로 default 는 등록자로 표기한다.
     */
    public String getUserName() {

        if (this.userId == null) {
            return "-";
        } else {
            this.userName = descString(userName);
            return this.userName;
        }
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the numberEnabled1
     */
    public Boolean getNumberEnabled1() {
        return numberEnabled1 == null ? false : numberEnabled1;
    }

    /**
     * @param numberEnabled1 the numberEnabled1 to set
     */
    public void setNumberEnabled1(Boolean numberEnabled1) {
        this.numberEnabled1 = numberEnabled1;
    }

    /**
     * @return the numberEnabled2
     */
    public Boolean getNumberEnabled2() {
        return numberEnabled2 == null ? false : numberEnabled2;
    }

    /**
     * @param numberEnabled2 the numberEnabled2 to set
     */
    public void setNumberEnabled2(Boolean numberEnabled2) {
        this.numberEnabled2 = numberEnabled2;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return the startDay
     */
    public String getStartDay() {
        return startDay;
    }

    /**
     * @param startDay the startDay to set
     */
    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    /**
     * @return the startHour
     */
    public String getStartHour() {
        return startHour;
    }

    /**
     * @param startHour the startHour to set
     */
    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    /**
     * @return the startMin
     */
    public String getStartMin() {
        return startMin;
    }

    /**
     * @param startMin the startMin to set
     */
    public void setStartMin(String startMin) {
        this.startMin = startMin;
    }

    /**
     * @return the endDay
     */
    public String getEndDay() {
        return endDay;
    }

    /**
     * @param endDay the endDay to set
     */
    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    /**
     * @return the endHour
     */
    public String getEndHour() {
        return endHour;
    }

    /**
     * @param endHour the endHour to set
     */
    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    /**
     * @return the endMin
     */
    public String getEndMin() {
        return endMin;
    }

    /**
     * @param endMin the endMin to set
     */
    public void setEndMin(String endMin) {
        this.endMin = endMin;
    }

    /**
     * @return the notiType
     */
    public String getNotiType() {
        return notiType;
    }

    /**
     * @param notiType the notiType to set
     */
    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }

    /**
     * @return the notiMsg
     */
    public String getNotiMsg() {
        return notiMsg;
    }

    /**
     * @param notiMsg the notiMsg to set
     */
    public void setNotiMsg(String notiMsg) {
        this.notiMsg = notiMsg;
    }

    /**
     * @return the fileCheckYn
     */
    public String getFileCheckYn() {
        return fileCheckYn == null ? "N" : "on".equalsIgnoreCase(fileCheckYn.trim()) || "Y".equalsIgnoreCase(fileCheckYn.trim()) ? "Y" : "N";
    }

    /**
     * @param fileCheckYn the fileCheckYn to set
     */
    public void setFileCheckYn(String fileCheckYn) {
        this.fileCheckYn = fileCheckYn;
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
     * @return the targetApt
     */
    public Long getTargetApt() {
        return targetApt;
    }

    /**
     * @param targetApt the targetApt to set
     */
    public void setTargetApt(Long targetApt) {
        this.targetApt = targetApt;
    }

    /**
     * @return the voteKey
     */
    public VoteKeyVo getVoteKey() {
        return voteKey;
    }

    /**
     * @param voteKey the voteKey to set
     */
    public void setVoteKey(VoteKeyVo voteKey) {
        this.voteKey = voteKey;
    }

    /**
     * @return the statusText
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * @param statusText the statusText to set
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
     * @return the multipleChoice
     */
    public Boolean getMultipleChoice() {
        return multipleChoice == null ? false : multipleChoice;
    }

    /**
     * @param multipleChoice the multipleChoice to set
     */
    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    /**
     * @return the numberEnabled
     */
    public Boolean getNumberEnabled() {
        return numberEnabled == null ? false : numberEnabled;
    }

    /**
     * @param numberEnabled the numberEnabled to set
     */
    public void setNumberEnabled(Boolean numberEnabled) {
        this.numberEnabled = numberEnabled;
    }

    /**
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return the rangeAll
     */
    public Boolean getRangeAll() {
        return rangeAll == null ? false : rangeAll;
    }

    /**
     * @param rangeAll the rangeAll to set
     */
    public void setRangeAll(Boolean rangeAll) {
        this.rangeAll = rangeAll;
    }

    /**
     * @return the rangeSido
     */
    public String getRangeSido() {
        return rangeSido;
    }

    /**
     * @param rangeSido the rangeSido to set
     */
    public void setRangeSido(String rangeSido) {
        this.rangeSido = rangeSido;
    }

    /**
     * @return the rangeSigungu
     */
    public String getRangeSigungu() {
        return rangeSigungu;
    }

    /**
     * @param rangeSigungu the rangeSigungu to set
     */
    public void setRangeSigungu(String rangeSigungu) {
        this.rangeSigungu = rangeSigungu;
    }

    /**
     * @return the regDate
     */
    public Date getRegDate() {
        return regDate == null ? new Date() : regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @return the targetDong
     */
    public String getTargetDong() {
        return targetDong;
    }

    /**
     * @param targetDong the targetDong to set
     */
    public void setTargetDong(String targetDong) {
        this.targetDong = targetDong;
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
     * @return the visible
     */
    public Boolean getVisible() {
        return visible == null ? false : visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the typeId
     */
    public Long getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the houseLimited
     */
    public Boolean getHouseLimited() {
        return houseLimited == null ? false : houseLimited;
    }

    /**
     * @param houseLimited the houseLimited to set
     */
    public void setHouseLimited(Boolean houseLimited) {
        this.houseLimited = houseLimited;
    }

    /**
     * @return the jsonArrayTargetHo
     */
    public String getJsonArrayTargetHo() {
        return jsonArrayTargetHo;
    }

    /**
     * @param jsonArrayTargetHo the jsonArrayTargetHo to set
     */
    public void setJsonArrayTargetHo(String jsonArrayTargetHo) {
        this.jsonArrayTargetHo = jsonArrayTargetHo;
    }

    /**
     * @return the voteResultAvailable
     */
    public Boolean getVoteResultAvailable() {
        return voteResultAvailable == null ? false : voteResultAvailable;
    }

    /**
     * @param voteResultAvailable the voteResultAvailable to set
     */
    public void setVoteResultAvailable(Boolean voteResultAvailable) {
        this.voteResultAvailable = voteResultAvailable;
    }

    /**
     * @return the jsonArrayTargetUserTypes
     */
    public String getJsonArrayTargetUserTypes() {
        return jsonArrayTargetUserTypes;
    }

    /**
     * @param jsonArrayTargetUserTypes the jsonArrayTargetUserTypes to set
     */
    public void setJsonArrayTargetUserTypes(String jsonArrayTargetUserTypes) {
        this.jsonArrayTargetUserTypes = jsonArrayTargetUserTypes;
    }

    /**
     * @return the votersCount
     */
    public Long getVotersCount() {
        return votersCount == null ? 0 : votersCount;
    }

    /**
     * @param votersCount the votersCount to set
     */
    public void setVotersCount(Long votersCount) {
        this.votersCount = votersCount;
    }

    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }

    /**
     * @param file1 the file1 to set
     */
    public void setFile1(String file1) {
        this.file1 = file1;
    }

    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }

    /**
     * @param file2 the file2 to set
     */
    public void setFile2(String file2) {
        this.file2 = file2;
    }

    /**
     * @return the enableSecurity
     */
    public String getEnableSecurity() {
        return enableSecurity;
    }

    /**
     * @param enableSecurity the enableSecurity to set
     */
    public void setEnableSecurity(String enableSecurity) {
        this.enableSecurity = enableSecurity;
    }

    /**
     * @return the securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * @param securityLevel the securityLevel to set
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
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
     * @return the decYn
     */
    public String getDecYn() {
        return decYn;
    }

    /**
     * @param decYn the decYn to set
     */
    public void setDecYn(String decYn) {
        this.decYn = decYn;
    }

    /**
     * @return the offlineAvailable
     */
    public Boolean getOfflineAvailable() {
        return offlineAvailable == null ? false : offlineAvailable;
    }

    /**
     * @param offlineAvailable the offlineAvailable to set
     */
    public void setOfflineAvailable(Boolean offlineAvailable) {
        this.offlineAvailable = offlineAvailable;
    }

    /**
     * @return the securityCheckState
     */
    public Integer getSecurityCheckState() {
        return securityCheckState == null ? 0 : securityCheckState;
    }

    /**
     * @param securityCheckState the securityCheckState to set
     */
    public void setSecurityCheckState(Integer securityCheckState) {
        this.securityCheckState = securityCheckState;
    }

    /**
     * @return the securityNoticeState
     */
    public Boolean getSecurityNoticeState() {
        return securityNoticeState == null ? false : securityNoticeState;
    }

    /**
     * @param securityNoticeState the securityNoticeState to set
     */
    public void setSecurityNoticeState(Boolean securityNoticeState) {
        this.securityNoticeState = securityNoticeState;
    }

    /**
     * @return the pushSendDate
     */
    public Date getPushSendDate() {
        return pushSendDate;
    }

    /**
     * @param pushSendDate the pushSendDate to set
     */
    public void setPushSendDate(Date pushSendDate) {
        this.pushSendDate = pushSendDate;
    }
}
