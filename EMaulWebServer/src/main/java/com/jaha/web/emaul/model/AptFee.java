package com.jaha.web.emaul.model;

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
 * Created by doring on 15. 3. 30..
 */
@Entity
@Table(name = "apt_fee", indexes = {@Index(name = "idx_apt_fee_search", columnList = "date, houseId")})
public class AptFee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(targetEntity = House.class, cascade = {})
    @JoinColumn(name = "houseId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    public House house;

    @Column(length = 20, nullable = false)
    public String date; // yyyyMM

    @Column(length = 2000, nullable = false)
    public String json;
}
