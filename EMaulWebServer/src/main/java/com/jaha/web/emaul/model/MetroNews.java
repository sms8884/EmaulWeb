package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * <pre>
 * Class Name : MetroNews.java
 * Description : 메트로신문사 뉴스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 27.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 27.
 * @version 1.0
 */
@Entity
@Table(name = "metro_news")
public class MetroNews {

    /** 기사번호, 기사URL(http://www.metroseoul.co.kr/news/newsview?newscd=2016080700078) */
    @Id
    private String newsCd;

    /** 기사입력상태(I,U,D) */
    @Column(length = 1, nullable = false)
    private String newsAction;

    /** 기사승인시간 */
    @Column(nullable = false)
    private Date newsAppNdt;

    /** 기사수정시간 */
    @Column(nullable = false)
    private Date newsAppEdt;

    /** 기사분류코드 */
    @Column(length = 20, nullable = false)
    private String newsCateCd;

    /** 기사분류명 */
    @Column(length = 40, nullable = false)
    private String newsCateNm;

    /** 기사제목 */
    @Column(length = 500, nullable = false)
    private String newsTitle;

    /** 기사부제목 */
    @Column(length = 500, nullable = true)
    private String newsSubtitle;

    /** 기사본문 */
    @Column(length = 4000, nullable = true)
    private String newsContent;

    /** 기자정보 */
    @Column(length = 1000, nullable = false)
    private String newsEditors;

    /** 메인이미지URL과 이미지정보 */
    @Column(length = 1000, nullable = true)
    private String mainImageUrl;

    /** 등록일시 */
    @Column(nullable = false)
    private Date regDate;

    /** 수정일시 */
    @Column(nullable = true)
    private Date modDate;

    @PrePersist
    public void prePersist() {
        this.regDate = new Date();
        this.modDate = this.regDate;
    }

    @PostLoad
    public void postPersist() {

    }

    @PreUpdate
    public void preUpdate() {
        this.modDate = new Date();
    }

    @PreRemove
    public void preRemove() {

    }

    /**
     * @return the newsCd
     */
    public String getNewsCd() {
        return newsCd;
    }

    /**
     * @param newsCd the newsCd to set
     */
    public void setNewsCd(String newsCd) {
        this.newsCd = newsCd;
    }

    /**
     * @return the newsAction
     */
    public String getNewsAction() {
        return newsAction;
    }

    /**
     * @param newsAction the newsAction to set
     */
    public void setNewsAction(String newsAction) {
        this.newsAction = newsAction;
    }

    /**
     * @return the newsAppNdt
     */
    public Date getNewsAppNdt() {
        return newsAppNdt;
    }

    /**
     * @param newsAppNdt the newsAppNdt to set
     */
    public void setNewsAppNdt(Date newsAppNdt) {
        this.newsAppNdt = newsAppNdt;
    }

    /**
     * @return the newsAppEdt
     */
    public Date getNewsAppEdt() {
        return newsAppEdt;
    }

    /**
     * @param newsAppEdt the newsAppEdt to set
     */
    public void setNewsAppEdt(Date newsAppEdt) {
        this.newsAppEdt = newsAppEdt;
    }

    /**
     * @return the newsCateCd
     */
    public String getNewsCateCd() {
        return newsCateCd;
    }

    /**
     * @param newsCateCd the newsCateCd to set
     */
    public void setNewsCateCd(String newsCateCd) {
        this.newsCateCd = newsCateCd;
    }

    /**
     * @return the newsCateNm
     */
    public String getNewsCateNm() {
        return newsCateNm;
    }

    /**
     * @param newsCateNm the newsCateNm to set
     */
    public void setNewsCateNm(String newsCateNm) {
        this.newsCateNm = newsCateNm;
    }

    /**
     * @return the newsTitle
     */
    public String getNewsTitle() {
        return newsTitle;
    }

    /**
     * @param newsTitle the newsTitle to set
     */
    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    /**
     * @return the newsSubtitle
     */
    public String getNewsSubtitle() {
        return newsSubtitle;
    }

    /**
     * @param newsSubtitle the newsSubtitle to set
     */
    public void setNewsSubtitle(String newsSubtitle) {
        this.newsSubtitle = newsSubtitle;
    }

    /**
     * @return the newsContent
     */
    public String getNewsContent() {
        return newsContent;
    }

    /**
     * @param newsContent the newsContent to set
     */
    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    /**
     * @return the newsEditors
     */
    public String getNewsEditors() {
        return newsEditors;
    }

    /**
     * @param newsEditors the newsEditors to set
     */
    public void setNewsEditors(String newsEditors) {
        this.newsEditors = newsEditors;
    }

    /**
     * @return the mainImageUrl
     */
    public String getMainImageUrl() {
        return mainImageUrl;
    }

    /**
     * @param mainImageUrl the mainImageUrl to set
     */
    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MetroNews [newsCd=" + newsCd + ", newsAction=" + newsAction + ", newsAppNdt=" + newsAppNdt + ", newsAppEdt=" + newsAppEdt + ", newsCateCd=" + newsCateCd + ", newsCateNm=" + newsCateNm
                + ", newsTitle=" + newsTitle + ", newsSubtitle=" + newsSubtitle + ", newsContent=" + newsContent + ", newsEditors=" + newsEditors + ", mainImageUrl=" + mainImageUrl + ", regDate="
                + regDate + ", modDate=" + modDate + "]";
    }

}
