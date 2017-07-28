package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by doring on 15. 5. 25..
 */
@Entity
@Table(name = "voter_offline", indexes = {@Index(name = "idx_voter_offline_apt_id", columnList = "aptId"), @Index(name = "idx_voter_offline_vote_id", columnList = "voteId"),
        @Index(name = "idx_voter_offline_dong", columnList = "dong"), @Index(name = "idx_voter_offline_ho", columnList = "ho")})
public class VoterOffline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long aptId;

    public Long voteId;

    @Column(length = 40)
    public String fullName;

    @Column(length = 20)
    public String dong;
    @Column(length = 20)
    public String ho;

    public Date regDate;
}
