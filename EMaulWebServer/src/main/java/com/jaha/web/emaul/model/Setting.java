package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by doring on 15. 4. 28..
 */
@Entity
public class Setting {

    public Setting(Long userId) {
        this.userId = userId;
    }

    public Setting() {

    }

    @Id
    public Long userId;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiAlarm = true;
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiPostReply = true;
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiPostCommentReply = true;
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiParcel = true;

    /** 아파트관리비수신여부 */
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiFeePush = false;

    /** 대기오염알림수신여부 */
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiAirPollution = false;

    /** 방문주차알림수신여부 */
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 1")
    public Boolean notiVisit = false;

}
