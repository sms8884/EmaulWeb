/**
 *
 */
package com.jaha.web.emaul.v2.mapper.group;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.apt.AptDto;
import com.jaha.web.emaul.v2.model.apt.AptVo;
import com.jaha.web.emaul.v2.model.group.CsDto;
import com.jaha.web.emaul.v2.model.group.CsVo;
import com.jaha.web.emaul.v2.model.group.GroupAdminVo;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Mapper class mapped db-table called groupadmin_target_area
 */
@Mapper
public interface GroupAdminMapper {

    /**
     * 그룹 관리자 정보 조회
     *
     * @param groupAdminVo
     * @return
     */
    public GroupAdminVo getGroupAdmin(GroupAdminVo groupAdminVo);

    /**
     * 그룹관리자 정보 수정
     *
     * @param groupAdminVo
     * @return
     */
    public int updateGroupAdmin(GroupAdminVo groupAdminVo);


    /**
     * 고객센터 1:1 문의내역 저장
     *
     * @param csVo
     * @return
     */
    public Long insertGroupAdminCs(CsVo csVo);

    /**
     * 고객센터 1:1 문의내역 수정
     *
     * @param csVo
     * @return
     */
    public int updateGroupAdminCs(CsVo csVo);


    /**
     * 고객센터 1:1 문의내역 리스트 조회
     *
     * @param csVo
     * @return
     */
    public List<CsVo> selectCsList(CsDto csDto);

    /**
     * 고객센터 1:1 문의내역 리스트 갯수 조회
     * 
     * @param csDto
     * @return
     */
    public int selectCsListCount(CsDto csDto);

    /**
     * 아파트 목록 ( 단체관리자의 지정지역내의 아파트 )
     * 
     * @param aptDto
     * @return
     */
    public List<AptVo> selectAptList(AptDto aptDto);

    public int selectAptListCount(AptDto aptDto);

}
