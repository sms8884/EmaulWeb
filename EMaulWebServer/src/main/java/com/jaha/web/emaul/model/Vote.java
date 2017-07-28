package com.jaha.web.emaul.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 15. 2. 25..
 */
@Entity
@Table(name = "vote", indexes = {@Index(name = "idx_target_apt_vote", columnList = "targetApt"), @Index(name = "idx_target_dong_vote", columnList = "targetDong"),
        @Index(name = "idx_visible_vote", columnList = "visible")})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "typeId", referencedColumnName = "id", nullable = false)
    public VoteType type;

    @Column(length = 20, nullable = false)
    public String status;

    @Column(length = 100)
    public String title;

    @Column(length = 1000)
    public String description;

    @Column(length = 400)
    public String question;

    @ManyToOne
    @JoinColumn(name = "targetApt", referencedColumnName = "id", nullable = false)
    public Apt targetApt;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean rangeAll = false;

    @Column(length = 20, nullable = false)
    @JsonIgnore
    public String targetDong = "";

    @Column(nullable = false, length = 4000, columnDefinition = "VARCHAR(4000) NOT NULL DEFAULT ''")
    @JsonIgnore
    public String jsonArrayTargetHo;

    @Column(length = 20, nullable = false)
    @JsonIgnore
    public String rangeSido = "";

    @Column(length = 20, nullable = false)
    @JsonIgnore
    public String rangeSigungu = "";

    @Column(nullable = false, columnDefinition = "BIGINT(20) NOT NULL DEFAULT 0")
    public Long votersCount = 0l;

    @Column(nullable = false, length = 1000, columnDefinition = "VARCHAR(1000) NOT NULL DEFAULT ''")
    public String jsonArrayTargetUserTypes;

    public Boolean numberEnabled;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    @JsonIgnore
    public Boolean houseLimited;

    @Column(nullable = false)
    @JsonIgnore
    public Boolean visible;

    @Column(nullable = false)
    public Boolean multipleChoice;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean voteResultAvailable;

    @Column(nullable = false)
    public Date regDate;

    public Date startDate;
    public Date endDate;

    public Date pushSendDate;

    public Integer imageCount;

    @Column(length = 200)
    public String file1;
    @Column(length = 200)
    public String file2;

    @Column(length = 2)
    public String enableSecurity;

    @Column(length = 2)
    public String securityLevel;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId", referencedColumnName = "id")
    public List<VoteItem> items;

    @ManyToOne(targetEntity = VoteKey.class, cascade = {})
    @JoinColumn(name = "vkId", referencedColumnName = "vkId")
    public VoteKey voteKey;

    @Column(length = 2)
    public String decYn = "N";

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean offlineAvailable;

    @Column
    public Integer securityCheckState = 0;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean securityNoticeState = false;


    /** Controller에서 모델 클래스에 status에 값을 직접 담을 경우 DB값이 변경되는 것 방지 */
    @Transient
    public String statusKor;
}
