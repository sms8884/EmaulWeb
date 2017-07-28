package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user_view_log")
public class UserViewLog {

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "viewUserId", referencedColumnName = "id")
    public User viewUser;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "regId", referencedColumnName = "id")
    public User regUser;

    public Date regDate;

    @PrePersist
    public void prePersist() {
        regDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {}

    @PreRemove
    public void preRemove() {}

}
