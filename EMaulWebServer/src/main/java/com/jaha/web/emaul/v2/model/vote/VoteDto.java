/**
 *
 */
package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.v2.model.common.CommonDto;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 */
@Alias(value = "VoteDto")
public class VoteDto extends CommonDto implements Serializable {

    private static final long serialVersionUID = -3569485106857012129L; // 2016.10.12

    /** 투표 종류 데이터 vote_type */
    private VoteTypeVo voteTypeVo;

    /** 투표 아이디 */
    private Long id;
    /** 상태 */
    private String voteStatus; // vote.status
    /** 종류 */
    private String voteTypeId; // vote.type_id - vote_type.id
    /** 아파크명 */
    private String aptName; // apt.name
    /** 아파트 아이디 */
    private Long aptId; // vote.target_apt
    /** 투표 항목 아이디 */
    private Long itemId; // vote_item.id
    /** 전체 출력여부 (투표 출력시 Y 인 경우 페이징처리 안함) */
    private String fullYn;
    /** 보안키 아이디 */
    private Long vkId;
    /** 아파트 동 */
    private String dong;

    /** 선거관리위원장 명 (개표 검색조건) */
    private String adminName;

    /** 선거구 아이디 리스트 / 수정화면에서 선거구 명 리스트를 조회하기 위해 사용한다. */
    private List<Long> groupIdList;



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
     * @return the groupIdList
     */
    public List<Long> getGroupIdList() {
        return groupIdList;
    }

    /**
     * @param groupIdList the groupIdList to set
     */
    public void setGroupIdList(List<Long> groupIdList) {
        this.groupIdList = groupIdList;
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
     * @return the fullYn
     */
    public String getFullYn() {
        return fullYn;
    }

    /**
     * @param fullYn the fullYn to set
     */
    public void setFullYn(String fullYn) {
        this.fullYn = fullYn;
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
     * @return the voteTypeVo
     */
    public VoteTypeVo getVoteTypeVo() {
        return voteTypeVo;
    }

    /**
     * @param voteTypeVo the voteTypeVo to set
     */
    public void setVoteTypeVo(VoteTypeVo voteTypeVo) {
        this.voteTypeVo = voteTypeVo;
    }

    /**
     * @return the voteStatus
     */
    public String getVoteStatus() {
        return voteStatus;
    }

    /**
     * @param voteStatus the voteStatus to set
     */
    public void setVoteStatus(String voteStatus) {
        this.voteStatus = voteStatus;
    }

    /**
     * @return the voteTypeId
     */
    public String getVoteTypeId() {
        return voteTypeId;
    }

    /**
     * @param voteTypeId the voteTypeId to set
     */
    public void setVoteTypeId(String voteTypeId) {
        this.voteTypeId = voteTypeId;
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



}
