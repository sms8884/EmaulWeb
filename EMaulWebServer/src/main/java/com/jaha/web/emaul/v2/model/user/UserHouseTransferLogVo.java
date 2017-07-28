/**
 *
 */
package com.jaha.web.emaul.v2.model.user;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called user_house_transfer_log
 */
@Alias(value = "UserHouseTransferLogVo")
public class UserHouseTransferLogVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = -9130686925271299846L;

    /** 구분-전입 */
    public static final String TRANSFER_IN = "전입";
    /** 구분-전출 */
    public static final String TRANSFER_OUT = "전출";

    /** 일련번호 */
    private Integer id;
    /** 사용자아이디 */
    private Long userId;
    /** 세대아이디 */
    private Long houseId;
    /** 구분(전입:IN, 전출:OUT) */
    private String gubun;
    /** 등록자아이디 */
    private Long regId;
    /** 등록일시 */
    private Date regDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public Long getRegId() {
        return regId;
    }

    public void setRegId(Long regId) {
        this.regId = regId;
    }

    public Date getRegDate() {
        return regDate;
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

}
