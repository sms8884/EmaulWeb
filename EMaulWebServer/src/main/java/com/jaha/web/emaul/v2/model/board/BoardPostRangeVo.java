/**
 *
 */
package com.jaha.web.emaul.v2.model.board;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * @author 전강욱(realsnake@jahasmart.com) <br />
 *         This Domain class mapped db-table called board_post_range
 */
@Alias(value = "BoardPostRangeVo")
public class BoardPostRangeVo extends BoardPostVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = -5180641847460169655L;

    /** 게시글공개정보일련번호 */
    private Long rangeId;

    /** 게시글일련번호 */
    private Long postId;

    /** 시도 */
    private String rangeSido;

    /** 시군구 */
    private String rangeSigungu;

    /** 동 */
    private String rangeDong;

    /** 대상아파트 */
    private Long rangeAptId;

    /** 예약여부 */
    private String reservYn;

    /** 공개일시(12자리, 연월일시분) */
    private String openDate;

    /** 푸시상태(예약:reserv, 즉시:instant) */
    private String pushStatus;

    /** 실재푸시발송일시 */
    private Date pushSendDate;

    public Long getRangeId() {
        return rangeId;
    }

    public void setRangeId(Long rangeId) {
        this.rangeId = rangeId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getRangeSido() {
        return rangeSido;
    }

    public void setRangeSido(String rangeSido) {
        this.rangeSido = rangeSido;
    }

    public String getRangeSigungu() {
        return rangeSigungu;
    }

    public void setRangeSigungu(String rangeSigungu) {
        this.rangeSigungu = rangeSigungu;
    }

    public Long getRangeAptId() {
        return rangeAptId;
    }

    public void setRangeAptId(Long rangeAptId) {
        this.rangeAptId = rangeAptId;
    }

    public String getRangeDong() {
        return rangeDong;
    }

    public void setRangeDong(String rangeDong) {
        this.rangeDong = rangeDong;
    }

    public String getReservYn() {
        return reservYn;
    }

    public void setReservYn(String reservYn) {
        this.reservYn = reservYn;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public Date getPushSendDate() {
        return pushSendDate;
    }

    public void setPushSendDate(Date pushSendDate) {
        this.pushSendDate = pushSendDate;
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
