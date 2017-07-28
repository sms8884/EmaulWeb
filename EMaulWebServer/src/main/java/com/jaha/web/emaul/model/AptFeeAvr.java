package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by doring on 15. 5. 4..
 */
@Entity
@Table(name = "apt_fee_avr", indexes = {@Index(name = "idx_apt_fee_avr_search", columnList = "aptId, date, houseSize")})
public class AptFeeAvr {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 20, nullable = false)
    public String houseSize;

    // yyyyMM
    @Column(length = 10, nullable = false)
    public String date;

    public Long aptId;

    @Column(length = 2000, nullable = false)
    public String json;
}
