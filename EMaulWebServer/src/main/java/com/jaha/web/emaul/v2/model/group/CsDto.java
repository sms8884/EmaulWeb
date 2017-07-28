/**
 *
 */
package com.jaha.web.emaul.v2.model.group;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.v2.model.common.CommonDto;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 */
@Alias(value = "CsDto")
public class CsDto extends CommonDto implements Serializable {

    private static final long serialVersionUID = 6404873816520880960L;

    /** 아이디 */
    private Long id;

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



}
