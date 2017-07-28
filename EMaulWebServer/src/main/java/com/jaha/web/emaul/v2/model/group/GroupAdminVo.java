package com.jaha.web.emaul.v2.model.group;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.model.BaseSecuModel;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called groupadmin_target_area
 */
@Alias(value = "GroupAdminVo")
public class GroupAdminVo extends BaseSecuModel implements Serializable {

    private static final long serialVersionUID = -211705794861870704L;

    /** 일련번호 */
    private Long id;
    /** 아파트 아이디 */
    private Long aptId;
    /** 사용자 아이디 */
    private Long userId;
    /** 지역 */
    private String area1;
    private String area2;
    private String area3;
    private String area4;

    /** 단체 주소 */
    private String groupZipcode;
    private String groupAddress;
    /** 단체 연락처 */
    private String groupPhone1;
    private String groupPhone2;
    private String groupPhone3;
    /** 담당자 명 */
    private String name;
    /** 담당자 연락처 */
    private String phone1;
    private String phone2;
    private String phone3;
    /** 담당자 이메일 */
    private String email;

    /** user */
    private String userName;
    private String userPhone;
    private String userEmail;

    /** 비밀번호 */
    private String password;

    /** 구청명 */
    private String guName;
    /** 구청 URL */
    private String guUrl;
    /** 기관명 (국민안정처) */
    private String orgName;
    /** 기관 URL */
    private String orgUrl;

    // -------- 고객센터 1:1 문의용
    /** 제목 */
    private String title;
    /** 문의 내용 */
    private String content;
    /** 개인정보 수집동의 */
    private String agree;



    /**
     * @return the agree
     */
    public String getAgree() {
        return agree;
    }

    /**
     * @param agree the agree to set
     */
    public void setAgree(String agree) {
        this.agree = agree;
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
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the guName
     */
    public String getGuName() {
        return guName;
    }

    /**
     * @param guName the guName to set
     */
    public void setGuName(String guName) {
        this.guName = guName;
    }

    /**
     * @return the guUrl
     */
    public String getGuUrl() {
        return guUrl;
    }

    /**
     * @param guUrl the guUrl to set
     */
    public void setGuUrl(String guUrl) {
        this.guUrl = guUrl;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return the orgUrl
     */
    public String getOrgUrl() {
        return orgUrl;
    }

    /**
     * @param orgUrl the orgUrl to set
     */
    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the groupZipcode
     */
    public String getGroupZipcode() {
        return groupZipcode;
    }

    /**
     * @param groupZipcode the groupZipcode to set
     */
    public void setGroupZipcode(String groupZipcode) {
        this.groupZipcode = groupZipcode;
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
     * @return the area1
     */
    public String getArea1() {
        if (this.area1 == null) {
            return "";
        } else {
            return area1;
        }
    }

    /**
     * @param area1 the area1 to set
     */
    public void setArea1(String area1) {
        this.area1 = area1;
    }

    /**
     * @return the area2
     */
    public String getArea2() {
        if (this.area2 == null) {
            return "";
        } else {
            return area2;
        }
    }

    /**
     * @param area2 the area2 to set
     */
    public void setArea2(String area2) {
        this.area2 = area2;
    }

    /**
     * @return the area3
     */
    public String getArea3() {
        if (this.area3 == null) {
            return "";
        } else {
            return area3;
        }
    }

    /**
     * @param area3 the area3 to set
     */
    public void setArea3(String area3) {
        this.area3 = area3;
    }

    /**
     * @return the area4
     */
    public String getArea4() {
        if (this.area4 == null) {
            return "";
        } else {
            return area4;
        }
    }

    /**
     * @param area4 the area4 to set
     */
    public void setArea4(String area4) {
        this.area4 = area4;
    }

    /**
     * @return the groupAddress
     */
    public String getGroupAddress() {
        return groupAddress;
    }

    /**
     * @param groupAddress the groupAddress to set
     */
    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
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
     * @return the groupPhone1
     */
    public String getGroupPhone1() {
        return groupPhone1;
    }

    /**
     * @param groupPhone1 the groupPhone1 to set
     */
    public void setGroupPhone1(String groupPhone1) {
        this.groupPhone1 = groupPhone1;
    }

    /**
     * @return the groupPhone2
     */
    public String getGroupPhone2() {
        return groupPhone2;
    }

    /**
     * @param groupPhone2 the groupPhone2 to set
     */
    public void setGroupPhone2(String groupPhone2) {
        this.groupPhone2 = groupPhone2;
    }

    /**
     * @return the groupPhone3
     */
    public String getGroupPhone3() {
        return groupPhone3;
    }

    /**
     * @param groupPhone3 the groupPhone3 to set
     */
    public void setGroupPhone3(String groupPhone3) {
        this.groupPhone3 = groupPhone3;
    }

    /**
     * @return the phone1
     */
    public String getPhone1() {
        return phone1;
    }

    /**
     * @param phone1 the phone1 to set
     */
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * @return the phone2
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     * @param phone2 the phone2 to set
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * @return the phone3
     */
    public String getPhone3() {
        return phone3;
    }

    /**
     * @param phone3 the phone3 to set
     */
    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        if (this.userName == null) {
            return null;
        } else {
            return descString(this.userName);
        }
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userPhone
     */
    public String getUserPhone() {
        if (this.userPhone == null) {
            return null;
        } else {
            return descString(this.userPhone);
        }
    }

    /**
     * @param userPhone the userPhone to set
     */
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    /**
     * @return the userEmail
     */
    public String getUserEmail() {
        if (this.userEmail == null) {
            return null;
        } else {
            return descString(this.userEmail);
        }
    }

    /**
     * @param userEmail the userEmail to set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


}
