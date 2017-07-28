package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by doring on 15. 4. 6..
 */
@Entity
@Table(name = "gas_check", indexes = {@Index(name = "idx_date_gas_check", columnList = "date")})
public class GasCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Date date;

    @Column(length = 100)
    public String imageUri;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    @JsonIgnore
    public User user;
}
