/**
 *
 */
package com.jaha.web.emaul.v2.model.apt;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

import com.jaha.web.emaul.v2.model.common.CommonDto;
import com.jaha.web.emaul.v2.model.group.GroupAdminVo;

/**
 * @author shavrani <br />
 */
@Alias(value = "AptDto")
public class AptDto extends CommonDto implements Serializable {

    private static final long serialVersionUID = -1065780325599821927L;

    /** 등록/미등록 구분 */
    private String registered;

    /** 아파트 검색 키워드 */
    private String searchApt;

    /** group admin 정보 */
    private GroupAdminVo groupAdminVo;

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getSearchApt() {
        return searchApt;
    }

    public void setSearchApt(String searchApt) {
        this.searchApt = searchApt;
    }

    public GroupAdminVo getGroupAdminVo() {
        return groupAdminVo;
    }

    public void setGroupAdminVo(GroupAdminVo groupAdminVo) {
        this.groupAdminVo = groupAdminVo;
    }

}
