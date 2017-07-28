package com.jaha.web.emaul.v2.model.group;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.model.BaseSecuModel;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Domain class mapped db-table called cs_history
 */
@Alias(value = "CsVo")
public class CsVo extends BaseSecuModel implements Serializable {

    private static final long serialVersionUID = -964420542711060510L;

    /** 일련번호 */
    private Long id;
    /** 사용자 아이디 */
    private Long userId;
    private String email;
    private String title;
    private String content;
    private String file1;
    private String file2;
    private String file3;
    private Date regDate;
    private Date mailSendDate;

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
     * @return the mailSendDate
     */
    public Date getMailSendDate() {
        return mailSendDate;
    }

    /**
     * @param mailSendDate the mailSendDate to set
     */
    public void setMailSendDate(Date mailSendDate) {
        this.mailSendDate = mailSendDate;
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
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }

    /**
     * @param file1 the file1 to set
     */
    public void setFile1(String file1) {
        this.file1 = file1;
    }

    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }

    /**
     * @param file2 the file2 to set
     */
    public void setFile2(String file2) {
        this.file2 = file2;
    }

    /**
     * @return the file3
     */
    public String getFile3() {
        return file3;
    }

    /**
     * @param file3 the file3 to set
     */
    public void setFile3(String file3) {
        this.file3 = file3;
    }


}
