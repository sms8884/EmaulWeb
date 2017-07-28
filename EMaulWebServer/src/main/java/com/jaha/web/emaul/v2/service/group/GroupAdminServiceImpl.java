/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.repo.UserRepository;
import com.jaha.web.emaul.util.PasswordHash;
import com.jaha.web.emaul.v2.constants.BoardConstants;
import com.jaha.web.emaul.v2.mapper.group.GroupAdminMapper;
import com.jaha.web.emaul.v2.model.apt.AptDto;
import com.jaha.web.emaul.v2.model.apt.AptVo;
import com.jaha.web.emaul.v2.model.common.Sort;
import com.jaha.web.emaul.v2.model.group.CsDto;
import com.jaha.web.emaul.v2.model.group.CsVo;
import com.jaha.web.emaul.v2.model.group.GroupAdminVo;

/**
 * <pre>
 * Class Name : GroupAdminServiceImpl.java
 * Description : 그룹 관리자 서비스 구현
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
@Service("v2GroupAdminService")
public class GroupAdminServiceImpl implements GroupAdminService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupAdminMapper groupAdminMapper;

    @Autowired
    private UserRepository userRepository;

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.v2.service.group.GroupAdminService#getGroupAdmin(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public GroupAdminVo getGroupAdmin(GroupAdminVo groupAdminVo) throws Exception {
        // 그룹 관리자 정보 조회
        return this.groupAdminMapper.getGroupAdmin(groupAdminVo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.v2.service.group.GroupAdminService#updateGroupAdmin(com.jaha.web.emaul.v2.model.group.GroupAdminVo)
     */
    @Override
    @Transactional
    public int updateGroupAdmin(GroupAdminVo groupAdminVo) throws Exception {

        if (StringUtils.isNotEmpty(groupAdminVo.getPassword())) {
            BaseSecuModel bsm = new BaseSecuModel();
            this.userRepository.updatePassword(PasswordHash.createHash(groupAdminVo.getPassword()), bsm.encString(groupAdminVo.getUserEmail()));
        }
        return this.groupAdminMapper.updateGroupAdmin(groupAdminVo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.v2.service.group.GroupAdminService#insertGroupAdminCs(com.jaha.web.emaul.v2.model.group.CsVo)
     */
    @Override
    @Transactional
    public Long insertGroupAdminCs(CsVo csVo) throws Exception {

        return this.groupAdminMapper.insertGroupAdminCs(csVo);
    }

    @Override
    @Transactional
    public int updateGroupAdminCs(CsVo csVo) throws Exception {

        return this.groupAdminMapper.updateGroupAdminCs(csVo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.v2.service.group.GroupAdminService#selectCsList(com.jaha.web.emaul.v2.model.group.CsVo)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CsVo> selectCsList(CsDto csDto) throws Exception {

        // 투표 목록 레코드 수 조회
        int totalRecordCount = this.groupAdminMapper.selectCsListCount(csDto);
        csDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        csDto.getPagingHelper().setSortList(sortList);

        return this.groupAdminMapper.selectCsList(csDto);
    }

    @Override
    public List<AptVo> selectAptList(AptDto aptDto) throws Exception {

        int totalRecordCount = this.groupAdminMapper.selectAptListCount(aptDto);
        aptDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        return groupAdminMapper.selectAptList(aptDto);
    }

}
