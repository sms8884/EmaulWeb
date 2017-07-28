package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <pre>
 * Class Name : SimpleAddress.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 12.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 9. 12.
 * @version 1.0
 */
@Entity
@Table(name = "simple_address")
public class SimpleAddress {

    @Id
    @Column(length = 10)
    private String regionCd;

    /** 시 또는 도명 */
    @Column(length = 200, nullable = false)
    private String sidoNm;

    @Column(length = 200, nullable = true)
    private String sggNm;

    @Column(length = 200, nullable = true)
    private String emdNm;

    @Column(nullable = true)
    private Integer sortOrder;

    /**
     * @return the regionCd
     */
    public String getRegionCd() {
        return regionCd;
    }

    /**
     * @param regionCd the regionCd to set
     */
    public void setRegionCd(String regionCd) {
        this.regionCd = regionCd;
    }

    /**
     * @return the sidoNm
     */
    public String getSidoNm() {
        return sidoNm;
    }

    /**
     * @param sidoNm the sidoNm to set
     */
    public void setSidoNm(String sidoNm) {
        this.sidoNm = sidoNm;
    }

    /**
     * @return the sggNm
     */
    public String getSggNm() {
        return sggNm;
    }

    /**
     * @param sggNm the sggNm to set
     */
    public void setSggNm(String sggNm) {
        this.sggNm = sggNm;
    }

    /**
     * @return the emdNm
     */
    public String getEmdNm() {
        return emdNm;
    }

    /**
     * @param emdNm the emdNm to set
     */
    public void setEmdNm(String emdNm) {
        this.emdNm = emdNm;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SimpleAddress [regionCd=" + regionCd + ", sidoNm=" + sidoNm + ", sggNm=" + sggNm + ", emdNm=" + emdNm + ", sortOrder=" + sortOrder + "]";
    }

}
