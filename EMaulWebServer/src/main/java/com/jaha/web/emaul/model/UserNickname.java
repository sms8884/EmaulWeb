package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by doring on 15. 3. 10..
 */
@Entity
@Table(name = "user_nickname")
public class UserNickname {
    @Id
    @Column(length = 40)
    public String name;
}
