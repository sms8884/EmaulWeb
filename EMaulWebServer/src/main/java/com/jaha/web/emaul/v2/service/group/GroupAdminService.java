/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.group;

import java.util.List;

import com.jaha.web.emaul.v2.model.apt.AptDto;
import com.jaha.web.emaul.v2.model.apt.AptVo;
import com.jaha.web.emaul.v2.model.group.CsDto;
import com.jaha.web.emaul.v2.model.group.CsVo;
import com.jaha.web.emaul.v2.model.group.GroupAdminVo;

/**
 * <pre>
 * Class Name : GroupAdminService.java
 * Description : 그룹관리자 서비스
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 11. 18.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 11. 18.
 * @version 1.0
 */
public interface GroupAdminService {

    /**
     * 그룹관리자 정보 조회
     *
     * @param groupAdminVo
     * @return
     * @throws Exception
     */
    GroupAdminVo getGroupAdmin(GroupAdminVo groupAdminVo) throws Exception;

    /**
     * 그룹 관리자 정보 수정
     *
     * @param groupAdminVo
     * @return
     * @throws Exception
     */
    int updateGroupAdmin(GroupAdminVo groupAdminVo) throws Exception;


    /**
     * 고객센터 1:1 문의내역 이력
     *
     * @param csVo
     * @return
     * @throws Exception
     */
    Long insertGroupAdminCs(CsVo csVo) throws Exception;

    /**
     * 고객센터 1:1 문의내역 메일 발송여부 및 파일 정보 업데이트
     *
     * @param csVo
     * @return
     * @throws Exception
     */
    int updateGroupAdminCs(CsVo csVo) throws Exception;

    /**
     * 고객센터 1:1 문의내역 리스트
     *
     * @param csDto
     * @return
     * @throws Exception
     */
    List<CsVo> selectCsList(CsDto csDto) throws Exception;

    /**
     * 아파트 목록 ( 단체관리자의 지정지역내의 아파트 )
     * 
     * @param aptDto
     * @return
     * @throws Exception
     */
    List<AptVo> selectAptList(AptDto aptDto) throws Exception;

}
