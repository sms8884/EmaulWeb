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
 *         This Domain class mapped db-table called board_post_air
 */
@Alias(value = "BoardPostAirVo")
public class BoardPostAirVo extends BoardPostVo implements Serializable {

    /** SID */
    private static final long serialVersionUID = -8069299256217595917L;

    /** 게시글일련번호 */
    private Long postId;

    /** 목소리구분 */
    private String voiceGubun;

    /** 목소리크기 */
    private int voiceVolume;

    /** 방송송출시간 */
    private Date airSendDate;

    /** 방송즉시/예약발송여부 */
    private String airReserveYn;

    /** 방송예약일시(형태: 2016.09.19 AM 09시40분 또는 2016.09.19 PM 07시40분) */
    private String airReserveDate;

    /** 방송예약취소여부(Y/ N, N일 경우 예약방송불가) */
    private String airReserveCancelYn;

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getVoiceGubun() {
        return voiceGubun;
    }

    public void setVoiceGubun(String voiceGubun) {
        this.voiceGubun = voiceGubun;
    }

    public int getVoiceVolume() {
        return voiceVolume;
    }

    public void setVoiceVolume(int voiceVolume) {
        this.voiceVolume = voiceVolume;
    }

    public Date getAirSendDate() {
        return airSendDate;
    }

    public void setAirSendDate(Date airSendDate) {
        this.airSendDate = airSendDate;
    }

    public String getAirReserveYn() {
        return airReserveYn;
    }

    public void setAirReserveYn(String airReserveYn) {
        this.airReserveYn = airReserveYn;
    }

    public String getAirReserveDate() {
        return airReserveDate;
    }

    public void setAirReserveDate(String airReserveDate) {
        this.airReserveDate = airReserveDate;
    }

    public String getAirReserveCancelYn() {
        return airReserveCancelYn;
    }

    public void setAirReserveCancelYn(String airReserveCancelYn) {
        this.airReserveCancelYn = airReserveCancelYn;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
