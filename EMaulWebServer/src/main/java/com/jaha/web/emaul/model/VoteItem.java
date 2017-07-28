package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by doring on 15. 3. 5..
 */
@Entity
@Table(name = "vote_item")
public class VoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long parentId;

    @Column(length = 200)
    public String title;

    @Column(length = 1000)
    public String commitment;

    @Column(length = 1000)
    public String profile;

    public Boolean isSubjective;

    public Integer imageCount;
}
