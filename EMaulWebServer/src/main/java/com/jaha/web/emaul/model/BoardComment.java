package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 15. 3. 9..
 */
@Entity
@Table(name = "board_comment", indexes = {@Index(name = "idx_date_comment", columnList = "regDate")})
public class BoardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    // db fkey 설정에서 restrict를 cascade로 바꿔줘야 함.
    @ManyToOne(targetEntity = BoardPost.class, cascade = {})
    @JoinColumn(name = "postId", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_board_post"))
    @JsonIgnore
    public BoardPost post;

    @Column(length = 400)
    public String content;

    @Column(nullable = false)
    public Date regDate;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;

    @Column(nullable = false, columnDefinition = "BIGINT(20) NOT NULL DEFAULT 0")
    public Long replyCount = 0l;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean blocked = false;

    @Transient
    public Boolean isDeletable = false;
}
