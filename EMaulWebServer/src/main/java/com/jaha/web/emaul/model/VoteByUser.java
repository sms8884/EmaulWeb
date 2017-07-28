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

/**
 * Created by doring on 15. 4. 8..
 */
@Entity
@Table(name = "vote_by_user", indexes = {@Index(name = "idx_target_apt_vote", columnList = "targetApt"), @Index(name = "idx_target_dong_vote", columnList = "targetDong"),
        @Index(name = "idx_visible_vote", columnList = "visible")})
public class VoteByUser {

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

    @Column(length = 400)
    public String userComment;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;

    public Integer targetApt;

    @Column(length = 20)
    public String targetDong;

    @Column(nullable = false)
    public Boolean visible;

    @Column(nullable = false)
    public Boolean multipleChoice;

    public Integer multipleChoiceMaxCount;

    @Column(length = 50)
    public String sendResultTo;

    @Column(nullable = false)
    public Date regDate;

    public Integer periodByDay;

    public Integer imageCount;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "parentId", referencedColumnName = "id")
    public List<VoteItem> items;
}
