package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by doring on 15. 6. 16..
 */
@Entity
public class BoardTag {
    @Id
    @Column(length = 20)
    public String name;
}
