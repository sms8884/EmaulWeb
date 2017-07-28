package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "CodeGroup")
@Table(name = "code_group")
public class CodeGroup {

    @Id
    @Column(name = "code_group", insertable = false, updatable = false)
    public String codeGroup;

    @Column
    public String name;

    @Column
    public int sort_order;

    @Column
    public String use_yn;

    /** view type이 varchar(20) */
    @Column
    public String regId;

    @Column
    public Date regDate;

    @Column
    public String modId;

    @Column
    public Date modDate;

    /** DB컬럼 아님 */
    public String description;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the codeGroup
     */
    public String getCodeGroup() {
        return codeGroup;
    }

    /**
     * @param codeGroup the codeGroup to set
     */
    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sort_order
     */
    public int getSort_order() {
        return sort_order;
    }

    /**
     * @param sort_order the sort_order to set
     */
    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    /**
     * @return the use_yn
     */
    public String getUse_yn() {
        return use_yn;
    }

    /**
     * @param use_yn the use_yn to set
     */
    public void setUse_yn(String use_yn) {
        this.use_yn = use_yn;
    }

    /**
     * @return the regId
     */
    public String getRegId() {
        return regId;
    }

    /**
     * @param regId the regId to set
     */
    public void setRegId(String regId) {
        this.regId = regId;
    }

    /**
     * @return the regDate
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the modId
     */
    public String getModId() {
        return modId;
    }

    /**
     * @param modId the modId to set
     */
    public void setModId(String modId) {
        this.modId = modId;
    }

    /**
     * @return the modDate
     */
    public Date getModDate() {
        return modDate;
    }

    /**
     * @param modDate the modDate to set
     */
    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }


}

