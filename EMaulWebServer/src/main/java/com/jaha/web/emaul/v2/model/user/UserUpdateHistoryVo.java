package com.jaha.web.emaul.v2.model.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.ibatis.type.Alias;

import com.google.common.collect.Maps;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.util.StringUtil;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called user_update_history
 */
@Alias(value = "UserUpdateHistoryVo")
public class UserUpdateHistoryVo extends BaseSecuModel implements Serializable {

    private static final long serialVersionUID = -2295716056407927979L;

    /** 외부기기 로그인 허용여부 */
    public final static String TYPE_MULTILOGIN = "MULTI_LOGIN";
    /** 전출 */
    public final static String TYPE_TRANSFER = "TRANSFER";
    /** 관리자 변경 권한 */
    public final static String TYPE_ADMIN_AUTH = "ADMIN_AUTH";
    /** 자하 변경 권한 */
    public final static String TYPE_JAHA_AUTH = "JAHA_AUTH";
    /** 비밀번호초기화 */
    public final static String TYPE_INIT_PWD = "INIT_PWD";
    /** 이메일초기화 */
    public final static String TYPE_INIT_EMAIL = "INIT_EMAIL";
    /** 사용자비밀번호변경 */
    public final static String TYPE_CHANGE_PWD = "CHANGE_PWD";
    /** 사용자 가입 */
    public final static String TYPE_SIGN_UP = "SIGN_UP";
    /** 닉네임 변경 */
    public final static String TYPE_CHANGE_NICK = "CHANGE_NICK";
    /** 아파트 변경 */
    public final static String TYPE_CHANGE_APT = "CHANGE_APT";
    /** 탈퇴 */
    public final static String TYPE_DEACTIVE = "DEACTIVE";

    public static final Map<String, String> typeMap;
    static {
        typeMap = Maps.newHashMap();

        typeMap.put(TYPE_MULTILOGIN, "외부기기 설정변경");
        typeMap.put(TYPE_TRANSFER, "전출");
        typeMap.put(TYPE_ADMIN_AUTH, "ADMIN권한에서 정보변경");
        typeMap.put(TYPE_JAHA_AUTH, "JAHA권한에서 정보변경");
        typeMap.put(TYPE_INIT_PWD, "비밀번호 초기화");
        typeMap.put(TYPE_INIT_EMAIL, "이메일 초기화");
        typeMap.put(TYPE_CHANGE_PWD, "비밀번호 변경");
        typeMap.put(TYPE_SIGN_UP, "회원가입");
        typeMap.put(TYPE_CHANGE_NICK, "닉네임 변경");
        typeMap.put(TYPE_CHANGE_APT, "아파트 변경");
        typeMap.put(TYPE_DEACTIVE, "탈퇴");
    }

    private Long id;
    /** 수정대상 사용자 아이디 */
    private Long userId;
    /** 수정내역 (상수정의내역) */
    private String type;
    /** 수정자 아이디 */
    private Long modId;
    /** 수정일시 */
    private Date modDate;
    /** 수정 데이터 */
    private String data;

    private String auth;

    private String userName;

    private String email;

    private String phone;

    private String birthYear;

    private String gender;

    private String nickname;

    private Long houseId;

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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getUserName() {
        return userName;
    }

    public String getDescUserName() {
        return descString(StringUtil.nvl(userName));
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescEmail() {
        return descString(StringUtil.nvl(email));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescPhone() {
        return descString(StringUtil.nvl(phone));
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }


}
