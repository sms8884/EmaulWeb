/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaha.web.emaul.model.User;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_category
 */
@Alias(value = "BoardCategoryVo")
public class BoardCategoryVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = 7473988107532966309L;

    /** 게시판분류일련번호 */
    private Long id;

    /** 아파트아이디 */
    private Long aptId;

    /** 읽기권한 */
    private String jsonArrayReadableUserType;

    /** 쓰기권한 */
    private String jsonArrayWritableUserType;

    /** 분류명 */
    private String name;

    /** 순서 */
    private int ord;

    /** 유형 */
    private String type;

    /** 본문형태(text/html) */
    private String contentMode;

    /** 글등록후푸시발송여부 */
    private String pushAfterWrite;

    /** 실명/닉네임/익명(ENUM타입) */
    private String userPrivacy;

    /** 노출여부 */
    private String displayYn;

    /** 사용여부 */
    private String delYn;

    /** 댓글목록 노출여부 */
    private String commentDisplayYn;

    /** 요청IP */
    private String reqIp;

    /** 등록자아이디 */
    private Long userId;

    /** 등록일시 */
    private Date regDate;

    /** 수정자아이디 */
    private Long modId;

    /** 수정일시 */
    private Date modDate;

    /**
     * @return the delYn
     */
    public String getDelYn() {
        return delYn;
    }

    /**
     * @param delYn the delYn to set
     */
    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAptId() {
        return aptId;
    }

    public void setAptId(Long aptId) {
        this.aptId = aptId;
    }

    public String getJsonArrayReadableUserType() {
        return jsonArrayReadableUserType;
    }

    public void setJsonArrayReadableUserType(String jsonArrayReadableUserType) {
        this.jsonArrayReadableUserType = jsonArrayReadableUserType;
    }

    public String getJsonArrayWritableUserType() {
        return jsonArrayWritableUserType;
    }

    public void setJsonArrayWritableUserType(String jsonArrayWritableUserType) {
        this.jsonArrayWritableUserType = jsonArrayWritableUserType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentMode() {
        return contentMode;
    }

    public void setContentMode(String contentMode) {
        this.contentMode = contentMode;
    }

    public String getPushAfterWrite() {
        return pushAfterWrite;
    }

    public void setPushAfterWrite(String pushAfterWrite) {
        this.pushAfterWrite = pushAfterWrite;
    }

    public String getUserPrivacy() {
        return userPrivacy;
    }

    public void setUserPrivacy(String userPrivacy) {
        this.userPrivacy = userPrivacy;
    }

    public String getDisplayYn() {
        return displayYn;
    }

    public void setDisplayYn(String displayYn) {
        this.displayYn = displayYn;
    }

    public String getCommentDisplayYn() {
        return commentDisplayYn;
    }

    public void setCommentDisplayYn(String commentDisplayYn) {
        this.commentDisplayYn = commentDisplayYn;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Long getModId() {
        return modId;
    }

    public void setModId(Long modId) {
        this.modId = modId;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Transient
    public List<String> getReadableUserTypes() {
        return new Gson().fromJson(jsonArrayReadableUserType, new TypeToken<List<String>>() {}.getType());
    }

    @Transient
    public List<String> getWritableUserTypes() {
        return new Gson().fromJson(jsonArrayWritableUserType, new TypeToken<List<String>>() {}.getType());
    }

    @Transient
    public boolean isUserReadable(User user) {
        List<String> trueTypes = user.type.getTrueTypes();
        List<String> readableUserTypes = getReadableUserTypes();
        List<String> writableUserTypes = getWritableUserTypes();

        for (String trueType : trueTypes) {
            if (readableUserTypes.contains(trueType) || writableUserTypes.contains(trueType)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean isUserWritable(User user) {
        List<String> trueTypes = user.type.getTrueTypes();
        List<String> writableUserTypes = getWritableUserTypes();

        for (String trueType : trueTypes) {
            if (writableUserTypes.contains(trueType)) {
                return true;
            }
        }
        return false;
    }

}
