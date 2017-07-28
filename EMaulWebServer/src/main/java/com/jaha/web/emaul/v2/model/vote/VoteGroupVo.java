package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.model.BaseSecuModel;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote_group
 */
@Alias(value = "VoteGroupVo")
public class VoteGroupVo extends BaseSecuModel implements Serializable {

    private static final long serialVersionUID = 1812927042448199771L;

    /** 선거구 일련번호 */
    private Long id;
    /** 선거구 설명 */
    private String description;
    /** 아파트 아이디 */
    private Long targetApt;
    /** 선거구 명 */
    private String name;
    /** 선거구 투표대상 */
    private String jsonArrayTarget;
    /** 유권자 수 */
    private Long votersCount; // 0l
    /** 세대 선택 방식 */
    private String groupType;
    /** 등록자 아이디 */
    private Long userId;
    /** 등록자 명 */
    private String userName;
    /** 등록일시 */
    private Date regDate;
    /** 수정자 아이디 */
    private Long modId;
    /** 수정자 명 */
    private String modName;
    /** 수정일시 */
    private Date modDate;
    /** 사용여부 */
    private String useYn;
    /** 아파트명 */
    private String aptName;

    /** 선택된 동s */
    private String[] dongs;


    /**
     * @return the dongs
     */
    public String[] getDongs() {
        return dongs;
    }

    /**
     * @param dongs the dongs to set
     */
    public void setDongs(String[] dongs) {
        this.dongs = dongs;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the jsonArrayTarget
     */
    public String getJsonArrayTarget() {
        return jsonArrayTarget;
    }

    /**
     * @param jsonArrayTarget the jsonArrayTarget to set
     */
    public void setJsonArrayTarget(String jsonArrayTarget) {
        this.jsonArrayTarget = jsonArrayTarget;
    }

    /**
     * @return the votersCount
     */
    public Long getVotersCount() {
        return votersCount;
    }

    /**
     * @param votersCount the votersCount to set
     */
    public void setVotersCount(Long votersCount) {
        this.votersCount = votersCount;
    }


    /**
     * @return the groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
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
     * @return the userName
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



}
