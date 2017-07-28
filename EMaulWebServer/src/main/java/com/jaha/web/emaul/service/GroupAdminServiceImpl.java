package com.jaha.web.emaul.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaha.web.emaul.model.GroupAdminTargetArea;
import com.jaha.web.emaul.repo.GroupAdminTargetAreaRepository;

/**
 * <pre>
 * Class Name : GroupAdminServiceImpl.java
 * Description : 단체 관리자용 Service 구현체
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 31.     박남석         Generation
 * </pre>
 *
 * @author 박남석
 * @since 2016. 8. 31.
 * @version 1.0
 */
@Service
public class GroupAdminServiceImpl implements GroupAdminService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GroupAdminTargetAreaRepository groupAdminTargetAreaRepository;

    @Override
    public GroupAdminTargetArea getTargetArea(Long userId, Long aptId) {
        return groupAdminTargetAreaRepository.findByAptIdAndUserId(aptId, userId);
    }

    /**
     * @author 박남석
     * @description 단체관리자에게 할당된 행정구역의 시/도 리스트를 리턴
     *
     * @param userId : 단체관리자 ID
     * @param aptId : 단체관리자가 소속된 아파트 ID
     *
     * @return List<String> : 단체관리자에게 할당된 행정구역의 시/도 리스트
     */
    @Override
    public List<String> getSidoNames(Long userId, Long aptId) {
        GroupAdminTargetArea groupAdminTargetArea = groupAdminTargetAreaRepository.findByAptIdAndUserId(aptId, userId);

        if (groupAdminTargetArea != null) {
            if (groupAdminTargetArea != null && groupAdminTargetArea.area1 != null) {
                List<String> resultList = new ArrayList<String>();
                resultList.add(groupAdminTargetArea.area1);

                return resultList;
            }
        }

        return null;
    }

    /**
     * @author 박남석
     * @description 단체관리자에게 할당된 행정구역의 시/군/구 리스트를 리턴
     *
     * @param sidoName : 단체관리자에게 할당된 행정구역의 시/도
     * @param userId : 단체관리자 ID
     * @param aptId : 단체관리자가 소속된 아파트 ID
     *
     * @return List<String> : 단체관리자에게 할당된 행정구역의 시/군/구 리스트
     */
    @Override
    public List<String> getSigunguNames(String sidoName, Long userId, Long aptId) {
        GroupAdminTargetArea groupAdminTargetArea = groupAdminTargetAreaRepository.findByAptIdAndUserId(aptId, userId);

        if (groupAdminTargetArea != null) {
            if (groupAdminTargetArea != null && groupAdminTargetArea.area2 != null) {
                List<String> resultList = new ArrayList<String>();
                resultList.add(groupAdminTargetArea.area2);

                return resultList;
            }
        }

        return null;
    }

    /**
     * @author 박남석
     * @description 단체관리자에게 할당된 행정구역의 구/군 리스트를 리턴
     *
     * @param sidoName : 단체관리자에게 할당된 행정구역의 시/도
     * @param userId : 단체관리자 ID
     * @param aptId : 단체관리자가 소속된 아파트 ID
     *
     * @return List<String> : 단체관리자에게 할당된 행정구역의 구/군 리스트
     */
    @Override
    public List<HashMap> getGugunList(String sidoName, Long userId, Long aptId) {
        GroupAdminTargetArea groupAdminTargetArea = groupAdminTargetAreaRepository.findByAptIdAndUserId(aptId, userId);

        if (groupAdminTargetArea != null) {
            if (groupAdminTargetArea != null && groupAdminTargetArea.area1 != null && groupAdminTargetArea.area2 != null) {
                List<HashMap> resultList = new ArrayList<HashMap>();
                HashMap item = new HashMap<String, String>();
                item.put("gugun", groupAdminTargetArea.area2);
                item.put("sido", groupAdminTargetArea.area1);
                resultList.add(item);

                return resultList;
            }
        }

        return null;
    }
}
