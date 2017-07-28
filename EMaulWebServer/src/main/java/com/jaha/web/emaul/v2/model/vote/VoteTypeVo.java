package com.jaha.web.emaul.v2.model.vote;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called vote_type
 */
@Alias(value = "VoteTypeVo")
public class VoteTypeVo implements Serializable {

    private static final long serialVersionUID = 6244324545512214527L; // 2016.10.12

    /** 투표 일련번호 */
    private Long id;

    /** 타입 (1Depth : vote, poll) */
    private String main;

    /** 하위 타입 (2Depth : default, candidate, yn...) */
    private String sub;

    /** 투표 유형 명 */
    private String text;

    /** 투표 유형 정렬 순서 */
    private Integer sortOrder;

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

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the main
     */
    public String getMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * @return the sub
     */
    public String getSub() {
        return sub;
    }

    /**
     * @param sub the sub to set
     */
    public void setSub(String sub) {
        this.sub = sub;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

}
