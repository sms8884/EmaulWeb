package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by doring on 15. 6. 7..
 */
@Entity
public class AptContact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 50, nullable = false)
    public String name;

    @Column(length = 30, nullable = false)
    public String phoneNumber;
}
