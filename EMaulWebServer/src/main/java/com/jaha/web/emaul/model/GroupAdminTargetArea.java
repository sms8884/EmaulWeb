package com.jaha.web.emaul.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <pre>
 * Class Name : GroupAdminTargetArea.java
 * Description : 단체 관리자용 권한 지역 설정 Model
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 31.     박남석         Generation
 * </pre>
 *
 * @author 박남석
 * @since 2016. 8. 31.
 * @version 1.0
 */
@Entity
@Table(name = "groupadmin_target_area")
public class GroupAdminTargetArea {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long aptId;

    public Long userId;

    public String area1;

    public String area2;

    public String area3;

    public String area4;
}
