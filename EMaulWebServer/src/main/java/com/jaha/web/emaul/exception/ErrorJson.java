/**
 *
 */
package com.jaha.web.emaul.exception;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
@Deprecated
@JsonPropertyOrder({"code", "message"})
public class ErrorJson implements Serializable {

    /** SID */
    private static final long serialVersionUID = 931981790737011635L;

    private int code;

    private String message;

    public ErrorJson() {

    }

    public ErrorJson(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
