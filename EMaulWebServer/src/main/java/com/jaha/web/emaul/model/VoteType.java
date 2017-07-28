package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by doring on 15. 3. 18..
 */
@Entity
@Table(name = "vote_type")
public class VoteType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 20, nullable = false)
    public String main;

    @Column(length = 20, nullable = false)
    public String sub;
}
