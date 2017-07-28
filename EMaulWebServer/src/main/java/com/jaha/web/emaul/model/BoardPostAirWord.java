package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "board_post_air_word")
public class BoardPostAirWord {

    @Id
    private Long categoryId;

    @Column(nullable = false)
    public String regDate;
    @Column(nullable = false)
    private Integer accWordCount;

    /**
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the regString
     */
    public String getRegDate() {
        return regDate;
    }

    /**
     * @param regDate the regDate to set
     */
    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    /**
     * @return the accWordCount
     */
    public Integer getAccWordCount() {
        return accWordCount;
    }

    /**
     * @param accWordCount the accWordCount to set
     */
    public void setAccWordCount(Integer accWordCount) {
        this.accWordCount = accWordCount;
    }


    @Override
    public String toString() {
        return new ToStringCreator(this).append("categoryid", categoryId).append("regDate", regDate).append("accWordCount", accWordCount).toString();
    }
}
