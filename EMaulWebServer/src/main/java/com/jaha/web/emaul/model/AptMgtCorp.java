package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by doring on 15. 6. 7..
 */
@Entity
@Table(name = "apt_mgt_corp")
public class AptMgtCorp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    @Column(length = 20)
    public Long aptId;

    @Column(length = 90)
    public String name;

    @Column(length = 20)
    public String tel;

    @Column(length = 20)
    public Long regId;

    @Column(nullable = false)
    public Date regDate;

    @Column(length = 20)
    public Long modId;

    @Column(nullable = false)
    public Date modDate;


}
