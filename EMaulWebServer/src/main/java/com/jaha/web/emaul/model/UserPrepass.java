package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by doring on 15. 5. 24..
 */
@Entity
@Table(name = "user_prepass", indexes = {@Index(name = "idx_user_prepass_fullName", columnList = "fullName"), @Index(name = "idx_user_prepass_phone", columnList = "phone"),
        @Index(name = "idx_user_prepass_apt_id", columnList = "aptId"), @Index(name = "idx_user_prepass_dong", columnList = "dong"), @Index(name = "idx_user_prepass_ho", columnList = "ho")})
public class UserPrepass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 40)
    public String fullName;
    @Column(length = 40)
    public String phone;

    public Long aptId;

    @Column(length = 20)
    public String dong;
    @Column(length = 20)
    public String ho;
}
