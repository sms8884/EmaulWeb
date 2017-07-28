package com.jaha.web.emaul.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaha.web.emaul.constants.UserPrivacy;

/**
 * Created by doring on 15. 3. 9..
 */
@Entity
@Table(name = "board_category", indexes = {@Index(name = "idx_ord_category", columnList = "ord"), @Index(name = "idx_type_category", columnList = "type")})
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(length = 100, nullable = false)
    public String type;

    @Column(length = 100, nullable = false)
    public String name;

    public Integer ord;

    @ManyToOne(targetEntity = Apt.class, cascade = {})
    @JoinColumn(name = "aptId", referencedColumnName = "id")
    @JsonIgnore
    public Apt apt;

    @Column(nullable = false, length = 500)
    @JsonIgnore
    public String jsonArrayReadableUserType = "[]";

    @Column(nullable = false, length = 500)
    @JsonIgnore
    public String jsonArrayWritableUserType = "[]";

    @Column(nullable = false, length = 4)
    public String contentMode = "text";

    @Column(nullable = false, length = 1)
    public String pushAfterWrite = "N";

    /** 댓글 표시 여부 */
    @Column
    public String commentDisplayYn = "Y";

    @Enumerated(EnumType.STRING)
    private UserPrivacy userPrivacy;

    /** 삭제 (사용) 여부 */
    @Column(nullable = true, length = 1)
    public String delYn = "N";

    public UserPrivacy getUserPrivacy() {
        return userPrivacy;
    }

    public void setUserPrivacy(UserPrivacy userPrivacy) {
        this.userPrivacy = userPrivacy;
    }

    @Transient
    public Boolean isWritable = false;

    @Transient
    public List<String> getReadableUserTypes() {
        return new Gson().fromJson(jsonArrayReadableUserType, new TypeToken<List<String>>() {}.getType());
    }

    @Transient
    public List<String> getWritableUserTypes() {
        return new Gson().fromJson(jsonArrayWritableUserType, new TypeToken<List<String>>() {}.getType());
    }

    @Transient
    public boolean isUserReadable(User user) {
        List<String> trueTypes = user.type.getTrueTypes();
        List<String> readableUserTypes = getReadableUserTypes();
        List<String> writableUserTypes = getWritableUserTypes();

        for (String trueType : trueTypes) {
            if (readableUserTypes.contains(trueType) || writableUserTypes.contains(trueType)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean isUserWritable(User user) {
        List<String> trueTypes = user.type.getTrueTypes();
        List<String> writableUserTypes = getWritableUserTypes();

        for (String trueType : trueTypes) {
            if (writableUserTypes.contains(trueType)) {
                return true;
            }
        }
        return false;
    }
}
